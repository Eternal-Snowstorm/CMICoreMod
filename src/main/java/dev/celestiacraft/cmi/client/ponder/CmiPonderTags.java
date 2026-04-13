package dev.celestiacraft.cmi.client.ponder;

import dev.celestiacraft.cmi.Cmi;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class CmiPonderTags {
	public static final ResourceLocation SMELTERY = Cmi.loadResource("smeltery");

	public static void register(@NotNull PonderTagRegistrationHelper<ResourceLocation> helper) {
		helper.registerTag(SMELTERY)
				.addToIndex()
				.item(TinkerSmeltery.smelteryController.asItem(), true, false)
				.title("Smeltery")
				.description("Tinker's components used to smelting and casting")
				.register();
	}

	public static void add(PonderTagRegistrationHelper<ResourceLocation> helper) {
		helper.addToTag(SMELTERY)
				.add(TinkerSmeltery.searedMelter.getId())
				.add(TinkerSmeltery.scorchedAlloyer.getId())
				.add(TinkerSmeltery.searedHeater.getId())
				.add(TinkerSmeltery.smelteryController.getId())
				.add(TinkerSmeltery.foundryController.getId())
				.add(TinkerSmeltery.searedTable.getId())
				.add(TinkerSmeltery.scorchedTable.getId())
				.add(TinkerSmeltery.searedBasin.getId())
				.add(TinkerSmeltery.scorchedBasin.getId())
				.add(TinkerSmeltery.copperCan.getId())
				.add(TinkerSmeltery.searedCastingTank.getId())
				.add(TinkerSmeltery.scorchedProxyTank.getId());
	}
}