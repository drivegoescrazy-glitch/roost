package lucraft.mods.lucraftcore.sizechanging;

import com.mojang.brigadier.arguments.FloatArgumentType;
import lucraft.mods.lucraftcore.LCConfig;
import lucraft.mods.lucraftcore.LucraftCore;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class SizeChanging {

    public static final float MIN_SIZE = 0.05F;
    public static final float MAX_SIZE = 10F;

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) ->
                dispatcher.register(CommandManager.literal("sizechange")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("size", FloatArgumentType.floatArg(MIN_SIZE, MAX_SIZE))
                                .executes(context -> apply(context.getSource(), List.of(context.getSource().getPlayerOrThrow()),
                                        FloatArgumentType.getFloat(context, "size"))))
                        .then(CommandManager.argument("targets", net.minecraft.command.argument.EntityArgumentType.entities())
                                .then(CommandManager.argument("size", FloatArgumentType.floatArg(MIN_SIZE, MAX_SIZE))
                                        .executes(context -> apply(context.getSource(),
                                                net.minecraft.command.argument.EntityArgumentType.getEntities(context, "targets"),
                                                FloatArgumentType.getFloat(context, "size")))))));
    }

    private static int apply(ServerCommandSource source, java.util.Collection<? extends net.minecraft.entity.Entity> targets, float size) {
        int changed = 0;
        for (net.minecraft.entity.Entity entity : targets) {
            if (!(entity instanceof LivingEntity living))
                continue;
            setSize(living, size);
            changed++;
        }
        int count = changed;
        source.sendFeedback(() -> Text.literal("Set size of " + count + " entities to " + size), false);
        return changed;
    }

    public static void setSize(LivingEntity entity, float size) {
        size = Math.max(MIN_SIZE, Math.min(MAX_SIZE, size));

        set(entity, EntityAttributes.GENERIC_SCALE, "scale", size - 1F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        set(entity, EntityAttributes.GENERIC_MOVEMENT_SPEED, "speed", (size - 1F) * 0.5D, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        set(entity, EntityAttributes.GENERIC_ATTACK_DAMAGE, "attack", size - 1F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        set(entity, EntityAttributes.GENERIC_STEP_HEIGHT, "step", (size - 1F) * 0.6D, EntityAttributeModifier.Operation.ADD_VALUE);
        set(entity, EntityAttributes.GENERIC_JUMP_STRENGTH, "jump", size - 1F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        set(entity, EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, "knockback",
                Math.max(-0.9D, Math.min(1.0D, (size - 1F) * 0.5D)), EntityAttributeModifier.Operation.ADD_VALUE);
        set(entity, EntityAttributes.GENERIC_FALL_DAMAGE_MULTIPLIER, "fall",
                size > 1F ? (1D / size) - 1D : 0D, EntityAttributeModifier.Operation.ADD_VALUE);
        entity.calculateDimensions();
    }

    private static void set(LivingEntity entity, RegistryEntry<EntityAttribute> attribute, String name, double value,
                            EntityAttributeModifier.Operation operation) {
        EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
        if (instance == null)
            return;
        Identifier id = LucraftCore.id("size_" + name);
        EntityAttributeModifier modifier = instance.getModifier(id);
        if (Math.abs(value) < 1.0E-5D) {
            if (modifier != null)
                instance.removeModifier(id);
            return;
        }
        instance.overwritePersistentModifier(new EntityAttributeModifier(id, value, operation));
    }
}
