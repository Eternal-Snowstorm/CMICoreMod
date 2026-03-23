package dev.celestiacraft.cmi.common.block.test_coke_oven;

import blusunrize.immersiveengineering.common.register.IEFluids;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.register.multiblock.ControllerBlockEntity;
import dev.celestiacraft.cmi.api.register.multiblock.IControllerRecipe;
import dev.celestiacraft.cmi.api.register.multiblock.MultiblockContext;
import dev.celestiacraft.cmi.common.register.CmiMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class TestCokeOvenBlockEntity extends ControllerBlockEntity implements IControllerRecipe {
	private int workTimer = 0;

	public TestCokeOvenBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state, CmiMultiblock.TEST_COKE_OVEN);
	}

	@Override
	public MultiblockContext tick(MultiblockContext context) {
		if (!context.isClient()) {
			recipe(context);
		}
		return context;
	}

	@Override
	public void recipe(MultiblockContext context) {
		Cmi.LOGGER.info("tick start");

		// ❌ 删掉 initHandlers()
		// initHandlers();

		// ❌ 删掉这段
		// System.out.println("handler: " + itemHandler);
		// if (itemHandler == null || fluidHandler == null) {
		// 	System.out.println("handler null");
		// 	return;
		// }

		TestCokeOvenIOBlockEntity io = (TestCokeOvenIOBlockEntity) level.getBlockEntity(worldPosition.below());

		if (io == null) {
			return;
		}

		IItemHandler itemHandler = io.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
		IFluidHandler fluidHandler = io.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);

		if (itemHandler == null || fluidHandler == null) {
			return;
		}

		ItemStack input = itemHandler.getStackInSlot(0);
		Cmi.LOGGER.info("input: {}", input);

		boolean structure = isStructureValid();
		Cmi.LOGGER.info("structure: {}", structure);

		ItemStack result = Items.CHARCOAL.getDefaultInstance();
		FluidStack fluidResult = new FluidStack(IEFluids.CREOSOTE.getStill(), 125);

		boolean canInsertItem = itemHandler.insertItem(1, result.copy(), true).isEmpty();
		int canFillFluid = fluidHandler.fill(fluidResult.copy(), IFluidHandler.FluidAction.SIMULATE);

		Cmi.LOGGER.info("canInsertItem: {}", canInsertItem);
		Cmi.LOGGER.info("canFillFluid: {}", canFillFluid);

		if (!structure || !input.is(ItemTags.LOGS)) {
			Cmi.LOGGER.info("fail: input or structure");
			workTimer = 0;
			return;
		}

		if (!canInsertItem || canFillFluid < fluidResult.getAmount()) {
			Cmi.LOGGER.info("fail: output");
			workTimer = 0;
			return;
		}

		workTimer++;
		Cmi.LOGGER.info("workTimer: {}", workTimer);
	}

	@Override
	protected String getMultiblockKey() {
		return String.format("multiblock.building.%s.test_coke_oven", Cmi.MODID);
	}

	/**
	 * Controller 只存自己的数据
	 *
	 * @param tag
	 */
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
}