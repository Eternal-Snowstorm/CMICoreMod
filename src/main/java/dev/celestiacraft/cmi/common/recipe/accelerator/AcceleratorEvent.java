package dev.celestiacraft.cmi.common.recipe.accelerator;

import com.simibubi.create.AllSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import dev.celestiacraft.cmi.Cmi;

@SuppressWarnings("ALL")
@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AcceleratorEvent {
	public static final Lazy<Block> ACCELERATOR_BLOCK = Lazy.of(() -> {
		return BuiltInRegistries.BLOCK.get(Cmi.loadResource("accelerator"));
	});

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		Level level = event.getLevel();
		Player player = event.getEntity();
		ItemStack item = player.getItemInHand(event.getHand());
		BlockPos pos = event.getPos();
		BlockState state = level.getBlockState(pos);

		if (level.isClientSide()) {
			return;
		}

		ServerLevel sl = (ServerLevel) level;

		if (!state.is(ACCELERATOR_BLOCK.get())) {
			return;
		}

		SimpleContainer container = new SimpleContainer(1);
		container.setItem(0, item);

		level.getRecipeManager()
				.getRecipesFor(AcceleratorRecipe.Type.INSTANCE, container, level)
				.forEach((recipe) -> {
					int required = 24;
					int matched = 0;

					for (int dx = -2; dx <= 2; dx++) {
						for (int dz = -2; dz <= 2; dz++) {

							if (dx == 0 && dz == 0) {
								continue;
							}

							BlockPos checkPos = pos.offset(dx, 0, dz);
							if (level.getBlockState(checkPos).is(recipe.targetBlock)) {
								matched++;
							}
						}
					}

					if (matched != required) {
						return;
					}
					RandomSource random = level.getRandom();

					for (int dx = -2; dx <= 2; dx++) {
						for (int dz = -2; dz <= 2; dz++) {
							BlockPos targetPos = pos.offset(dx, 0, dz);

							if (level.getBlockState(targetPos).is(recipe.targetBlock)) {
								for (AcceleratorRecipe.OutputEntry out : recipe.outputs) {
									BlockState outputBlock = out.block.defaultBlockState();

									if (random.nextFloat() <= out.chance) {
										// 破坏方块为了粒子效果和音效
										level.destroyBlock(targetPos, false);
										level.setBlock(targetPos, outputBlock, 3);
										break;
									}
								}
							}
						}
					}

					// 申必音效
					level.playSound(
							null,
							pos.getX() + 0.5,
							pos.getY() + 1.0,
							pos.getZ() + 0.5,
							AllSoundEvents.CRAFTER_CRAFT.getMainEvent(),
							SoundSource.VOICE,
							3,
							1
					);
					// 申必粒子效果
					for (int number = 0; number < 10; number++) {
						double offsetX = (Math.random() - 0.5) * 0.5;
						double offsetY = Math.random() * 0.5;
						double offsetZ = (Math.random() - 0.5) * 0.5;

						sl.sendParticles(
								ParticleTypes.HAPPY_VILLAGER,
								pos.getX() + 0.5,
								pos.getY() + 1.2,
								pos.getZ() + 0.5,
								1,
								offsetX,
								offsetY,
								offsetZ,
								0.02
						);
					}

					if (!player.isCreative()) {
						item.shrink(1);
					}

					event.setCanceled(true);
					event.setCancellationResult(InteractionResult.SUCCESS);
				});
	}
}