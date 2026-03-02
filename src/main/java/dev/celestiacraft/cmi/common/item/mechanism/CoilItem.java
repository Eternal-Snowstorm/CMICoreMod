package dev.celestiacraft.cmi.common.item.mechanism;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.item.MechanismItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CoilItem extends MechanismItem {
	public CoilItem(Properties properties) {
		super(properties);
	}

	@NotNull
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}
}