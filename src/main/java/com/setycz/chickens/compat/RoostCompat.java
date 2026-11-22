package com.setycz.chickens.compat;

import com.setycz.chickens.ChickensMod;
import com.setycz.chickens.ChickensRegistry;
import com.setycz.chickens.ChickensRegistryItem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Registers every chicken kind with Roost through its public
 * {@code com.timwoodcreates.roost.api.ChickenType} interface. Everything goes
 * through reflection so Roost stays an optional mod and no Roost class is ever
 * touched when it is missing.
 */
public final class RoostCompat {
	private static boolean available;
	private static Class<?> chickenTypeClass;
	private static Method roostRegister;
	private static Method roostGet;
	private static Method dataChickenOf;
	private static Method buildChickenStack;
	private static Method createDropStack;

	private RoostCompat() {
	}

	public static boolean isLoaded() {
		return FabricLoader.getInstance().isModLoaded("roost");
	}

	public static void init() {
		if (!isLoaded()) {
			return;
		}
		try {
			chickenTypeClass = Class.forName("com.timwoodcreates.roost.api.ChickenType");
			Class<?> chickens = Class.forName("com.timwoodcreates.roost.RoostChickens");
			roostRegister = chickens.getMethod("register", chickenTypeClass);
			roostGet = chickens.getMethod("get", String.class);
			Class<?> dataChicken = Class.forName("com.timwoodcreates.roost.data.DataChicken");
			dataChickenOf = dataChicken.getMethod("of", chickenTypeClass);
			buildChickenStack = dataChicken.getMethod("buildChickenStack");
			createDropStack = dataChicken.getMethod("createDropStack");
			available = true;

			int registered = 0;
			for (ChickensRegistryItem item : ChickensRegistry.getItems()) {
				roostRegister.invoke(null, createType(item));
				registered++;
			}
			ChickensMod.LOGGER.info("Registered {} chicken kinds with Roost", registered);
			verifyRoundTrip();
		} catch (Throwable throwable) {
			available = false;
			ChickensMod.LOGGER.warn("Roost integration unavailable, chickens will stay Chicken-only", throwable);
		}
	}

	/**
	 * Proves the whole path the roost uses for drops: the kind id stored on a
	 * chicken item has to resolve back to our type, and that type has to build
	 * our drop item.
	 */
	private static void verifyRoundTrip() {
		if (ChickensRegistry.getItems().isEmpty()) {
			return;
		}
		try {
			String sample = ChickensRegistry.getItems().get(0).getRegistryName().toString();
			Object type = roostGet.invoke(null, sample);
			Object data = type == null ? null : dataChickenOf.invoke(null, type);
			Object drop = data == null ? null : createDropStack.invoke(data);
			logModelCoverage();
			if (drop instanceof ItemStack stack && !stack.isEmpty()) {
				ChickensMod.LOGGER.info("Roost round-trip check passed for {} -> {}",
						sample, net.minecraft.registry.Registries.ITEM.getId(stack.getItem()));
			} else {
				ChickensMod.LOGGER.warn("Roost did not resolve {} back to one of our chicken kinds", sample);
			}
		} catch (Throwable throwable) {
			ChickensMod.LOGGER.warn("Roost round-trip check failed", throwable);
		}
	}

	/** Reports how many kinds Roost can draw with their own chicken model. */
	private static void logModelCoverage() {
		try {
			Class<?> textures = Class.forName("com.timwoodcreates.roost.RoostTextures");
			Method modelIndex = textures.getMethod("modelIndex", String.class);
			int unknown = (Integer) modelIndex.invoke(null, "unknownchicken");
			int missing = 0;
			for (ChickensRegistryItem item : ChickensRegistry.getItems()) {
				if ((Integer) modelIndex.invoke(null, textureName(item)) == unknown) {
					missing++;
				}
			}
			int total = ChickensRegistry.getItems().size();
			ChickensMod.LOGGER.info("Roost item models resolved for {}/{} chicken kinds",
					total - missing, total);
		} catch (Throwable throwable) {
			ChickensMod.LOGGER.warn("Could not check Roost item models", throwable);
		}
	}

	private static Object createType(ChickensRegistryItem item) {
		InvocationHandler handler = (proxy, method, args) -> switch (method.getName()) {
			case "id" -> item.getRegistryName();
			case "texture" -> textureName(item);
			case "getDisplayName", "getSpawnDescription" -> Text.translatable("entity." + item.getEntityName() + ".name");
			case "minLayTime" -> item.getMinLayTime();
			case "maxLayTime" -> item.getMaxLayTime();
			// Roost asks for a drop with its own java.util.Random; our kinds do
			// not use randomness so the argument is ignored.
			case "createDrop" -> item.createDropItem();
			case "getParents" -> List.of();
			case "equals" -> proxy == args[0];
			case "hashCode" -> System.identityHashCode(proxy);
			case "toString" -> "ChickensRoostType[" + item.getRegistryName() + "]";
			default -> null;
		};
		return Proxy.newProxyInstance(RoostCompat.class.getClassLoader(),
				new Class<?>[]{chickenTypeClass}, handler);
	}

	/** The texture file name Roost looks up in its own asset namespace. */
	private static String textureName(ChickensRegistryItem item) {
		String path = item.getTexture().getPath();
		int slash = path.lastIndexOf('/');
		String file = slash >= 0 ? path.substring(slash + 1) : path;
		String base = file.endsWith(".png") ? file.substring(0, file.length() - 4) : file;
		// Roost names its chicken models and textures without underscores
		// (white_chicken -> whitechicken), and we ship both lookups for them.
		return base.replace("_", "");
	}

	/**
	 * A Roost chicken item describing the given kind, used when one of our
	 * chickens is caught with Roost's chicken catcher.
	 */
	public static ItemStack captureStack(String typeId) {
		if (!available || typeId == null || typeId.isEmpty()) {
			return ItemStack.EMPTY;
		}
		try {
			Object type = roostGet.invoke(null, typeId);
			if (type == null) {
				return ItemStack.EMPTY;
			}
			Object data = dataChickenOf.invoke(null, type);
			Object stack = buildChickenStack.invoke(data);
			return stack instanceof ItemStack itemStack ? itemStack : ItemStack.EMPTY;
		} catch (Throwable throwable) {
			ChickensMod.LOGGER.warn("Could not build a Roost chicken item for {}", typeId, throwable);
			return ItemStack.EMPTY;
		}
	}
}
