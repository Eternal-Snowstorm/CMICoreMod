package dev.celestiacraft.cmi.client.ponder;

import dev.celestiacraft.cmi.Cmi;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class CmiPonderPlugin implements PonderPlugin {
	@Override
	public @NotNull String getModId() {
		return Cmi.MODID;
	}

	@Override
	public void registerScenes(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
		PonderSceneRegistrationHelper<Item> itemHelper = itemHelper(helper);
		CmiPonderScenes.register(itemHelper);
	}

	@Override
	public void registerTags(@NotNull PonderTagRegistrationHelper<ResourceLocation> helper) {
		CmiPonderTags.register(helper);
		CmiPonderTags.add(helper);
	}

	private static PonderSceneRegistrationHelper<Item> itemHelper(PonderSceneRegistrationHelper<ResourceLocation> helper) {
		return helper.withKeyFunction((item) -> {
			return ForgeRegistries.ITEMS.getKey(item);
		});
	}
}