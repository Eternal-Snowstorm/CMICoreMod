package dev.celestiacraft.cmi.common.register;

import blusunrize.immersiveengineering.common.blocks.wooden.TreatedWoodStyles;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import dev.celestiacraft.cmi.tag.ModBlockTags;
import dev.celestiacraft.cmi.utils.ModResources;
import dev.celestiacraft.libs.compat.patchouli.multiblock.PropertyImmutableMap;
import dev.celestiacraft.libs.compat.patchouli.multiblock.StructureBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.patchouli.api.IMultiblock;

public class CmiMultiblock {
	public static final Lazy<IMultiblock> WATER_PUMP;
	public static final Lazy<IMultiblock> TEST_MULTIBLOCK;
	public static final Lazy<IMultiblock> TEST_COKE_OVEN;

	static {
		WATER_PUMP = structure(StructureBuilder.create(new String[][]{
						{
								"DED",
								"E E",
								"DED"
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
				.define('E', (builder) -> {
					builder.map(ForgeRegistries.BLOCKS.getValue(ModResources.TREATED_WOOD_SLAB), PropertyImmutableMap.create()
							.add(SlabBlock.TYPE, SlabType.TOP)
							.build());
				}));

		TEST_MULTIBLOCK = structure(StructureBuilder.create(new String[][]{
						{
								"AAA",
								"AAA",
								"AAA"
						},
						{
								"AAA",
								"0AA",
								"AAA"
						},
						{
								"AAA",
								"AAA",
								"AAA"
						}
				})
				// 外壳
				.define('A', (builder) -> {
					builder.tag(Tags.Blocks.COBBLESTONE);
				})
				.define('0', (builder) -> {
					builder.block(CmiBlock.TEST_MULTIBLOCK.get());
				}));

		TEST_COKE_OVEN = structure(StructureBuilder.create(new String[][]{
						{
								"AAA",
								"AAA",
								"AAA"
						},
						{
								"AAA",
								"0AA",
								"AAA"
						},
						{
								"AAA",
								"AAA",
								"AAA"
						}
				})
				// 外壳
				.define('A', (builder) -> {
					builder.tag(ModBlockTags.COKE_OVEN_STRUCTURE);
				})
				// 控制器
				.define('0', (builder) -> {
					builder.block(CmiBlock.TEST_COKE_OVEN.get());
				}));
	}

	private static Lazy<IMultiblock> structure(StructureBuilder structure) {
		return Lazy.of(structure::build);
	}
}