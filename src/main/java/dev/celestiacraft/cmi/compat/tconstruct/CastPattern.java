package dev.celestiacraft.cmi.compat.tconstruct;

import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import dev.celestiacraft.cmi.Cmi;

public class CastPattern {
	Pattern mechanism = register("mechanism");

	private static Pattern register(String name) {
		return new Pattern(Cmi.MODID, name);
	}
}