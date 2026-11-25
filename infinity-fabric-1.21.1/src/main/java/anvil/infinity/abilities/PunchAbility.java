package anvil.infinity.abilities;

import lucraft.mods.lucraftcore.LucraftCore;
import lucraft.mods.lucraftcore.superpowers.Ability;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * The 1.12 Power Stone punch added {@code Float.MAX_VALUE} extra damage to
 * unarmed hits through Lucraft's own PUNCH_DAMAGE attribute. Modern Minecraft
 * has no such attribute, so the bonus is applied as a temporary attack-damage
 * modifier while the main hand is empty.
 *
 * <p>When the strength ability is active it already gives the same maximum
 * attack damage; adding a second maximum modifier would overflow the float
 * damage calculation, so the punch modifier is skipped in that case.</p>
 */
public class PunchAbility extends Ability {

    private static final Identifier MODIFIER_ID = LucraftCore.id("ability_infinity_punch");

    public PunchAbility(ServerPlayerEntity player) {
        super(player);
    }

    @Override
    public String getId() {
        return "infinity_punch";
    }

    @Override
    protected void onEnabled() {
        update();
    }

    @Override
    protected void onTick() {
        update();
    }

    @Override
    protected void onDisabled() {
        remove();
    }

    @Override
    protected void cleanup() {
        remove();
    }

    private void update() {
        EntityAttributeInstance instance = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (instance == null)
            return;
        // AttributeAbility uses this id for the Power Stone strength modifier.
        boolean strengthActive = instance.getModifier(LucraftCore.id("ability_infinity_strength")) != null;
        boolean shouldApply = player.getMainHandStack().isEmpty() && !strengthActive;
        if (!shouldApply) {
            instance.removeModifier(MODIFIER_ID);
            return;
        }
        if (instance.getModifier(MODIFIER_ID) == null)
            instance.addPersistentModifier(new EntityAttributeModifier(MODIFIER_ID,
                    Float.MAX_VALUE, EntityAttributeModifier.Operation.ADD_VALUE));
    }

    private void remove() {
        EntityAttributeInstance instance = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (instance != null)
            instance.removeModifier(MODIFIER_ID);
    }
}
