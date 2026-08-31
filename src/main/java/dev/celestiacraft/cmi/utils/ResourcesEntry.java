package dev.celestiacraft.cmi.utils;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ResourcesEntry {
	@Getter
	private final ResourceLocation id;

	private Item itemCache;
	private Block blockCache;
	private Fluid fluidCache;

	private ResourcesEntry(ResourceLocation id) {
		this.id = id;
	}

	public static ResourcesEntry of(ResourceLocation id) {
		return new ResourcesEntry(id);
	}

	private <T> T requireNonNull(T obj, String type) {
		if (obj == null) {
			throw new IllegalStateException(
					"[ModResources] Missing %s for id: %s".formatted(type, id)
			);
		}
		return obj;
	}

	public Item getItem() {
		if (itemCache == null) {
			itemCache = requireNonNull(
					ForgeRegistries.ITEMS.getValue(id),
					"Item"
			);
		}
		return itemCache;
	}

	public Block getBlock() {
		if (blockCache == null) {
			blockCache = requireNonNull(
					ForgeRegistries.BLOCKS.getValue(id),
					"Block"
			);
		}
		return blockCache;
	}

	public Fluid getFluid() {
		if (fluidCache == null) {
			fluidCache = requireNonNull(
					ForgeRegistries.FLUIDS.getValue(id),
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

	public BlockState defaultBlockState() {
		return getBlock().defaultBlockState();
	}

	public FluidStack getFluidStack() {
		return new FluidStack(getFluid(), 1000);
	}

	public FluidStack getFluidStack(int amount) {
		return new FluidStack(getFluid(), amount);
	}

	public Item getBucket() {
		return getFluid().getBucket();
	}

	public boolean hasItem() {
		return ForgeRegistries.ITEMS.containsKey(id);
	}

	public boolean hasBlock() {
		return ForgeRegistries.BLOCKS.containsKey(id);
	}

	public boolean hasFluid() {
		return ForgeRegistries.FLUIDS.containsKey(id);
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
		return id + " " + typeHint();
	}
}