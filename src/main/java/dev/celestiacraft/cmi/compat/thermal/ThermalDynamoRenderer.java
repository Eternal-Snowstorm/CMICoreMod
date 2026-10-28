package dev.celestiacraft.cmi.compat.thermal;

import cofh.lib.util.constants.BlockStatePropertiesCoFH;
import cofh.thermal.lib.common.block.DynamoBlock;
import cofh.thermal.lib.common.block.entity.DynamoBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ThermalDynamoRenderer implements BlockEntityRenderer<DynamoBlockEntity> {
	private static final double PUMP_BASE_Y = -0.825D;
	private static final double PUMP_STROKE_RANGE = 0.245D;
	private static final int VERTEX_STRIDE = 8;

	private final ThermalModelPump pumpModel = new ThermalModelPump();

	public ThermalDynamoRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(@NotNull DynamoBlockEntity dynamo, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		try {
			BlockState state = dynamo.getBlockState();

			if (!(state.getBlock() instanceof DynamoBlock)) {
				return;
			}

			TextureAtlasSprite sprite = findSprite(dynamo, state);

			if (sprite == null) {
				return;
			}

			Direction facing = state.hasProperty(BlockStatePropertiesCoFH.FACING_ALL)
					? state.getValue(BlockStatePropertiesCoFH.FACING_ALL)
					: Direction.UP;
			BlockPos pos = dynamo.getBlockPos();
			float speed = ThermalDynamoClient.getSpeed(pos);
			float stroke = ThermalDynamoClient.hasStroke(pos)
					? (Mth.sin((ThermalDynamoClient.getStroke(pos) + (partialTick * speed)) / 10.0F) + 1.0F) / 2.0F
					: 0.0F;
			double pumpY = PUMP_BASE_Y - (stroke * PUMP_STROKE_RANGE);

			RenderType renderType = RenderType.entityCutout(sprite.atlasLocation());
			VertexConsumer consumer = sprite.wrap(bufferSource.getBuffer(renderType));

			poseStack.pushPose();

			try {
				poseStack.translate(0.5D, 0.5D, 0.5D);

				switch (facing) {
					case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
					case NORTH -> poseStack.mulPose(Axis.XN.rotationDegrees(90.0F));
					case EAST -> poseStack.mulPose(Axis.ZN.rotationDegrees(90.0F));
					case SOUTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
					case WEST -> poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
					default -> {
					}
				}

				poseStack.translate(0.0D, pumpY, 0.0D);
				pumpModel.renderToBuffer(poseStack, consumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
			} finally {
				poseStack.popPose();
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	/**
	 * 按方块模型里的朝向摆放 arm 的顺序取贴图: 把方块的 facing 强制当成 UP 拿模型,
	 * 再在朝上的面里挑 y 最大的那个 —— 发电机模型有两个 UP 面(外壳顶面 y=10 与线圈顶面 y=16), 活塞臂要的是线圈.
	 */
	private static TextureAtlasSprite findSprite(DynamoBlockEntity dynamo, BlockState state) {
		Level level = dynamo.getLevel();
		RandomSource random = level == null ? RandomSource.create() : level.getRandom();
		BlockState upState = state.hasProperty(BlockStatePropertiesCoFH.FACING_ALL)
				? state.setValue(BlockStatePropertiesCoFH.FACING_ALL, Direction.UP)
				: state;
		BakedModel model = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(upState);

		// 线圈的面在模型里没写 cullface, 只会出现在 side = null 的结果里; UP 侧再兜一次底
		TextureAtlasSprite sprite = findTopSprite(model.getQuads(upState, null, random, ModelData.EMPTY, null));

		if (sprite == null) {
			sprite = findTopSprite(model.getQuads(upState, Direction.UP, random, ModelData.EMPTY, null));
		}

		return sprite == null ? coilSprite() : sprite;
	}

	private static TextureAtlasSprite findTopSprite(List<BakedQuad> quads) {
		TextureAtlasSprite sprite = null;
		float highest = Float.NEGATIVE_INFINITY;

		for (BakedQuad quad : quads) {
			int[] vertices = quad.getVertices();

			if (quad.getDirection() != Direction.UP || vertices.length < VERTEX_STRIDE) {
				continue;
			}

			float y = Float.intBitsToFloat(vertices[1]);

			if (y <= highest) {
				continue;
			}

			highest = y;
			sprite = quad.getSprite();
		}

		return sprite;
	}

	/**
	 * 模型没解析出线圈面时退回线圈贴图本身, 免得活塞臂整个消失
	 */
	private static TextureAtlasSprite coilSprite() {
		return Minecraft.getInstance()
				.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
				.apply(ResourceLocation.fromNamespaceAndPath("thermal", "block/dynamos/dynamo_coil_rf"));
	}

	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		List<BlockState> dynamoStates = new ArrayList<>();

		for (Block block : ForgeRegistries.BLOCKS) {
			if (block instanceof DynamoBlock) {
				dynamoStates.add(block.defaultBlockState());
			}
		}

		if (dynamoStates.isEmpty()) {
			return;
		}

		// 不写死 registry 名字: 凡是接收这些发电机方块的 BlockEntityType 都挂上渲染器
		for (BlockEntityType<?> type : ForgeRegistries.BLOCK_ENTITY_TYPES) {
			for (BlockState state : dynamoStates) {
				if (type.isValid(state)) {
					register(event, type);
					break;
				}
			}
		}
	}

	private static void register(EntityRenderersEvent.RegisterRenderers event, BlockEntityType<?> type) {
		event.registerBlockEntityRenderer((BlockEntityType<DynamoBlockEntity>) type, ThermalDynamoRenderer::new);
	}
}