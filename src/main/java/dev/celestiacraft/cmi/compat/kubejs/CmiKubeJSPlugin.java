package dev.celestiacraft.cmi.compat.kubejs;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.client.CmiLang;
import dev.celestiacraft.cmi.compat.create.CmiHeatLevel;
import dev.celestiacraft.cmi.compat.kubejs.recipe.*;
import dev.celestiacraft.cmi.network.ClientSeedHandler;
import dev.celestiacraft.cmi.utils.CmiGlobal;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
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
	}

	public void registerBindings(BindingsEvent event) {
		event.add("CmiCore", Cmi.class);
		event.add("CmiLang", CmiLang.class);
		event.add("CmiLang$JeiLang", CmiLang.JeiLang.class);
		event.add("ClientSeedHandler", ClientSeedHandler.class);
		event.add("CmiHeatLevel", CmiHeatLevel.class);
		event.add("CmiGlobal", CmiGlobal.class);
	}
}