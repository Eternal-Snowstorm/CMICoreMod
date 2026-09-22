package dev.celestiacraft.cmi.common.entity.coin_projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * coin 弹射物渲染器
 *
 * <p>
 * 直接用物品模型渲染被发射出去的 coin, 因此任何拥有物品模型的东西(模组硬币、KubeJS 物品等)
 * 都能正确显示, 无需额外的模型与贴图资源
 * </p>
 *
 * <p>
 * 旋转用的时间来自 {@link CoinProjectileEntity#getSpinTime(float)}, 硬币落地静止后不再旋转
 * </p>
 */
public class CoinProjectileRenderer extends EntityRenderer<CoinProjectileEntity> {
	private static final float BILLBOARD_YAW_OFFSET = 180.0F;

	private final ItemRenderer itemRenderer;

	public CoinProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(@NotNull CoinProjectileEntity entity, float entityYaw, float partialTick, @NotNull PoseStack pose, @NotNull MultiBufferSource buffer, int packedLight) {
		ItemStack coin = entity.getCoin();

		if (!coin.isEmpty()) {
			pose.pushPose();
			pose.translate(0.0D, entity.getBoundingBox().getYsize() / 2.0D - 0.125D, 0.0D);
			transform(pose, entity, partialTick);
			itemRenderer.renderStatic(coin, ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, pose, buffer, entity.level(), 0);
			pose.popPose();
		}

		super.render(entity, entityYaw, partialTick, pose, buffer, packedLight);
	}

	private void transform(PoseStack pose, CoinProjectileEntity entity, float partialTick) {
		float time = entity.getSpinTime(partialTick);

		switch (entity.getRenderMode()) {
			case BILLBOARD -> billboard(pose, entity, partialTick);
			case TUMBLE -> {
				billboard(pose, entity, partialTick);
				pose.mulPose(Axis.ZP.rotationDegrees(time * 2.0F * randomOffset(entity, 16)));
				pose.mulPose(Axis.XP.rotationDegrees(time * randomOffset(entity, 32)));
			}
			case TOWARD_MOTION -> towardMotion(pose, entity, time);
		}
	}

	/**
	 * 正对摄像机
	 */
	private void billboard(PoseStack pose, CoinProjectileEntity entity, float partialTick) {
		Entity camera = Minecraft.getInstance().getCameraEntity();

		if (camera == null) {
			return;
		}

		Vec3 diff = entity.getBoundingBox().getCenter().subtract(camera.getEyePosition(partialTick));

		pose.mulPose(Axis.YP.rotationDegrees(degrees(Mth.atan2(diff.x, diff.z)) + BILLBOARD_YAW_OFFSET));
		pose.mulPose(Axis.XP.rotationDegrees(degrees(Mth.atan2(diff.y, Math.sqrt(diff.x * diff.x + diff.z * diff.z)))));
	}

	/**
	 * 硬币平面朝向飞行方向, 并绕飞行轴自旋
	 */
	private void towardMotion(PoseStack pose, CoinProjectileEntity entity, float time) {
		Vec3 motion = entity.getDeltaMovement();

		pose.mulPose(Axis.YP.rotationDegrees(degrees(Mth.atan2(motion.x, motion.z))));
		pose.mulPose(Axis.XP.rotationDegrees(270.0F + degrees(Mth.atan2(motion.y, -Math.sqrt(motion.x * motion.x + motion.z * motion.z)))));
		pose.mulPose(Axis.YP.rotationDegrees(time * 20.0F * entity.getRenderSpin() + randomOffset(entity, 360)));
	}

	private static float degrees(double radians) {
		return (float) (radians * (180.0D / Math.PI));
	}

	private static int randomOffset(Entity entity, int max) {
		return Math.abs(System.identityHashCode(entity) * 31 % max);
	}

	@Override
	public @NotNull ResourceLocation getTextureLocation(@NotNull CoinProjectileEntity entity) {
		return InventoryMenu.BLOCK_ATLAS;
	}
}
