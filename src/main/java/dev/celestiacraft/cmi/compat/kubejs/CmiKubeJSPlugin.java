package dev.celestiacraft.cmi.compat.kubejs;

import com.jesz.createdieselgenerators.CreateDieselGenerators;
import com.lowdragmc.lowdraglib.client.renderer.impl.IModelRenderer;
import com.lowdragmc.lowdraglib.gui.widget.TextTextureWidget;
import com.lowdragmc.mbd2.api.block.RotationState;
import com.lowdragmc.mbd2.api.capability.recipe.IO;
import com.lowdragmc.mbd2.api.pattern.FactoryBlockPattern;
import com.lowdragmc.mbd2.api.pattern.MultiblockShapeInfo;
import com.lowdragmc.mbd2.api.pattern.Predicates;
import com.lowdragmc.mbd2.api.pattern.TraceabilityPredicate;
import com.lowdragmc.mbd2.api.pattern.predicates.PredicateBlocks;
import com.lowdragmc.mbd2.api.pattern.predicates.PredicateTags;
import com.lowdragmc.mbd2.api.pattern.util.RelativeDirection;
import com.lowdragmc.mbd2.api.recipe.content.ContentModifier;
import com.lowdragmc.mbd2.common.gui.editor.multiblock.MultiblockShapeInfoPanel;
import com.lowdragmc.mbd2.common.machine.definition.MultiblockMachineDefinition;
import com.lowdragmc.mbd2.common.machine.definition.config.*;
import com.lowdragmc.mbd2.common.machine.definition.config.toggle.ToggleCreativeTab;
import com.lowdragmc.mbd2.common.machine.definition.config.toggle.ToggleMachineSound;
import com.lowdragmc.mbd2.common.machine.definition.config.toggle.ToggleRenderer;
import com.lowdragmc.mbd2.common.trait.AutoWorldIO;
import com.lowdragmc.mbd2.common.trait.ToggleAutoIO;
import com.lowdragmc.mbd2.common.trait.fluid.FluidFilterSettings;
import com.lowdragmc.mbd2.common.trait.fluid.FluidTankCapabilityTrait;
import com.lowdragmc.mbd2.common.trait.fluid.FluidTankCapabilityTraitDefinition;
import com.lowdragmc.mbd2.common.trait.forgeenergy.ForgeEnergyCapabilityTrait;
import com.lowdragmc.mbd2.common.trait.forgeenergy.ForgeEnergyCapabilityTraitDefinition;
import com.lowdragmc.mbd2.common.trait.item.ItemFilterSettings;
import com.lowdragmc.mbd2.common.trait.item.ItemSlotCapabilityTrait;
import com.lowdragmc.mbd2.common.trait.item.ItemSlotCapabilityTraitDefinition;
import com.lowdragmc.mbd2.integration.mekanism.trait.chemical.ChemicalTankCapabilityTraitDefinition;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.api.client.CmiLang;
import dev.celestiacraft.cmi.compat.create.CmiHeatLevel;
import dev.celestiacraft.cmi.compat.create.CreateFluidIngredient;
import dev.celestiacraft.cmi.compat.kubejs.custom.item.CdgCuttersItemBuilder;
import dev.celestiacraft.cmi.compat.kubejs.custom.item.CdgHammerItemBuilder;
import dev.celestiacraft.cmi.compat.kubejs.recipe.*;
import dev.celestiacraft.cmi.compat.kubejs.recipe.cdg.CdgRecipesSchema;
import dev.celestiacraft.cmi.compat.ldlib.LDLibHelpers;
import dev.celestiacraft.cmi.compat.mbd2.MBDFluidIngredient;
import dev.celestiacraft.cmi.compat.mbd2.MBDHelpers;
import dev.celestiacraft.cmi.network.ClientSeedHandler;
import dev.celestiacraft.cmi.utils.CmiGlobal;
import dev.celestiacraft.cmi.utils.metal.CmiMetal;
import dev.celestiacraft.cmi.utils.tool.CmiMiningLevel;
import dev.celestiacraft.cmi.utils.tool.CmiToolType;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.BindingsEvent;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class CmiKubeJSPlugin extends KubeJSPlugin {
	@Override
	public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
		event.namespace(Cmi.MODID)
				.register("freezing", FreezingSchema.SCHEMA)
				.register("space_elevator_base", SpaceElevatorBaseSchema.SCHEMA)
				.register("space_elevator_construction", SpaceElevatorConstructionSchema.SCHEMA)
				.register("accelerator", AcceleratorSchema.SCHEMA)
				.register("test_coke_oven", MachineRecipeSchema.SCHEMA)
				.register("grinding", GrindingSchema.SCHEMA)
				.register("fluid_burn", FluidBurnSchema.SCHEMA);

		event.namespace(CreateDieselGenerators.ID)
				.register("basin_fermenting", CdgRecipesSchema.BASIN)
				.register("distillation", CdgRecipesSchema.DISTILLATION)
				.register("bulk_fermenting", CdgRecipesSchema.BULK)
				.register("casting", CdgRecipesSchema.CASTING)
				.register("compression_molding", CdgRecipesSchema.COMPRESSION)
				.register("hammering", CdgRecipesSchema.HAMMERING)
				.register("wire_cutting", CdgRecipesSchema.CUTTING);
	}

	@Override
	public void registerBindings(BindingsEvent event) {
		event.add("IntStream", IntStream.class);
		event.add("JavaArray", Array.class);
		event.add("JavaArrays", Arrays.class);

		bindCmi(event);
		bindCmiMBD(event);
		bindMBD(event);
		bindLDLib(event);
		bindCmiLDLib(event);
	}

	private void bindCmi(BindingsEvent event) {
		event.add("Cmi", Cmi.class);
		event.add("CmiLang", CmiLang.class);
		event.add("CmiLang$JeiLang", CmiLang.JeiLang.class);
		event.add("ClientSeedHandler", ClientSeedHandler.class);
		event.add("CmiHeatLevel", CmiHeatLevel.class);
		event.add("CmiGlobal", CmiGlobal.class);
		event.add("CmiMetal", CmiMetal.class);
		event.add("CmiToolType", CmiToolType.class);
		event.add("CmiMiningLevel", CmiMiningLevel.class);
		event.add("CreateFluidIngredient", CreateFluidIngredient.class);
	}

	private void bindMBD(BindingsEvent event) {
		List<Class<?>> mbdClasses = List.of(
				MachineState.class,
				MultiblockShapeInfo.class,
				MultiblockShapeInfoPanel.class,
				ConfigBlockProperties.class,
				ConfigItemProperties.class,
				ConfigMachineSettings.class,
				ConfigMultiblockSettings.class,
				ConfigRecipeLogicSettings.class,
				ConfigPartSettings.class,
				ConfigPartSettings.ProxyCapability.class,
				RotationState.class,
				ItemSlotCapabilityTraitDefinition.class,
				FluidTankCapabilityTraitDefinition.class,
				ForgeEnergyCapabilityTraitDefinition.class,
				ItemSlotCapabilityTrait.class,
				FluidTankCapabilityTrait.class,
				ForgeEnergyCapabilityTrait.class,
				ChemicalTankCapabilityTraitDefinition.class,
				ChemicalTankCapabilityTraitDefinition.Gas.class,
				ChemicalTankCapabilityTraitDefinition.Infuse.class,
				ChemicalTankCapabilityTraitDefinition.Pigment.class,
				ChemicalTankCapabilityTraitDefinition.Slurry.class,
				IO.class,
				AutoWorldIO.class,
				ToggleAutoIO.class,
				ItemFilterSettings.class,
				FluidFilterSettings.class,
				RecipeModifier.class,
				ContentModifier.class,
				StateMachine.class,
				FactoryBlockPattern.class,
				Predicates.class,
				ToggleCreativeTab.class,
				ToggleRenderer.class,
				IModelRenderer.class,
				ToggleMachineSound.class,
				RelativeDirection.class,
				PredicateTags.class,
				MultiblockMachineDefinition.class,
				TraceabilityPredicate.class,
				PredicateBlocks.class
		);
		mbdClasses.forEach((clazz) -> {
			String name = clazz.getSimpleName();

			if (clazz.getEnclosingClass() != null) {
				name = clazz.getEnclosingClass().getSimpleName() + "$" + name;
			}

			event.add(name, clazz);
		});
	}

	private void bindLDLib(BindingsEvent event) {
		List<Class<?>> ldlibClasses = List.of(
				TextTextureWidget.class
		);
		ldlibClasses.forEach((clazz) -> {
			String name = clazz.getSimpleName();

			if (clazz.getEnclosingClass() != null) {
				name = clazz.getEnclosingClass().getSimpleName() + "$" + name;
			}

			event.add(name, clazz);
		});
	}

	private void bindCmiMBD(BindingsEvent event) {
		event.add("MBDFluidIngredient", MBDFluidIngredient.class);
		event.add("MBDHelpers", MBDHelpers.class);
	}

	private void bindCmiLDLib(BindingsEvent event) {
		event.add("LDLibHelpers", LDLibHelpers.class);
	}

	@Override
	public void init() {
		RegistryInfo.ITEM.addType(
				CreateDieselGenerators.rl("hammer").toString(),
				CdgHammerItemBuilder.class,
				CdgHammerItemBuilder::new
		);
		RegistryInfo.ITEM.addType(
				CreateDieselGenerators.rl("wire_cutter").toString(),
				CdgCuttersItemBuilder.class,
				CdgCuttersItemBuilder::new
		);
	}
}