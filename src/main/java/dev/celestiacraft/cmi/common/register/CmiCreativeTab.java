package dev.celestiacraft.cmi.common.register;

import com.simibubi.create.AllItems;
import dev.celestiacraft.cmi.Cmi;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CmiCreativeTab {
	public static final DeferredRegister<CreativeModeTab> TABS;
	public static final Supplier<CreativeModeTab> MECHANISMS;

	static {
		TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Cmi.MODID);

		MECHANISMS = addCreativeModeTab("mechanisms", () -> {
			return AllItems.PRECISION_MECHANISM.asStack();
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

	public static void register(IEventBus bus) {
		Cmi.LOGGER.info("Cmi Creative Tab Registered!");
		TABS.register(bus);
	}
}