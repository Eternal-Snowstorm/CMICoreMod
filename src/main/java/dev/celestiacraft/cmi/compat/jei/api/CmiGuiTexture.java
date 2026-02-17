package dev.celestiacraft.cmi.compat.jei.api;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import dev.celestiacraft.cmi.Cmi;

public class CmiGuiTexture implements ScreenElement {
	public final ResourceLocation location;
	public final int width;
	public final int height;
	public final int startX;
	public final int startY;

	public CmiGuiTexture(String path, int width, int height) {
		this(path, 0, 0, width, height);
	}

	public CmiGuiTexture(String path, int startX, int startY, int width, int height) {
		this.location = Cmi.loadResource(String.format("textures/gui/jei/%s.png", path));
		this.startX = startX;
		this.startY = startY;
		this.width = width;
		this.height = height;
	}

	@OnlyIn(Dist.CLIENT)
	public void bind() {
		RenderSystem.setShaderTexture(0, location);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void render(GuiGraphics graphics, int x, int y) {
		graphics.blit(location, x, y, startX, startY, width, height);
	}
}