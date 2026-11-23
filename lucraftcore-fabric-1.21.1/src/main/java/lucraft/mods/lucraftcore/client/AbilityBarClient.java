package lucraft.mods.lucraftcore.client;

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
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** HUD ability bar with next/previous/use keybinds. */
public final class AbilityBarClient {

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

        int size = 22;
        int gap = 3;
        int total = ENTRIES.size() * (size + gap) - gap;
        int startX = (context.getScaledWindowWidth() - total) / 2;
        int y = context.getScaledWindowHeight() - 32;

        for (int index = 0; index < ENTRIES.size(); index++) {
            AbilityBarPayload.Entry entry = ENTRIES.get(index);
            int x = startX + index * (size + gap);
            context.fill(x, y, x + size, y + size, index == selected ? 0xCC33CCFF : 0xAA000000);
            if (entry.enabled())
                context.fill(x, y, x + size, y + 2, 0xFF00FF88);
            String label = entry.id().isEmpty() ? "?" : entry.id().substring(0, 1).toUpperCase(Locale.ROOT);
            context.drawText(client.textRenderer, label, x + 8, y + 7, 0xFFFFFFFF, true);
            if (entry.cooldown() > 0 && entry.maxCooldown() > 0) {
                int height = Math.round((float) entry.cooldown() / entry.maxCooldown() * size);
                context.fill(x, y + size - height, x + size, y + size, 0x99000000);
            }
        }
    }
}
