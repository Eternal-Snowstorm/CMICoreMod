package dev.celestiacraft.cmi.common.block.water_pump;

import blusunrize.immersiveengineering.common.blocks.wooden.TreatedWoodStyles;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import dev.celestiacraft.libs.compat.patchouli.multiblock.IMultiblockProvider;
import dev.celestiacraft.libs.compat.patchouli.multiblock.MultiblockHandler;
import dev.celestiacraft.libs.compat.patchouli.multiblock.MultiblockStructureBuilder;
import dev.celestiacraft.libs.compat.patchouli.multiblock.PropertyImmutableMap;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraftforge.registries.ForgeRegistries;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.CmiBlocks;
import dev.celestiacraft.cmi.api.CmiLang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IMultiblock;

import java.util.List;

public class WaterPumpBlockEntity extends BlockEntity implements IHaveGoggleInformation, IMultiblockProvider {
	public WaterPumpBlockEntity(BlockEntityType<? extends WaterPumpBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	private static final Lazy<Fluid> SEA_WATER = Lazy.of(() -> {
		return ForgeRegistries.FLUIDS.getValue(Cmi.loadResource("sea_water"));
	});

	private static final ResourceLocation STAIRS =
			ResourceLocation.parse("immersiveengineering:stairs_treated_wood_horizontal");

	private static final Lazy<IMultiblock> STRUCTURE = Lazy.of(() -> {
		return new MultiblockStructureBuilder(new String[][]{
				{
						// 四个角为脚手架, 四边为楼梯, 中心镂空
						"DFD",
						"G H",
						"DID"
				},
				{
						// 木栅栏
						"C C",
						"   ",
						"C C"
				},
				{
						// 木栅栏
						"C C",
						"   ",
						"C C"
				},
				{
						// 木板 + 水泵
						"AAA",
						"A0A",
						"AAA"
				}
		})
				// 木板
				.define('A', (builder) -> {
					builder.block(IEBlocks.WoodenDecoration.TREATED_WOOD.get(TreatedWoodStyles.HORIZONTAL).get());
				})
				// 水泵
				.define('0', (builder) -> {
					builder.block(CmiBlocks.WATER_PUMP.get());
				})
				// 木栅栏
				.define('C', (builder) -> {
					builder.block(IEBlocks.WoodenDecoration.TREATED_FENCE.get());
				})
				// 脚手架
				.define('D', (builder) -> {
					builder.block(IEBlocks.WoodenDecoration.TREATED_SCAFFOLDING.get());
				})
				// 空位
				.define(' ', (builder) -> {
					builder.any();
				})
				// 北边楼梯(上方), 朝南
				.define('F', (builder) -> {
					builder.stateMap(ForgeRegistries.BLOCKS.getValue(STAIRS), PropertyImmutableMap.create()
							.add(StairBlock.FACING, Direction.WEST)
							.add(StairBlock.HALF, Half.TOP)
							.add(StairBlock.SHAPE, StairsShape.STRAIGHT)
							.build());
				})
				// 西边楼梯(左边), 朝东
				.define('G', (builder) -> {
					builder.stateMap(ForgeRegistries.BLOCKS.getValue(STAIRS), PropertyImmutableMap.create()
							.add(StairBlock.FACING, Direction.NORTH)
							.add(StairBlock.HALF, Half.TOP)
							.add(StairBlock.SHAPE, StairsShape.STRAIGHT)
							.build());
				})
				// 东边楼梯(右边), 朝西
				.define('H', (builder) -> {
					builder.stateMap(ForgeRegistries.BLOCKS.getValue(STAIRS), PropertyImmutableMap.create()
							.add(StairBlock.FACING, Direction.SOUTH)
							.add(StairBlock.HALF, Half.TOP)
							.add(StairBlock.SHAPE, StairsShape.STRAIGHT)
							.build());
				})
				// 南边楼梯(下方), 朝北
				.define('I', (builder) -> {
					builder.stateMap(ForgeRegistries.BLOCKS.getValue(STAIRS), PropertyImmutableMap.create()
							.add(StairBlock.FACING, Direction.EAST)
							.add(StairBlock.HALF, Half.TOP)
							.add(StairBlock.SHAPE, StairsShape.STRAIGHT)
							.build());
				})
				.build();
	});

	// 多方块处理器：封装验证缓存(20tick) + 渲染切换逻辑
	private final MultiblockHandler multiblock = MultiblockHandler.builder(this, STRUCTURE)
			.translationKey(String.format("multiblock.building.%s.water_pump", Cmi.MODID))
			.renderOffset(0, -1, 0)
			.cacheTicks(20)
			.build();

	// 获取多方块处理器
	@Override
	public MultiblockHandler getMultiblockHandler() {
		return this.multiblock;
	}

	private final IFluidHandler fluidHandler = new IFluidHandler() {
		@Override
		public int getTanks() {
			return 1;
		}

		@Override
		public @NotNull FluidStack getFluidInTank(int amount) {
			if (isStructureValid()) {
				if (isOcean()) {
					return new FluidStack(SEA_WATER.get(), Integer.MAX_VALUE);
				}
				return new FluidStack(Fluids.WATER, Integer.MAX_VALUE);
			}
			return FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int amount) {
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isFluidValid(int amount, @NotNull FluidStack fluidStack) {
			return false;
		}

		@Override
		public int fill(FluidStack fluidStack, FluidAction fluidAction) {
			return 0;
		}

		@Override
		public @NotNull FluidStack drain(FluidStack fluidStack, FluidAction fluidAction) {
			if (isStructureValid()) {
				if (isOcean()) {
					if (fluidStack.getFluid() == SEA_WATER.get()) {
						return fluidStack;
					}
				} else if (fluidStack.getFluid() == Fluids.WATER) {
					return fluidStack;
				}
				return FluidStack.EMPTY;
			}
			return FluidStack.EMPTY;
		}

		@Override
		public @NotNull FluidStack drain(int amount, FluidAction fluidAction) {
			if (isStructureValid()) {
				if (isOcean()) {
					return new FluidStack(SEA_WATER.get(), amount);
				}
				return new FluidStack(Fluids.WATER, amount);
			}
			return FluidStack.EMPTY;
		}
	};

	private boolean isOcean() {
		if (this.level != null) {
			return this.level.getBiome(this.getBlockPos()).is(BiomeTags.IS_OCEAN) && this.getBlockPos().getY() == 62;
		}
		return false;
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
		if (capability == ForgeCapabilities.FLUID_HANDLER) {
			return LazyOptional.of(() -> {
				return fluidHandler;
			}).cast();
		}
		return super.getCapability(capability, side);
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		if (isStructureValid()) {
			CmiLang.builder()
					.translate("tooltip.water_pump.functional")
					.forGoggles(tooltip);
		} else {
			CmiLang.builder()
					.translate("tooltip.water_pump.non_functional")
					.forGoggles(tooltip);
		}
		return true;
	}
}