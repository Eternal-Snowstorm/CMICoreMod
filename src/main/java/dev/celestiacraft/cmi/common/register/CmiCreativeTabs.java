package dev.celestiacraft.cmi.common.register;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.CreateRegistrate;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.block.MachineBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CmiCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> TABS;

	public static final Supplier<CreativeModeTab>
			MECHANISMS,
			MACHINES;

	static {
		TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Cmi.MODID);

		MECHANISMS = addCreativeModeTab("mechanisms", AllItems.PRECISION_MECHANISM::asStack);
		MACHINES = addCreativeModeTab("machines", MachineBlocks.STEAM_HAMMER::asStack);
	}

	private static Supplier<CreativeModeTab> addCreativeModeTab(String name, Supplier<ItemStack> icon) {
		return TABS.register(name, () -> {
			String tranKey = String.format("itemGroup.%s.%s", Cmi.MODID, name);
			return CreativeModeTab.builder()
					.icon(icon)
					.title(Component.translatable(tranKey))
					.build();
		});
	}

	public static CreateRegistrate getTab(String name) {
		return Cmi.REGISTRATE.defaultCreativeTab(ResourceKey.create(
				Registries.CREATIVE_MODE_TAB,
				Cmi.loadResource(name)
		));
	}

	public static ResourceKey<CreativeModeTab> getTabKey(String name) {
		return ResourceKey.create(
				Registries.CREATIVE_MODE_TAB,
				Cmi.loadResource(name)
		);
	}

	public static void register(IEventBus bus) {
		Cmi.LOGGER.info("{} Creative Tabs Registered!", Cmi.MODID);
		TABS.register(bus);
	}
}