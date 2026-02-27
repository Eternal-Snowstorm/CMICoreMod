package dev.celestiacraft.cmi.client.exporter;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.CmiLang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StructureExportScreen extends Screen {

    private EditBox pathInput;
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
        if (initialPath != null) {
            pathInput.setValue(initialPath);
        }
        addRenderableWidget(pathInput);

        addRenderableWidget(Button.builder(CmiLang.translateDirect("export.load"),
                        btn -> loadStructure())
                .bounds(centerX + 65, 10, 50, 20).build());

        addRenderableWidget(CycleButton.<Integer>builder(v -> Component.literal(v + "px"))
                .withValues(1024, 2048, 4096, 8192)
                .withInitialValue(selectedResolution)
                .create(centerX - 140, this.height - 30, 130, 20,
                        CmiLang.translateDirect("export.resolution"),
                        (btn, val) -> selectedResolution = val));

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

        Path path = Minecraft.getInstance().gameDirectory.toPath().resolve(pathStr);
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

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);

        if (!pathInput.isFocused()) {
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
    }

    private void doExport() {
        String pathStr = pathInput.getValue().trim();
        String baseName = Path.of(pathStr).getFileName().toString()
                .replaceAll("\\.[^.]+$", "");
        Path exportDir = Minecraft.getInstance().gameDirectory.toPath().resolve("exports");
        Path outputPath = exportDir.resolve(baseName + "_" + selectedResolution + ".png");

        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(
                    Component.literal("§7Exporting..."), false);
        }

        renderer.exportToPng(outputPath, selectedResolution, rotationX, rotationY, zoom, panX, panY,
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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
