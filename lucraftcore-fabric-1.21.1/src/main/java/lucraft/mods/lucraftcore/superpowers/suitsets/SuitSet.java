package lucraft.mods.lucraftcore.superpowers.suitsets;

import com.google.gson.JsonObject;
import lucraft.mods.lucraftcore.superpowers.effects.Effect;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SuitSet {

    private final Identifier id;
    private final String name;
    private final Map<EquipmentSlot, Item> pieces = new EnumMap<>(EquipmentSlot.class);
    private final List<String> abilities = new ArrayList<>();
    private final List<Effect> effects = new ArrayList<>();
    private final Map<EquipmentSlot, Boolean> glow = new EnumMap<>(EquipmentSlot.class);
    private Identifier parent;
    private Text description = Text.empty();
    private JsonObject data = new JsonObject();
    private String creativeTab;

    public SuitSet(Identifier id, String name) {
        this.id = id;
        this.name = name;
    }

    public Identifier getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<EquipmentSlot, Item> getPieces() {
        return pieces;
    }

    public void setPiece(EquipmentSlot slot, Item item) {
        pieces.put(slot, item);
    }

    public Item getPiece(EquipmentSlot slot) {
        return pieces.get(slot);
    }

    public List<String> getAbilities() {
        return abilities;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public Identifier getParent() {
        return parent;
    }

    public void setParent(Identifier parent) {
        this.parent = parent;
    }

    public Text getDescription() {
        return description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data == null ? new JsonObject() : data;
    }

    public String getCreativeTab() {
        return creativeTab;
    }

    public void setCreativeTab(String creativeTab) {
        this.creativeTab = creativeTab;
    }

    public boolean hasGlow(EquipmentSlot slot) {
        return glow.getOrDefault(slot, false);
    }

    public void setGlow(EquipmentSlot slot, boolean value) {
        glow.put(slot, value);
    }

    public boolean hasFullSuit(LivingEntity entity) {
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            Item piece = pieces.get(slot);
            if (piece == null)
                continue;
            ItemStack stack = entity.getEquippedStack(slot);
            if (stack.isEmpty() || stack.getItem() != piece)
                return false;
        }
        return !pieces.isEmpty();
    }

    public static SuitSet getWornSuit(LivingEntity entity) {
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack stack = entity.getEquippedStack(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof SuitSetArmorItem armor) {
                SuitSet suit = armor.getSuitSet();
                if (suit.hasFullSuit(entity))
                    return suit;
            }
        }
        return null;
    }
}
