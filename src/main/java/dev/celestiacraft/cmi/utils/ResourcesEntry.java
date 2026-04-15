package dev.celestiacraft.cmi.utils;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ResourcesEntry {
	@Getter
	private final ResourceLocation location;

	private Item itemCache;
	private Block blockCache;
	private Fluid fluidCache;

	public ResourcesEntry(ResourceLocation location) {
		this.location = location;
	}

	private <T> T requireNonNull(T obj, String type) {
		if (obj == null) {
			throw new IllegalStateException(
					"[ModResources] Missing %s for id: %s".formatted(type, location)
			);
		}
		return obj;
	}

	public Item getItem() {
		if (itemCache == null) {
			itemCache = requireNonNull(
					ForgeRegistries.ITEMS.getValue(location),
					"Item"
			);
		}
		return itemCache;
	}

	public Block getBlock() {
		if (blockCache == null) {
			blockCache = requireNonNull(
					ForgeRegistries.BLOCKS.getValue(location),
					"Block"
			);
		}
		return blockCache;
	}

	public Fluid getFluid() {
		if (fluidCache == null) {
			fluidCache = requireNonNull(
					ForgeRegistries.FLUIDS.getValue(location),
					"Fluid"
			);
		}
		return fluidCache;
	}

	public ItemStack getItemStack() {
		return new ItemStack(getItem());
	}

	public ItemStack getItemStack(int count) {
		return new ItemStack(getItem(), count);
	}

	public FluidStack getFluidStack(int amount) {
		return new FluidStack(getFluid(), amount);
	}

	public Item getBucket() {
		return getFluid().getBucket();
	}

	public boolean hasItem() {
		return ForgeRegistries.ITEMS.containsKey(location);
	}

	public boolean hasBlock() {
		return ForgeRegistries.BLOCKS.containsKey(location);
	}

	public boolean hasFluid() {
		return ForgeRegistries.FLUIDS.containsKey(location);
	}

	private String typeHint() {
		return "[item=%s, block=%s, fluid=%s]".formatted(
				hasItem(),
				hasBlock(),
				hasFluid()
		);
	}

	@Override
	public String toString() {
		return location + " " + typeHint();
	}
}