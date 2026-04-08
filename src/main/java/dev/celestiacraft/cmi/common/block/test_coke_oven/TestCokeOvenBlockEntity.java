package dev.celestiacraft.cmi.common.block.test_coke_oven;

import blusunrize.immersiveengineering.common.register.IEFluids;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.register.multiblock.machine.FluidSlots;
import dev.celestiacraft.cmi.api.register.multiblock.machine.IControllerRecipe;
import dev.celestiacraft.cmi.api.register.multiblock.machine.IOMode;
import dev.celestiacraft.cmi.api.register.multiblock.machine.MachineControllerBlockEntity;
import dev.celestiacraft.cmi.api.register.multiblock.machine.MultiblockContext;
import dev.celestiacraft.cmi.common.register.CmiBlock;
import dev.celestiacraft.cmi.common.register.CmiMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class TestCokeOvenBlockEntity extends MachineControllerBlockEntity implements IControllerRecipe<TestCokeOvenBlockEntity> {
	private static final int MAX_WORK_TIME = 20;

	private int workTimer = 0;

	public TestCokeOvenBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state, CmiMultiblock.TEST_COKE_OVEN);
	}

	@Override
	public ResourceLocation getRecipeTypeId() {
		return Cmi.loadResource("test_coke_oven");
	}

	@Override
	public MultiblockContext<TestCokeOvenBlockEntity> tick(MultiblockContext<TestCokeOvenBlockEntity> context) {
		if (prepareRecipeTick(context)) {
			recipe(context);
		}
		return context;
	}

	@Override
	public void recipe(MultiblockContext<TestCokeOvenBlockEntity> context) {
		TestCokeOvenIOBlockEntity ioBlock = findFirstMatchedBlockEntity(CmiBlock.TEST_COKE_OVEN_IO.get(), TestCokeOvenIOBlockEntity.class);
		if (ioBlock == null) {
			workTimer = 0;
			return;
		}

		ItemStack input = ioBlock.getInternalStackInSlot(0);
		ItemStack result = Items.CHARCOAL.getDefaultInstance();
		FluidStack fluidResult = new FluidStack(IEFluids.CREOSOTE.getStill(), 125);

		boolean validInput = input.is(ItemTags.LOGS);
		boolean canInsertItem = ioBlock.insertItemInternal(1, result.copy(), true).isEmpty();
		int canFillFluid = ioBlock.fillFluid(fluidResult.copy(), IFluidHandler.FluidAction.SIMULATE);

		if (!validInput || !canInsertItem || canFillFluid < fluidResult.getAmount()) {
			workTimer = 0;
			return;
		}

		workTimer++;
		if (workTimer <= MAX_WORK_TIME) {
			return;
		}

		ioBlock.extractItemInternal(0, 1, false);
		ioBlock.insertItemInternal(1, result.copy(), false);
		ioBlock.fillFluid(fluidResult.copy(), IFluidHandler.FluidAction.EXECUTE);
		workTimer = 0;
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("WorkTimer", workTimer);
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		workTimer = tag.getInt("WorkTimer");
	}

	@Override
	protected boolean supportsControllerItemIO() {
		return false;
	}

	@Override
	protected boolean supportsControllerFluidIO() {
		return false;
	}

	@Override
	protected boolean useInternalItemStorage() {
		return false;
	}

	@Override
	protected boolean useInternalFluidStorage() {
		return false;
	}

	@Override
	protected IOMode getItemIO(int slot) {
		return slot == 0 ? IOMode.INPUT : IOMode.OUTPUT;
	}

	@Override
	protected int getMinItemIO() {
		return 1;
	}

	@Override
	protected int getMaxItemIO() {
		return 1;
	}

	@Override
	protected int getMinFluidIO() {
		return 1;
	}

	@Override
	protected int getMaxFluidIO() {
		return 1;
	}

	@Override
	protected int getActualItemIOCount() {
		return countMatchedBlocks(CmiBlock.TEST_COKE_OVEN_IO.get());
	}

	@Override
	protected int getActualFluidIOCount() {
		return countMatchedBlocks(CmiBlock.TEST_COKE_OVEN_IO.get());
	}

	@Override
	protected int getItemSlots() {
		return 2;
	}

	@Override
	protected FluidSlots[] getFluidSlots() {
		return new FluidSlots[]{
				new FluidSlots(4000, IOMode.OUTPUT)
		};
	}

	@Override
	protected String getModId() {
		return Cmi.MODID;
	}

	@Override
	protected String getMultiblockName() {
		return "test_coke_oven";
	}
}
