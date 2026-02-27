package dev.celestiacraft.cmi.client.exporter;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.BuiltInRegistries;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class StructureScene {
    private final Map<BlockPos, BlockState> blocks = new HashMap<>();
    private final Map<BlockPos, CompoundTag> blockEntityNbt = new HashMap<>();
    private final List<String> missingBlocks = new ArrayList<>();
    private int sizeX, sizeY, sizeZ;
    private float centerX, centerY, centerZ;
    private float maxDimension;

    private StructureScene() {}

    public static StructureScene loadFromFile(Path path) throws IOException {
        CompoundTag root = NbtIo.readCompressed(path.toFile());
        StructureScene scene = new StructureScene();
        scene.parse(root);
        return scene;
    }

    @SuppressWarnings("deprecation")
    private void parse(CompoundTag root) {
        ListTag sizeTag = root.getList("size", Tag.TAG_INT);
        sizeX = sizeTag.getInt(0);
        sizeY = sizeTag.getInt(1);
        sizeZ = sizeTag.getInt(2);

        ListTag paletteTag = root.getList("palette", Tag.TAG_COMPOUND);
        BlockState[] palette = new BlockState[paletteTag.size()];
        for (int i = 0; i < paletteTag.size(); i++) {
            CompoundTag paletteEntry = paletteTag.getCompound(i);
            String blockName = paletteEntry.getString("Name");
            ResourceLocation blockId = new ResourceLocation(blockName);
            if (!BuiltInRegistries.BLOCK.containsKey(blockId)) {
                // 方块来自未加载的mod
                palette[i] = Blocks.AIR.defaultBlockState();
                if (!missingBlocks.contains(blockName)) {
                    missingBlocks.add(blockName);
                }
            } else {
                palette[i] = NbtUtils.readBlockState(
                        BuiltInRegistries.BLOCK.asLookup(), paletteEntry);
            }
        }

        ListTag blocksList = root.getList("blocks", Tag.TAG_COMPOUND);
        for (int i = 0; i < blocksList.size(); i++) {
            CompoundTag entry = blocksList.getCompound(i);
            ListTag posTag = entry.getList("pos", Tag.TAG_INT);
            BlockPos pos = new BlockPos(posTag.getInt(0), posTag.getInt(1), posTag.getInt(2));
            int stateIndex = entry.getInt("state");
            BlockState state = (stateIndex >= 0 && stateIndex < palette.length)
                    ? palette[stateIndex] : Blocks.AIR.defaultBlockState();

            if (!state.isAir()) {
                blocks.put(pos, state);
            }
            if (entry.contains("nbt", Tag.TAG_COMPOUND)) {
                blockEntityNbt.put(pos, entry.getCompound("nbt"));
            }
        }

        centerX = sizeX / 2f;
        centerY = sizeY / 2f;
        centerZ = sizeZ / 2f;
        maxDimension = Math.max(sizeX, Math.max(sizeY, sizeZ));
    }
}
