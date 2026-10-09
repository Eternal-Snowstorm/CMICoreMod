package dev.celestiacraft.cmi.compat.mekanism;

import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import lombok.experimental.UtilityClass;
import mekanism.api.chemical.ChemicalTags;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import mekanism.api.providers.IChemicalProvider;
import mekanism.api.providers.IFluidProvider;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

@UtilityClass
public class MekanismIngredient {
	@RemapForJS("fluid")
	public FluidStackIngredient fluid(Fluid fluid, int amount) {
		return IngredientCreatorAccess.fluid().from(fluid, amount);
	}

	@RemapForJS("fluidProvider")
	public FluidStackIngredient fluidProvider(IFluidProvider provider, int amount) {
		return IngredientCreatorAccess.fluid().from(provider, amount);
	}

	@RemapForJS("fluidStack")
	public FluidStackIngredient fluidStack(FluidStack stack) {
		return IngredientCreatorAccess.fluid().from(stack);
	}

	@RemapForJS("fluidStackAmount")
	public FluidStackIngredient fluidStackAmount(FluidStack stack, int amount) {
		return IngredientCreatorAccess.fluid().from(new FluidStack(stack, amount));
	}

	@RemapForJS("fluidStackJS")
	public FluidStackIngredient fluidStackJS(FluidStackJS stack) {
		return fluidStackJSAmount(stack, (int) stack.getAmount());
	}

	@RemapForJS("fluidStackJSAmount")
	public FluidStackIngredient fluidStackJSAmount(FluidStackJS stack, int amount) {
		Fluid fluid = stack.getFluid();

		if (fluid == null || fluid == Fluids.EMPTY) {
			return fluidTagId(ResourceLocation.parse(stack.getId()), amount);
		}

		return fluid(fluid, amount);
	}

	@RemapForJS("fluidId")
	public FluidStackIngredient fluidId(ResourceLocation fluidId, int amount) {
		return fluid(resolveFluid(fluidId), amount);
	}

	@RemapForJS("fluidTag")
	public FluidStackIngredient fluidTag(TagKey<Fluid> tag, int amount) {
		return IngredientCreatorAccess.fluid().from(tag, amount);
	}

	@RemapForJS("fluidTagId")
	public FluidStackIngredient fluidTagId(ResourceLocation tag, int amount) {
		return fluidTag(FluidTags.create(tag), amount);
	}

	@RemapForJS("gas")
	public ChemicalStackIngredient.GasStackIngredient gas(IChemicalProvider<Gas> gas, long amount) {
		return IngredientCreatorAccess.gas().from(gas, amount);
	}

	@RemapForJS("gasStack")
	public ChemicalStackIngredient.GasStackIngredient gasStack(GasStack stack) {
		return IngredientCreatorAccess.gas().from(stack);
	}

	@RemapForJS("gasStackAmount")
	public ChemicalStackIngredient.GasStackIngredient gasStackAmount(GasStack stack, long amount) {
		return IngredientCreatorAccess.gas().from(new GasStack(stack, amount));
	}

	@RemapForJS("gasId")
	public ChemicalStackIngredient.GasStackIngredient gasId(ResourceLocation gasId, long amount) {
		return gas(resolveGas(gasId), amount);
	}

	@RemapForJS("gasTag")
	public ChemicalStackIngredient.GasStackIngredient gasTag(TagKey<Gas> tag, long amount) {
		return IngredientCreatorAccess.gas().from(tag, amount);
	}

	@RemapForJS("gasTagId")
	public ChemicalStackIngredient.GasStackIngredient gasTagId(ResourceLocation tag, long amount) {
		return gasTag(ChemicalTags.GAS.tag(tag), amount);
	}

	@RemapForJS("infusion")
	public ChemicalStackIngredient.InfusionStackIngredient infusion(IChemicalProvider<InfuseType> infusion, long amount) {
		return IngredientCreatorAccess.infusion().from(infusion, amount);
	}

	@RemapForJS("infusionStack")
	public ChemicalStackIngredient.InfusionStackIngredient infusionStack(InfusionStack stack) {
		return IngredientCreatorAccess.infusion().from(stack);
	}

	@RemapForJS("infusionStackAmount")
	public ChemicalStackIngredient.InfusionStackIngredient infusionStackAmount(InfusionStack stack, long amount) {
		return IngredientCreatorAccess.infusion().from(new InfusionStack(stack, amount));
	}

	@RemapForJS("infusionId")
	public ChemicalStackIngredient.InfusionStackIngredient infusionId(ResourceLocation infusionId, long amount) {
		return infusion(resolveInfuseType(infusionId), amount);
	}

	@RemapForJS("infusionTag")
	public ChemicalStackIngredient.InfusionStackIngredient infusionTag(TagKey<InfuseType> tag, long amount) {
		return IngredientCreatorAccess.infusion().from(tag, amount);
	}

	@RemapForJS("infusionTagId")
	public ChemicalStackIngredient.InfusionStackIngredient infusionTagId(ResourceLocation tag, long amount) {
		return infusionTag(ChemicalTags.INFUSE_TYPE.tag(tag), amount);
	}

	@RemapForJS("pigment")
	public ChemicalStackIngredient.PigmentStackIngredient pigment(IChemicalProvider<Pigment> pigment, long amount) {
		return IngredientCreatorAccess.pigment().from(pigment, amount);
	}

	@RemapForJS("pigmentStack")
	public ChemicalStackIngredient.PigmentStackIngredient pigmentStack(PigmentStack stack) {
		return IngredientCreatorAccess.pigment().from(stack);
	}

	@RemapForJS("pigmentStackAmount")
	public ChemicalStackIngredient.PigmentStackIngredient pigmentStackAmount(PigmentStack stack, long amount) {
		return IngredientCreatorAccess.pigment().from(new PigmentStack(stack, amount));
	}

	@RemapForJS("pigmentId")
	public ChemicalStackIngredient.PigmentStackIngredient pigmentId(ResourceLocation pigmentId, long amount) {
		return pigment(resolvePigment(pigmentId), amount);
	}

	@RemapForJS("pigmentTag")
	public ChemicalStackIngredient.PigmentStackIngredient pigmentTag(TagKey<Pigment> tag, long amount) {
		return IngredientCreatorAccess.pigment().from(tag, amount);
	}

	@RemapForJS("pigmentTagId")
	public ChemicalStackIngredient.PigmentStackIngredient pigmentTagId(ResourceLocation tag, long amount) {
		return pigmentTag(ChemicalTags.PIGMENT.tag(tag), amount);
	}

	@RemapForJS("slurry")
	public ChemicalStackIngredient.SlurryStackIngredient slurry(IChemicalProvider<Slurry> slurry, long amount) {
		return IngredientCreatorAccess.slurry().from(slurry, amount);
	}

	@RemapForJS("slurryStack")
	public ChemicalStackIngredient.SlurryStackIngredient slurryStack(SlurryStack stack) {
		return IngredientCreatorAccess.slurry().from(stack);
	}

	@RemapForJS("slurryStackAmount")
	public ChemicalStackIngredient.SlurryStackIngredient slurryStackAmount(SlurryStack stack, long amount) {
		return IngredientCreatorAccess.slurry().from(new SlurryStack(stack, amount));
	}

	@RemapForJS("slurryId")
	public ChemicalStackIngredient.SlurryStackIngredient slurryId(ResourceLocation slurryId, long amount) {
		return slurry(resolveSlurry(slurryId), amount);
	}

	@RemapForJS("slurryTag")
	public ChemicalStackIngredient.SlurryStackIngredient slurryTag(TagKey<Slurry> tag, long amount) {
		return IngredientCreatorAccess.slurry().from(tag, amount);
	}

	@RemapForJS("slurryTagId")
	public ChemicalStackIngredient.SlurryStackIngredient slurryTagId(ResourceLocation tag, long amount) {
		return slurryTag(ChemicalTags.SLURRY.tag(tag), amount);
	}

	@RemapForJS("item")
	public ItemStackIngredient item(ItemLike item, int amount) {
		return IngredientCreatorAccess.item().from(item, amount);
	}

	@RemapForJS("itemStack")
	public ItemStackIngredient itemStack(ItemStack stack) {
		return IngredientCreatorAccess.item().from(stack);
	}

	@RemapForJS("itemStackAmount")
	public ItemStackIngredient itemStackAmount(ItemStack stack, int amount) {
		return IngredientCreatorAccess.item().from(stack, amount);
	}

	@RemapForJS("itemIngredient")
	public ItemStackIngredient itemIngredient(Ingredient ingredient, int amount) {
		return IngredientCreatorAccess.item().from(ingredient, amount);
	}

	@RemapForJS("itemId")
	public ItemStackIngredient itemId(ResourceLocation itemId, int amount) {
		return item(resolveItem(itemId), amount);
	}

	@RemapForJS("itemTag")
	public ItemStackIngredient itemTag(TagKey<Item> tag, int amount) {
		return IngredientCreatorAccess.item().from(tag, amount);
	}

	@RemapForJS("itemTagId")
	public ItemStackIngredient itemTagId(ResourceLocation tag, int amount) {
		return itemTag(TagKey.create(Registries.ITEM, tag), amount);
	}

	@RemapForJS("multiFluid")
	public FluidStackIngredient multiFluid(FluidStackIngredient... ingredients) {
		return IngredientCreatorAccess.fluid().createMulti(ingredients);
	}

	@RemapForJS("multiGas")
	public ChemicalStackIngredient.GasStackIngredient multiGas(ChemicalStackIngredient.GasStackIngredient... ingredients) {
		return IngredientCreatorAccess.gas().createMulti(ingredients);
	}

	@RemapForJS("multiInfusion")
	public ChemicalStackIngredient.InfusionStackIngredient multiInfusion(ChemicalStackIngredient.InfusionStackIngredient... ingredients) {
		return IngredientCreatorAccess.infusion().createMulti(ingredients);
	}

	@RemapForJS("multiPigment")
	public ChemicalStackIngredient.PigmentStackIngredient multiPigment(ChemicalStackIngredient.PigmentStackIngredient... ingredients) {
		return IngredientCreatorAccess.pigment().createMulti(ingredients);
	}

	@RemapForJS("multiSlurry")
	public ChemicalStackIngredient.SlurryStackIngredient multiSlurry(ChemicalStackIngredient.SlurryStackIngredient... ingredients) {
		return IngredientCreatorAccess.slurry().createMulti(ingredients);
	}

	@RemapForJS("multiItem")
	public ItemStackIngredient multiItem(ItemStackIngredient... ingredients) {
		return IngredientCreatorAccess.item().createMulti(ingredients);
	}

	@RemapForJS("gasOutput")
	public String gasOutput(ResourceLocation gasId, long amount) {
		resolveGas(gasId);

		return outputValue(gasId, amount);
	}

	@RemapForJS("infusionOutput")
	public String infusionOutput(ResourceLocation infusionId, long amount) {
		resolveInfuseType(infusionId);

		return outputValue(infusionId, amount);
	}

	@RemapForJS("pigmentOutput")
	public String pigmentOutput(ResourceLocation pigmentId, long amount) {
		resolvePigment(pigmentId);

		return outputValue(pigmentId, amount);
	}

	@RemapForJS("slurryOutput")
	public String slurryOutput(ResourceLocation slurryId, long amount) {
		resolveSlurry(slurryId);

		return outputValue(slurryId, amount);
	}

	private Fluid resolveFluid(ResourceLocation id) {
		Fluid fluid = ForgeRegistries.FLUIDS.getValue(id);

		if (fluid == null || fluid == Fluids.EMPTY) {
			throw new IllegalArgumentException("Unknown fluid: " + id);
		}

		return fluid;
	}

	private Gas resolveGas(ResourceLocation id) {
		Gas gas = Gas.getFromRegistry(id);

		if (gas == null || gas.isEmptyType()) {
			throw new IllegalArgumentException("Unknown gas: " + id);
		}

		return gas;
	}

	private InfuseType resolveInfuseType(ResourceLocation id) {
		InfuseType type = InfuseType.getFromRegistry(id);

		if (type == null || type.isEmptyType()) {
			throw new IllegalArgumentException("Unknown infuse type: " + id);
		}

		return type;
	}

	private Pigment resolvePigment(ResourceLocation id) {
		Pigment pigment = Pigment.getFromRegistry(id);

		if (pigment == null || pigment.isEmptyType()) {
			throw new IllegalArgumentException("Unknown pigment: " + id);
		}

		return pigment;
	}

	private Slurry resolveSlurry(ResourceLocation id) {
		Slurry slurry = Slurry.getFromRegistry(id);

		if (slurry == null || slurry.isEmptyType()) {
			throw new IllegalArgumentException("Unknown slurry: " + id);
		}

		return slurry;
	}

	private Item resolveItem(ResourceLocation id) {
		Item item = ForgeRegistries.ITEMS.getValue(id);

		if (item == null || item == Items.AIR) {
			throw new IllegalArgumentException("Unknown item: " + id);
		}

		return item;
	}

	public String outputValue(ResourceLocation id, long amount) {
		return amount + "x " + id;
	}
}