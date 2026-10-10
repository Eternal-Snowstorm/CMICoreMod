package dev.celestiacraft.cmi.compat.kubejs.modify.fluid;

import com.google.gson.JsonObject;
import dev.architectury.fluid.FluidStack;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class UnboundFluidStackJS extends FluidStackJS {
	private final ResourceLocation fluidRL;
	private final String fluid;
	private long amount;
	private @Nullable CompoundTag nbt;
	private @Nullable FluidStack cached;

	// 标记是否为 tag 模式
	private final boolean isTag;
	// "tag" 或 "fluidTag"
	@Nullable
	private final String tagType;

	// 普通流体构造
	public UnboundFluidStackJS(ResourceLocation location) {
		fluidRL = location;
		fluid = location.toString();
		amount = FluidStack.bucketAmount();
		nbt = null;
		cached = null;
		isTag = false;
		tagType = null;
	}

	// tag 构造
	public UnboundFluidStackJS(ResourceLocation location, @Nullable String tagType) {
		fluidRL = location;
		fluid = location.toString();
		amount = FluidStack.bucketAmount();
		nbt = null;
		cached = null;
		isTag = true;
		this.tagType = tagType;
	}

	@Override
	public String getId() {
		return fluid;
	}

	@Override
	public boolean kjs$isEmpty() {
		return super.kjs$isEmpty() || (!isTag && getFluid() == Fluids.EMPTY);
	}

	@Override
	public FluidStack getFluidStack() {
		if (isTag) {
			return FluidStack.empty();
		}

		if (cached == null) {
			cached = FluidStack.create(this::getFluid, amount, nbt);
		}
		return cached;
	}

	@Override
	public long kjs$getAmount() {
		return amount;
	}

	@Override
	public void setAmount(long amount) {
		this.amount = amount;
		cached = null;
	}

	@Override
	@Nullable
	public CompoundTag getNbt() {
		return nbt;
	}

	@Override
	public void setNbt(@Nullable CompoundTag nbt) {
		this.nbt = nbt;
		cached = null;
	}

	@Override
	public FluidStackJS kjs$copy(long amount) {
		UnboundFluidStackJS fluidStack;
		if (isTag) {
			fluidStack = new UnboundFluidStackJS(fluidRL, tagType);
		} else {
			fluidStack = new UnboundFluidStackJS(fluidRL);
		}
		fluidStack.amount = amount;
		fluidStack.nbt = nbt == null ? null : nbt.copy();
		return fluidStack;
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("amount", amount);

		if (isTag) {
			json.addProperty("tag", fluidRL.toString());
		} else {
			json.addProperty("fluid", fluidRL.toString());
		}

		if (nbt != null && !nbt.isEmpty()) {
			json.addProperty("nbt", nbt.toString());
		}

		return json;
	}
}