package top.nebula.cmi;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.nebula.cmi.common.register.*;
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

	/**
	 * 加载ResourceLocation资源
	 *
	 * @param path 命名空间下的资源路径
	 *             <p>
	 *             Resource path under namespace
	 * @return
	 */
	@Info("加载\"cmi\"命名空间下的资源\n\nLoad resource under namespace \"cmi\"")
	public static ResourceLocation loadResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	public static String loadString(String path) {
		return String.format("cmi:%s", path);
	}

	public Cmi(FMLJavaModLoadingContext context) {
		IEventBus bus = context.getModEventBus();

		ModBlocks.register(bus);
		ModBlockEntityTypes.register(bus);
		ModItems.register(bus);
		ModRecipeType.register(bus);
		ModRecipeSerializer.register(bus);
		ModModifier.MODIFIERS.register(bus);

		bus.addListener(this::commonSetup);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			Regions.register(new ModOverworldRegion(5));

			SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, Cmi.MODID, ModSurfaceRuleData.makeRules());
			SurfaceRuleManager.addToDefaultSurfaceRulesAtStage(SurfaceRuleManager.RuleCategory.OVERWORLD, SurfaceRuleManager.RuleStage.AFTER_BEDROCK, 0, ModSurfaceRuleData.makeInjections());
		});
	}
}