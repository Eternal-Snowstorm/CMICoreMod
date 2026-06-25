package dev.celestiacraft.cmi.utils;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import com.simibubi.create.Create;
import dev.celestiacraft.cmi.Cmi;
import dev.latvian.mods.kubejs.KubeJS;
import earth.terrarium.adastra.AdAstra;
import lombok.Getter;
import mekanism.common.Mekanism;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;

public enum ModResources {
	SEA_WATER(loadCmi("sea_water")),
	CRUCIBLE_BASE(loadCmi("crucible_base")),
	CRUCIBLE_TUYERE(loadCmi("crucible_tuyere")),
	STEEL_INGOT(loadCmi("steel_ingot")),
	VOID_SPRING(loadCmi("void_spring")),
	VOID_DUST(loadCmi("void_dust")),
	PARCHMENT(loadCmi("parchment")),

	TREATED_WOOD_SLAB(loadIE("slab_treated_wood_horizontal")),

	NAHUATL_SLAB(loadTCon("nahuatl_slab")),
	NAHUATL_FENCE(loadTCon("nahuatl_fence")),
	BLAZEWOOD_SLAB(loadTCon("blazewood_slab")),
	BLAZEWOOD_FENCE(loadTCon("blazewood_fence")),

	COAL_COKE(loadThermal("coal_coke")),

	NETWORK_TOOL(loadAe2("network_tool")),

	STEAM(loadMek("steam")),

	RESSTONE_MODULE(loadVi("redstone_module")),

	IRON_DEPOSIT_BLOCK(loadResource("create_rns:iron_deposit_block"));

	@Getter
	private final ResourcesEntry entry;

	ModResources(ResourcesEntry entry) {
		this.entry = entry;
	}

	public ResourceLocation id() {
		return entry.getLocation();
	}

	public String toString() {
		return id().toString();
	}

	public Item getItem() {
		return entry.getItem();
	}

	public Block getBlock() {
		return entry.getBlock();
	}

	public Fluid getFluid() {
		return entry.getFluid();
	}

	public ItemStack getItemStack() {
		return entry.getItemStack();
	}

	public ItemStack getItemStack(int count) {
		return entry.getItemStack(count);
	}

	public FluidStack getFluidStack() {
		return entry.getFluidStack();
	}

	public FluidStack getFluidStack(int amount) {
		return entry.getFluidStack(amount);
	}

	public BlockState defaultBlockState() {
		return entry.defaultBlockState();
	}

	public Item getBucket() {
		return entry.getBucket();
	}

	public ItemStack getBucketStack() {
		return entry.getBucket().getDefaultInstance();
	}

	public boolean hasItem() {
		return entry.hasItem();
	}

	public boolean hasBlock() {
		return entry.hasBlock();
	}

	public boolean hasFluid() {
		return entry.hasFluid();
	}

	public static ResourcesEntry loadResource(ResourceLocation location) {
		return new ResourcesEntry(location);
	}

	public static ResourcesEntry loadResource(String path) {
		return loadResource(ResourceLocation.parse(path));
	}

	public static ResourcesEntry loadCmi(String path) {
		return loadResource(Cmi.loadResource(path));
	}

	public static ResourcesEntry loadMek(String path) {
		return loadResource(Mekanism.rl(path));
	}

	public static ResourcesEntry loadCreate(String path) {
		return loadResource(Create.asResource(path));
	}

	public static ResourcesEntry loadTCon(String path) {
		return loadResource(TConstruct.getResource(path));
	}

	public static ResourcesEntry loadIE(String path) {
		return loadResource(ImmersiveEngineering.rl(path));
	}

	public static ResourcesEntry loadAd(String path) {
		return loadResource(ResourceLocation.fromNamespaceAndPath(AdAstra.MOD_ID, path));
	}

	public static ResourcesEntry loadThermal(String path) {
		return loadResource(ResourceLocation.fromNamespaceAndPath("thermal", path));
	}

	public static ResourcesEntry loadAe2(String path) {
		return loadResource(ResourceLocation.fromNamespaceAndPath("ae2", path));
	}

	public static ResourcesEntry loadVi(String path) {
		return loadResource(ResourceLocation.fromNamespaceAndPath("vintageimprovements", path));
	}

	public static ResourcesEntry loadKubeJS(String path) {
		return loadResource(KubeJS.id(path));
	}
}