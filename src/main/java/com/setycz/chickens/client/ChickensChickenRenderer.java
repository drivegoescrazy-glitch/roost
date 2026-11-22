package com.setycz.chickens.client;

import com.setycz.chickens.entity.ChickensChickenEntity;
import net.minecraft.client.render.entity.ChickenEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.Identifier;

/** Draws modded chickens with the texture of their kind. */
public class ChickensChickenRenderer extends ChickenEntityRenderer {
	public ChickensChickenRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	@Override
	public Identifier getTexture(ChickenEntity entity) {
		if (entity instanceof ChickensChickenEntity chicken) {
			return chicken.getTexture();
		}
		return super.getTexture(entity);
	}
}
