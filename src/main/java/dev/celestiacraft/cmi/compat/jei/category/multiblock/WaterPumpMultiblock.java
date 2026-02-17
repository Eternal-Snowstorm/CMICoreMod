package dev.celestiacraft.cmi.compat.jei.category.multiblock;

import blusunrize.immersiveengineering.common.blocks.wooden.TreatedWoodStyles;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import dev.celestiacraft.cmi.common.register.CmiBlocks;

public class WaterPumpMultiblock extends AnimatedKinetics {
	private static final Lazy<Block> STAIRS = Lazy.of(() -> {
		String stair = "immersiveengineering:stairs_treated_wood_horizontal";
		return ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(stair));
	});

	@Override
	public void draw(@NotNull GuiGraphics graphics, int offsetX, int offsetY) {
		PoseStack matrixStack = graphics.pose();
		matrixStack.pushPose();
		matrixStack.translate(offsetX, offsetY, 100.0F);
		matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5F));
		matrixStack.mulPose(Axis.YP.rotationDegrees(22.5F));

		int scale = 15;
		defaultBlockElement(CmiBlocks.WATER_PUMP.get().defaultBlockState())
				.atLocal(0.0F, 4.0F, 0.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_WOOD.get(TreatedWoodStyles.HORIZONTAL).defaultBlockState())
				.atLocal(1.0F, 4.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_WOOD.get(TreatedWoodStyles.HORIZONTAL).defaultBlockState())
				.atLocal(1.0F, 4.0F, 0.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_WOOD.get(TreatedWoodStyles.HORIZONTAL).defaultBlockState())
				.atLocal(1.0F, 4.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_WOOD.get(TreatedWoodStyles.HORIZONTAL).defaultBlockState())
				.atLocal(0.0F, 4.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_WOOD.get(TreatedWoodStyles.HORIZONTAL).defaultBlockState())
				.atLocal(0.0F, 4.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_WOOD.get(TreatedWoodStyles.HORIZONTAL).defaultBlockState())
				.atLocal(-1.0F, 4.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_WOOD.get(TreatedWoodStyles.HORIZONTAL).defaultBlockState())
				.atLocal(-1.0F, 4.0F, 0.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_WOOD.get(TreatedWoodStyles.HORIZONTAL).defaultBlockState())
				.atLocal(-1.0F, 4.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_FENCE.get().defaultBlockState())
				.atLocal(1.0F, 3.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_FENCE.get().defaultBlockState())
				.atLocal(1.0F, 3.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_FENCE.get().defaultBlockState())
				.atLocal(-1.0F, 3.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_FENCE.get().defaultBlockState())
				.atLocal(-1.0F, 3.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_FENCE.get().defaultBlockState())
				.atLocal(1.0F, 2.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_FENCE.get().defaultBlockState())
				.atLocal(1.0F, 2.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_FENCE.get().defaultBlockState())
				.atLocal(-1.0F, 2.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_FENCE.get().defaultBlockState())
				.atLocal(-1.0F, 2.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_SCAFFOLDING.get().defaultBlockState())
				.atLocal(-1.0F, 1.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_SCAFFOLDING.get().defaultBlockState())
				.atLocal(-1.0F, 1.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_SCAFFOLDING.get().defaultBlockState())
				.atLocal(1.0F, 1.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(IEBlocks.WoodenDecoration.TREATED_SCAFFOLDING.get().defaultBlockState())
				.atLocal(1.0F, 1.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(STAIRS.get().defaultBlockState()
				.setValue(StairBlock.FACING, Direction.NORTH)
				.setValue(StairBlock.HALF, Half.TOP)
				.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT))
				.atLocal(0.0F, 1.0F, -1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(STAIRS.get().defaultBlockState()
				.setValue(StairBlock.FACING, Direction.SOUTH)
				.setValue(StairBlock.HALF, Half.TOP)
				.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT))
				.atLocal(0.0F, 1.0F, 1.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(STAIRS.get().defaultBlockState()
				.setValue(StairBlock.FACING, Direction.WEST)
				.setValue(StairBlock.HALF, Half.TOP)
				.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT))
				.atLocal(-1.0F, 1.0F, 0.0F)
				.scale(scale)
				.render(graphics);
		defaultBlockElement(STAIRS.get().defaultBlockState()
				.setValue(StairBlock.FACING, Direction.EAST)
				.setValue(StairBlock.HALF, Half.TOP)
				.setValue(StairBlock.SHAPE, StairsShape.STRAIGHT))
				.atLocal(1.0F, 1.0F, 0.0F)
				.scale(scale)
				.render(graphics);
	}
}