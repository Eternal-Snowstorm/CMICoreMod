package dev.celestiacraft.cmi;

import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import dev.celestiacraft.cmi.client.CmiClient;
import dev.celestiacraft.cmi.client.block.resource.CmiBlockPartialModel;
import dev.celestiacraft.cmi.client.block.resource.CmiSpriteShiftEntry;
import dev.celestiacraft.cmi.client.ponder.CmiPonderPlugin;
import dev.celestiacraft.cmi.common.recipe.fan_processig.CmiFanProcessingTypes;
import dev.celestiacraft.cmi.common.register.*;
import dev.celestiacraft.cmi.compat.adastra.AdAstraOxygenCompat;
import dev.celestiacraft.cmi.compat.create.CmiStress;
import dev.celestiacraft.cmi.config.CommonConfig;
import dev.celestiacraft.cmi.datagen.worldgen.region.ModOverworldRegion;
import dev.celestiacraft.cmi.datagen.worldgen.surfacerule.ModSurfaceRuleData;
import dev.celestiacraft.cmi.network.CmiNetwork;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

@Mod(Cmi.MODID)
public class Cmi {
	public static final String MODID = "cmi";
	public static final String NAME = "CMI";
	public static final Logger LOGGER = LogManager.getLogger(NAME);
	private static CmiStress STRESS_VALUES;
	private static ForgeConfigSpec STRESS_VALUES_SPEC;
	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
			.setTooltipModifierFactory((item) -> {
				return new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
						.andThen(TooltipModifier.mapNull(KineticStats.create(item)));
			});

	public static ResourceLocation loadResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	public static String loadString(String path) {
		return loadResource(path).toString();
	}

	public Cmi(FMLJavaModLoadingContext context) {
		IEventBus bus = context.getModEventBus();

		REGISTRATE.registerEventListeners(bus);

		CmiBlock.register();
		CmiBlockEntity.register();
		CmiEntity.register();
		CmiItem.register();
		CmiMechanism.register();
		CmiRecipeType.register(bus);
		CmiRecipeSerializer.register(bus);
		CmiCreateRecipe.register(bus);
		CmiAdvanmentTrigger.register();

		CmiCreativeTab.register(bus);

		CmiBlockPartialModel.init();
		CmiSpriteShiftEntry.init();

		CmiNetwork.register();

		PonderIndex.addPlugin(new CmiPonderPlugin());

		bus.addListener(this::onCommonSetup);
		bus.addListener(this::onRegister);
		bus.addListener(this::onConfigLoad);
		bus.addListener(this::onConfigReload);

		registerConfig(context);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CmiClient.onCtorClient(bus));
	}

	private static void registerConfig(FMLJavaModLoadingContext context) {
		context.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "nebula/cmi/common.toml");

		if (STRESS_VALUES == null) {
			Pair<CmiStress, ForgeConfigSpec> stressConfig = CmiStress.createConfig();
			STRESS_VALUES = stressConfig.getLeft();
			STRESS_VALUES_SPEC = stressConfig.getRight();
			BlockStressValues.IMPACTS.registerProvider(STRESS_VALUES::getImpact);
			BlockStressValues.CAPACITIES.registerProvider(STRESS_VALUES::getCapacity);
		}

		context.registerConfig(ModConfig.Type.SERVER, STRESS_VALUES_SPEC, "nebula/cmi/stressValues.toml");
	}

	private void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			Regions.register(new ModOverworldRegion(5));

			SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, Cmi.MODID, ModSurfaceRuleData.makeRules());
			SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(SurfaceRuleManager.RuleCategory.OVERWORLD, SurfaceRuleManager.RuleStage.AFTER_BEDROCK, 0, ModSurfaceRuleData.makeInjections());
			AdAstraOxygenCompat.register();
		});
	}

	private void onRegister(RegisterEvent event) {
		CmiFanProcessingTypes.register();
	}

	private void onConfigLoad(ModConfigEvent.Loading event) {
		if (STRESS_VALUES != null && event.getConfig().getSpec() == STRESS_VALUES_SPEC) {
			STRESS_VALUES.onLoad();
		}
	}

	private void onConfigReload(ModConfigEvent.Reloading event) {
		if (STRESS_VALUES != null && event.getConfig().getSpec() == STRESS_VALUES_SPEC) {
			STRESS_VALUES.onReload();
		}
	}
}