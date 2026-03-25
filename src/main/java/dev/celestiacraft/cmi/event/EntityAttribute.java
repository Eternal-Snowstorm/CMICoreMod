package dev.celestiacraft.cmi.event;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.entity.qi_month.QiMonthEntity;
import dev.celestiacraft.cmi.common.register.CmiEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityAttribute {
	@SubscribeEvent
	public static void onEntityAttribute(EntityAttributeCreationEvent event) {
		event.put(CmiEntity.QI_MONTH.get(), QiMonthEntity.setAttributes());
	}
}