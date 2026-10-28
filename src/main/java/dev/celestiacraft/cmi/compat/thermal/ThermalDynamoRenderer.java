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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
	public void render(@NotNull DynamoBlockEntity dynamo, float partialTick, @NotNull PoseStack stack, @NotNull MultiBufferSource source, int packedLight, int packedOverlay) {
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
			VertexConsumer consumer = sprite.wrap(source.getBuffer(renderType));

			stack.pushPose();

			try {
				stack.translate(0.5D, 0.5D, 0.5D);

				switch (facing) {
					case DOWN -> stack.mulPose(Axis.XP.rotationDegrees(180.0F));
					case NORTH -> stack.mulPose(Axis.XN.rotationDegrees(90.0F));
					case EAST -> stack.mulPose(Axis.ZN.rotationDegrees(90.0F));
					case SOUTH -> stack.mulPose(Axis.XP.rotationDegrees(90.0F));
					case WEST -> stack.mulPose(Axis.ZP.rotationDegrees(90.0F));
					default -> {
					}
				}

				stack.translate(0.0D, pumpY, 0.0D);
				pumpModel.renderToBuffer(stack, consumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
			} finally {
				stack.popPose();
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	private static TextureAtlasSprite findSprite(DynamoBlockEntity dynamo, BlockState state) {
		Level level = dynamo.getLevel();
		RandomSource random = level == null ? RandomSource.create() : level.getRandom();
		BlockState upState = state.hasProperty(BlockStatePropertiesCoFH.FACING_ALL)
				? state.setValue(BlockStatePropertiesCoFH.FACING_ALL, Direction.UP)
				: state;
		BakedModel model = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(upState);

		// 外壳顶面在模型里没写 cullface, 只会出现在 side = null 的结果里; UP 侧再兜一次底
		TextureAtlasSprite sprite = findCasingSprite(model.getQuads(upState, null, random, ModelData.EMPTY, null));

		if (sprite == null) {
			sprite = findCasingSprite(model.getQuads(upState, Direction.UP, random, ModelData.EMPTY, null));
		}

		return sprite;
	}

	private static TextureAtlasSprite findCasingSprite(List<BakedQuad> quads) {
		TextureAtlasSprite sprite = null;
		float lowest = Float.POSITIVE_INFINITY;

		for (BakedQuad quad : quads) {
			int[] vertices = quad.getVertices();

			if (quad.getDirection() != Direction.UP || vertices.length < VERTEX_STRIDE) {
				continue;
			}

			float y = Float.intBitsToFloat(vertices[1]);

			if (y >= lowest) {
				continue;
			}

			lowest = y;
			sprite = quad.getSprite();
		}

		return sprite;
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