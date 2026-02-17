package dev.celestiacraft.cmi.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.compat.kubejs.recipe.AcceleratorSchema;
import dev.celestiacraft.cmi.compat.kubejs.recipe.GrindingSchema;
import dev.celestiacraft.cmi.api.CmiLang;

public class ModKubeJSPlugin extends KubeJSPlugin {
	@Override
	public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
		event.namespace(Cmi.MODID)
				.register("accelerator", AcceleratorSchema.SCHEMA)
				.register("grinding", GrindingSchema.SCHEMA);
	}

	public void registerBindings(BindingsEvent event) {
		super.registerBindings(event);

		event.add("CmiCore", Cmi.class);
		event.add("CmiLang", CmiLang.class);
		event.add("CmiLang$JeiLang", CmiLang.JeiLang.class);
	}
}