package dev.celestiacraft.cmi.common.block.test_coke_oven;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.CmiMultiblock;
import dev.celestiacraft.cmi.common.register.CmiRecipeType;
import dev.celestiacraft.libs.api.register.multiblock.machine.*;
import dev.celestiacraft.libs.common.recipe.machine.MachineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class TestCokeOvenBlockEntity extends MachineControllerBlockEntity implements IControllerRecipe<TestCokeOvenBlockEntity> {
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
		if (level == null) {
			workTimer = 0;
			return;
		}

		IItemHandler itemHandler = findFirstMatchedItemHandler();
		IFluidHandler fluidHandler = findFirstMatchedFluidHandler();
		if (itemHandler == null || fluidHandler == null) {
			workTimer = 0;
			return;
		}

		MachineRecipe recipe = level.getRecipeManager()
				.getAllRecipesFor(CmiRecipeType.TEST_COKE_OVEN.get())
				.stream()
				.filter((candidate) -> {
					return candidate.matchesItemInputs(itemHandler, 0);
				})
				.filter((candidate) -> {
					return candidate.canOutputItems(itemHandler, 1);
				})
				.filter((candidate) -> {
					return candidate.canOutputFluids(fluidHandler);
				})
				.findFirst()
				.orElse(null);

		if (recipe == null) {
			workTimer = 0;
			return;
		}

		workTimer++;
		if (workTimer < recipe.getDuration()) {
			return;
		}

		recipe.consumeItemInputs(itemHandler, 0);
		recipe.produceItemOutputs(level, itemHandler, 1);
		recipe.produceFluidOutputs(level, fluidHandler);
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
		return countMatchedItemIOBlockEntities();
	}

	@Override
	protected int getActualFluidIOCount() {
		return countMatchedFluidIOBlockEntities();
	}

	@Override
	protected int getItemSlots() {
		return 2;
	}

	@Override
	protected FluidSlots[] getFluidSlots() {
		return new FluidSlots[] {
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
