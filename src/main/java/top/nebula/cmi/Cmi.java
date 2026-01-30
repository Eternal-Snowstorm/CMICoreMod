package top.nebula.cmi;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.Registrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.nebula.cmi.client.CmiClient;
import top.nebula.cmi.client.block.resource.CmiBlockPartialModel;
import top.nebula.cmi.common.register.*;
import top.nebula.cmi.compat.create.CmiStressValueProvider;
import top.nebula.cmi.config.CommonConfig;
import top.nebula.cmi.worldgen.region.ModOverworldRegion;
import top.nebula.cmi.worldgen.surfacerule.ModSurfaceRuleData;
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
	public static final Registrate REGISTRATE = Registrate.create(MODID);
	public static final CreateRegistrate CREATE_REGISTRATE = CreateRegistrate.create(MODID)
			.setTooltipModifierFactory((item) -> {
				return new ItemDescription.Modifier(item, Palette.STANDARD_CREATE)
						.andThen(TooltipModifier.mapNull(KineticStats.create(item)));
			});

	public static ResourceLocation loadResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	public Cmi(FMLJavaModLoadingContext context) {
		IEventBus bus = context.getModEventBus();

		CREATE_REGISTRATE.registerEventListeners(bus);

		ModBlocks.register();
		ModBlockEntityTypes.register();
		ModItems.register();
		ModRecipeType.register(bus);
		ModRecipeSerializer.register(bus);

		CmiBlockPartialModel.init();

		bus.addListener(this::onCommonSetup);

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
}