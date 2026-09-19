package dev.celestiacraft.cmi.datagen.worldgen.tree;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.celestiacraft.cmi.common.register.CmiTrunkPlacer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 寒冷星球上的粗壮大乔木主干
 * <p>
 * 现实依据翻译成方块语言:
 * 1. 底部扩座 = 冻土上只有浅根盘, 只能靠外扩的基部抗倒(顺带做出猴面包树那种粗腰)
 * 2. 分层放射枝 = 黎巴嫩雪松的卸雪层, 每层薄而宽
 * 3. 枝尖下垂 = 积雪顺着枝条滑走, 不会堆在树冠中间压垮自己
 * 4. 风旗 = 冰原常年单向风, 迎风侧枝条被风"剪"短, 背风侧拖长
 */
public class GlacianTrunkPlacer extends TrunkPlacer {
	public static final Codec<GlacianTrunkPlacer> CODEC = RecordCodecBuilder.create((instance) -> {
		/* 前三个参数与其它树干放置器保持一致, 后面是这套树形自己的旋钮 */
		return instance.group(
				Codec.intRange(0, 32)
						.fieldOf("base_height")
						.forGetter((placer) -> {
							return placer.baseHeight;
						}),
				Codec.intRange(0, 24)
						.fieldOf("height_rand_a")
						.forGetter((placer) -> {
							return placer.heightRandA;
						}),
				Codec.intRange(0, 24)
						.fieldOf("height_rand_b")
						.forGetter((placer) -> {
							return placer.heightRandB;
						}),
				Codec.intRange(1, 4)
						.fieldOf("trunk_width")
						.forGetter((placer) -> {
							return placer.trunkWidth;
						}),
				Codec.intRange(0, 4)
						.fieldOf("base_flare")
						.forGetter((placer) -> {
							return placer.baseFlare;
						}),
				Codec.intRange(1, 8)
						.fieldOf("flare_height")
						.forGetter((placer) -> {
							return placer.flareHeight;
						}),
				Codec.intRange(1, 8)
						.fieldOf("tiers")
						.forGetter((placer) -> {
							return placer.tiers;
						}),
				Codec.intRange(2, 10)
						.fieldOf("branches")
						.forGetter((placer) -> {
							return placer.branches;
						}),
				IntProvider.codec(1, 16)
						.fieldOf("branch_length")
						.forGetter((placer) -> {
							return placer.branchLength;
						}),
				Codec.intRange(0, 4)
						.fieldOf("droop")
						.forGetter((placer) -> {
							return placer.droop;
						}),
				Codec.intRange(0, 4)
						.fieldOf("thick_steps")
						.forGetter((placer) -> {
							return placer.thickSteps;
						}),
				Codec.intRange(0, 4)
						.fieldOf("hub_height")
						.forGetter((placer) -> {
							return placer.hubHeight;
						}),
				Codec.intRange(0, 4)
						.fieldOf("hub_arms")
						.forGetter((placer) -> {
							return placer.hubArms;
						}),
				Codec.floatRange(0.0F, 1.0F)
						.fieldOf("wind_flag")
						.forGetter((placer) -> {
							return placer.windFlag;
						})
		).apply(instance, GlacianTrunkPlacer::new);
	});

	private final int trunkWidth;
	private final int baseFlare;
	private final int flareHeight;
	private final int tiers;
	private final int branches;
	private final IntProvider branchLength;
	private final int droop;
	private final int thickSteps;
	private final int hubHeight;
	private final int hubArms;
	private final float windFlag;

	public GlacianTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, int trunkWidth, int baseFlare, int flareHeight, int tiers, int branches, IntProvider branchLength, int droop, int thickSteps, int hubHeight, int hubArms, float windFlag) {
		super(baseHeight, heightRandA, heightRandB);
		this.trunkWidth = trunkWidth;
		this.baseFlare = baseFlare;
		this.flareHeight = flareHeight;
		this.tiers = tiers;
		this.branches = branches;
		this.branchLength = branchLength;
		this.droop = droop;
		this.thickSteps = thickSteps;
		this.hubHeight = hubHeight;
		this.hubArms = hubArms;
		this.windFlag = windFlag;
	}

	@Override
	protected @NotNull TrunkPlacerType<?> type() {
		return CmiTrunkPlacer.GLACIAN.get();
	}

	@Override
	public @NotNull List<FoliagePlacer.FoliageAttachment> placeTrunk(@NotNull LevelSimulatedReader level, @NotNull BiConsumer<BlockPos, BlockState> blockSetter, @NotNull RandomSource random, int freeTreeHeight, @NotNull BlockPos pos, @NotNull TreeConfiguration config) {
		List<FoliagePlacer.FoliageAttachment> attachments = Lists.newArrayList();
		boolean doubleTrunk = trunkWidth == 2;

		placeDirt(level, blockSetter, random, pos, config);
		clearSnow(level, blockSetter, pos, freeTreeHeight);

		for (int y = 0; y < freeTreeHeight; y++) {
			int flare = getFlare(y);

			for (int x = -flare; x < trunkWidth + flare; x++) {
				for (int z = -flare; z < trunkWidth + flare; z++) {
					if (isCorner(x, z, flare)) {
						continue;
					}

					placeLog(level, blockSetter, random, pos.offset(x, y, z), config, logAxis(Direction.Axis.Y));
				}
			}
		}

		BlockPos apex = pos.above(freeTreeHeight);

		placeHub(level, blockSetter, random, apex, config);
		attachments.add(new FoliagePlacer.FoliageAttachment(apex, -1, doubleTrunk));

		placeTiers(level, blockSetter, random, pos, freeTreeHeight, attachments, config);

		return attachments;
	}

	private void placeTiers(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, BlockPos pos, int freeTreeHeight, List<FoliagePlacer.FoliageAttachment> attachments, TreeConfiguration config) {
		float windAngle = random.nextFloat() * Mth.TWO_PI;
		int topTier = freeTreeHeight - 2;
		int bottomTier = Math.max(3, topTier - Math.round(freeTreeHeight * 0.55F));

		for (int tier = 0; tier < tiers; tier++) {
			int tierY = tiers == 1 ? topTier : Mth.lerpInt((float) tier / (tiers - 1), topTier, bottomTier);

			if (tierY < 2) {
				continue;
			}

			int tierBranches = Math.max(2, branches - (tiers - 1 - tier) / 2);
			int radiusOffset = tier == 0 ? -1 : 0;
			float tierShift = tier * (Mth.TWO_PI / (branches * 2.0F));

			for (int branch = 0; branch < tierBranches; branch++) {
				float angle = tierShift + branch * (Mth.TWO_PI / tierBranches) + random.nextFloat() * 0.3F;
				BlockPos tip = placeBranch(level, blockSetter, random, pos, tierY, angle, windAngle, config);

				if (tip == null) {
					continue;
				}

				placeHub(level, blockSetter, random, tip, config);
				attachments.add(new FoliagePlacer.FoliageAttachment(tip, radiusOffset, false));
			}
		}
	}

	private BlockPos placeBranch(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, BlockPos pos, int tierY, float angle, float windAngle, TreeConfiguration config) {
		float leeward = (Mth.cos(angle - windAngle) + 1.0F) * 0.5F;
		float scale = 1.0F - windFlag * (1.0F - leeward) * 0.7F;
		int length = Math.round(branchLength.sample(random) * scale);

		if (length < 1) {
			return null;
		}

		float center = (trunkWidth - 1) * 0.5F;
		float cursorX = center;
		float cursorZ = center;
		float dirX = Mth.cos(angle);
		float dirZ = Mth.sin(angle);
		Direction.Axis axis = Math.abs(dirX) >= Math.abs(dirZ) ? Direction.Axis.X : Direction.Axis.Z;
		BlockPos tip = null;

		for (int step = 0; step < length; step++) {
			cursorX += dirX;
			cursorZ += dirZ;

			int rise = Math.min(1, step / 3);
			int droop = step > length - 1 - this.droop ? step - (length - 1 - this.droop) : 0;
			BlockPos cursor = pos.offset(Mth.floor(cursorX), tierY + rise - droop, Mth.floor(cursorZ));
			Direction.Axis stepAxis = droop > 0 ? Direction.Axis.Y : axis;

			placeLog(level, blockSetter, random, cursor, config, logAxis(stepAxis));

			if (step < thickSteps) {
				placeLog(level, blockSetter, random, cursor.below(), config, logAxis(stepAxis));
			}

			tip = cursor;
		}

		return tip;
	}

	/**
	 * 叶团中枢: 顺着叶团中心往上立一根原木柱
	 * <p>
	 * MC 的树叶衰减是按"叶子之间走到原木的步数"算的, 超过 6 步就掉;
	 * 中枢让叶团的每一层都紧挨着原木, 顺带让树冠看起来是挂在枝头上, 而不是浮在空中
	 */
	private void placeHub(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, BlockPos center, TreeConfiguration config) {
		for (int y = 1; y < hubHeight; y++) {
			placeLog(level, blockSetter, random, center.above(y), config, logAxis(Direction.Axis.Y));
		}

		/* 水平臂: 让叶团底下有一副看得见的枝架, 同时把每一层叶子都压在原木附近 */
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			for (int step = 1; step <= hubArms; step++) {
				placeLog(level, blockSetter, random, center.relative(direction, step), config, logAxis(direction.getAxis()));
			}
		}
	}

	private void placeDirt(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, BlockPos pos, TreeConfiguration config) {
		int flare = getFlare(0);

		for (int x = -flare; x < trunkWidth + flare; x++) {
			for (int z = -flare; z < trunkWidth + flare; z++) {
				if (isCorner(x, z, flare)) {
					continue;
				}

				setDirtAt(level, blockSetter, random, pos.offset(x, -1, z), config);
			}
		}
	}

	/**
	 * 雪层不在 #minecraft:replaceable_by_trees 里, 不先清掉的话基部会被雪啃掉一圈
	 */
	private void clearSnow(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, BlockPos pos, int freeTreeHeight) {
		int flare = getFlare(0);
		int height = Math.min(freeTreeHeight - 1, flareHeight);

		for (int y = 0; y <= height; y++) {
			for (int x = -flare; x < trunkWidth + flare; x++) {
				for (int z = -flare; z < trunkWidth + flare; z++) {
					BlockPos cursor = pos.offset(x, y, z);

					if (level.isStateAtPosition(cursor, (state) -> {
						return state.is(Blocks.SNOW);
					})) {
						blockSetter.accept(cursor, Blocks.AIR.defaultBlockState());
					}
				}
			}
		}
	}

	/**
	 * 从下往上收的裙座: y = 0 时外扩 base_flare 格, 到 flare_height 收平
	 */
	private int getFlare(int y) {
		if (baseFlare <= 0 || y >= flareHeight) {
			return 0;
		}

		return (int) Math.ceil((double) baseFlare * (flareHeight - y) / flareHeight);
	}

	/**
	 * 削掉四条竖棱上的方块, 让方形的干变成八角形
	 */
	private boolean isCorner(int x, int z, int flare) {
		if (flare <= 0) {
			return false;
		}

		boolean edgeX = x == -flare || x == trunkWidth - 1 + flare;
		boolean edgeZ = z == -flare || z == trunkWidth - 1 + flare;

		return edgeX && edgeZ;
	}

	private static Function<BlockState, BlockState> logAxis(Direction.Axis axis) {
		return (state) -> state.hasProperty(RotatedPillarBlock.AXIS) ? state.setValue(RotatedPillarBlock.AXIS, axis) : state;
	}
}
