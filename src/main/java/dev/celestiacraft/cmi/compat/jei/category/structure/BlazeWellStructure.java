package dev.celestiacraft.cmi.compat.jei.category.structure;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import dev.celestiacraft.cmi.common.register.CmiBlock;
import dev.celestiacraft.cmi.utils.ModResources;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.common.util.Lazy;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.shared.TinkerMaterials;

public class BlazeWellStructure extends AnimatedKinetics {
	private static final Lazy<Block> SLAB = Lazy.of(() -> {
		return ModResources.BLAZEWOOD_SLAB.getBlock();
	});


	@Override
	public void draw(@NotNull GuiGraphics graphics, int offsetX, int offsetY) {
		PoseStack matrixStack = graphics.pose();
		matrixStack.pushPose();
		matrixStack.translate(offsetX, offsetY, 100.0F);
		matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5F));
		matrixStack.mulPose(Axis.YP.rotationDegrees(22.5F));

		int scale = 15;
		defaultBlockElement(CmiBlock.BLAZING_BLOOD_WELL.get().defaultBlockState())
				.atLocal(0.0F, 4.0F, 0.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.blazewood.get().defaultBlockState())
				.atLocal(1.0F, 4.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.blazewood.get().defaultBlockState())
				.atLocal(1.0F, 4.0F, 0.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.blazewood.get().defaultBlockState())
				.atLocal(1.0F, 4.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.blazewood.get().defaultBlockState())
				.atLocal(0.0F, 4.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.blazewood.get().defaultBlockState())
				.atLocal(0.0F, 4.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.blazewood.get().defaultBlockState())
				.atLocal(-1.0F, 4.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.blazewood.get().defaultBlockState())
				.atLocal(-1.0F, 4.0F, 0.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.blazewood.get().defaultBlockState())
				.atLocal(-1.0F, 4.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.blazewood.getFence().defaultBlockState())
				.atLocal(1.0F, 3.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.blazewood.getFence().defaultBlockState())
				.atLocal(1.0F, 3.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.blazewood.getFence().defaultBlockState())
				.atLocal(-1.0F, 3.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.blazewood.getFence().defaultBlockState())
				.atLocal(-1.0F, 3.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.blazewood.getFence().defaultBlockState())
				.atLocal(1.0F, 2.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.blazewood.getFence().defaultBlockState())
				.atLocal(1.0F, 2.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.blazewood.getFence().defaultBlockState())
				.atLocal(-1.0F, 2.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.blazewood.getFence().defaultBlockState())
				.atLocal(-1.0F, 2.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(CmiBlock.BLAZEWOOD_SCAFFOLD.get().defaultBlockState())
				.atLocal(-1.0F, 1.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(CmiBlock.BLAZEWOOD_SCAFFOLD.get().defaultBlockState())
				.atLocal(-1.0F, 1.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(CmiBlock.BLAZEWOOD_SCAFFOLD.get().defaultBlockState())
				.atLocal(1.0F, 1.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(CmiBlock.BLAZEWOOD_SCAFFOLD.get().defaultBlockState())
				.atLocal(1.0F, 1.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(SLAB.get().defaultBlockState()
				.setValue(SlabBlock.TYPE, SlabType.TOP))
				.atLocal(0.0F, 1.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(SLAB.get().defaultBlockState()
				.setValue(SlabBlock.TYPE, SlabType.TOP))
				.atLocal(0.0F, 1.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(SLAB.get().defaultBlockState()
				.setValue(SlabBlock.TYPE, SlabType.TOP))
				.atLocal(-1.0F, 1.0F, 0.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(SLAB.get().defaultBlockState()
				.setValue(SlabBlock.TYPE, SlabType.TOP))
				.atLocal(1.0F, 1.0F, 0.0F)
				.scale(scale)
				.render(graphics);
	}
}
