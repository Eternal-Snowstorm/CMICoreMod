package dev.celestiacraft.cmi.compat.adastra;

import com.teamresourceful.resourcefullib.common.utils.SaveHandler;
import dev.celestiacraft.cmi.common.recipe.space_elevator_construction.FluidIngredientEntry;
import dev.celestiacraft.cmi.common.recipe.space_elevator_construction.SpaceElevatorConstructionRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpaceElevatorMaterialStorage extends SaveHandler {
	private static final String ENTRIES_KEY = "Entries";
	private static final String ANCHOR_POS_KEY = "AnchorPos";
	private static final String COUNTS_KEY = "Counts";
	private static final String FLUID_COUNTS_KEY = "FluidCounts";

	private final Map<Long, List<Integer>> storedMaterials = new HashMap<>();
	private final Map<Long, List<Integer>> storedFluids = new HashMap<>();

	@Override
	public void loadData(CompoundTag tag) {
		storedMaterials.clear();
		storedFluids.clear();
		ListTag entryTags = tag.getList(ENTRIES_KEY, Tag.TAG_COMPOUND);
		for (Tag value : entryTags) {
			CompoundTag entryTag = (CompoundTag) value;
			long anchorKey = NbtUtils.readBlockPos(entryTag.getCompound(ANCHOR_POS_KEY)).asLong();
			storedMaterials.put(anchorKey, readIntList(entryTag, COUNTS_KEY));
			if (entryTag.contains(FLUID_COUNTS_KEY)) {
				storedFluids.put(anchorKey, readIntList(entryTag, FLUID_COUNTS_KEY));
			}
		}
	}

	@Override
	public void saveData(CompoundTag tag) {
		ListTag entryTags = new ListTag();
		java.util.Set<Long> keys = new java.util.HashSet<>();
		keys.addAll(storedMaterials.keySet());
		keys.addAll(storedFluids.keySet());
		for (Long key : keys) {
			CompoundTag entryTag = new CompoundTag();
			entryTag.put(ANCHOR_POS_KEY, NbtUtils.writeBlockPos(BlockPos.of(key)));
			List<Integer> itemCounts = storedMaterials.getOrDefault(key, List.of());
			entryTag.put(COUNTS_KEY, writeIntList(itemCounts));
			List<Integer> fluidCounts = storedFluids.getOrDefault(key, List.of());
			if (!fluidCounts.isEmpty()) {
				entryTag.put(FLUID_COUNTS_KEY, writeIntList(fluidCounts));
			}
			entryTags.add(entryTag);
		}
		tag.put(ENTRIES_KEY, entryTags);
	}

	private static List<Integer> readIntList(CompoundTag tag, String key) {
		ListTag listTag = tag.getList(key, Tag.TAG_INT);
		List<Integer> result = new ArrayList<>(listTag.size());
		for (Tag t : listTag) {
			result.add(((IntTag) t).getAsInt());
		}
		return result;
	}

	private static ListTag writeIntList(List<Integer> values) {
		ListTag listTag = new ListTag();
		for (Integer value : values) {
			listTag.add(IntTag.valueOf(value));
		}
		return listTag;
	}

	@Override
	public boolean isDirty() {
		return true;
	}

	public static SpaceElevatorMaterialStorage read(ServerLevel level) {
		return read(level.getDataStorage(), SpaceElevatorMaterialStorage::new, "cmi_space_elevator_materials");
	}

	public static int[] getStoredCounts(ServerLevel level, BlockPos anchorPos, int size) {
		List<Integer> counts = read(level).getOrCreate(read(level).storedMaterials, anchorPos, size);
		return toArray(counts, size);
	}

	public static int[] getStoredFluidAmounts(ServerLevel level, BlockPos anchorPos, int size) {
		List<Integer> counts = read(level).getOrCreate(read(level).storedFluids, anchorPos, size);
		return toArray(counts, size);
	}

	private static int[] toArray(List<Integer> source, int size) {
		int[] result = new int[size];
		for (int i = 0; i < size; i++) {
			result[i] = i < source.size() ? source.get(i) : 0;
		}
		return result;
	}

	public static boolean hasAllMaterials(ServerLevel level, BlockPos anchorPos, SpaceElevatorConstructionRecipe recipe) {
		int[] itemCounts = getStoredCounts(level, anchorPos, recipe.ingredients().size());
		for (int i = 0; i < recipe.ingredients().size(); i++) {
			if (itemCounts[i] < recipe.ingredients().get(i).count()) {
				return false;
			}
		}
		int[] fluidCounts = getStoredFluidAmounts(level, anchorPos, recipe.fluidIngredients().size());
		for (int i = 0; i < recipe.fluidIngredients().size(); i++) {
			if (fluidCounts[i] < recipe.fluidIngredients().get(i).amount()) {
				return false;
			}
		}
		return true;
	}

	public static void clear(ServerLevel level, BlockPos anchorPos) {
		SpaceElevatorMaterialStorage storage = read(level);
		storage.storedMaterials.remove(anchorPos.asLong());
		storage.storedFluids.remove(anchorPos.asLong());
	}

	public static StoreResult storeFromItemHandler(ServerLevel level, BlockPos anchorPos, SpaceElevatorConstructionRecipe recipe, IItemHandlerModifiable handler) {
		SpaceElevatorMaterialStorage storage = read(level);
		List<Integer> counts = storage.getOrCreate(storage.storedMaterials, anchorPos, recipe.ingredients().size());
		boolean movedAny = false;

		for (int ingredientIndex = 0; ingredientIndex < recipe.ingredients().size(); ingredientIndex++) {
			SpaceElevatorConstructionRecipe.IngredientEntry entry = recipe.ingredients().get(ingredientIndex);
			int stored = ingredientIndex < counts.size() ? counts.get(ingredientIndex) : 0;
			int remaining = Math.max(0, entry.count() - stored);
			if (remaining <= 0) {
				continue;
			}

			for (int slot = 0; slot < handler.getSlots() && remaining > 0; slot++) {
				ItemStack stack = handler.getStackInSlot(slot);
				if (stack.isEmpty() || !entry.ingredient().test(stack)) {
					continue;
				}

				int desired = Math.min(remaining, stack.getCount());
				ItemStack extracted = handler.extractItem(slot, desired, false);
				if (extracted.isEmpty()) {
					continue;
				}
				int actuallyMoved = extracted.getCount();
				remaining -= actuallyMoved;
				stored += actuallyMoved;
				movedAny = true;
			}

			counts.set(ingredientIndex, stored);
		}

		if (!movedAny) {
			return StoreResult.NOTHING_TO_STORE;
		}
		return hasAllMaterials(level, anchorPos, recipe) ? StoreResult.COMPLETE : StoreResult.STORED;
	}

	public static StoreResult storeFromFluidHandler(ServerLevel level, BlockPos anchorPos, SpaceElevatorConstructionRecipe recipe, IFluidHandler handler) {
		if (recipe.fluidIngredients().isEmpty()) {
			return StoreResult.NOTHING_TO_STORE;
		}
		SpaceElevatorMaterialStorage storage = read(level);
		List<Integer> counts = storage.getOrCreate(storage.storedFluids, anchorPos, recipe.fluidIngredients().size());
		boolean movedAny = false;

		for (int ingredientIndex = 0; ingredientIndex < recipe.fluidIngredients().size(); ingredientIndex++) {
			FluidIngredientEntry entry = recipe.fluidIngredients().get(ingredientIndex);
			int stored = ingredientIndex < counts.size() ? counts.get(ingredientIndex) : 0;
			int remaining = Math.max(0, entry.amount() - stored);
			if (remaining <= 0) {
				continue;
			}

			for (int tank = 0; tank < handler.getTanks() && remaining > 0; tank++) {
				FluidStack tankStack = handler.getFluidInTank(tank);
				if (tankStack.isEmpty() || !entry.test(tankStack)) {
					continue;
				}

				int desired = Math.min(remaining, tankStack.getAmount());
				FluidStack drainTarget = new FluidStack(tankStack, desired);
				FluidStack drained = handler.drain(drainTarget, IFluidHandler.FluidAction.EXECUTE);
				if (drained.isEmpty()) {
					continue;
				}
				int moved = drained.getAmount();
				remaining -= moved;
				stored += moved;
				movedAny = true;
			}

			counts.set(ingredientIndex, stored);
		}

		if (!movedAny) {
			return StoreResult.NOTHING_TO_STORE;
		}
		return hasAllMaterials(level, anchorPos, recipe) ? StoreResult.COMPLETE : StoreResult.STORED;
	}

	private List<Integer> getOrCreate(Map<Long, List<Integer>> source, BlockPos anchorPos, int size) {
		List<Integer> counts = source.computeIfAbsent(anchorPos.asLong(), key -> new ArrayList<>());
		while (counts.size() < size) {
			counts.add(0);
		}
		if (counts.size() > size) {
			counts.subList(size, counts.size()).clear();
		}
		return counts;
	}

	public enum StoreResult {
		STORED("text.cmi.space_elevator.store_success"),
		COMPLETE("text.cmi.space_elevator.store_complete"),
		NOTHING_TO_STORE("text.cmi.space_elevator.store_nothing");

		private final String translationKey;

		StoreResult(String translationKey) {
			this.translationKey = translationKey;
		}

		public String translationKey() {
			return translationKey;
		}
	}
}
