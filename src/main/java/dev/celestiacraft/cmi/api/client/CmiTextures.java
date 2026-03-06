package dev.celestiacraft.cmi.api.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.gui.element.ScreenElement;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.cmi.Cmi;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CmiTextures {
	public static class Items {
		public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> setSimpleTexture(ResourceLocation texture) {
			return (context, provider) -> {
				provider.generated(context::getEntry, texture);
			};
		}

		public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> setSimpleTexture(String texture) {
			return (context, provider) -> {
				provider.generated(context::getEntry, provider.modLoc(texture));
			};
		}
	}

	public static class Guis implements ScreenElement {
		public final ResourceLocation location;
		public final int width;
		public final int height;
		public final int startX;
		public final int startY;

		public Guis(String path, int width, int height) {
			this(path, 0, 0, width, height);
		}

		public Guis(String path, int startX, int startY, int width, int height) {
			this.location = Cmi.loadResource(String.format("textures/gui/%s.png", path));
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
}