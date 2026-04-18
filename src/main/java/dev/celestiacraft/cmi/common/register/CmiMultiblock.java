package dev.celestiacraft.cmi.common.register;

import blusunrize.immersiveengineering.common.blocks.wooden.TreatedWoodStyles;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import dev.celestiacraft.cmi.tag.ModBlockTags;
import dev.celestiacraft.cmi.utils.ModResources;
import dev.celestiacraft.libs.compat.patchouli.multiblock.PropertyImmutableMap;
import dev.celestiacraft.libs.compat.patchouli.multiblock.StructureBuilder;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Lazy;
import slimeknights.tconstruct.shared.TinkerMaterials;
import vazkii.patchouli.api.IMultiblock;

public class CmiMultiblock {
	public static final Lazy<IMultiblock> WATER_PUMP;
	public static final Lazy<IMultiblock> LAVA_PUMP;
	public static final Lazy<IMultiblock> BLAZING_BLOOD_PUMP;
	public static final Lazy<IMultiblock> TEST_MULTIBLOCK;
	public static final Lazy<IMultiblock> TEST_COKE_OVEN;

	static {
		WATER_PUMP = structure(StructureBuilder.create(new String[][] {
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
					builder.block(CmiBlock.WATER_WELL.get());
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
					builder.map(ModResources.TREATED_WOOD_SLAB.getBlock(), PropertyImmutableMap.create()
							.add(SlabBlock.TYPE, SlabType.TOP)
							.build());
				}));

		LAVA_PUMP = structure(StructureBuilder.create(new String[][] {
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
					builder.block(TinkerMaterials.nahuatl.get());
				})
				.define('0', (builder) -> {
					builder.block(CmiBlock.LAVA_WELL.get());
				})
				.define('C', (builder) -> {
					builder.block(ModResources.NAHUATL_FENCE.getBlock());
				})
				.define('D', (builder) -> {
					builder.block(CmiBlock.NAHUATL_SCAFFOLD.get());
				})
				.define(' ', (builder) -> {
					builder.any();
				})
				.define('E', (builder) -> {
					builder.map(ModResources.NAHUATL_SLAB.getBlock(), PropertyImmutableMap.create()
							.add(SlabBlock.TYPE, SlabType.TOP)
							.build());
				}));

		BLAZING_BLOOD_PUMP = structure(StructureBuilder.create(new String[][] {
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
					builder.block(TinkerMaterials.blazewood.get());
				})
				.define('0', (builder) -> {
					builder.block(CmiBlock.BLAZING_BLOOD_WELL.get());
				})
				.define('C', (builder) -> {
					builder.block(ModResources.BLAZEWOOD_FENCE.getBlock());
				})
				.define('D', (builder) -> {
					builder.block(CmiBlock.BLAZEWOOD_SCAFFOLD.get());
				})
				.define(' ', (builder) -> {
					builder.any();
				})
				.define('E', (builder) -> {
					builder.map(ModResources.BLAZEWOOD_SLAB.getBlock(), PropertyImmutableMap.create()
							.add(SlabBlock.TYPE, SlabType.TOP)
							.build());
				}));

		TEST_MULTIBLOCK = structure(StructureBuilder.create(new String[][] {
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

		TEST_COKE_OVEN = structure(StructureBuilder.create(new String[][] {
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