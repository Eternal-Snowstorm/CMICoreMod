package dev.celestiacraft.cmi.common.item.mechanism;

import com.simibubi.create.foundation.item.CustomArmPoseItem;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.item.equipments.railgun.BuiltinRailgunAmmoTypes;
import dev.celestiacraft.cmi.common.item.equipments.railgun.RailGunAmmoType;
import dev.celestiacraft.cmi.common.item.equipments.railgun.RailgunAmmoProvider;
import dev.celestiacraft.cmi.common.item.equipments.railgun.RailgunPacket;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CoilItem extends ProjectileWeaponItem implements CustomArmPoseItem {

	@Override
	public HumanoidModel.@Nullable ArmPose getArmPose(ItemStack stack, AbstractClientPlayer player, InteractionHand hand) {
		return null;
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return null;
	}

	@Override
	public int getDefaultProjectileRange() {
		return 0;
	}
}