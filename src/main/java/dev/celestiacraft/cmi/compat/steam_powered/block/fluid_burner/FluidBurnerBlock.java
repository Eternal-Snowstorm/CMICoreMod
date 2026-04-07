package dev.celestiacraft.cmi.compat.steam_powered.block.fluid_burner;

import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.interaction.UseContext;
import dev.celestiacraft.cmi.api.register.block.BasicBlock;
import dev.celestiacraft.cmi.api.register.multiblock.ControllerBlockFacing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.fluids.FluidUtil;

public abstract class FluidBurnerBlock extends BasicBlock implements IBE<FluidBurnerBlockEntity> {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;

	public FluidBurnerBlock(Properties properties) {
		super(properties.sound(SoundType.LANTERN));
	}

	@Override
	protected ControllerBlockFacing useFacingType() {
		return ControllerBlockFacing.HORIZONTAL;
	}

	@Override
	protected boolean useLitState() {
		return true;
	}

	@Override
	public InteractionResult useOn(UseContext context) {
		Player player = context.getPlayer();
		InteractionHand hand = context.getHand();
		Level level = context.getLevel();
		BlockPos pos = context.getPos();
		BlockHitResult result = context.getResult();

		if (!player.isCreative()) {
			return null;
		}
		if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, result.getDirection())) {
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	public static <T extends Block, P> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> burnerBlockState(String material) {
		return (context, provider) -> {
			provider.getVariantBuilder(context.get())
					.forAllStates((state) -> {
						boolean lit = state.getValue(FluidBurnerBlock.LIT);
						Direction facing = state.getValue(FluidBurnerBlock.FACING);
						String stateName = lit ? "on" : "off";
						String path = String.format("block/fluid_burner/%s/%s", material, stateName);
						BlockModelProvider models = provider.models();

						BlockModelBuilder modelFile = models.withExistingParent(path, "block/orientable")
								.texture("top", Cmi.loadResource("block/fluid_burner/" + material + "/top"))
								.texture("bottom", Cmi.loadResource("block/fluid_burner/" + material + "/down"))
								.texture("side", Cmi.loadResource("block/fluid_burner/" + material + "/side"))
								.texture("front", Cmi.loadResource("block/fluid_burner/" + material + (lit ? "/on" : "/off")));

						return ConfiguredModel.builder()
								.modelFile(modelFile)
								.rotationY(BasicBlock.getYRotFromFacing(facing))
								.build();
					});
		};
	}
}
