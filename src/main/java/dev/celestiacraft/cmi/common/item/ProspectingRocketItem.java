package dev.celestiacraft.cmi.common.item;

import dev.celestiacraft.cmi.common.block.space_elevator_base_console.io.IoPortShape;
import dev.celestiacraft.cmi.common.entity.prospecting_rocket.ProspectingRocketEntity;
import dev.celestiacraft.cmi.common.entity.prospecting_rocket.ProspectingRocketTier;
import dev.celestiacraft.cmi.compat.adastra.SpaceElevatorConstructionHandler;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ProspectingRocketItem extends Item implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private final ProspectingRocketTier tier;
	private final Supplier<EntityType<?>> entityType;

	public ProspectingRocketItem(ProspectingRocketTier tier, Supplier<EntityType<?>> entityType, Properties properties) {
		super(properties);
		this.tier = tier;
		this.entityType = entityType;
	}

	public ProspectingRocketTier getTier() {
		return tier;
	}

	@Override
	public @NotNull InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (context.getClickedFace() != Direction.UP) {
			return InteractionResult.PASS;
		}

		Player player = context.getPlayer();
		BlockPos clickedPos = context.getClickedPos();
		BlockPos anchor = SpaceElevatorConstructionHandler.resolveLaunchPadAnchor(level, clickedPos);
		if (anchor == null) {
			notify(player, "text.cmi.prospecting_rocket.not_launch_pad");
			return InteractionResult.PASS;
		}
		if (SpaceElevatorConstructionHandler.hasNearbyElevator(level, anchor)) {
			notify(player, "text.cmi.prospecting_rocket.base_has_elevator");
			return InteractionResult.PASS;
		}
		if (SpaceElevatorConstructionHandler.hasRocketOnBase(level, anchor)) {
			notify(player, "text.cmi.prospecting_rocket.base_has_rocket");
			return InteractionResult.PASS;
		}

		Entity entity = entityType.get().create(level);
		if (!(entity instanceof ProspectingRocketEntity rocket)) {
			return InteractionResult.PASS;
		}

		double surfaceY = clickedPos.getY() + IoPortShape.TOP_CENTER.solidShape().max(Direction.Axis.Y);
		rocket.setPos(anchor.getX() + 0.5D, surfaceY, anchor.getZ() + 0.5D);
		rocket.setYRot(context.getHorizontalDirection().getOpposite().toYRot());
		level.addFreshEntity(rocket);
		level.playSound(null, anchor, SoundEvents.NETHERITE_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);

		ItemStack stack = context.getItemInHand();
		if (player == null || !player.isCreative()) {
			stack.shrink(1);
		}
		return InteractionResult.CONSUME;
	}

	private static void notify(@Nullable Player player, String key) {
		if (player != null) {
			player.displayClientMessage(Component.translatable(key), true);
		}
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			private GeoItemRenderer<ProspectingRocketItem> renderer;

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				if (renderer == null) {
					renderer = new GeoItemRenderer<>(new ProspectingRocketItemModel());
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
}
