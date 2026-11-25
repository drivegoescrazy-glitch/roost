package anvil.infinity;

import anvil.infinity.client.StoneItemRenderer;
import anvil.infinity.items.Items;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.item.Item;

/** Client setup: the stones are 3D, so they get a built-in item renderer. */
public class InfinityClient implements ClientModInitializer {

    private static final StoneItemRenderer STONE_RENDERER = new StoneItemRenderer();

    @Override
    public void onInitializeClient() {
        for (Item item : Items.ALL)
            BuiltinItemRendererRegistry.INSTANCE.register(item, STONE_RENDERER);
        Infinity.LOGGER.debug("Registered infinity stone renderers");
    }
}
