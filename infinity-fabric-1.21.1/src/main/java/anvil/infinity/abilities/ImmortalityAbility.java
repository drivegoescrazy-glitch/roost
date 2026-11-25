package anvil.infinity.abilities;

import lucraft.mods.lucraftcore.superpowers.abilities.AttributeAbility;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * The Soul Stone's immortality. The original set the maximum health modifier to
 * {@code Float.MAX_VALUE}, which the modern client renders as hundreds of
 * hearts; this boost plus the lethal damage immunity in
 * {@link anvil.infinity.helpers.ImmortalityHandler} keeps the same "you cannot
 * die" behaviour with a usable health bar.
 */
public class ImmortalityAbility extends AttributeAbility {

    public static final double HEALTH_BOOST = 20.0D;

    public ImmortalityAbility(ServerPlayerEntity player) {
        super(player, "immortality", EntityAttributes.GENERIC_MAX_HEALTH, HEALTH_BOOST,
                EntityAttributeModifier.Operation.ADD_VALUE);
    }
}
