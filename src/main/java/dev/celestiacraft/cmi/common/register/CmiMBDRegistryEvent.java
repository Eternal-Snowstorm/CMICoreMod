package dev.celestiacraft.cmi.common.register;

import com.lowdragmc.mbd2.common.event.MBDRegistryEvent;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.compat.mbd2.BlockExistCondition;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CmiMBDRegistryEvent {

    @SubscribeEvent
    public static void onRecipeConditionRegister(MBDRegistryEvent.RecipeCondition event) {
        event.register(Cmi.loadResource("block_exist").toString(), BlockExistCondition.class);
    }
}