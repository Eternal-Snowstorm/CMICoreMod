package dev.celestiacraft.cmi.client.menu.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.celestiacraft.cmi.Cmi;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.common.util.TickTimer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.stream.Stream;

public class InternalDrawable implements IDrawable {
	private final int width;
	private final int height;
	private final ITickTimer tickTimer;
	private final List<ResourceLocation> textures = Stream.of(
			"textures/gui/jei/config_button/0.png",
			"textures/gui/jei/config_button/1.png",
			"textures/gui/jei/config_button/2.png",
			"textures/gui/jei/config_button/3.png",
			"textures/gui/jei/config_button/4.png",
			"textures/gui/jei/config_button/5.png",
			"textures/gui/jei/config_button/6.png",
			"textures/gui/jei/config_button/7.png"
	).map((textures) -> {
		return Cmi.loadResource(textures);
	}).toList();

	public InternalDrawable(int width, int height) {
		this.width = width;
		this.height = height;
		this.tickTimer = new TickTimer(15, 7, false);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
		int index = tickTimer.getValue();
		int angle = 360 / textures.size();
		PoseStack stack = graphics.pose();
		stack.pushPose();
		stack.translate(xOffset + (float) width / 2, yOffset + (float) height / 2, 0);
		stack.mulPose(Axis.ZP.rotationDegrees(angle * index));
		graphics.blit(
				textures.get(index),
				-width / 2,
				-height / 2,
				0,
				0, width,
				height,
				16,
				16
		);
		stack.popPose();
	}
}