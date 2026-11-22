package com.timwoodcreates.roost.mixin;

import com.timwoodcreates.roost.RoostConfig;
import net.minecraft.entity.passive.ChickenEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChickenEntity.class)
public class ChickenEntityMixin {
	@Shadow
	public int eggLayTime;

	@Inject(method = "tickMovement", at = @At("HEAD"))
	private void roost$preventEggLaying(CallbackInfo info) {
		if (RoostConfig.get().disableEggLaying && this.eggLayTime <= 1) {
			this.eggLayTime = 999999999;
		}
	}
}
