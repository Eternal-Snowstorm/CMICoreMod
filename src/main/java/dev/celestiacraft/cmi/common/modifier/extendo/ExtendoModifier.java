package dev.celestiacraft.cmi.common.modifier.extendo;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Portions derived from Tinkers' Gears, Copyright (c) 2026 Chemiofitor4096.
 * <p>
 * Source: <a href="https://github.com/Chemiofitor4096/TinkersGears">
 * https://github.com/Chemiofitor4096/TinkersGears
 * </a>
 * <p>
 * Licensed under the MIT License; see the project LICENSE for the full license text.
 */
public class ExtendoModifier extends NoLevelsModifier implements EquipmentChangeModifierHook {
	public static final String EXTENDO_MARKER = "createExtendo";

	public static final AttributeModifier BLOCK_MODIFIER = new AttributeModifier(
			UUID.fromString("505501b0-7368-498e-89c3-1723ef0f73e6"), "Tinker Block Range modifier", 3,
			AttributeModifier.Operation.ADDITION
	);

	public static final AttributeModifier ENTITY_MODIFIER = new AttributeModifier(
			UUID.fromString("23ee415e-d319-9cac-2cb0-d04a637d5876"), "Tinker Entity Range modifier", 3,
			AttributeModifier.Operation.ADDITION
	);

	private static final Supplier<Multimap<Attribute, AttributeModifier>> BLOCK_MEMOIZED = Suppliers.memoize(() -> {
		return ImmutableMultimap.of(ForgeMod.BLOCK_REACH.get(), BLOCK_MODIFIER);
	});

	private static final Supplier<Multimap<Attribute, AttributeModifier>> ENTITY_MEMOIZED = Suppliers.memoize(() -> {
		return ImmutableMultimap.of(ForgeMod.ENTITY_REACH.get(), ENTITY_MODIFIER);
	});

	@Override
	protected void registerHooks(ModuleHookMap.@NotNull Builder builder) {
		super.registerHooks(builder);
		builder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
	}

	@Override
	public void onEquip(@NotNull IToolStackView view, @NotNull ModifierEntry entry, EquipmentChangeContext context) {
		if (context.getEntity() instanceof Player player) {
			AttributeMap attributes = player.getAttributes();

			attributes.addTransientAttributeModifiers(BLOCK_MEMOIZED.get());
			attributes.addTransientAttributeModifiers(ENTITY_MEMOIZED.get());
			player.getPersistentData().putBoolean(EXTENDO_MARKER, true);
		}
	}

	@Override
	public void onUnequip(@NotNull IToolStackView view, @NotNull ModifierEntry entry, EquipmentChangeContext context) {
		if (context.getEntity() instanceof Player player) {
			AttributeMap attributes = player.getAttributes();

			attributes.removeAttributeModifiers(BLOCK_MEMOIZED.get());
			attributes.removeAttributeModifiers(ENTITY_MEMOIZED.get());
			player.getPersistentData().remove(EXTENDO_MARKER);
		}
	}
}