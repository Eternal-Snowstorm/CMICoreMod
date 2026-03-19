package dev.celestiacraft.cmi.event;

import com.mojang.datafixers.util.Pair;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.recipe.space_elevator_base.SpaceElevatorBaseRecipe;
import dev.celestiacraft.cmi.mixin.PlanetsScreenAccessor;
import dev.celestiacraft.cmi.network.BuildEarthSpaceElevatorBasePacket;
import dev.celestiacraft.cmi.network.CmiNetwork;
import dev.celestiacraft.cmi.network.RequestSpaceElevatorBaseStatePacket;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.client.screens.PlanetsScreen;
import earth.terrarium.adastra.common.constants.ConstantComponents;
import earth.terrarium.adastra.common.handlers.base.SpaceStation;
import earth.terrarium.adastra.common.menus.PlanetsMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = Cmi.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlanetsScreenBaseBuildClientHandler {
	private static final Map<PlanetsScreen, PopupState> POPUPS = new WeakHashMap<>();
	private static final Map<StationKey, Boolean> BUILT_STATE_CACHE = new java.util.HashMap<>();
	private static final int POPUP_WIDTH = 188;
	private static final int POPUP_HEIGHT = 102;
	private static final int BUILD_BUTTON_X = 10;
	private static final int BUTTON_Y = 74;
	private static final int BUTTON_WIDTH = 60;
	private static final int BUTTON_HEIGHT = 16;
	private static final int CANCEL_BUTTON_X = 118;

	@SubscribeEvent
	public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
		BUILT_STATE_CACHE.clear();
		POPUPS.clear();
	}

	@SubscribeEvent
	public static void onMouseClick(ScreenEvent.MouseButtonPressed.Pre event) {
		if (!(event.getScreen() instanceof PlanetsScreen screen)) {
			return;
		}

		PlanetsScreenAccessor accessor = (PlanetsScreenAccessor) screen;
		PopupState state = POPUPS.computeIfAbsent(screen, key -> new PopupState());
		if (state.visible) {
			handlePopupClick(event, accessor, screen.getMenu(), state);
			return;
		}

		if (event.getButton() != 1 || accessor.cmi$getPageIndex() != 2) {
			return;
		}

		Planet selectedPlanet = accessor.cmi$getSelectedPlanet();
		if (selectedPlanet == null || !Level.OVERWORLD.equals(selectedPlanet.dimension())) {
			return;
		}

		PlanetsMenu menu = screen.getMenu();
		List<Pair<String, SpaceStation>> stations = menu.getOwnedAndTeamSpaceStations(selectedPlanet.orbitIfPresent());
		List<Button> buttons = accessor.cmi$getSpaceStationButtons();
		for (int i = 0; i < Math.min(buttons.size(), stations.size()); i++) {
			Button button = buttons.get(i);
			if (button.active && button.visible && button.isMouseOver(event.getMouseX(), event.getMouseY())) {
				state.visible = true;
				state.requestedState = false;
				state.spaceStationPos = stations.get(i).getSecond().position();
				state.orbitDimension = selectedPlanet.orbitIfPresent();
				requestBuiltStateIfNeeded(state);
				int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
				int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
				state.x = screenWidth - POPUP_WIDTH - 8;
				state.y = Mth.clamp(button.getY() - 4, 8, screenHeight - POPUP_HEIGHT - 8);
				event.setCanceled(true);
				return;
			}
		}
	}

	@SubscribeEvent
	public static void onScreenRender(ScreenEvent.Render.Post event) {
		if (!(event.getScreen() instanceof PlanetsScreen screen)) {
			return;
		}
		PopupState state = POPUPS.get(screen);
		if (state == null || !state.visible) {
			return;
		}
		renderPopup(event.getGuiGraphics(), state, (PlanetsScreenAccessor) screen, screen.getMenu(), event.getMouseX(), event.getMouseY());
	}

	private static void handlePopupClick(ScreenEvent.MouseButtonPressed.Pre event, PlanetsScreenAccessor accessor, PlanetsMenu menu, PopupState state) {
		if (event.getButton() != 0) {
			event.setCanceled(true);
			return;
		}

		double mouseX = event.getMouseX();
		double mouseY = event.getMouseY();
		if (isInside(mouseX, mouseY, state.x + BUILD_BUTTON_X, state.y + BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT)) {
			PopupRenderState renderState = getRenderState(state, accessor, menu);
			if (renderState.canBuild() && renderState.orbitDimension() != null && state.spaceStationPos != null) {
				CmiNetwork.CHANNEL.sendToServer(new BuildEarthSpaceElevatorBasePacket(renderState.orbitDimension(), state.spaceStationPos));
				state.visible = false;
			}
			event.setCanceled(true);
			return;
		}

		if (isInside(mouseX, mouseY, state.x + CANCEL_BUTTON_X, state.y + BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT) || !isInside(mouseX, mouseY, state.x, state.y, POPUP_WIDTH, POPUP_HEIGHT)) {
			state.visible = false;
			event.setCanceled(true);
			return;
		}

		event.setCanceled(true);
	}

	private static void renderPopup(GuiGraphics graphics, PopupState state, PlanetsScreenAccessor accessor, PlanetsMenu menu, int mouseX, int mouseY) {
		PopupRenderState renderState = getRenderState(state, accessor, menu);
		if (renderState.orbitDimension() == null) {
			return;
		}

		int x = state.x;
		int y = state.y;
		graphics.fill(x, y, x + POPUP_WIDTH, y + POPUP_HEIGHT, 0xEE0A1227);
		graphics.fill(x, y, x + POPUP_WIDTH, y + 1, 0xFF8AA7FF);
		graphics.fill(x, y + POPUP_HEIGHT - 1, x + POPUP_WIDTH, y + POPUP_HEIGHT, 0xFF8AA7FF);
		graphics.fill(x, y, x + 1, y + POPUP_HEIGHT, 0xFF8AA7FF);
		graphics.fill(x + POPUP_WIDTH - 1, y, x + POPUP_WIDTH, y + POPUP_HEIGHT, 0xFF8AA7FF);

		Font font = Minecraft.getInstance().font;
		graphics.drawCenteredString(font, Component.translatable("gui.cmi.space_elevator_base.title"), x + POPUP_WIDTH / 2, y + 8, 0xFFFFFF);
		graphics.drawWordWrap(font, Component.translatable("gui.cmi.space_elevator_base.cost"), x + 8, y + 22, POPUP_WIDTH - 16, 0xC7D6FF);
		graphics.drawWordWrap(font, Component.translatable("gui.cmi.space_elevator_base.confirm"), x + 8, y + 44, POPUP_WIDTH - 16, 0xC7D6FF);

		Component statusMessage = renderState.statusMessage();
		if (statusMessage != null) {
			graphics.drawWordWrap(font, statusMessage, x + 8, y + 58, POPUP_WIDTH - 16, 0xFF8A8A);
		}

		boolean buildHovered = isInside(mouseX, mouseY, x + BUILD_BUTTON_X, y + BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
		boolean cancelHovered = isInside(mouseX, mouseY, x + CANCEL_BUTTON_X, y + BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
		drawButton(graphics, font, x + BUILD_BUTTON_X, y + BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, Component.translatable("gui.cmi.space_elevator_base.build"), buildHovered, renderState.canBuild());
		drawButton(graphics, font, x + CANCEL_BUTTON_X, y + BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, Component.translatable("gui.cmi.space_elevator_base.cancel"), cancelHovered, true);

		if (buildHovered && renderState.recipe() != null) {
			graphics.renderComponentTooltip(font, createRecipeTooltip(menu, renderState.recipe()), mouseX, mouseY);
		}
	}

	private static PopupRenderState getRenderState(PopupState state, PlanetsScreenAccessor accessor, PlanetsMenu menu) {
		requestBuiltStateIfNeeded(state);
		Planet selectedPlanet = accessor.cmi$getSelectedPlanet();
		if (selectedPlanet == null) {
			return new PopupRenderState(null, false, null, null);
		}

		SpaceElevatorBaseRecipe recipe = SpaceElevatorBaseRecipe.getRecipe(menu.player().level(), selectedPlanet.orbitIfPresent());
		boolean launchedFromEarth = Level.OVERWORLD.equals(menu.player().level().dimension());
		boolean hasMaterials = recipe != null && SpaceElevatorBaseRecipe.hasIngredients(menu.player(), recipe);
		Boolean builtState = getBuiltState(state);
		boolean alreadyBuilt = Boolean.TRUE.equals(builtState);
		boolean canBuild = launchedFromEarth && hasMaterials && !alreadyBuilt;
		Component statusMessage = null;
		if (alreadyBuilt) {
			statusMessage = Component.translatable("text.cmi.space_elevator_base.already_built").withStyle(ChatFormatting.RED);
		} else if (!launchedFromEarth) {
			statusMessage = Component.translatable("text.cmi.space_elevator_base.need_earth_launch").withStyle(ChatFormatting.RED);
		} else if (recipe != null && !hasMaterials) {
			statusMessage = Component.translatable("text.cmi.space_elevator_base.not_enough_materials").withStyle(ChatFormatting.RED);
		}

		return new PopupRenderState(selectedPlanet.orbitIfPresent(), canBuild, recipe, statusMessage);
	}


	public static void updateBuiltState(ResourceKey<Level> dimension, ChunkPos stationPos, boolean built) {
		BUILT_STATE_CACHE.put(new StationKey(dimension, stationPos), built);
	}

	private static void requestBuiltStateIfNeeded(PopupState state) {
		if (state.orbitDimension == null || state.spaceStationPos == null) {
			return;
		}
		StationKey key = new StationKey(state.orbitDimension, state.spaceStationPos);
		if (BUILT_STATE_CACHE.containsKey(key) || state.requestedState) {
			return;
		}
		state.requestedState = true;
		CmiNetwork.CHANNEL.sendToServer(new RequestSpaceElevatorBaseStatePacket(state.orbitDimension, state.spaceStationPos));
	}

	private static Boolean getBuiltState(PopupState state) {
		if (state.orbitDimension == null || state.spaceStationPos == null) {
			return null;
		}
		return BUILT_STATE_CACHE.get(new StationKey(state.orbitDimension, state.spaceStationPos));
	}

	private static List<Component> createRecipeTooltip(PlanetsMenu menu, SpaceElevatorBaseRecipe recipe) {
		List<Component> tooltip = new ArrayList<>();
		tooltip.add(ConstantComponents.CONSTRUCTION_COST.copy().withStyle(ChatFormatting.AQUA));
		for (Pair<ItemStack, Integer> ingredient : SpaceElevatorBaseRecipe.getDisplayIngredients(menu.player(), recipe)) {
			ItemStack stack = ingredient.getFirst();
			if (stack.isEmpty()) {
				continue;
			}
			int amountOwned = ingredient.getSecond();
			boolean hasEnough = menu.player().isCreative() || menu.player().isSpectator() || amountOwned >= stack.getCount();
			tooltip.add(Component.translatable(
					"tooltip.ad_astra.requirement",
					amountOwned,
					stack.getCount(),
					stack.getHoverName().copy().withStyle(ChatFormatting.DARK_AQUA)
				).copy().withStyle(hasEnough ? ChatFormatting.GREEN : ChatFormatting.RED));
		}
		return tooltip;
	}

	private static void drawButton(GuiGraphics graphics, Font font, int x, int y, int width, int height, Component label, boolean hovered, boolean active) {
		int fillColor = active ? (hovered ? 0xFF3656A8 : 0xFF243B74) : 0xFF1A2236;
		int borderColor = active ? 0xFF9FB8FF : 0xFF5A647A;
		int textColor = active ? 0xFFFFFFFF : 0xFF8D96AA;
		graphics.fill(x, y, x + width, y + height, fillColor);
		graphics.fill(x, y, x + width, y + 1, borderColor);
		graphics.fill(x, y + height - 1, x + width, y + height, borderColor);
		graphics.fill(x, y, x + 1, y + height, borderColor);
		graphics.fill(x + width - 1, y, x + width, y + height, borderColor);
		graphics.drawCenteredString(font, label, x + width / 2, y + 4, textColor);
	}

	private static boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}

	private static class PopupState {
		private boolean visible;
		private ChunkPos spaceStationPos;
		private ResourceKey<Level> orbitDimension;
		private boolean requestedState;
		private int x;
		private int y;
	}

	private record PopupRenderState(ResourceKey<Level> orbitDimension, boolean canBuild, SpaceElevatorBaseRecipe recipe, Component statusMessage) {
	}

	private record StationKey(ResourceKey<Level> dimension, ChunkPos stationPos) {
	}
}
