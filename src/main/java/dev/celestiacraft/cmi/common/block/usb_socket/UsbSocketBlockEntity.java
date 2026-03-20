package dev.celestiacraft.cmi.common.block.usb_socket;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class UsbSocketBlockEntity extends BlockEntity {
    public UsbSocketBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        CompoundTag nbtTemp = new CompoundTag();
        nbtTemp.putIntArray("savedDrives",new int[64]);
        super.saveAdditional(nbtTemp);
    }

    public int[] savedDrives = new int[64];

    @Override
    public void load(CompoundTag nbt){
        savedDrives = nbt.getIntArray("savedDrives");
    }

    public void saveAdditional(CompoundTag nbt){
        nbt.putIntArray("savedDrives",savedDrives);
        super.saveAdditional(nbt);
    }
}
