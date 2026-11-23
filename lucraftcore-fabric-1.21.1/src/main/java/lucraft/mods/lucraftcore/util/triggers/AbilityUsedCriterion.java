package lucraft.mods.lucraftcore.util.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lucraft.mods.lucraftcore.LucraftCore;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Optional;

/** Advancement trigger fired when a player uses an ability. */
public class AbilityUsedCriterion extends AbstractCriterion<AbilityUsedCriterion.Conditions> {

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, String abilityId) {
        Identifier id = Identifier.tryParse(abilityId);
        trigger(player, conditions -> conditions.ability().map(ability -> ability.equals(id)).orElse(true));
    }

    public record Conditions(Optional<LootContextPredicate> player, Optional<Identifier> ability)
            implements AbstractCriterion.Conditions {

        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                LootContextPredicate.CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                Identifier.CODEC.optionalFieldOf("ability").forGetter(Conditions::ability)
        ).apply(instance, Conditions::new));
    }
}
