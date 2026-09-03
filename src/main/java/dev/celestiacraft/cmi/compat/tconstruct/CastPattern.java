package dev.celestiacraft.cmi.compat.tconstruct;

import dev.celestiacraft.cmi.Cmi;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;

public class CastPattern {
	Pattern mechanism = register("mechanism");

	private static Pattern register(String name) {
		return new Pattern(Cmi.MODID, name);
	}
}