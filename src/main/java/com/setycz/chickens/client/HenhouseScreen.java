package com.setycz.chickens.client;

import com.setycz.chickens.ChickensMod;
import com.setycz.chickens.screen.HenhouseScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/** The original 166x166 henhouse GUI with its hay energy bar. */
public class HenhouseScreen extends HandledScreen<HenhouseScreenHandler> {
	private static final Identifier TEXTURE = Identifier.of(ChickensMod.MOD_ID, "textures/gui/henhouse.png");
	private static final int BAR_HEIGHT = 57;

	public HenhouseScreen(HenhouseScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.backgroundWidth = 166;
		this.backgroundHeight = 166;
		this.titleX = 8;
		this.titleY = 6;
		this.playerInventoryTitleX = 8;
		this.playerInventoryTitleY = 72;
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

		int energy = this.handler.getEnergy();
		int offset = BAR_HEIGHT - energy * BAR_HEIGHT / 100;
		if (offset < BAR_HEIGHT) {
			context.drawTexture(TEXTURE, this.x + 75, this.y + 14 + offset, 195, offset, 12, BAR_HEIGHT - offset);
		}
	}

	@Override
	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
		context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX,
				this.playerInventoryTitleY, 4210752, false);
	}
}
