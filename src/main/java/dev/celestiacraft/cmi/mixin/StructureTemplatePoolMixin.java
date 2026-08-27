package dev.celestiacraft.cmi.mixin;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(StructureTemplatePool.class)
public interface StructureTemplatePoolMixin {
	@Accessor("templates")
	ObjectArrayList<StructurePoolElement> cmi$getTemplates();

	@Accessor("rawTemplates")
	List<Pair<StructurePoolElement, Integer>> cmi$getRawTemplates();
}