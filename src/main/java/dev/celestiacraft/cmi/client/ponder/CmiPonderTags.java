package dev.celestiacraft.cmi.client.ponder;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.CmiMechanism;
import mekanism.common.registries.MekanismBlocks;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class CmiPonderTags {
	public static final ResourceLocation TCONSTRUCT = Cmi.loadResource("tconstruct");
	public static final ResourceLocation CMI = Cmi.loadResource("cmi");
	public static final ResourceLocation MEKANISM = Cmi.loadResource("mekanism");

	public static void register(@NotNull PonderTagRegistrationHelper<ResourceLocation> helper) {
		helper.registerTag(TCONSTRUCT)
				.addToIndex()
				.item(TinkerSmeltery.smelteryController.asItem(), true, false)
				.title("匠魂")
				.description("匠魂思索")
				.register();

		helper.registerTag(CMI)
				.addToIndex()
				.item(CmiMechanism.CREATIVE, true, false)
				.title("机械动力: 构件与革新")
				.description("CMI思索")
				.register();

		helper.registerTag(MEKANISM)
				.addToIndex()
				.item(MekanismBlocks.CARDBOARD_BOX.asItem(), true, false)
				.title("通用机械")
				.description("通用机械思索")
				.register();
	}

	public static void add(PonderTagRegistrationHelper<ResourceLocation> helper) {
		helper.addToTag(TCONSTRUCT)
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