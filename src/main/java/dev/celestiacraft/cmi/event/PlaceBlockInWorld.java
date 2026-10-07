package dev.celestiacraft.cmi.event;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Optional;

public class PlaceBlockInWorld {
	public static void placeStructure(ServerLevel level, int x, int y, int z, ResourceLocation structureId) {
		StructureTemplateManager manager = level.getStructureManager();
		ResourceLocation structureName = structureId;
		Optional<StructureTemplate> template = manager.get(structureName);
		template.ifPresent((temp) -> {
			temp.placeInWorld(
					level,
					new BlockPos(x, y, z),
					new BlockPos(x, y, z),
					new StructurePlaceSettings()
							.setRotation(Rotation.NONE)
							.setMirror(Mirror.NONE)
							.setIgnoreEntities(false),
					level.random,
					3
			);
		});
	}

	public static void fillArea(ServerLevel level, BlockState blockState, int x0, int y0, int z0, int x1, int y1, int z1) {
		for (int x = x0; x <= x1; x++) {
			for (int y = y0; y <= y1; y++) {
				for (int z = z0; z <= z1; z++) {
					BlockPos pos = new BlockPos(x, y, z);
					BlockState state = level.getBlockState(pos);
					if (state.isAir() || !state.getFluidState().isEmpty()) {
						level.setBlockAndUpdate(pos, blockState);
					}
				}
			}
		}
	}
}