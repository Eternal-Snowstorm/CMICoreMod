package dev.celestiacraft.cmi.common.block.space_elevator_base_console;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class SpaceElevatorBaseConsoleBlockItem extends BlockItem implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public SpaceElevatorBaseConsoleBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		boolean ok = super.placeBlock(context, state);
		if (ok) {
			Level level = context.getLevel();
			if (!level.isClientSide()) {
				SpaceElevatorBaseConsoleBlock.deployStructure(level, context.getClickedPos());
			}
		}
		return ok;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			private GeoItemRenderer<SpaceElevatorBaseConsoleBlockItem> renderer;

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				if (renderer == null) {
					renderer = new GeoItemRenderer<>(new SpaceElevatorBaseConsoleItemModel());
				}
				return renderer;
			}
		});
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
