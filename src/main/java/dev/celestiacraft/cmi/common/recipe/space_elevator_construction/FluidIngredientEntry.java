package dev.celestiacraft.cmi.common.recipe.space_elevator_construction;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public final class FluidIngredientEntry {
	@Nullable
	private final ResourceLocation fluidId;
	@Nullable
	private final TagKey<Fluid> tag;
	private final int amount;

	private FluidIngredientEntry(@Nullable ResourceLocation fluidId, @Nullable TagKey<Fluid> tag, int amount) {
		this.fluidId = fluidId;
		this.tag = tag;
		this.amount = amount;
	}

	public static FluidIngredientEntry ofFluid(ResourceLocation fluidId, int amount) {
		return new FluidIngredientEntry(fluidId, null, amount);
	}

	public static FluidIngredientEntry ofTag(TagKey<Fluid> tag, int amount) {
		return new FluidIngredientEntry(null, tag, amount);
	}

	public int amount() {
		return amount;
	}

	public boolean test(FluidStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		if (fluidId != null) {
			ResourceLocation key = ForgeRegistries.FLUIDS.getKey(stack.getFluid());
			return fluidId.equals(key);
		}
		if (tag != null) {
			return stack.getFluid().is(tag);
		}
		return false;
	}

	public FluidStack displayStack() {
		Fluid fluid = resolveDisplayFluid();
		return fluid == null ? FluidStack.EMPTY : new FluidStack(fluid, amount);
	}

	@Nullable
	private Fluid resolveDisplayFluid() {
		if (fluidId != null) {
			return ForgeRegistries.FLUIDS.getValue(fluidId);
		}
		if (tag != null) {
			return ForgeRegistries.FLUIDS.tags().getTag(tag).stream().findFirst().orElse(null);
		}
		return null;
	}

	public static FluidIngredientEntry fromJson(JsonObject json) {
		if (!json.has("amount")) {
			throw new JsonParseException("Fluid ingredient must define amount");
		}
		int amount = json.get("amount").getAsInt();
		if (amount <= 0) {
			throw new JsonParseException("Fluid ingredient amount must be positive");
		}
		if (json.has("fluid")) {
			ResourceLocation fluidId = ResourceLocation.parse(json.get("fluid").getAsString());
			return ofFluid(fluidId, amount);
		}
		if (json.has("tag")) {
			TagKey<Fluid> tag = TagKey.create(Registries.FLUID, ResourceLocation.parse(json.get("tag").getAsString()));
			return ofTag(tag, amount);
		}
		throw new JsonParseException("Fluid ingredient must define fluid or tag");
	}

	public void toNetwork(FriendlyByteBuf buf) {
		buf.writeVarInt(amount);
		if (fluidId != null) {
			buf.writeByte(0);
			buf.writeResourceLocation(fluidId);
		} else if (tag != null) {
			buf.writeByte(1);
			buf.writeResourceLocation(tag.location());
		} else {
			buf.writeByte(2);
		}
	}

	public static FluidIngredientEntry fromNetwork(FriendlyByteBuf buf) {
		int amount = buf.readVarInt();
		byte type = buf.readByte();
		return switch (type) {
			case 0 -> ofFluid(buf.readResourceLocation(), amount);
			case 1 -> ofTag(TagKey.create(Registries.FLUID, buf.readResourceLocation()), amount);
			default -> new FluidIngredientEntry(null, null, amount);
		};
	}
}
