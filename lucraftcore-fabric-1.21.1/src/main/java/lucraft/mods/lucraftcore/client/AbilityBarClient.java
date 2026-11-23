package lucraft.mods.lucraftcore.client;

import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.util.network.AbilityBarPayload;
import lucraft.mods.lucraftcore.util.network.UseAbilityPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * HUD ability bar, laid out like the original 1.12 one: a vertical column of
 * 22x22 frames from {@code textures/gui/ability_bar.png} hanging from the top
 * left of the screen, one ability per frame, the selected ability's name shown
 * beside its frame and the use/scroll keys spelled out.
 */
public final class AbilityBarClient {

    private static final Identifier BAR_TEXTURE = LucraftCore.id("textures/gui/ability_bar.png");
    /** Frame UV inside the 256x256 sheet: the white frame (EnumAbilityBarColor.WHITE). */
    private static final int FRAME_U = 0;
    private static final int FRAME_V = 36;
    private static final int FRAME_SIZE = 22;
    private static final int FRAME_STEP = 21;
    /** How many entries the bar shows at once, as in the original. */
    private static final int ENTRY_SHOW_AMOUNT = 5;

    private static final List<AbilityBarPayload.Entry> ENTRIES = new ArrayList<>();
    private static int selected;

    private static KeyBinding nextKey;
    private static KeyBinding previousKey;
    private static KeyBinding useKey;

    private AbilityBarClient() {
    }

    public static void register() {
        nextKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.lucraftcore.ability_next",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET, "category.lucraftcore"));
        previousKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.lucraftcore.ability_previous",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET, "category.lucraftcore"));
        useKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.lucraftcore.ability_use",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "category.lucraftcore"));

        ClientPlayNetworking.registerGlobalReceiver(AbilityBarPayload.ID, (payload, context) -> {
            ENTRIES.clear();
            ENTRIES.addAll(payload.entries());
            if (selected >= ENTRIES.size())
                selected = 0;
        });

        HudRenderCallback.EVENT.register((context, tickCounter) -> render(context));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (nextKey.wasPressed())
                if (!ENTRIES.isEmpty())
                    selected = (selected + 1) % ENTRIES.size();
            while (previousKey.wasPressed())
                if (!ENTRIES.isEmpty())
                    selected = (selected - 1 + ENTRIES.size()) % ENTRIES.size();
            while (useKey.wasPressed())
                if (!ENTRIES.isEmpty())
                    ClientPlayNetworking.send(new UseAbilityPayload(ENTRIES.get(selected).id()));
        });
    }

    private static void render(DrawContext context) {
        if (ENTRIES.isEmpty())
            return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden)
            return;

        // The original hung the bar from the top left corner, starting at y = 12,
        // and showed five entries starting at the selected one.
        int x = 2;
        int y = 12;
        int shown = Math.min(ENTRY_SHOW_AMOUNT, ENTRIES.size());
        for (int slot = 0; slot < shown; slot++) {
            int index = (selected + slot) % ENTRIES.size();
            AbilityBarPayload.Entry entry = ENTRIES.get(index);
            int frameY = y + slot * FRAME_STEP;
            boolean active = index == selected;

            context.drawTexture(BAR_TEXTURE, x, frameY, FRAME_U, FRAME_V, FRAME_SIZE, FRAME_SIZE,
                    256, 256);

            // Ability icon: the original 16x16 sprite drawn inside the 22x22 frame.
            AbilityIcons.draw(context, entry.id(), x + 3, frameY + 3);

            if (entry.enabled())
                context.fill(x + 4, frameY + 18, x + 18, frameY + 19, 0xFF00FF88);

            if (entry.cooldown() > 0 && entry.maxCooldown() > 0) {
                int height = Math.round((float) entry.cooldown() / entry.maxCooldown() * 14.0F);
                context.fill(x + 4, frameY + 18 - height, x + 18, frameY + 18, 0x99000000);
            }

            if (active) {
                // Name preview to the right of the selected frame, like the
                // original's key/description text.
                String name = displayName(entry.id());
                context.fill(x + FRAME_SIZE + 2, frameY + 4, x + FRAME_SIZE + 8
                        + client.textRenderer.getWidth(name), frameY + 16, 0x80000000);
                context.drawText(client.textRenderer, name, x + FRAME_SIZE + 5, frameY + 7,
                        0xFEFEFE, true);
            }
        }

        if (ENTRIES.size() > shown) {
            String up = keyName(previousKey);
            String down = keyName(nextKey);
            int hintY = y + shown * FRAME_STEP + 2;
            context.drawText(client.textRenderer, up + " / " + down, x + 4, hintY, 0xFFB0B0B0, true);
            context.drawText(client.textRenderer, keyName(useKey), x + FRAME_SIZE + 5, y + 7,
                    0xFFB0B0B0, true);
        }
    }

    /** "fire_resistance" -> "Fire Resistance", as the original showed display names. */
    private static String displayName(String id) {
        if (id.isEmpty())
            return "?";
        String[] words = id.split("_");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty())
                continue;
            if (builder.length() > 0)
                builder.append(' ');
            builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return builder.toString();
    }

    private static String keyName(KeyBinding binding) {
        return binding.getBoundKeyLocalizedText().getString();
    }
}
