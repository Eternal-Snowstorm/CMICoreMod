package dev.celestiacraft.cmi.client.overlay;

import dev.celestiacraft.cmi.client.render.SpaceElevatorHudRenderer;
import dev.celestiacraft.cmi.common.recipe.space_elevator_construction.SpaceElevatorConstructionRecipe;
import dev.celestiacraft.cmi.compat.adastra.SpaceElevatorConstructionHandler;
import dev.celestiacraft.cmi.event.SpaceElevatorWrenchClientHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SpaceElevatorConstructionOverlay implements IGuiOverlay {
	private static final SpaceElevatorConstructionOverlay INSTANCE = new SpaceElevatorConstructionOverlay();
	private static final int CARD_WIDTH = 214;
	private static final int ROW_HEIGHT = 22;
	private static final int FOOTER_MAX_LINES = 2;

	public static void register(RegisterGuiOverlaysEvent event) {
		event.registerAboveAll("cmi_space_elevator_construction", INSTANCE);
	}

	@Override
	public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height) {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (mc.options.hideGui || player == null || !SpaceElevatorConstructionHandler.isWrench(player.getMainHandItem())) {
			return;
		}

		BlockPos anchorPos = getLookedAtAnchor(mc, player);
		if (anchorPos == null) {
			return;
		}

		SpaceElevatorConstructionRecipe recipe = SpaceElevatorConstructionHandler.getRecipe(player.level());
		List<SpaceElevatorConstructionRecipe.DisplayIngredient> ingredients = recipe == null ? List.of() : getSortedIngredients(anchorPos, player, recipe);
		boolean bypassRequirements = player.isCreative() || player.isSpectator();
		boolean deployed = SpaceElevatorConstructionHandler.hasNearbyElevator(player.level(), anchorPos);
		boolean ready = recipe != null && !deployed && SpaceElevatorWrenchClientHandler.hasStoredMaterials(anchorPos, recipe, bypassRequirements);
		float completion = recipe == null
				? 0.0F
				: SpaceElevatorConstructionRecipe.getCompletionRatio(
						recipe,
						ingredientIndex -> SpaceElevatorWrenchClientHandler.getStoredCount(anchorPos, ingredientIndex),
						bypassRequirements
				);
		float holdProgress = SpaceElevatorWrenchClientHandler.getHoldProgress(anchorPos);
		boolean charging = SpaceElevatorWrenchClientHandler.isCharging(anchorPos);

		ProjectedPoint projectedPoint = projectToScreen(mc, anchorPos, width, height);
		float anchorX = projectedPoint != null ? projectedPoint.x() : width * 0.5F;
		float anchorY = projectedPoint != null ? projectedPoint.y() : height * 0.42F;
		int side = anchorX < width * 0.56F ? 1 : -1;
		int visibleRows = Math.min(ingredients.size(), SpaceElevatorWrenchClientHandler.maxVisibleRows());
		int cardHeight = 132 + visibleRows * ROW_HEIGHT;
		int cardX = side > 0 ? Mth.floor(anchorX + 58.0F) : Mth.floor(anchorX - CARD_WIDTH - 58.0F);
		int cardY = Mth.floor(anchorY - cardHeight * 0.5F);
		cardX = Mth.clamp(cardX, 12, width - CARD_WIDTH - 12);
		cardY = Mth.clamp(cardY, 12, height - cardHeight - 12);

		float cardAttachX = side > 0 ? cardX : cardX + CARD_WIDTH;
		float cardAttachY = cardY + 30.0F;
		int coolBlue = withAlpha(0x5DBBFF, 0.55F);
		int accentOrange = withAlpha(0xFF9E37, 1.0F);

		drawSignalLine(graphics, width * 0.5F, height * 0.5F, anchorX, anchorY, coolBlue, 1);
		drawSignalLine(graphics, anchorX, anchorY, cardAttachX, cardAttachY, accentOrange, 2);
		drawAnchorMarker(graphics, anchorX, anchorY, deployed ? withAlpha(0x7EF7C7, 1.0F) : accentOrange);
		renderCard(graphics, mc.font, player, cardX, cardY, cardHeight, ingredients, completion, holdProgress, charging, bypassRequirements, deployed, ready, recipe != null);
	}

	@Nullable
	private static BlockPos getLookedAtAnchor(Minecraft mc, Player player) {
		if (!(mc.hitResult instanceof BlockHitResult blockHitResult) || blockHitResult.getType() != HitResult.Type.BLOCK) {
			return null;
		}

		BlockPos pos = blockHitResult.getBlockPos();
		return SpaceElevatorConstructionHandler.isAnchorBlock(player.level(), pos) ? pos : null;
	}

	@Nullable
	private static ProjectedPoint projectToScreen(Minecraft mc, BlockPos anchorPos, int width, int height) {
		Camera camera = mc.gameRenderer.getMainCamera();
		Vec3 cameraPos = camera.getPosition();
		Vec3 worldPos = Vec3.atCenterOf(anchorPos).add(0.0D, 1.15D, 0.0D);
		Vector3f relative = new Vector3f(
				(float) (worldPos.x - cameraPos.x),
				(float) (worldPos.y - cameraPos.y),
				(float) (worldPos.z - cameraPos.z)
		);
		relative.rotate(new Quaternionf(camera.rotation()).conjugate());
		if (relative.z() <= 0.05F) {
			return null;
		}

		double fov = Math.toRadians(mc.options.fov().get());
		double tanHalfFov = Math.tan(fov / 2.0D);
		double aspect = width / (double) height;
		return new ProjectedPoint(
				(float) (width * 0.5D * (1.0D - relative.x() / (relative.z() * tanHalfFov * aspect))),
				(float) (height * 0.5D * (1.0D - relative.y() / (relative.z() * tanHalfFov)))
		);
	}

	private static void renderCard(
			GuiGraphics graphics,
			Font font,
			Player player,
			int x,
			int y,
			int height,
			List<SpaceElevatorConstructionRecipe.DisplayIngredient> ingredients,
			float completion,
			float holdProgress,
			boolean charging,
			boolean bypassRequirements,
			boolean deployed,
			boolean ready,
			boolean hasRecipe
	) {
		boolean shaderReady = SpaceElevatorHudRenderer.isAvailable();
		int panelBg    = withAlpha(0x1B1B1B, 0.91F);
		int borderCol  = withAlpha(0xC87828, 0.45F);
		int sepCol     = withAlpha(0xB06820, 0.55F);
		int textMain   = withAlpha(0xE8E0D0, 1.0F);
		int textMuted  = withAlpha(0x887860, 1.0F);
		int textGood   = withAlpha(0x78C878, 1.0F);
		int textWarn   = withAlpha(0xE8883A, 1.0F);
		int buildColor = deployed ? withAlpha(0x78C878, 1.0F) : withAlpha(0xE8883A, 1.0F);
		int visibleRows = Math.min(ingredients.size(), SpaceElevatorWrenchClientHandler.maxVisibleRows());
		int scrollOffset = SpaceElevatorWrenchClientHandler.getScrollOffset(ingredients.size());
		List<SpaceElevatorConstructionRecipe.DisplayIngredient> visibleIngredients = ingredients.subList(scrollOffset, Math.min(ingredients.size(), scrollOffset + visibleRows));
		String status = statusText(deployed, ready, hasRecipe);
		int pillWidth = font.width(status) + 14;

		if (shaderReady) {
			SpaceElevatorHudRenderer.drawRoundedRect(graphics.pose(), x, y, CARD_WIDTH, height, 6.0F, 0.0F, panelBg, panelBg, borderCol, 1.0F);
			SpaceElevatorHudRenderer.drawRoundedRect(graphics.pose(), x + 10.0F, y + 33.0F, CARD_WIDTH - 20.0F, 1.0F, 0.5F, 0.0F, sepCol, sepCol, 0, 0.0F);
		} else {
			graphics.fill(x, y, x + CARD_WIDTH, y + height, panelBg);
			graphics.fill(x, y, x + CARD_WIDTH, y + 1, borderCol);
			graphics.fill(x, y + height - 1, x + CARD_WIDTH, y + height, borderCol);
			graphics.fill(x, y, x + 1, y + height, borderCol);
			graphics.fill(x + CARD_WIDTH - 1, y, x + CARD_WIDTH, y + height, borderCol);
			graphics.fill(x + 10, y + 33, x + CARD_WIDTH - 10, y + 34, sepCol);
		}

		drawStatusPill(graphics, font, x + CARD_WIDTH - pillWidth - 14, y + 10, status, statusColor(deployed, ready, hasRecipe));
		graphics.drawString(font, translation("gui.cmi.space_elevator.title"), x + 12, y + 10, textMain, false);
		String dimPath = player.level().dimension().location().getPath().toUpperCase().replace("_", " ");
		graphics.drawString(font, dimPath, x + 12, y + 21, textMuted, false);
		graphics.drawString(font, translation("gui.cmi.space_elevator.header.materials"), x + 12, y + 38, textMuted, false);
		if (ingredients.size() > visibleRows) {
			String scrollHint = (scrollOffset + 1) + "-" + (scrollOffset + visibleIngredients.size()) + "/" + ingredients.size();
			graphics.drawString(font, scrollHint, x + CARD_WIDTH - 12 - font.width(scrollHint), y + 38, textMuted, false);
		}

		for (int i = 0; i < visibleIngredients.size(); i++) {
			int rowY = y + 48 + i * ROW_HEIGHT;
			SpaceElevatorConstructionRecipe.DisplayIngredient ingredient = visibleIngredients.get(i);
			boolean complete = ingredient.complete(bypassRequirements);
			int accentColor = complete ? textGood : textWarn;
			graphics.fill(x + 10, rowY + 2, x + 13, rowY + ROW_HEIGHT - 3, accentColor);
			if (!ingredient.stack().isEmpty()) {
				graphics.renderItem(ingredient.stack(), x + 16, rowY + 1);
			}
			String itemName = ingredient.stack().isEmpty() ? translation("gui.cmi.space_elevator.material.unknown") : ingredient.stack().getHoverName().getString();
			graphics.drawString(font, font.plainSubstrByWidth(itemName, 114), x + 36, rowY + 5, textMain, false);
			String countText = ingredient.owned() + "/" + ingredient.required();
			graphics.drawString(font, countText, x + CARD_WIDTH - 12 - font.width(countText), rowY + 5, complete ? textGood : textWarn, false);
		}

		String footerValue = charging ? holdFooterText(deployed, ready, hasRecipe) : footerText(deployed, ready, hasRecipe);
		List<FormattedCharSequence> footerLines = font.split(Component.literal(footerValue), CARD_WIDTH - 24);
		int renderedFooterLines = Math.max(1, Math.min(FOOTER_MAX_LINES, footerLines.size()));
		int footerStartY = y + height - 10;
		for (int i = renderedFooterLines - 1; i >= 0; i--) {
			graphics.drawString(font, footerLines.get(i), x + 12, footerStartY - (renderedFooterLines - i) * font.lineHeight, textMuted, false);
		}
		int barX = x + 12;
		int barY = y + height - 52;
		int barWidth = CARD_WIDTH - 24;
		float barProgress = charging && holdProgress > 0.0F ? holdProgress : completion;
		int barFillWidth = Math.max(1, Mth.floor(barWidth * barProgress));
		int progressLabelY = y + height - 64;
		String progressLabel = charging && holdProgress > 0.0F
				? translation("gui.cmi.space_elevator.progress.holding")
				: translation("gui.cmi.space_elevator.progress");
		graphics.drawString(font, progressLabel, x + 12, progressLabelY, textMuted, false);
		String percentText = (int) (barProgress * 100.0F) + "%";
		graphics.drawString(font, percentText, x + CARD_WIDTH - 12 - font.width(percentText), progressLabelY, textMain, false);
		if (shaderReady) {
			SpaceElevatorHudRenderer.drawRoundedRect(graphics.pose(), barX, barY, barWidth, 6.0F, 3.0F, 0.0F, withAlpha(0x3A3228, 0.95F), withAlpha(0x3A3228, 0.95F), 0, 0.0F);
			SpaceElevatorHudRenderer.drawRoundedRect(graphics.pose(), barX, barY, barFillWidth, 6.0F, 3.0F, 0.0F, buildColor, buildColor, 0, 0.0F);
		} else {
			graphics.fill(barX, barY, barX + barWidth, barY + 6, withAlpha(0x3A3228, 0.95F));
			graphics.fill(barX, barY, barX + barFillWidth, barY + 6, buildColor);
		}
	}

	private static String statusText(boolean deployed, boolean ready, boolean hasRecipe) {
		if (deployed) return translation("gui.cmi.space_elevator.status.deployed");
		if (!hasRecipe) return translation("gui.cmi.space_elevator.status.no_recipe");
		return ready ? translation("gui.cmi.space_elevator.status.ready") : translation("gui.cmi.space_elevator.status.missing");
	}

	private static int statusColor(boolean deployed, boolean ready, boolean hasRecipe) {
		if (deployed) return withAlpha(0x78C878, 1.0F);
		if (!hasRecipe) return withAlpha(0x887860, 1.0F);
		return ready ? withAlpha(0xE8883A, 1.0F) : withAlpha(0xC85030, 1.0F);
	}

	private static String footerText(boolean deployed, boolean ready, boolean hasRecipe) {
		if (deployed) return translation("gui.cmi.space_elevator.footer.deployed");
		if (!hasRecipe) return translation("gui.cmi.space_elevator.footer.no_recipe");
		return ready ? translation("gui.cmi.space_elevator.footer.ready") : translation("gui.cmi.space_elevator.footer.missing");
	}

	private static String holdFooterText(boolean deployed, boolean ready, boolean hasRecipe) {
		if (deployed) return translation("gui.cmi.space_elevator.footer.deployed");
		if (!hasRecipe) return translation("gui.cmi.space_elevator.footer.no_recipe");
		return ready ? translation("gui.cmi.space_elevator.footer.holding") : translation("gui.cmi.space_elevator.footer.missing");
	}

	private static List<SpaceElevatorConstructionRecipe.DisplayIngredient> getSortedIngredients(BlockPos anchorPos, Player player, SpaceElevatorConstructionRecipe recipe) {
		List<SpaceElevatorConstructionRecipe.DisplayIngredient> ingredients = new ArrayList<>(SpaceElevatorConstructionRecipe.getDisplayIngredients(recipe, ingredientIndex -> SpaceElevatorWrenchClientHandler.getStoredCount(anchorPos, ingredientIndex)));
		boolean bypassRequirements = player.isCreative() || player.isSpectator();
		ingredients.sort(Comparator.comparing((SpaceElevatorConstructionRecipe.DisplayIngredient ingredient) -> ingredient.complete(bypassRequirements)).thenComparing((a, b) -> Integer.compare(b.required() - Math.min(b.owned(), b.required()), a.required() - Math.min(a.owned(), a.required()))));
		return ingredients;
	}

	private static void drawStatusPill(GuiGraphics graphics, Font font, int x, int y, String text, int color) {
		int width = font.width(text) + 14;
		if (SpaceElevatorHudRenderer.isAvailable()) {
			SpaceElevatorHudRenderer.drawRoundedRect(graphics.pose(), x, y, width, 14.0F, 7.0F, 0.55F, withAlpha(0x111922, 0.96F), withAlpha(0x182330, 0.96F), color, 0.8F);
		} else {
			graphics.fill(x, y, x + width, y + 12, withAlpha(0x131D26, 0.95F));
			graphics.fill(x, y, x + width, y + 1, color);
			graphics.fill(x, y + 11, x + width, y + 12, color);
		}
		graphics.drawString(font, text, x + 7, y + 4, color, false);
	}

	private static void drawAnchorMarker(GuiGraphics graphics, float x, float y, int color) {
		if (SpaceElevatorHudRenderer.isAvailable()) {
			SpaceElevatorHudRenderer.drawSignalLine(graphics.pose(), x - 15.0F, y, x - 4.0F, y, color, 1.1F);
			SpaceElevatorHudRenderer.drawSignalLine(graphics.pose(), x + 4.0F, y, x + 15.0F, y, color, 1.1F);
			SpaceElevatorHudRenderer.drawSignalLine(graphics.pose(), x, y - 15.0F, x, y - 4.0F, color, 1.1F);
			SpaceElevatorHudRenderer.drawSignalLine(graphics.pose(), x, y + 4.0F, x, y + 15.0F, color, 1.1F);
			SpaceElevatorHudRenderer.drawGlowDot(graphics.pose(), x, y, 2.2F, color);
			return;
		}

		int xi = Mth.floor(x);
		int yi = Mth.floor(y);
		graphics.fill(xi - 14, yi - 1, xi - 4, yi + 1, color);
		graphics.fill(xi + 4, yi - 1, xi + 14, yi + 1, color);
		graphics.fill(xi - 1, yi - 14, xi + 1, yi - 4, color);
		graphics.fill(xi - 1, yi + 4, xi + 1, yi + 14, color);
		graphics.fill(xi - 2, yi - 2, xi + 2, yi + 2, color);
	}

	private static void drawSignalLine(GuiGraphics graphics, float x1, float y1, float x2, float y2, int color, int radius) {
		if (SpaceElevatorHudRenderer.isAvailable()) {
			SpaceElevatorHudRenderer.drawSignalLine(graphics.pose(), x1, y1, x2, y2, color, radius + 0.2F);
			return;
		}

		int steps = Math.max(1, Mth.ceil(Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1))));
		for (int i = 0; i <= steps; i++) {
			float delta = i / (float) steps;
			int x = Mth.floor(Mth.lerp(delta, x1, x2));
			int y = Mth.floor(Mth.lerp(delta, y1, y2));
			graphics.fill(x - radius, y - radius, x + radius + 1, y + radius + 1, color);
		}
	}

	private static int withAlpha(int rgb, float alpha) {
		int a = Mth.clamp((int) (alpha * 255.0F), 0, 255);
		return a << 24 | rgb;
	}

	private static String translation(String key) {
		return Component.translatable(key).getString();
	}

	private record ProjectedPoint(float x, float y) {
	}
}
