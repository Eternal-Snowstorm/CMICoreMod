package dev.celestiacraft.cmi.common.register;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CmiArmorMaterial implements ArmorMaterial {
	private final String name;
	private final int maxDamageFactor;
	private final int[] defense;
	private final int enchantability;
	private final Supplier<SoundEvent> soundEvent;
	private final float toughness;
	private final float knockbackResistance;
	private final Supplier<Ingredient> material;

	// 原版基础耐久
	private static final int[] BASE_DURABILITY = new int[]{11, 16, 15, 13};

	private CmiArmorMaterial(String name, int maxDamageFactor, int[] defense, int enchantability, Supplier<SoundEvent> sound, float toughness, float knockbackResistance, Supplier<Ingredient> material) {
		this.name = name;
		this.maxDamageFactor = maxDamageFactor;
		this.defense = defense;
		this.enchantability = enchantability;
		this.soundEvent = sound;
		this.toughness = toughness;
		this.knockbackResistance = knockbackResistance;
		this.material = material;
	}

	@Override
	public int getDurabilityForType(ArmorItem.@NotNull Type type) {
		return BASE_DURABILITY[type.ordinal()] * this.maxDamageFactor;
	}

	@Override
	public int getDefenseForType(ArmorItem.@NotNull Type type) {
		return defense[type.ordinal()];
	}

	@Override
	public int getEnchantmentValue() {
		return enchantability;
	}

	@Override
	public @NotNull SoundEvent getEquipSound() {
		return soundEvent.get();
	}

	@Override
	public @NotNull Ingredient getRepairIngredient() {
		return material.get();
	}

	@Override
	public @NotNull String getName() {
		return "cmi:" + name;
	}

	@Override
	public float getToughness() {
		return toughness;
	}

	@Override
	public float getKnockbackResistance() {
		return knockbackResistance;
	}

	private static int[] armor(int helmet, int chestplate, int leggings, int boots) {
		return new int[]{boots, leggings, chestplate, helmet};
	}
}