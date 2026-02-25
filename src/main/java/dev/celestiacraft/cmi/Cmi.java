package dev.celestiacraft.cmi;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;
import com.simibubi.create.foundation.item.TooltipModifier;
import dev.celestiacraft.cmi.common.register.*;
import dev.celestiacraft.cmi.network.CmiNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import dev.celestiacraft.cmi.client.CmiClient;
import dev.celestiacraft.cmi.client.block.resource.CmiBlockPartialModel;
import dev.celestiacraft.cmi.client.block.resource.CmiSpriteShiftEntry;
import dev.celestiacraft.cmi.common.register.*;
import dev.celestiacraft.cmi.compat.create.CmiStressValueProvider;
import dev.celestiacraft.cmi.config.CommonConfig;
import dev.celestiacraft.cmi.worldgen.WorldGenProvider;
import dev.celestiacraft.cmi.worldgen.region.ModOverworldRegion;
import dev.celestiacraft.cmi.worldgen.surfacerule.ModSurfaceRuleData;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

@Mod(Cmi.MODID)
public class Cmi {
	public static final String MODID = "cmi";
	public static final String NAME = "CMI";
	public static final Logger LOGGER = LogManager.getLogger(NAME);
	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
			.setTooltipModifierFactory((item) -> {
				return new ItemDescription.Modifier(item, Palette.STANDARD_CREATE)
						.andThen(TooltipModifier.mapNull(KineticStats.create(item)));
			});

	public static ResourceLocation loadResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	public Cmi(FMLJavaModLoadingContext context) {
		IEventBus bus = context.getModEventBus();

		REGISTRATE.registerEventListeners(bus);

		CmiBlocks.register();
		CmiBlockEntityTypes.register();
		CmiItems.register();
		CmiRecipeType.register(bus);
		CmiRecipeSerializer.register(bus);
		CmiCreateRecipe.register(bus);

		CmiBlockPartialModel.init();
		CmiSpriteShiftEntry.init();

		CmiNetwork.register();

		bus.addListener(this::onCommonSetup);
		bus.addListener(Cmi::gatherData);

		context.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "nebula/cmi/common.toml");

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CmiClient.onCtorClient(bus));
	}

	private void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			CmiStressValueProvider.register();

			Regions.register(new ModOverworldRegion(5));

			SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, Cmi.MODID, ModSurfaceRuleData.makeRules());
			SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(SurfaceRuleManager.RuleCategory.OVERWORLD, SurfaceRuleManager.RuleStage.AFTER_BEDROCK, 0, ModSurfaceRuleData.makeInjections());
		});
	}

	private static void gatherData(GatherDataEvent event) {
		event.getGenerator().addProvider(event.includeServer(),
				new WorldGenProvider(event.getGenerator().getPackOutput(), event.getLookupProvider()));
	}
}