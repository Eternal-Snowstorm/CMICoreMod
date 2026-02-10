package top.nebula.cmi.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import top.nebula.cmi.Cmi;
import top.nebula.cmi.compat.kubejs.recipe.AcceleratorSchema;
import top.nebula.cmi.compat.kubejs.recipe.GrindingSchema;
import top.nebula.cmi.api.CmiLang;

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