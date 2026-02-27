package dev.celestiacraft.cmi.client.exporter;

import com.simibubi.create.foundation.utility.worldWrappers.WrappedWorld;
import lombok.Getter;
import mekanism.common.content.network.transmitter.Transmitter;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VirtualBlockLevel extends WrappedWorld {
    private final Map<BlockPos, BlockState> blocks;
    private final Map<BlockPos, CompoundTag> blockEntityNbt;
    private final Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();
    @Getter
    private final List<BlockEntity> renderedBlockEntities = new ArrayList<>();

    public VirtualBlockLevel(Level wrapped, Map<BlockPos, BlockState> blocks,
                             Map<BlockPos, CompoundTag> blockEntityNbt) {
        super(wrapped);
        this.blocks = blocks;
        this.blockEntityNbt = blockEntityNbt;
    }

    @Override
    public @NotNull BlockState getBlockState(BlockPos pos) {
        return blocks.getOrDefault(pos, Blocks.AIR.defaultBlockState());
    }

    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockPos pos) {
        return getBlockState(pos).getFluidState();
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(@NotNull BlockPos pos) {
        if (blockEntities.containsKey(pos))
            return blockEntities.get(pos);

        BlockState state = getBlockState(pos);
        if (!(state.getBlock() instanceof EntityBlock entityBlock))
            return null;

        try {
            BlockEntity be = entityBlock.newBlockEntity(pos, state);
            if (be != null) {
                be.setLevel(this);
                blockEntities.put(pos, be);
                renderedBlockEntities.add(be);
                if (blockEntityNbt.containsKey(pos)) {
                    try {
                        be.load(blockEntityNbt.get(pos));
                    } catch (Exception e) {
                    }
                }
            }
            return be;
        } catch (Exception e) {
            blockEntities.put(pos, null);
            return null;
        }
    }

    public void initAllBlockEntities() {
        for (Map.Entry<BlockPos, BlockState> entry : blocks.entrySet()) {
            if (entry.getValue().getBlock() instanceof EntityBlock) {
                getBlockEntity(entry.getKey());
            }
        }
    }

    public void refreshTransmitterConnections() {
        for (Map.Entry<BlockPos, BlockEntity> entry : blockEntities.entrySet()) {
            BlockEntity be = entry.getValue();
            if (!(be instanceof TileEntityTransmitter transmitterTile)) continue;
            Transmitter<?, ?, ?> transmitter = transmitterTile.getTransmitter();
            byte connections = 0x00;
            for (Direction side : Direction.values()) {
                BlockPos neighborPos = entry.getKey().relative(side);
                BlockEntity neighborBe = blockEntities.get(neighborPos);
                if (neighborBe instanceof TileEntityTransmitter neighborTile
                        && transmitter.supportsTransmissionType(neighborTile)
                        && transmitter.canConnect(side)
                        && neighborTile.getTransmitter().canConnect(side.getOpposite())) {
                    connections |= (byte) (1 << side.ordinal());
                }
            }
            transmitter.currentTransmitterConnections = connections;
        }
    }

    public void updateNeighborStates() {
        Map<BlockPos, BlockState> updates = new HashMap<>();
        for (Map.Entry<BlockPos, BlockState> entry : blocks.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();
            BlockState updated = state;
            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = pos.relative(dir);
                BlockState neighborState = getBlockState(neighborPos);
                updated = updated.updateShape(dir, neighborState, this, pos, neighborPos);
            }
            if (updated != state) {
                updates.put(pos, updated);
            }
        }
        blocks.putAll(updates);
    }
}
