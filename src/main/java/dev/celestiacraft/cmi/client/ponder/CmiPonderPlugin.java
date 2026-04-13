package dev.celestiacraft.cmi.client.ponder;

import dev.celestiacraft.cmi.Cmi;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CmiPonderPlugin implements PonderPlugin {
	@Override
	public @NotNull String getModId() {
		return Cmi.MODID;
	}

	@Override
	public void registerScenes(@NotNull PonderSceneRegistrationHelper<ResourceLocation> helper) {
		CmiPonderScenes.register(helper);
	}

	@Override
	public void registerTags(@NotNull PonderTagRegistrationHelper<ResourceLocation> helper) {
		CmiPonderTags.register(helper);
		CmiPonderTags.add(helper);
	}

	public static void init5x5(SceneBuilder builder, SceneBuildingUtil util) {
		builder.configureBasePlate(0, 0, 5);
		builder.scaleSceneView(0.9f);
		builder.world().showSection(util.select().layer(0), Direction.UP);
	}

	public static void init7x7(SceneBuilder builder, SceneBuildingUtil util) {
		builder.configureBasePlate(0, 0, 7);
		builder.scaleSceneView(0.75f);
		builder.world().showSection(util.select().layer(0), Direction.UP);
	}

	public static void init9x9(SceneBuilder builder, SceneBuildingUtil util) {
		builder.configureBasePlate(0, 0, 9);
		builder.scaleSceneView(0.6f);
		builder.world().showSection(util.select().layer(0), Direction.UP);
	}

	public static void rotateAround(SceneBuilder builder, int duration, int angle) {
		float times = 360f / angle;

		for (int i = 0; i < times; i++) {
			rotate(builder, (int) (duration / times), angle);
		}
	}

	public static void rotate(SceneBuilder builder, int time, int angle) {
		builder.rotateCameraY(angle);
		builder.idle(time);
	}
}