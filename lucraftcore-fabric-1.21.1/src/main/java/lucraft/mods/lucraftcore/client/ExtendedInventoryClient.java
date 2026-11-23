package lucraft.mods.lucraftcore.client;

import lucraft.mods.lucraftcore.util.network.OpenExtendedInventoryPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/** Keybind that asks the server to open the extended inventory. */
public final class ExtendedInventoryClient {

    private static KeyBinding openKey;

    private ExtendedInventoryClient() {
    }

    public static void register() {
        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.lucraftcore.extended_inventory",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "category.lucraftcore"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openKey.wasPressed()) {
                if (client.player != null)
                    ClientPlayNetworking.send(OpenExtendedInventoryPayload.INSTANCE);
            }
        });
    }
}
