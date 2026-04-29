package dev.celestiacraft.cmi.common.item;

import dev.celestiacraft.libs.api.register.item.BasicItem;
import dev.celestiacraft.libs.common.item.energy.EnergyItemCapabilityProvider;
import dev.celestiacraft.libs.common.item.energy.IEnergyItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;

public class SimpleBatteryItem extends BasicItem implements IEnergyItem {
	public SimpleBatteryItem(Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public int getCapacity(ItemStack stack) {
		return 150000;
	}

	@Override
	public int getMaxReceive(ItemStack stack) {
		return 1000;
	}

	@Override
	public int getMaxExtract(ItemStack stack) {
		return 1000;
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack) {
		return isEnergyBarVisible(stack);
	}

	@Override
	public int getBarWidth(@NotNull ItemStack stack) {
		return getEnergyBarWidth(stack);
	}

	@Override
	public int getBarColor(@NotNull ItemStack stack) {
		return getEnergyBarColor(stack);
	}

	@Override
	public int getEnergyBarColor(ItemStack stack) {
		float ratio = getRatioBar(stack);

		if (ratio >= 0.6f) {
			return 0x00FFFF;
		} else if (ratio >= 0.2f) {
			return 0xFFFF00;
		} else {
			return 0xFF0000;
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
		return new EnergyItemCapabilityProvider(stack, this);
	}
}