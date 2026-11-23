package lucraft.mods.lucraftcore.superpowers.abilities;

import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.superpowers.Ability;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class AttributeAbility extends Ability {

    private final String id;
    private final RegistryEntry<EntityAttribute> attribute;
    private final double amount;
    private final EntityAttributeModifier.Operation operation;

    public AttributeAbility(ServerPlayerEntity player, String id, RegistryEntry<EntityAttribute> attribute,
                            double amount, EntityAttributeModifier.Operation operation) {
        super(player);
        this.id = id;
        this.attribute = attribute;
        this.amount = amount;
        this.operation = operation;
    }

    @Override
    public String getId() {
        return id;
    }

    private Identifier modifierId() {
        return LucraftCore.id("ability_" + id);
    }

    @Override
    protected void onEnabled() {
        EntityAttributeInstance instance = player.getAttributeInstance(attribute);
        if (instance == null)
            return;
        instance.removeModifier(modifierId());
        instance.addPersistentModifier(new EntityAttributeModifier(modifierId(), amount, operation));
    }

    @Override
    protected void onDisabled() {
        cleanup();
    }

    @Override
    protected void cleanup() {
        EntityAttributeInstance instance = player.getAttributeInstance(attribute);
        if (instance != null)
            instance.removeModifier(modifierId());
    }
}
