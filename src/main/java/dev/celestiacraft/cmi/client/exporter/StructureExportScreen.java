package dev.celestiacraft.cmi.client.exporter;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.CmiLang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class StructureExportScreen extends Screen {

    private EditBox pathInput;
    private EditBox resolutionInput;
    private StructureScene scene;
    private StructureRenderer renderer;

    private float rotationX = -30f;
    private float rotationY = 45f;
    private float zoom = 1.0f;
    private float panX = 0f;
    private float panY = 0f;
    private boolean dragging = false;

    private int selectedResolution = 2048;
    private boolean pendingExport = false;
    private final String initialPath;

    private List<String> tabCompletionList = new ArrayList<>();
    private int tabCompletionIndex = -1;
    private String lastInputForSuggestions = null;
    private boolean showSuggestions = false;
    private static final int SUGGESTION_HEIGHT = 12;
    private static final int MAX_SUGGESTIONS = 8;

    public StructureExportScreen(String initialPath) {
        super(CmiLang.translateDirect("screen.structure_export"));
        this.initialPath = initialPath;
    }

    public StructureExportScreen() {
        this(null);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        pathInput = new EditBox(this.font, centerX - 140, 10, 200, 20,
                Component.literal("Path"));
        pathInput.setMaxLength(512);
        pathInput.setHint(Component.literal("filename.nbt"));
        pathInput.setResponder(text -> refreshSuggestions());
        if (initialPath != null) {
            pathInput.setValue(initialPath);
        }
        addRenderableWidget(pathInput);

        addRenderableWidget(Button.builder(CmiLang.translateDirect("export.load"),
                        btn -> loadStructure())
                .bounds(centerX + 65, 10, 50, 20).build());

        resolutionInput = new EditBox(this.font, centerX - 140, this.height - 30, 130, 20,
                Component.literal("Resolution"));
        resolutionInput.setMaxLength(5);
        resolutionInput.setValue(String.valueOf(selectedResolution));
        resolutionInput.setHint(Component.literal("2048"));
        resolutionInput.setFilter(s -> s.isEmpty() || s.matches("\\d+"));
        addRenderableWidget(resolutionInput);

        addRenderableWidget(Button.builder(CmiLang.translateDirect("export.save"),
                        btn -> pendingExport = true)
                .bounds(centerX + 5, this.height - 30, 110, 20).build());

        if (initialPath != null && scene == null) {
            loadStructure();
        }
    }

    private void loadStructure() {
        String pathStr = pathInput.getValue().trim();
        if (pathStr.isEmpty()) return;
        if (!pathStr.endsWith(".nbt")) {
            pathStr = pathStr + ".nbt";
        }
        Path path = Minecraft.getInstance().gameDirectory.toPath().resolve("schematics").resolve(pathStr);
        if (!Files.exists(path)) {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(
                        Component.literal("File not found: " + pathStr), false);
            }
            return;
        }
        try {
            scene = StructureScene.loadFromFile(path);
            // 提示缺失方块
            if (!scene.getMissingBlocks().isEmpty() && Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(
                        Component.literal("§e[Warning] Missing blocks: " +
                                String.join(", ", scene.getMissingBlocks())), false);
            }
            if (Minecraft.getInstance().level != null) {
                renderer = new StructureRenderer(scene, Minecraft.getInstance().level);
            }
        } catch (IOException e) {
            Cmi.LOGGER.error("Failed to load structure: {}", pathStr, e);
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(
                        Component.literal("Failed to load: " + e.getMessage()), false);
            }
        }
    }

    private void refreshSuggestions() {
        String input = pathInput.getValue().trim().toLowerCase();
        if (input.equals(lastInputForSuggestions)) return;
        lastInputForSuggestions = input;
        tabCompletionList.clear();
        tabCompletionIndex = -1;
        Path schematicsDir = Minecraft.getInstance().gameDirectory.toPath().resolve("schematics");
        if (Files.isDirectory(schematicsDir)) {
            try (var stream = Files.list(schematicsDir)) {
                stream.filter(p -> p.toString().endsWith(".nbt"))
                        .map(p -> p.getFileName().toString())
                        .filter(name -> name.toLowerCase().startsWith(input))
                        .sorted()
                        .forEach(tabCompletionList::add);
            } catch (Exception ignored) {}
        }
        showSuggestions = pathInput.isFocused() && !tabCompletionList.isEmpty();
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);

        if (!pathInput.isFocused() && !resolutionInput.isFocused()) {
            long window = Minecraft.getInstance().getWindow().getWindow();
            float panSpeed = 0.05f / zoom;
            if (InputConstants.isKeyDown(window, InputConstants.KEY_A)) panX += panSpeed;
            if (InputConstants.isKeyDown(window, InputConstants.KEY_D)) panX -= panSpeed;
            if (InputConstants.isKeyDown(window, InputConstants.KEY_W)) panY -= panSpeed;
            if (InputConstants.isKeyDown(window, InputConstants.KEY_S)) panY += panSpeed;
        }
        super.render(graphics, mouseX, mouseY, partialTick);
        if (scene == null || renderer == null) {
            graphics.drawCenteredString(this.font,
                    CmiLang.translateDirect("export.no_structure"),
                    this.width / 2, this.height / 2, 0xAAAAAA);
        } else {
            int previewH = this.height - 80;
            PoseStack poseStack = graphics.pose();
            renderer.renderPreview(poseStack, rotationX, rotationY, zoom, panX, panY,
                    this.width, previewH);
        }
        if (pendingExport && renderer != null) {
            pendingExport = false;
            doExport();
        }
        showSuggestions = pathInput.isFocused() && !tabCompletionList.isEmpty();
        if (showSuggestions) {
            int dropX = pathInput.getX();
            int dropY = pathInput.getY() + pathInput.getHeight();
            int dropW = pathInput.getWidth();
            int visibleCount = Math.min(tabCompletionList.size(), MAX_SUGGESTIONS);
            // Background
            graphics.fill(dropX, dropY, dropX + dropW, dropY + visibleCount * SUGGESTION_HEIGHT, 0xE0000000);
            graphics.fill(dropX, dropY, dropX + 1, dropY + visibleCount * SUGGESTION_HEIGHT, 0xFF555555);
            graphics.fill(dropX + dropW - 1, dropY, dropX + dropW, dropY + visibleCount * SUGGESTION_HEIGHT, 0xFF555555);
            graphics.fill(dropX, dropY + visibleCount * SUGGESTION_HEIGHT - 1, dropX + dropW, dropY + visibleCount * SUGGESTION_HEIGHT, 0xFF555555);
            for (int i = 0; i < visibleCount; i++) {
                int itemY = dropY + i * SUGGESTION_HEIGHT;
                boolean hovered = mouseX >= dropX && mouseX < dropX + dropW
                        && mouseY >= itemY && mouseY < itemY + SUGGESTION_HEIGHT;
                boolean selected = i == tabCompletionIndex;
                if (selected || hovered) {
                    graphics.fill(dropX + 1, itemY, dropX + dropW - 1, itemY + SUGGESTION_HEIGHT, 0xFF333355);
                }
                int textColor = selected ? 0xFFFFFF00 : (hovered ? 0xFFFFFFCC : 0xFFCCCCCC);
                graphics.drawString(this.font, tabCompletionList.get(i), dropX + 3, itemY + 2, textColor);
            }
        }
    }

    private void doExport() {
        int resolution;
        try {
            resolution = Integer.parseInt(resolutionInput.getValue().trim());
        } catch (NumberFormatException e) {
            resolution = selectedResolution;
        }
        resolution = Math.max(1024, Math.min(16384, resolution));

        String pathStr = pathInput.getValue().trim();
        String baseName = Path.of(pathStr).getFileName().toString()
                .replaceAll("\\.[^.]+$", "");
        Path exportDir = Minecraft.getInstance().gameDirectory.toPath().resolve("screenshots").resolve("exports");
        Path outputPath = exportDir.resolve(baseName + "_" + resolution + ".png");

        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(
                    Component.literal("§7Exporting..."), false);
        }

        renderer.exportToPng(outputPath, resolution, rotationX, rotationY, zoom, panX, panY,
                path -> {
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.displayClientMessage(
                                CmiLang.translateDirect("export.success", path.toString()), false);
                    }
                },
                error -> {
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.displayClientMessage(
                                Component.literal("Export failed: " + error.getMessage()), false);
                    }
                }
        );
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (pathInput.isFocused() && showSuggestions && !tabCompletionList.isEmpty()) {
            int visibleCount = Math.min(tabCompletionList.size(), MAX_SUGGESTIONS);
            if (keyCode == InputConstants.KEY_DOWN) {
                tabCompletionIndex = Math.min(tabCompletionIndex + 1, visibleCount - 1);
                return true;
            }
            if (keyCode == InputConstants.KEY_UP) {
                tabCompletionIndex = Math.max(tabCompletionIndex - 1, 0);
                return true;
            }
            if ((keyCode == InputConstants.KEY_TAB || keyCode == InputConstants.KEY_RETURN) && tabCompletionIndex >= 0) {
                applySuggestion(tabCompletionIndex);
                return true;
            }
            if (keyCode == InputConstants.KEY_TAB) {
                applySuggestion(0);
                return true;
            }
            if (keyCode == InputConstants.KEY_ESCAPE) {
                showSuggestions = false;
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void applySuggestion(int index) {
        if (index >= 0 && index < tabCompletionList.size()) {
            pathInput.setValue(tabCompletionList.get(index));
            showSuggestions = false;
            tabCompletionIndex = -1;
            lastInputForSuggestions = null;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && showSuggestions && !tabCompletionList.isEmpty()) {
            int dropX = pathInput.getX();
            int dropY = pathInput.getY() + pathInput.getHeight();
            int dropW = pathInput.getWidth();
            int visibleCount = Math.min(tabCompletionList.size(), MAX_SUGGESTIONS);
            if (mouseX >= dropX && mouseX < dropX + dropW
                    && mouseY >= dropY && mouseY < dropY + visibleCount * SUGGESTION_HEIGHT) {
                int clickedIndex = (int) ((mouseY - dropY) / SUGGESTION_HEIGHT);
                if (clickedIndex >= 0 && clickedIndex < visibleCount) {
                    applySuggestion(clickedIndex);
                    return true;
                }
            }
        }
        if (button == 0 && mouseY > 35 && mouseY < this.height - 40) {
            dragging = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (dragging && button == 0) {
            rotationY += (float) dragX * 0.5f;
            rotationX += (float) dragY * 0.5f;
            rotationX = Math.max(-90f, Math.min(90f, rotationX));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        zoom = (float) Math.max(0.1, Math.min(10.0, zoom + delta * 0.1));
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
