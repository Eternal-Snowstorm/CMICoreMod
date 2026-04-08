package dev.celestiacraft.cmi.compat.kubejs;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.client.CmiLang;
import dev.celestiacraft.cmi.compat.create.CmiHeatLevel;
import dev.celestiacraft.cmi.compat.kubejs.recipe.*;
import dev.celestiacraft.cmi.network.ClientSeedHandler;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class ModKubeJSPlugin extends KubeJSPlugin {
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
		new Binder(event)
				.simple(CmiLang.class)
				.name("CmiCore", Cmi.class)
				.name("CmiLang$JeiLang", CmiLang.JeiLang.class)
				.simple(ClientSeedHandler.class)
				.simple(CmiHeatLevel.class);
	}

	private static class Binder {
		private final BindingsEvent event;

		private Binder(BindingsEvent event) {
			this.event = event;
		}

		public Binder simple(Class<?> clazz) {
			event.add(clazz.getSimpleName(), clazz);
			return this;
		}

		public Binder name(String name, Class<?> clazz) {
			event.add(name, clazz);
			return this;
		}
	}
}
