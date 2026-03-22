package dev.celestiacraft.cmi.common.block.test_coke_oven;

import blusunrize.immersiveengineering.common.register.IEFluids;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.register.multiblock.ControllerBlockEntity;
import dev.celestiacraft.cmi.common.block.test_coke_oven.capability.CokeOvenFluidCapability;
import dev.celestiacraft.cmi.common.block.test_coke_oven.capability.CokeOvenItemCapability;
import dev.celestiacraft.cmi.common.register.CmiMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class TestCokeOvenBlockEntity extends ControllerBlockEntity {

	private CokeOvenItemCapability itemHandler;

	private CokeOvenFluidCapability fluidHandler;

	private void initHandlers() {
		if (level == null) {
			return;
		}
		if (itemHandler != null) {
			return;
		}
		TestCokeOvenIOBlockEntity entity = (TestCokeOvenIOBlockEntity) level.getBlockEntity(this.getBlockPos().below());
		if (entity == null) {
			return;
		}
		itemHandler = new CokeOvenItemCapability(entity);
		fluidHandler = new CokeOvenFluidCapability(entity);
	}


	private ItemStack input = itemHandler.getStackInSlot(0);
	private ItemStack output = itemHandler.getStackInSlot(1);
	private int workTimer = 0;

	public TestCokeOvenBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state, CmiMultiblock.TEST_COKE_OVEN);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, TestCokeOvenBlockEntity entity) {
		if (level.isClientSide()) {
			entity.runRecipe();
		}
	}

	public void runRecipe() {
		if (level == null || level.isClientSide()) {
			return;
		}

		initHandlers();
		if (itemHandler == null || fluidHandler == null) {
			return;
		}
		ItemStack input = itemHandler.getStackInSlot(0);
		ItemStack output = itemHandler.getStackInSlot(1);
		int timeToWork = 20;

		boolean canWork = isStructureValid() && input.is(ItemTags.LOGS) && output.getCount() < 64;

		if (!canWork) {
			workTimer = 0;
			return;
		}

		workTimer++;

		setChanged();

		if (workTimer >= timeToWork) {
			workTimer = 0;

			input.shrink(1);
			itemHandler.insertItem(1, Items.CHARCOAL.getDefaultInstance(), false);
			fluidHandler.fill(new FluidStack(IEFluids.CREOSOTE.getStill(), 125), IFluidHandler.FluidAction.EXECUTE);
		}
	}

	@Override
	protected String getMultiblockKey() {
		return String.format("multiblock.building.%s.test_coke_oven", Cmi.MODID);
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("Input", input.save(new CompoundTag()));
		tag.put("Output", output.save(new CompoundTag()));
		tag.putInt("WorkTimer", workTimer);
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		input = ItemStack.of(tag.getCompound("Input"));
		output = ItemStack.of(tag.getCompound("Output"));
		workTimer = tag.getInt("WorkTimer");
	}
}