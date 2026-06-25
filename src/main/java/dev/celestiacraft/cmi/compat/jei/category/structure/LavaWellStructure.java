package dev.celestiacraft.cmi.compat.jei.category.structure;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import dev.celestiacraft.cmi.common.register.block.OtherBlocks;
import dev.celestiacraft.cmi.common.register.block.WallBlocks;
import dev.celestiacraft.cmi.utils.ModResources;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.shared.TinkerMaterials;

public class LavaWellStructure extends AnimatedKinetics {
	@Override
	public void draw(@NotNull GuiGraphics graphics, int offsetX, int offsetY) {
		PoseStack matrixStack = graphics.pose();
		matrixStack.pushPose();
		matrixStack.translate(offsetX, offsetY, 100.0F);
		matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5F));
		matrixStack.mulPose(Axis.YP.rotationDegrees(22.5F));

		int scale = 15;
		defaultBlockElement(WallBlocks.LAVA_WELL.get().defaultBlockState())
				.atLocal(0.0F, 4.0F, 0.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.nahuatl.get().defaultBlockState())
				.atLocal(1.0F, 4.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.nahuatl.get().defaultBlockState())
				.atLocal(1.0F, 4.0F, 0.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.nahuatl.get().defaultBlockState())
				.atLocal(1.0F, 4.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.nahuatl.get().defaultBlockState())
				.atLocal(0.0F, 4.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.nahuatl.get().defaultBlockState())
				.atLocal(0.0F, 4.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.nahuatl.get().defaultBlockState())
				.atLocal(-1.0F, 4.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.nahuatl.get().defaultBlockState())
				.atLocal(-1.0F, 4.0F, 0.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.nahuatl.get().defaultBlockState())
				.atLocal(-1.0F, 4.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.nahuatl.getFence().defaultBlockState())
				.atLocal(1.0F, 3.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.nahuatl.getFence().defaultBlockState())
				.atLocal(1.0F, 3.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.nahuatl.getFence().defaultBlockState())
				.atLocal(-1.0F, 3.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.nahuatl.getFence().defaultBlockState())
				.atLocal(-1.0F, 3.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.nahuatl.getFence().defaultBlockState())
				.atLocal(1.0F, 2.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.nahuatl.getFence().defaultBlockState())
				.atLocal(1.0F, 2.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.nahuatl.getFence().defaultBlockState())
				.atLocal(-1.0F, 2.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(TinkerMaterials.nahuatl.getFence().defaultBlockState())
				.atLocal(-1.0F, 2.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(OtherBlocks.NAHUATL_SCAFFOLD.get().defaultBlockState())
				.atLocal(-1.0F, 1.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(OtherBlocks.NAHUATL_SCAFFOLD.get().defaultBlockState())
				.atLocal(-1.0F, 1.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(OtherBlocks.NAHUATL_SCAFFOLD.get().defaultBlockState())
				.atLocal(1.0F, 1.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(OtherBlocks.NAHUATL_SCAFFOLD.get().defaultBlockState())
				.atLocal(1.0F, 1.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(ModResources.NAHUATL_SLAB.defaultBlockState()
				.setValue(SlabBlock.TYPE, SlabType.TOP))
				.atLocal(0.0F, 1.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(ModResources.NAHUATL_SLAB.defaultBlockState()
				.setValue(SlabBlock.TYPE, SlabType.TOP))
				.atLocal(0.0F, 1.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(ModResources.NAHUATL_SLAB.defaultBlockState()
				.setValue(SlabBlock.TYPE, SlabType.TOP))
				.atLocal(-1.0F, 1.0F, 0.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(ModResources.NAHUATL_SLAB.defaultBlockState()
				.setValue(SlabBlock.TYPE, SlabType.TOP))
				.atLocal(1.0F, 1.0F, 0.0F)
				.scale(scale)
				.render(graphics);
	}
}
