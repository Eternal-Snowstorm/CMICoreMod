package dev.celestiacraft.cmi.common.entity;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RailgunAmmoEntity extends AbstractArrow {


	protected RailgunAmmoEntity(EntityType<? extends AbstractArrow> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}

	@Override
	protected ItemStack getPickupItem() {
		return null;
	}
}
