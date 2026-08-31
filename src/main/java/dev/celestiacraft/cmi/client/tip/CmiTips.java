package dev.celestiacraft.cmi.client.tip;

import cc.sighs.auratip.api.tip.TipBuilder;
import cc.sighs.auratip.api.tip.TipRegistry;
import cc.sighs.auratip.api.tip.TipServer;
import cc.sighs.auratip.data.TipData;
import dev.celestiacraft.cmi.Cmi;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Map;

public class CmiTips {
	public static final ResourceLocation DRAGON_DEATH;
	public static final ResourceLocation DRAGON_DEATH_TRIGGER;

	static {
		DRAGON_DEATH = Cmi.loadResource("dragon_death");
		DRAGON_DEATH_TRIGGER = Cmi.loadResource("dragon_death_trigger");
	}

	public static void register() {
		TipData dragonDeathTip = new TipBuilder(DRAGON_DEATH)
				.triggerRepeatable(DRAGON_DEATH_TRIGGER, 0)
				.visual((builder) -> {
					builder.size(220, 55)
							.positionPreset("TOP_LEFT")
							.animationStyle(Cmi.loadResource("fade_and_slide"))
							.hoverAnimationStyle(Cmi.loadResource("none"));
				})
				.behavior((builder) -> {
					builder.duration(160)
							.pauseOnHover(true);
				})
				.page(0, (builder) -> {
					builder.title(Component.literal("末影龙"))
							.content(Component.literal("狩猎完成"));
				})
				.build();

		TipRegistry.setTips(Cmi.MODID, List.of(dragonDeathTip));
	}

	public static void triggerLoginTip(ServerPlayer player) {
		player.sendSystemMessage(Component.literal("[Debug] 触发"), true);
		TipServer.trigger(DRAGON_DEATH_TRIGGER, player, Map.of(
				"player", player.getDisplayName()
		));
	}
}