package dev.celestiacraft.cmi.common.entity.prospecting_rocket;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import dev.celestiacraft.cmi.client.gui.ProspectingRocketCargoUI;
import dev.celestiacraft.cmi.client.gui.ProspectingRocketUIFactory;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class ProspectingRocketEntity extends Entity implements GeoEntity, IUIHolder {
	private static final TagKey<Fluid> FUEL_TAG = TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("forge", "fuel"));

	private final ProspectingRocketTier tier;
	private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

	private final SimpleContainer cargoItems;
	private final FluidTank fuelTank;
	private LazyOptional<IItemHandler> cargoItemsCap = LazyOptional.empty();
	private LazyOptional<IFluidHandler> fuelTankCap = LazyOptional.empty();

	public ProspectingRocketEntity(EntityType<?> type, Level level, ProspectingRocketTier tier) {
		super(type, level);
		this.tier = tier;
		this.cargoItems = new SimpleContainer(tier.cargoSlots());
		this.fuelTank = new FluidTank(tier.fuelCapacity(), stack -> stack.getFluid().is(FUEL_TAG));
		this.noCulling = true;
		rebuildCaps();
	}

	public ProspectingRocketTier getTier() {
		return tier;
	}

	public Container getCargoItems() {
		return cargoItems;
	}

	public FluidTank getFuelTank() {
		return fuelTank;
	}

	public boolean isFueled() {
		return fuelTank.getFluidAmount() >= fuelTank.getCapacity();
	}

	/**
	 * Launch hook for the upcoming launch pad. Currently a no-op until the launch base is designed.
	 */
	public boolean launch(ServerPlayer player) {
		return false;
	}

	@Override
	public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
		if (hand != InteractionHand.MAIN_HAND) {
			return InteractionResult.PASS;
		}
		if (level().isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (player instanceof ServerPlayer serverPlayer) {
			ProspectingRocketUIFactory.INSTANCE.openUI(this, serverPlayer);
			return InteractionResult.CONSUME;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
		registrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
	}

	private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
		return PlayState.STOP;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
		cargoItems.fromTag(tag.getList("CargoItems", Tag.TAG_COMPOUND));
		fuelTank.readFromNBT(tag.getCompound("FuelTank"));
	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
		tag.put("CargoItems", cargoItems.createTag());
		tag.put("FuelTank", fuelTank.writeToNBT(new CompoundTag()));
	}

	private void rebuildCaps() {
		this.cargoItemsCap = LazyOptional.of(() -> new InvWrapper(cargoItems));
		this.fuelTankCap = LazyOptional.of(() -> fuelTank);
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER) {
			return cargoItemsCap.cast();
		}
		if (cap == ForgeCapabilities.FLUID_HANDLER) {
			return fuelTankCap.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		cargoItemsCap.invalidate();
		fuelTankCap.invalidate();
	}

	@Override
	public void reviveCaps() {
		super.reviveCaps();
		rebuildCaps();
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	public boolean isNoGravity() {
		return true;
	}

	@Override
	public boolean hurt(@NotNull DamageSource source, float amount) {
		if (level().isClientSide()) {
			return false;
		}
		if (source.getEntity() instanceof Player player && player.isCreative()) {
			discard();
			return true;
		}
		return false;
	}

	@Override
	public ModularUI createUI(Player entityPlayer) {
		return ProspectingRocketCargoUI.create(this, entityPlayer);
	}

	@Override
	public boolean isInvalid() {
		return this.isRemoved();
	}

	@Override
	public boolean isRemote() {
		return level().isClientSide();
	}

	@Override
	public void markAsDirty() {
	}

	public void dropCargo() {
		if (level().isClientSide()) {
			return;
		}
		for (int i = 0; i < cargoItems.getContainerSize(); i++) {
			ItemStack stack = cargoItems.getItem(i);
			if (!stack.isEmpty()) {
				spawnAtLocation(stack);
			}
		}
		cargoItems.clearContent();
	}

	public boolean hasFuel() {
		return !fuelTank.isEmpty();
	}

	public FluidStack getFuel() {
		return fuelTank.getFluid();
	}
}
