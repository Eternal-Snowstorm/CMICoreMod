package dev.celestiacraft.cmi.mixin.kubejs;

import dev.latvian.mods.kubejs.client.GeneratedClientResourcePack;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

@Mixin(value = GeneratedClientResourcePack.class, remap = false)
public class GeneratedClientResourcePackMixin {
	@Inject(method = "inject", at = @At("RETURN"), cancellable = true)
	private static void cmi$inject(Minecraft client, List<PackResources> packs, CallbackInfoReturnable<List<PackResources>> cir) {
		List<PackResources> original = cir.getReturnValue();

		if (original == null || original == packs || original.size() < 2) {
			return;
		}

		Set<PackResources> injectedBefore = Collections.newSetFromMap(new IdentityHashMap<>());
		injectedBefore.addAll(packs);

		List<PackResources> kubejs = new ArrayList<>(2);
		List<PackResources> rest = new ArrayList<>(original.size());

		for (PackResources pack : original) {
			if (pack != null && !injectedBefore.contains(pack)) {
				kubejs.add(pack);
			} else {
				rest.add(pack);
			}
		}

		if (kubejs.isEmpty() || rest.isEmpty()) {
			return;
		}

		rest.addAll(kubejs);
		cir.setReturnValue(rest);
	}
}