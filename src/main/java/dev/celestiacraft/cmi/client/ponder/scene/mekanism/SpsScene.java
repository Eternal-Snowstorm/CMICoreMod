package dev.celestiacraft.cmi.client.ponder.scene.mekanism;

import dev.celestiacraft.libs.client.ponder.NebulaSceneBuilder;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;

public class SpsScene {
	public static void building(SceneBuilder builder, SceneBuildingUtil util) {
		NebulaSceneBuilder scene = new NebulaSceneBuilder(builder);

		scene.title("sps_building", "超临界移相器搭建");
	}
}