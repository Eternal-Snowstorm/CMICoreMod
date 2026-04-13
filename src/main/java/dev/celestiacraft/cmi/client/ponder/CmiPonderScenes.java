package dev.celestiacraft.cmi.client.ponder;

import dev.celestiacraft.cmi.client.ponder.scene.tconstruct.*;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class CmiPonderScenes {
	public static void register(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
		ResourceLocation[] melter = new ResourceLocation[] {
				TinkerSmeltery.searedMelter.getId()
		};

		helper.forComponents(melter)
				.addStoryBoard("tconstruct/melter_building", MelterScene::building, CmiPonderTags.SMELTERY)
				.addStoryBoard("tconstruct/melter_using", MelterScene::using, CmiPonderTags.SMELTERY);

		ResourceLocation[] alloyer = new ResourceLocation[] {
				TinkerSmeltery.scorchedAlloyer.getId(),
		};

		helper.forComponents(alloyer)
				.addStoryBoard("tconstruct/alloyer_building", AlloyerScene::building, CmiPonderTags.SMELTERY);

		ResourceLocation[] heater = new ResourceLocation[] {
				TinkerSmeltery.searedHeater.getId(),
		};

		helper.forComponents(heater)
				.addStoryBoard("tconstruct/heater_using", HeaterScene::using, CmiPonderTags.SMELTERY);

		ResourceLocation[] casting = new ResourceLocation[] {
				TinkerSmeltery.searedTable.getId(),
				TinkerSmeltery.searedBasin.getId(),
				TinkerSmeltery.scorchedTable.getId(),
				TinkerSmeltery.scorchedBasin.getId(),
				TinkerSmeltery.searedFaucet.getId(),
				TinkerSmeltery.scorchedFaucet.getId(),
				TinkerSmeltery.searedChannel.getId(),
				TinkerSmeltery.scorchedChannel.getId(),
		};

		helper.forComponents(casting)
				.addStoryBoard("tconstruct/casting", CastingScene::cast, CmiPonderTags.SMELTERY);

		ResourceLocation[] sand = new ResourceLocation[] {
				TinkerSmeltery.blankSandCast.getId(),
				TinkerSmeltery.blankRedSandCast.getId()
		};

		helper.forComponents(sand)
				.addStoryBoard("tconstruct/sand_casting", CastingScene::sand, CmiPonderTags.SMELTERY);

		ResourceLocation[] smeltery = new ResourceLocation[] {
				TinkerSmeltery.smelteryController.getId(),
				TinkerSmeltery.searedDrain.getId(),
				TinkerSmeltery.searedDuct.getId(),
				TinkerSmeltery.searedChute.getId()
		};

		helper.forComponents(smeltery)
				.addStoryBoard("tconstruct/smeltery_building", SmelteryScene::building, CmiPonderTags.SMELTERY)
				.addStoryBoard("tconstruct/smeltery_using", SmelteryScene::using, CmiPonderTags.SMELTERY)
				.addStoryBoard("tconstruct/smeltery_mini", SmelteryScene::mini, CmiPonderTags.SMELTERY);

		ResourceLocation[] foundry = new ResourceLocation[] {
				TinkerSmeltery.foundryController.getId(),
				TinkerSmeltery.scorchedDrain.getId(),
				TinkerSmeltery.scorchedDuct.getId(),
				TinkerSmeltery.scorchedChute.getId()
		};

		helper.forComponents(foundry)
				.addStoryBoard("tconstruct/foundry_building", FoundryScene::building, CmiPonderTags.SMELTERY);

		ResourceLocation[] tank = new ResourceLocation[] {
				TinkerSmeltery.searedCastingTank.getId(),
				TinkerSmeltery.scorchedProxyTank.getId()
		};

		helper.forComponents(tank)
				.addStoryBoard("tconstruct/tank", TankScene::tank, CmiPonderTags.SMELTERY);

		ResourceLocation[] cannons = new ResourceLocation[] {
				TinkerSmeltery.searedFluidCannon.getId(),
				TinkerSmeltery.scorchedFluidCannon.getId()
		};

		helper.forComponents(cannons)
				.addStoryBoard("tconstruct/fluid_cannon", CannonScene::using, CmiPonderTags.SMELTERY);
	}
}