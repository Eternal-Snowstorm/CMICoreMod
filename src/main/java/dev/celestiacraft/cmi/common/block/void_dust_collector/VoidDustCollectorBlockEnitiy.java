package dev.celestiacraft.cmi.common.block.void_dust_collector;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import dev.celestiacraft.cmi.api.client.CmiLang;
import dev.celestiacraft.cmi.common.block.void_dust_collector.capability.VDCEnergyStorage;
import dev.celestiacraft.cmi.common.block.void_dust_collector.capability.VDCItemHandler;
import dev.celestiacraft.cmi.common.block.void_dust_collector.capability.VDCItmeCapability;
import dev.celestiacraft.cmi.config.common.VoidDustCollectorConfig;
import dev.celestiacraft.cmi.utils.ModResources;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class VoidDustCollectorBlockEnitiy extends BlockEntity implements IHaveGoggleInformation {
	private static final int CAPACITY = VoidDustCollectorConfig.ENERGY_CAPACITY.get();
	private static final int MAX_RECEIVE = VoidDustCollectorConfig.MAX_RECEIVE.get();
	private static final int ENERGY_CONSUMPTION = VoidDustCollectorConfig.ENERGY_CONSUMPTION.get();
	private static final int MAX_WORK_HEIGHT = VoidDustCollectorConfig.MAX_WORK_HEIGHT.get();
	private static final int MIN_WORK_HEIGHT = VoidDustCollectorConfig.MIN_WORK_HEIGHT.get();
	private static final int WORK_TIME = VoidDustCollectorConfig.WORK_TIME.get();

	@Getter
	private int energyStored = 0;
	private int workTimer = 0;
	private int workTimeRequired = 0;

	public VoidDustCollectorBlockEnitiy(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, VoidDustCollectorBlockEnitiy enitiy) {
		if (level.isClientSide()) {
			return;
		}

		enitiy.serverTick();
	}

	private void serverTick() {
		if (level == null || level.isClientSide()) {
			return;
		}

		ItemStack stack = itemHandler.getStackInSlot(0);

		boolean canWork = energyStored >= 1000 &&
				level.getBlockState(worldPosition.below()).is(ModResources.VOID_SPRING.getBlock()) &&
				this.getBlockPos().getY() <= MAX_WORK_HEIGHT &&
				this.getBlockPos().getY() >= MIN_WORK_HEIGHT &&
				(stack.isEmpty() || stack.getCount() < stack.getMaxStackSize());

		// 同步BlockState
		BlockState state = getBlockState();
		boolean wasWorking = state.getValue(VoidDustCollectorBlock.WORKING);

		if (wasWorking != canWork) {
			level.setBlock(
					worldPosition,
					state.setValue(VoidDustCollectorBlock.WORKING, canWork),
					3
			);
		}

		if (!canWork) {
			workTimer = 0;
			workTimeRequired = 0;
			return;
		}

		// 初始化随机工作时间
		if (workTimeRequired <= 0) {
			workTimeRequired = WORK_TIME;
			workTimer = 0;
		}

		// 每 tick 消耗能量
		energyStored -= ENERGY_CONSUMPTION;
		workTimer++;

		setChanged();

		// 完成一次生成
		if (workTimer >= workTimeRequired) {
			ItemStack output = ModResources.VOID_DUST.getItemStack().copy();
			itemHandler.insertItem(0, output, false);

			workTimer = 0;
			workTimeRequired = 0;
			setChanged();
		}
	}

	public int getCapacity() {
		return CAPACITY;
	}

	public int getMaxReceive() {
		return MAX_RECEIVE;
	}

	public void addEnergy(int amount) {
		this.energyStored += amount;
		setChanged();

		if (level != null) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			if (direction == Direction.UP) {
				return LazyOptional.empty();
			}
			return itemCap.cast();
		}

		if (capability == ForgeCapabilities.ENERGY) {
			return energyCap.cast();
		}

		return super.getCapability(capability, direction);
	}

	public boolean isWorking() {
		BlockState state = getBlockState();
		if (state.hasProperty(VoidDustCollectorBlock.WORKING)) {
			return state.getValue(VoidDustCollectorBlock.WORKING);
		}
		return false;
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		itemCap.invalidate();
		energyCap.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();

		this.itemHandler = new VDCItmeCapability(this);
		this.itemCap = LazyOptional.of(() -> new VDCItemHandler(itemHandler));

		this.energyHandler = new VDCEnergyStorage(this);
		this.energyCap = LazyOptional.of(() -> energyHandler);
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("Energy", energyStored);
		tag.putInt("WorkTimer", workTimer);
		tag.putInt("WorkTimeRequired", workTimeRequired);
		tag.put("Inventory", itemHandler.serializeNBT());
	}

	@Override
	public @NotNull CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Override
	@Nullable
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		energyStored = tag.getInt("Energy");
		workTimer = tag.getInt("WorkTimer");
		workTimeRequired = tag.getInt("WorkTimeRequired");
		itemHandler.deserializeNBT(tag.getCompound("Inventory"));
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		if (isWorking()) {
			CmiLang.builder()
					.translate("tooltip.void_dust_collector.working")
					.forGoggles(tooltip);
		} else {
			CmiLang.builder()
					.translate("tooltip.void_dust_collector.unworking")
					.forGoggles(tooltip);
		}
		return true;
	}

	// 物品
	private VDCItmeCapability itemHandler;
	private LazyOptional<IItemHandler> itemCap = LazyOptional.empty();

	// 能量
	private VDCEnergyStorage energyHandler;
	private LazyOptional<IEnergyStorage> energyCap = LazyOptional.empty();

	@Override
	public void onLoad() {
		super.onLoad();

		// 物品
		this.itemHandler = new VDCItmeCapability(this);
		this.itemCap = LazyOptional.of(() -> new VDCItemHandler(itemHandler));

		// 能量
		this.energyHandler = new VDCEnergyStorage(this);
		this.energyCap = LazyOptional.of(() -> energyHandler);
	}
}