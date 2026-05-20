package dev.celestiacraft.cmi.common.recipe.well;

public class BlazeWellRecipe extends WellRecipe {
	@Override
	public boolean equals(Object obj) {
		return obj != null && obj.getClass() == getClass();
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
