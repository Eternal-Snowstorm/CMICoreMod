package dev.celestiacraft.cmi.common.register;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CmiCreativeTab {
	public static final DeferredRegister<CreativeModeTab> TABS;
	public static final Supplier<CreativeModeTab> MECHANISMS;

	static {
		TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Cmi.MODID);

		MECHANISMS = addCreativeModeTab("mechanisms", () -> {
			return CmiMechanism.NUCLEAR;
		});
	}

	private static Supplier<CreativeModeTab> addCreativeModeTab(String name, Supplier<? extends ItemLike> icon) {
		return TABS.register(name, () -> {
			return CreativeModeTab.builder()
					.icon(() -> {
						return icon.get().asItem().getDefaultInstance();
					})
					.title(Component.translatable(String.format("itemGroup.%s.%s", Cmi.MODID, name)))
					.build();
		});
	}
}