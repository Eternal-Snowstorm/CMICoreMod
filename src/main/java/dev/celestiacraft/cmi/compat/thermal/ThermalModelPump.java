package dev.celestiacraft.cmi.compat.thermal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ThermalModelPump extends Model {
	private static final int TEXTURE_WIDTH = 20;
	private static final int TEXTURE_HEIGHT = 20;

	private final ModelPart pump;

	public ThermalModelPump() {
		super(RenderType::entityCutout);

		ModelPart west = limb();
		west.setPos(-8.0F, 0.0F, 8.0F);
		west.setRotation(1.5708F, 0.0F, 1.5708F);

		ModelPart north = limb();
		north.setPos(-8.0F, 0.0F, 8.0F);
		north.setRotation(3.1416F, 0.0F, 1.5708F);

		ModelPart east = limb();
		east.setPos(-8.0F, 0.0F, 8.0F);
		east.setRotation(-1.5708F, 0.0F, 1.5708F);

		ModelPart south = limb();
		south.setPos(-8.0F, 0.0F, 8.0F);
		south.setRotation(0.0F, 0.0F, 1.5708F);

		ModelPart bottom = new ModelPart(bake(CubeListBuilder.create()
				.texOffs(-5, 5)
				.mirror(true)
				.addBox(-5.0F, 0.0F, -5.0F, 10.0F, 0.0F, 10.0F)), Map.of());
		bottom.setPos(-8.0F, -3.0F, 8.0F);
		bottom.setRotation(3.1416F, 0.0F, 0.0F);

		pump = new ModelPart(bake(CubeListBuilder.create()
				.texOffs(-5, 5)
				.mirror(true)
				.addBox(-13.0F, -5.0F, 3.0F, 10.0F, 0.0F, 10.0F)), Map.of(
				"west_r1", west,
				"north_r1", north,
				"east_r1", east,
				"south_r1", south,
				"bottom_r1", bottom
		));
		pump.setPos(8.0F, 24.0F, -8.0F);
	}

	private static ModelPart limb() {
		return new ModelPart(bake(CubeListBuilder.create()
				.texOffs(3, 5)
				.addBox(-4.0F, -5.0F, 5.0F, 1.0F, 10.0F, 0.0F)
				.addBox(-5.0F, -5.0F, 5.0F, 1.0F, 10.0F, 0.0F)), Map.of());
	}

	private static List<ModelPart.Cube> bake(CubeListBuilder builder) {
		List<ModelPart.Cube> cubes = new ArrayList<>();

		for (CubeDefinition definition : builder.getCubes()) {
			cubes.add(definition.bake(TEXTURE_WIDTH, TEXTURE_HEIGHT));
		}

		return cubes;
	}

	@Override
	public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer consumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		pump.render(poseStack, consumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}