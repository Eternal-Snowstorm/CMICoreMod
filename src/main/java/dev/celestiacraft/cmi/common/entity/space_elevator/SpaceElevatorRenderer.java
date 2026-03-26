package dev.celestiacraft.cmi.common.entity.space_elevator;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SpaceElevatorRenderer extends GeoEntityRenderer<SpaceElevatorEntity> {
	public SpaceElevatorRenderer(EntityRendererProvider.Context context) {
		super(context, new SpaceElevatiorModel());
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull SpaceElevatorEntity entity) {
		return Cmi.loadResource("textures/entity/space_elevator.png");
	}
}