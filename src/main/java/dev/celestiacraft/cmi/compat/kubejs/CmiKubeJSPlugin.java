package dev.celestiacraft.cmi.compat.kubejs;

import com.jesz.createdieselgenerators.CreateDieselGenerators;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.client.CmiLang;
import dev.celestiacraft.cmi.compat.create.CmiHeatLevel;
import dev.celestiacraft.cmi.compat.kubejs.custom.item.CdgCuttersItemBuilder;
import dev.celestiacraft.cmi.compat.kubejs.custom.item.CdgHammerItemBuilder;
import dev.celestiacraft.cmi.compat.kubejs.recipe.*;
import dev.celestiacraft.cmi.compat.kubejs.recipe.cdg.CdgRecipesSchema;
import dev.celestiacraft.cmi.network.ClientSeedHandler;
import dev.celestiacraft.cmi.utils.CmiGlobal;
import dev.celestiacraft.cmi.utils.metal.CmiMetalRegistry;
import dev.celestiacraft.cmi.utils.tool.CmiMiningLevel;
import dev.celestiacraft.cmi.utils.tool.CmiToolType;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class CmiKubeJSPlugin extends KubeJSPlugin {
	@Override
	public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
		event.namespace(Cmi.MODID)
				.register("freezing", FreezingSchema.SCHEMA)
				.register("space_elevator_base", SpaceElevatorBaseSchema.SCHEMA)
				.register("space_elevator_construction", SpaceElevatorConstructionSchema.SCHEMA)
				.register("accelerator", AcceleratorSchema.SCHEMA)
				.register("test_coke_oven", MachineRecipeSchema.SCHEMA)
				.register("grinding", GrindingSchema.SCHEMA)
				.register("fluid_burn", FluidBurnSchema.SCHEMA);

		event.namespace(CreateDieselGenerators.ID)
				.register("basin_fermenting", CdgRecipesSchema.BASIN)
				.register("distillation", CdgRecipesSchema.DISTILLATION)
				.register("bulk_fermenting", CdgRecipesSchema.BULK)
				.register("casting", CdgRecipesSchema.CASTING)
				.register("compression_molding", CdgRecipesSchema.COMPRESSION)
				.register("hammering", CdgRecipesSchema.HAMMERING)
				.register("wire_cutting", CdgRecipesSchema.CUTTING);
	}

	public void registerBindings(BindingsEvent event) {
		event.add("CmiCore", Cmi.class);
		event.add("CmiLang", CmiLang.class);
		event.add("CmiLang$JeiLang", CmiLang.JeiLang.class);
		event.add("ClientSeedHandler", ClientSeedHandler.class);
		event.add("CmiHeatLevel", CmiHeatLevel.class);
		event.add("CmiGlobal", CmiGlobal.class);
		event.add("CmiMetalRegistry", CmiMetalRegistry.class);
		event.add("CmiToolType", CmiToolType.class);
		event.add("CmiMiningLevel", CmiMiningLevel.class);
	}

	public void init() {
		RegistryInfo.ITEM.addType(
				"createdieselgenerators:hammer",
				CdgHammerItemBuilder.class,
				CdgHammerItemBuilder::new
		);
		RegistryInfo.ITEM.addType(
				"createdieselgenerators:wire_cutter",
				CdgCuttersItemBuilder.class,
				CdgCuttersItemBuilder::new
		);
	}
}