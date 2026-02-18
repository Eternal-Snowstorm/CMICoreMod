package dev.celestiacraft.cmi.tag;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import dev.celestiacraft.libs.tags.TagsBuilder;

public class ModFluidTags {
	public static final TagKey<Fluid> STEAM;

	static {
		STEAM = TagsBuilder.fluid("steam")
				.forge();
	}
}