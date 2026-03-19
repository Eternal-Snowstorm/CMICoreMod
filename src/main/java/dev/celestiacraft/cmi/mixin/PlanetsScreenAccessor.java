package dev.celestiacraft.cmi.mixin;

import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.client.screens.PlanetsScreen;
import net.minecraft.client.gui.components.Button;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = PlanetsScreen.class, remap = false)
public interface PlanetsScreenAccessor {
	@Accessor("spaceStationButtons")
	List<Button> cmi$getSpaceStationButtons();

	@Accessor("selectedPlanet")
	@Nullable
	Planet cmi$getSelectedPlanet();

	@Accessor("pageIndex")
	int cmi$getPageIndex();
}
