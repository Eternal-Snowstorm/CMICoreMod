package dev.celestiacraft.cmi.event;

import dev.celestiacraft.cmi.utils.CmiGlobal;
import dev.celestiacraft.libs.NebulaLibs;
import dev.celestiacraft.libs.utils.FestivalUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModifyTitle {
	private static final List<String> NORMAL_GREETINGS = List.of(
			"Wishing You Happiness and Peace Every Day!",
			"May Your Day Be Filled with Joy and Creativity!",
			"Wishing You Good Health and Great Adventures!",
			"Hope Everything Goes Smoothly for You Today!",
			"May Happiness and Inspiration Always Be with You!",
			"Wishing You a Relaxing and Wonderful Day!",
			"May Your Creativity Shine Brighter Than Ever!",
			"Wishing You Success, Joy, and Endless Inspiration!",
			"Hope Your Day Is Full of Fun and Good Luck!",
			"May Every Machine You Build Work Perfectly!",
			"Wishing You Peace, Happiness, and Good Fortune!",
			"Hope You Enjoy Every Moment of Your Journey!",
			"May Your World Be Filled with Warmth and Creativity!",
			"Wishing You Energy, Inspiration, and Happiness!",
			"May You Always Have the Courage to Create Something Amazing!",
			"Hope Your Automation Never Breaks!",
			"Wishing You Good Luck in Everything You Build!",
			"May Your Day Be as Smooth as Perfectly Aligned Cogwheels!",
			"Wishing You Endless Creativity and Wonderful Discoveries!",
			"Hope Today Brings You Happiness and Inspiration!"
	);

	private static String getRandomNormalGreeting() {
		int nextInt = ThreadLocalRandom.current().nextInt(NORMAL_GREETINGS.size());
		return NORMAL_GREETINGS.get(nextInt);
	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			String title;

			if (FestivalUtils.isAprilFoolsDay()) {
				title = "Create: Infinity Mechanism";
			} else {
				title = "Create: Mechanism and Innovation";
			}

			String greeting = FestivalUtils.getFestivalGreeting();

			// 非节日随机普通标题
			if (greeting == null) {
				greeting = getRandomNormalGreeting();
			}

			title += " - " + greeting;

			String version = String.format(
					" %s-%s",
					CmiGlobal.getModPackState(),
					CmiGlobal.getModpackNumberVersion()
			);
			NebulaLibs.modifyWindowsTitle(title + version);
		});
	}
}