package dev.celestiacraft.cmi.common.entity.prospecting_rocket;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.resources.ResourceLocation;

public enum ProspectingRocketTier {
	TIER_1(1, 1.4F, 3.5F, 4, 3, 12_000),
	TIER_2(2, 1.6F, 4.0F, 5, 4, 18_000),
	TIER_3(3, 1.8F, 4.5F, 6, 5, 27_000),
	TIER_4(4, 2.2F, 5.0F, 8, 6, 40_000);

	private final int level;
	private final float width;
	private final float height;
	private final int cargoColumns;
	private final int cargoRows;
	private final int fuelCapacity;

	ProspectingRocketTier(int level, float width, float height, int cargoColumns, int cargoRows, int fuelCapacity) {
		this.level = level;
		this.width = width;
		this.height = height;
		this.cargoColumns = cargoColumns;
		this.cargoRows = cargoRows;
		this.fuelCapacity = fuelCapacity;
	}

	public int level() {
		return level;
	}

	public float width() {
		return width;
	}

	public float height() {
		return height;
	}

	public int cargoColumns() {
		return cargoColumns;
	}

	public int cargoRows() {
		return cargoRows;
	}

	public int cargoSlots() {
		return cargoColumns * cargoRows;
	}

	public int fuelCapacity() {
		return fuelCapacity;
	}

	public String registryName() {
		return "prospecting_rocket_tier" + level;
	}

	public ResourceLocation modelResource() {
		return Cmi.loadResource("geo/entity/prospecting_rocket/tier" + level + "_prospecting_rocket.geo.json");
	}

	public ResourceLocation textureResource() {
		return Cmi.loadResource("textures/entity/prospecting_rocket/tier" + level + "_prospecting_rocket.png");
	}

	public ResourceLocation animationResource() {
		return Cmi.loadResource("animations/entity/prospecting_rocket.animation.json");
	}
}
