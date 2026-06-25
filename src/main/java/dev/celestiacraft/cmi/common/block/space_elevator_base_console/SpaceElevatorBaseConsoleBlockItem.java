package dev.celestiacraft.cmi.common.block.space_elevator_base_console;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.render.SpaceElevatorBaseConsoleItemModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.NotNull;
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
	protected boolean placeBlock(@NotNull BlockPlaceContext context, @NotNull BlockState state) {
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
		return cache;
	}

	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> genItemModel() {
		return (context, provider) -> {
			provider.getBuilder(context.getName())
					.parent(new ModelFile.UncheckedModelFile("minecraft:builtin/entity"))
					.transforms()
					.transform(ItemDisplayContext.GUI)
					.rotation(30.0F, 45.0F, 0.0F)
					.translation(0.0F, 0.0F, 0.0F)
					.scale(0.18F)
					.end()
					.transform(ItemDisplayContext.GROUND)
					.rotation(0.0F, 0.0F, 0.0F)
					.translation(0.0F, 2.0F, 0.0F)
					.scale(0.15F)
					.end()
					.transform(ItemDisplayContext.FIXED)
					.rotation(0.0F, 0.0F, 0.0F)
					.translation(0.0F, 0.0F, 0.0F)
					.scale(0.25F)
					.end()
					.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
					.rotation(75.0F, 45.0F, 0.0F)
					.translation(0.0F, 2.5F, 0.0F)
					.scale(0.20F)
					.end()
					.transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
					.rotation(75.0F, 45.0F, 0.0F)
					.translation(0.0F, 2.5F, 0.0F)
					.scale(0.20F)
					.end()
					.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
					.rotation(0.0F, 45.0F, 0.0F)
					.translation(0.0F, 4.0F, 2.0F)
					.scale(0.25F)
					.end()
					.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
					.rotation(0.0F, 225.0F, 0.0F)
					.translation(0.0F, 4.0F, 2.0F)
					.scale(0.25F)
					.end()
					.end();
		};
	}
}
