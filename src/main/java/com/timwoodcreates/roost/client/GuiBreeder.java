package com.timwoodcreates.roost.client;

import com.timwoodcreates.roost.Roost;
import com.timwoodcreates.roost.screen.BreederScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GuiBreeder extends HandledScreen<BreederScreenHandler> {
	private static final Identifier TEXTURE = Identifier.of(Roost.MODID, "textures/gui/breeder.png");

	public GuiBreeder(BreederScreenHandler handler, PlayerInventory playerInventory, Text title) {
		super(handler, playerInventory, title);
		this.backgroundWidth = 176;
		this.backgroundHeight = 133;
		this.titleX = 8;
		this.titleY = 6;
		this.playerInventoryTitleX = 8;
		this.playerInventoryTitleY = 39;
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

		int progressWidth = this.handler.getProgressWidth();
		if (progressWidth > 0) {
			context.drawTexture(TEXTURE, this.x + 84, this.y + 22, 176, 0, progressWidth, 12);
		}
	}

	@Override
	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		super.drawForeground(context, mouseX, mouseY);

		if (mouseX > this.x + 84 && mouseX < this.x + 110 && mouseY > this.y + 22 && mouseY < this.y + 34) {
			context.drawTooltip(this.textRenderer, Text.literal(this.handler.getFormattedProgress()), mouseX - this.x, mouseY - this.y);
		}
	}
}
