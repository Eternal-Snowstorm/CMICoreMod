package dev.celestiacraft.cmi.event;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.recipe.space_elevator_construction.SpaceElevatorConstructionRecipe;
import dev.celestiacraft.cmi.compat.adastra.SpaceElevatorConstructionHandler;
import dev.celestiacraft.cmi.network.CmiNetwork;
import dev.celestiacraft.cmi.network.c2s.ConstructSpaceElevatorPacket;
import dev.celestiacraft.cmi.network.c2s.RequestSpaceElevatorMaterialsPacket;
import dev.celestiacraft.cmi.network.c2s.StoreSpaceElevatorMaterialsPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = Cmi.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SpaceElevatorWrenchClientHandler {
	private static final int HOLD_TICKS_REQUIRED = 24;
	private static final int MAX_VISIBLE_ROWS = 6;

	@Nullable
	private static BlockPos trackedAnchor;
	@Nullable
	private static BlockPos syncedAnchor;
	private static int holdTicks;
	private static boolean packetSent;
	private static int scrollOffset;
	private static int[] syncedCounts = new int[0];

	public static float getHoldProgress(@Nullable BlockPos anchorPos) {
		if (trackedAnchor == null || !trackedAnchor.equals(anchorPos)) {
			return 0.0F;
		}
		return Mth.clamp(holdTicks / (float) HOLD_TICKS_REQUIRED, 0.0F, 1.0F);
	}

	public static boolean isCharging(@Nullable BlockPos anchorPos) {
		return trackedAnchor != null && trackedAnchor.equals(anchorPos) && holdTicks > 0 && !packetSent;
	}

	public static int getStoredCount(@Nullable BlockPos anchorPos, int ingredientIndex) {
		if (syncedAnchor == null || !syncedAnchor.equals(anchorPos)) {
			return 0;
		}
		return ingredientIndex >= 0 && ingredientIndex < syncedCounts.length ? syncedCounts[ingredientIndex] : 0;
	}

	public static void syncStoredCounts(BlockPos anchorPos, int[] counts) {
		syncedAnchor = anchorPos.immutable();
		syncedCounts = counts.clone();
	}

	public static boolean hasStoredMaterials(@Nullable BlockPos anchorPos, @Nullable SpaceElevatorConstructionRecipe recipe, boolean bypassRequirements) {
		if (bypassRequirements) {
			return true;
		}
		if (anchorPos == null || recipe == null) {
			return false;
		}
		for (int i = 0; i < recipe.ingredients().size(); i++) {
			if (getStoredCount(anchorPos, i) < recipe.ingredients().get(i).count()) {
				return false;
			}
		}
		return true;
	}

	public static int maxVisibleRows() {
		return MAX_VISIBLE_ROWS;
	}

	public static int getScrollOffset(int totalEntries) {
		return Mth.clamp(scrollOffset, 0, Math.max(0, totalEntries - MAX_VISIBLE_ROWS));
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (!event.getLevel().isClientSide()) {
			return;
		}
		if (event.getHand() != InteractionHand.MAIN_HAND) {
			return;
		}
		if (!SpaceElevatorConstructionHandler.isWrench(event.getItemStack())) {
			return;
		}
		if (!SpaceElevatorConstructionHandler.isAnchorBlock(event.getLevel(), event.getPos())) {
			return;
		}

		SpaceElevatorConstructionRecipe recipe = SpaceElevatorConstructionHandler.getRecipe(event.getLevel());
		boolean bypassRequirements = event.getEntity().isCreative() || event.getEntity().isSpectator();
		if (recipe != null && !hasStoredMaterials(event.getPos(), recipe, bypassRequirements)) {
			CmiNetwork.CHANNEL.sendToServer(new StoreSpaceElevatorMaterialsPacket(event.getPos()));
		}

		// 建造流程触发，阻止use使用，将流畅修改为存储->建造
		event.setCanceled(true);
		event.setCancellationResult(InteractionResult.SUCCESS);
	}

	@SubscribeEvent
	public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null || mc.level == null) {
			return;
		}

		BlockPos anchorPos = getLookedAtAnchor(mc, player);
		if (anchorPos == null || !SpaceElevatorConstructionHandler.isWrench(player.getMainHandItem())) {
			return;
		}

		SpaceElevatorConstructionRecipe recipe = SpaceElevatorConstructionHandler.getRecipe(player.level());
		if (recipe == null) {
			return;
		}

		int totalEntries = SpaceElevatorConstructionRecipe.getDisplayIngredients(player, recipe).size();
		if (totalEntries <= MAX_VISIBLE_ROWS) {
			return;
		}

		scrollOffset = Mth.clamp(scrollOffset - (int) Math.signum(event.getScrollDelta()), 0, Math.max(0, totalEntries - MAX_VISIBLE_ROWS));
		event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null || mc.level == null || mc.screen != null) {
			resetAll();
			return;
		}
		if (!SpaceElevatorConstructionHandler.isWrench(player.getMainHandItem())) {
			resetAll();
			return;
		}

		BlockPos anchorPos = getLookedAtAnchor(mc, player);
		if (anchorPos == null) {
			resetAll();
			return;
		}

		if (trackedAnchor == null || !trackedAnchor.equals(anchorPos)) {
			trackedAnchor = anchorPos.immutable();
			holdTicks = 0;
			packetSent = false;
			scrollOffset = 0;
			CmiNetwork.CHANNEL.sendToServer(new RequestSpaceElevatorMaterialsPacket(anchorPos));
		}

		if (!mc.options.keyUse.isDown()) {
			holdTicks = 0;
			packetSent = false;
			return;
		}
		if (!canCharge(player, anchorPos)) {
			holdTicks = 0;
			packetSent = false;
			return;
		}

		if (holdTicks < HOLD_TICKS_REQUIRED) {
			holdTicks++;
		}

		if (holdTicks >= HOLD_TICKS_REQUIRED && !packetSent) {
			CmiNetwork.CHANNEL.sendToServer(new ConstructSpaceElevatorPacket(anchorPos));
			packetSent = true;
		}
	}

	@Nullable
	private static BlockPos getLookedAtAnchor(Minecraft mc, Player player) {
		if (!(mc.hitResult instanceof BlockHitResult blockHitResult) || blockHitResult.getType() != HitResult.Type.BLOCK) {
			return null;
		}

		BlockPos pos = blockHitResult.getBlockPos();
		return SpaceElevatorConstructionHandler.isAnchorBlock(player.level(), pos) ? pos : null;
	}

	private static boolean canCharge(Player player, BlockPos anchorPos) {
		if (!SpaceElevatorConstructionHandler.isWithinUseRange(player, anchorPos)) {
			return false;
		}
		if (SpaceElevatorConstructionHandler.hasNearbyElevator(player.level(), anchorPos)) {
			return false;
		}

		SpaceElevatorConstructionRecipe recipe = SpaceElevatorConstructionHandler.getRecipe(player.level());
		return hasStoredMaterials(anchorPos, recipe, player.isCreative() || player.isSpectator());
	}

	private static void resetAll() {
		trackedAnchor = null;
		syncedAnchor = null;
		syncedCounts = new int[0];
		holdTicks = 0;
		packetSent = false;
		scrollOffset = 0;
	}
}
