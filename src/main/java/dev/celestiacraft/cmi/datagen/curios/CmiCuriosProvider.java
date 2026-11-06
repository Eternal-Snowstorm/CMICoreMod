package dev.celestiacraft.cmi.datagen.curios;

import dev.celestiacraft.cmi.Cmi;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosDataProvider;

import java.util.concurrent.CompletableFuture;

public class CmiCuriosProvider extends CuriosDataProvider {
	public CmiCuriosProvider(PackOutput output, ExistingFileHelper helper, CompletableFuture<HolderLookup.Provider> registries) {
		super(Cmi.MODID, output, helper, registries);
	}

	@Override
	public void generate(HolderLookup.Provider provider, ExistingFileHelper helper) {
		addPlayerGenerate("mechanisms", Cmi.loadResource("slot/mechanisms"));
	}

	private void addPlayerGenerate(String slot, ResourceLocation icon) {
		createSlot(slot)
				.size(2)
				.icon(icon)
				.replace(false);

		createEntities("player")
				.addPlayer()
				.addSlots(slot);
	}
}