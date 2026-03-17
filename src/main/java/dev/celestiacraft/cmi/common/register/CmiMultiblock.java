package dev.celestiacraft.cmi.common.register;

import blusunrize.immersiveengineering.common.blocks.wooden.TreatedWoodStyles;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import dev.celestiacraft.cmi.utils.ModResource;
import dev.celestiacraft.libs.compat.patchouli.multiblock.PropertyImmutableMap;
import dev.celestiacraft.libs.compat.patchouli.multiblock.StructureBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.patchouli.api.IMultiblock;

public class CmiMultiblock {
	public static final Lazy<IMultiblock> WATER_PUMP;
	public static final Lazy<IMultiblock> TEST_MULTIBLOCK;

	static {
		WATER_PUMP = structure(StructureBuilder.create(new String[][]{
						{
								"DFD",
								"G H",
								"DID"
						},
						{
								"C C",
								"   ",
								"C C"
						},
						{
								"C C",
								"   ",
								"C C"
						},
						{
								"AAA",
								"A0A",
								"AAA"
						}
				})
				.define('A', (builder) -> {
					builder.block(IEBlocks.WoodenDecoration.TREATED_WOOD.get(TreatedWoodStyles.HORIZONTAL).get());
				})
				.define('0', (builder) -> {
					builder.block(CmiBlock.WATER_PUMP.get());
				})
				.define('C', (builder) -> {
					builder.block(IEBlocks.WoodenDecoration.TREATED_FENCE.get());
				})
				.define('D', (builder) -> {
					builder.block(IEBlocks.WoodenDecoration.TREATED_SCAFFOLDING.get());
				})
				.define(' ', (builder) -> {
					builder.any();
				})
				.define('F', (builder) -> {
					builder.map(ForgeRegistries.BLOCKS.getValue(ModResource.TREATED_WOOD_STAIRS), PropertyImmutableMap.create()
							.add(StairBlock.FACING, Direction.WEST)
							.add(StairBlock.HALF, Half.TOP)
							.add(StairBlock.SHAPE, StairsShape.STRAIGHT)
							.build());
				})
				.define('G', (builder) -> {
					builder.map(ForgeRegistries.BLOCKS.getValue(ModResource.TREATED_WOOD_STAIRS), PropertyImmutableMap.create()
							.add(StairBlock.FACING, Direction.NORTH)
							.add(StairBlock.HALF, Half.TOP)
							.add(StairBlock.SHAPE, StairsShape.STRAIGHT)
							.build());
				})
				.define('H', (builder) -> {
					builder.map(ForgeRegistries.BLOCKS.getValue(ModResource.TREATED_WOOD_STAIRS), PropertyImmutableMap.create()
							.add(StairBlock.FACING, Direction.SOUTH)
							.add(StairBlock.HALF, Half.TOP)
							.add(StairBlock.SHAPE, StairsShape.STRAIGHT)
							.build());
				})
				.define('I', (builder) -> {
					builder.map(ForgeRegistries.BLOCKS.getValue(ModResource.TREATED_WOOD_STAIRS), PropertyImmutableMap.create()
							.add(StairBlock.FACING, Direction.EAST)
							.add(StairBlock.HALF, Half.TOP)
							.add(StairBlock.SHAPE, StairsShape.STRAIGHT)
							.build());
				}));

		TEST_MULTIBLOCK = structure(StructureBuilder.create(new String[][]{
						{
								"AAA",
								"AAA",
								"AAA"
						},
						{
								"A0A",
								"AAA",
								"AAA"
						},
						{
								"AAA",
								"AAA",
								"AAA"
						}
				})
				// 木板
				.define('A', (builder) -> {
					builder.block(Blocks.COBBLESTONE);
				})
				.define('0', (builder) -> {
					builder.block(CmiBlock.TEST_MULTIBLOCK.get());
				}));
	}

	private static Lazy<IMultiblock> structure(StructureBuilder structure) {
		return Lazy.of(structure::build);
	}
}