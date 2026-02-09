package top.nebula.cmi.compat.jei.api;

public class CmiGuiTextures {
    public static final CmiGuiTexture WATER_PUMP_SEA_WATER_ARROW;
    public static final CmiGuiTexture WATER_PUMP_ARROW;

    static {
        WATER_PUMP_SEA_WATER_ARROW = addGuiTexture("arrows", 1, 1, 63, 24);
        WATER_PUMP_ARROW = addGuiTexture("arrows", 1, 25, 63, 48);
    }

    public static CmiGuiTexture addGuiTexture(String path, int startX, int startY, int width, int height) {
        return new CmiGuiTexture(path, startX, startY, width, height);
    }

    public static CmiGuiTexture addGuiTexture(String path, int width, int height) {
        return new CmiGuiTexture(path, 0, 0, width, height);
    }
}