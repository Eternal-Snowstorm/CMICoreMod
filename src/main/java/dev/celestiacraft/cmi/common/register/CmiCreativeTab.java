package dev.celestiacraft.cmi.common.register;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CmiCreativeTab {
	public static final DeferredRegister<CreativeModeTab> TABS;
	public static final Supplier<CreativeModeTab> MECHANISMS;

	static {
		TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Cmi.MODID);

		MECHANISMS = addCreativeModeTab("mechanisms", () -> {
			return CmiMechanism.NUCLEAR.asStack();
		});
	}

	private static Supplier<CreativeModeTab> addCreativeModeTab(String name, Supplier<ItemStack> icon) {
		return TABS.register(name, () -> {
			return CreativeModeTab.builder()
					.icon(icon)
					.title(Component.translatable(String.format("itemGroup.%s.%s", Cmi.MODID, name)))
					.build();
		});
	}
}