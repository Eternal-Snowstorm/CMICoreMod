package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.common.item.MechanismItem;
import mekanism.api.radiation.capability.IRadiationShielding;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class NuclearItem extends MechanismItem {
	public NuclearItem(Properties properties) {
		super(properties);
	}

	private static final Capability<ICurio> CURIO_CAP = CapabilityManager.get(new CapabilityToken<>() {
	});

	private static final Capability<IRadiationShielding> RADIATION_CAP = CapabilityManager.get(new CapabilityToken<>() {
	});

	@Override
	public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new ICapabilityProvider() {
			private final LazyOptional<ICurio> curio = LazyOptional.of(() -> {
				return new ICurio() {
					@Override
					public ItemStack getStack() {
						return stack;
					}
				};
			});

			private final LazyOptional<IRadiationShielding> shielding = LazyOptional.of(() -> {
				return new IRadiationShielding() {
					@Override
					public double getRadiationShielding() {
						return 1.0;
					}
				};
			});

			@Override
			public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
				if (capability == CURIO_CAP) {
					return curio.cast();
				}

				if (capability == RADIATION_CAP) {
					return shielding.cast();
				}

				return LazyOptional.empty();
			}
		};
	}
}