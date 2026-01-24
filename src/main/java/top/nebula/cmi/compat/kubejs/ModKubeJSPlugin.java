package top.nebula.cmi.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import top.nebula.cmi.Cmi;
import top.nebula.cmi.compat.kubejs.recipe.AcceleratorSchema;
import top.nebula.cmi.utils.ModLang;

public class ModKubeJSPlugin extends KubeJSPlugin {
	@Override
	public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
		event.namespace(Cmi.MODID)
				.register("accelerator", AcceleratorSchema.SCHEMA);
	}

	public void registerBindings(BindingsEvent event) {
		super.registerBindings(event);

		event.add("CMICore", Cmi.class);
		event.add("CMIModLang", ModLang.class);
	}
}