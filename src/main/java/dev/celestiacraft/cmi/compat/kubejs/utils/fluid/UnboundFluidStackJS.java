package dev.celestiacraft.cmi.compat.kubejs.utils.fluid;

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
	private CompoundTag nbt;
	private FluidStack cached;

	// 标记是否为 tag 模式
	private final boolean isTag;
	// "tag" 或 "fluidTag"
	@Nullable
	private final String tagType;

	// 普通流体构造
	public UnboundFluidStackJS(ResourceLocation location) {
		this.fluidRL = location;
		this.fluid = location.toString();
		this.amount = FluidStack.bucketAmount();
		this.nbt = null;
		this.cached = null;
		this.isTag = false;
		this.tagType = null;
	}

	// tag 构造
	public UnboundFluidStackJS(ResourceLocation location, @Nullable String tagType) {
		this.fluidRL = location;
		this.fluid = location.toString();
		this.amount = FluidStack.bucketAmount();
		this.nbt = null;
		this.cached = null;
		this.isTag = true;
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
	public void setAmount(long a) {
		amount = a;
		cached = null;
	}

	@Override
	@Nullable
	public CompoundTag getNbt() {
		return nbt;
	}

	@Override
	public void setNbt(@Nullable CompoundTag n) {
		nbt = n;
		cached = null;
	}

	@Override
	public FluidStackJS kjs$copy(long amount) {
		if (isTag) {
			var fs = new UnboundFluidStackJS(fluidRL, tagType);
			fs.amount = amount;
			fs.nbt = nbt == null ? null : nbt.copy();
			return fs;
		} else {
			var fs = new UnboundFluidStackJS(fluidRL);
			fs.amount = amount;
			fs.nbt = nbt == null ? null : nbt.copy();
			return fs;
		}
	}

	@Override
	public JsonObject toJson() {
		JsonObject o = new JsonObject();
		o.addProperty("amount", amount);

		if (isTag) {
			o.addProperty("tag", fluidRL.toString());
		} else {
			o.addProperty("fluid", fluidRL.toString());
		}

		if (nbt != null && !nbt.isEmpty()) {
			o.addProperty("nbt", nbt.toString());
		}

		return o;
	}
}