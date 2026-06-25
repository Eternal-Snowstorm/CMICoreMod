package dev.celestiacraft.cmi.tags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import dev.celestiacraft.libs.tags.TagsBuilder;

public class CmiFluidTags {
	public static final TagKey<Fluid>
			STEAM,
			GG_WORK_FLUID;

	static {
		STEAM = TagsBuilder.fluid("steam").forge();
		GG_WORK_FLUID = TagsBuilder.fluid("geothermal_generator_work_fluid").cmi();
	}
}