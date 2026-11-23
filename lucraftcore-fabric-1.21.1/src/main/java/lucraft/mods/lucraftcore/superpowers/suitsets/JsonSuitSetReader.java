package lucraft.mods.lucraftcore.superpowers.suitsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.superpowers.AbilityRegistry;
import lucraft.mods.lucraftcore.superpowers.effects.EffectReader;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class JsonSuitSetReader {

    private static final String[] SLOT_NAMES = {"head", "chest", "legs", "feet"};
    private static final net.minecraft.entity.EquipmentSlot[] SLOTS = {
            net.minecraft.entity.EquipmentSlot.HEAD, net.minecraft.entity.EquipmentSlot.CHEST,
            net.minecraft.entity.EquipmentSlot.LEGS, net.minecraft.entity.EquipmentSlot.FEET
    };
    private static final ArmorItem.Type[] TYPES = {
            ArmorItem.Type.HELMET, ArmorItem.Type.CHESTPLATE, ArmorItem.Type.LEGGINGS, ArmorItem.Type.BOOTS
    };

    public static SuitSet read(Identifier id, JsonObject json) {
        try {
            JsonObject armor = json.has("armor_material") ? json.getAsJsonObject("armor_material") : new JsonObject();
            int durability = armor.has("durability") ? armor.get("durability").getAsInt() : 15;
            int enchantability = armor.has("enchantibility") ? armor.get("enchantibility").getAsInt() : 9;
            float toughness = armor.has("toughness") ? armor.get("toughness").getAsFloat() : 0.0F;
            int[] reduction = new int[]{1, 3, 2, 1};
            if (armor.has("damage_reduction_amounts")) {
                JsonArray array = armor.getAsJsonArray("damage_reduction_amounts");
                for (int i = 0; i < Math.min(4, array.size()); i++)
                    reduction[i] = array.get(i).getAsInt();
            }

            Map<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
            for (int i = 0; i < TYPES.length; i++)
                defense.put(TYPES[i], reduction[i]);

            ArmorMaterial material = new ArmorMaterial(defense, enchantability,
                    SoundEvents.ITEM_ARMOR_EQUIP_IRON,
                    () -> Ingredient.EMPTY,
                    List.of(new ArmorMaterial.Layer(Identifier.of(id.getNamespace(), "suit/" + id.getPath()))),
                    toughness, 0.0F);
            Registry.register(Registries.ARMOR_MATERIAL, id, material);
            RegistryEntry<ArmorMaterial> materialEntry = Registries.ARMOR_MATERIAL.getEntry(material);

            String name = text(json.get("name"), id.getPath());
            SuitSet suit = new SuitSet(id, name);
            suit.setDescription(Text.literal(text(json.get("description"), "")));
            if (json.has("parent"))
                suit.setParent(Identifier.tryParse(json.get("parent").getAsString()));
            if (json.has("creative_tab"))
                suit.setCreativeTab(json.get("creative_tab").getAsString());
            if (json.has("data") && json.get("data").isJsonObject())
                suit.setData(json.getAsJsonObject("data"));
            suit.getEffects().addAll(EffectReader.read(json));

            JsonObject parts = json.has("armor_parts") ? json.getAsJsonObject("armor_parts") : new JsonObject();
            for (int i = 0; i < SLOTS.length; i++) {
                if (!parts.has(SLOT_NAMES[i]))
                    continue;
                Identifier itemId = Identifier.of(id.getNamespace(), id.getPath() + "_" + SLOT_NAMES[i]);
                JsonObject part = parts.getAsJsonObject(SLOT_NAMES[i]);
                boolean glow = part.has("glow") && part.get("glow").getAsBoolean();
                Item item = new SuitSetArmorItem(materialEntry, TYPES[i],
                        new Item.Settings().maxDamage(durability * durabilityFactor(TYPES[i])), suit, glow);
                Registry.register(Registries.ITEM, itemId, item);
                suit.setPiece(SLOTS[i], item);
                suit.setGlow(SLOTS[i], glow);
            }

            if (json.has("abilities")) {
                JsonArray abilities = json.getAsJsonArray("abilities");
                for (JsonElement element : abilities) {
                    String ability = null;
                    if (element.isJsonPrimitive())
                        ability = element.getAsString();
                    else if (element.isJsonObject()) {
                        JsonObject object = element.getAsJsonObject();
                        if (object.has("ability"))
                            ability = object.get("ability").getAsString();
                        else if (object.has("type"))
                            ability = object.get("type").getAsString();
                    }
                    if (ability != null) {
                        String key = ability.contains(":") ? ability.substring(ability.indexOf(':') + 1) : ability;
                        if (AbilityRegistry.exists(key))
                            suit.getAbilities().add(key);
                        else
                            LucraftCore.LOGGER.warn("Suit {} uses unknown ability {}", id, ability);
                    }
                }
            }

            SuitSetRegistry.register(suit);
            LucraftCore.LOGGER.info("Registered suit set {} ({} pieces, {} abilities, {} effects)",
                    id, suit.getPieces().size(), suit.getAbilities().size(), suit.getEffects().size());
            return suit;
        } catch (Exception e) {
            LucraftCore.LOGGER.error("Could not read suit set {}", id, e);
            return null;
        }
    }

    private static int durabilityFactor(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> 11;
            case CHESTPLATE -> 16;
            case LEGGINGS -> 15;
            case BOOTS -> 13;
            default -> 1;
        };
    }

    private static String text(JsonElement element, String fallback) {
        if (element == null || element.isJsonNull())
            return fallback;
        if (element.isJsonPrimitive())
            return element.getAsString();
        JsonObject object = element.getAsJsonObject();
        if (object.has("text"))
            return object.get("text").getAsString();
        if (object.has("translate"))
            return object.get("translate").getAsString();
        return fallback;
    }
}
