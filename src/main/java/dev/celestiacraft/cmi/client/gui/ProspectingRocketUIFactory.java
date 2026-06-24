package dev.celestiacraft.cmi.client.gui;

import com.lowdragmc.lowdraglib.gui.factory.UIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.entity.prospecting_rocket.ProspectingRocketEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class ProspectingRocketUIFactory extends UIFactory<ProspectingRocketEntity> {
	public static final ProspectingRocketUIFactory INSTANCE = new ProspectingRocketUIFactory();

	private ProspectingRocketUIFactory() {
		super(Cmi.loadResource("prospecting_rocket"));
	}

	@Override
	protected @Nullable ModularUI createUITemplate(ProspectingRocketEntity holder, Player entityPlayer) {
		if (holder == null) {
			return null;
		}
		return ProspectingRocketCargoUI.create(holder, entityPlayer);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	protected @Nullable ProspectingRocketEntity readHolderFromSyncData(FriendlyByteBuf syncData) {
		int entityId = syncData.readVarInt();
		Level level = Minecraft.getInstance().level;
		if (level == null) {
			return null;
		}
		Entity entity = level.getEntity(entityId);
		return entity instanceof ProspectingRocketEntity rocket ? rocket : null;
	}

	@Override
	protected void writeHolderToSyncData(FriendlyByteBuf syncData, ProspectingRocketEntity holder) {
		syncData.writeVarInt(holder.getId());
	}
}
