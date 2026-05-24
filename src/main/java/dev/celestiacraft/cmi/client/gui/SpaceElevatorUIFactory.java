package dev.celestiacraft.cmi.client.gui;

import com.lowdragmc.lowdraglib.gui.factory.UIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.entity.space_elevator.SpaceElevatorEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SpaceElevatorUIFactory extends UIFactory<SpaceElevatorEntity> {
	public static final SpaceElevatorUIFactory INSTANCE = new SpaceElevatorUIFactory();

	private SpaceElevatorUIFactory() {
		super(Cmi.loadResource("space_elevator_cargo"));
	}

	@Override
	protected ModularUI createUITemplate(SpaceElevatorEntity holder, Player entityPlayer) {
		if (holder == null) {
			return null;
		}
		return SpaceElevatorCargoUI.create(holder, entityPlayer);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	protected SpaceElevatorEntity readHolderFromSyncData(FriendlyByteBuf syncData) {
		int entityId = syncData.readVarInt();
		Level level = Minecraft.getInstance().level;
		if (level == null) {
			return null;
		}
		Entity entity = level.getEntity(entityId);
		return entity instanceof SpaceElevatorEntity elevator ? elevator : null;
	}

	@Override
	protected void writeHolderToSyncData(FriendlyByteBuf syncData, SpaceElevatorEntity holder) {
		syncData.writeVarInt(holder.getId());
	}
}
