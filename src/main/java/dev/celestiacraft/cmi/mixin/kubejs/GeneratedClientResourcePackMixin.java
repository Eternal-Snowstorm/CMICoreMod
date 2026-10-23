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

/**
 * KubeJS 2001.6.5 的 {@code GeneratedClientResourcePack#inject} 把 KubeJS 资源包插到
 * "第一个 FilePackResources（磁盘资源包）之前"，即永远低于所有用户资源包。
 * <p>
 * 结果就是 {@code kubejs/assets/**} 的静态资源与 {@code ClientEvents.lang} 生成的语言条目，
 * 都会被 resourcepacks / Paxi 里任何优先级更高的资源包顶掉。
 * <p>
 * 这里在 inject 返回后重排：把 KubeJS 注入的整块包（生成包 + kubejs/assets 下的 zip）挪到列表末尾。
 * MC 的 packs 列表顺序即优先级，越靠后覆盖力越强，因此 KubeJS 资源会变成最高优先级。
 * <p>
 * 识别方式是拿 inject 的入参 packs 与返回值做引用差集 —— 凡是"返回里有、入参里没有"的元素，
 * 就是 KubeJS 这次注入的，既不依赖具体类名也不依赖 FilePackResources#getName。
 * <p>
 * 依赖 KubeJS 内部实现，升级 KubeJS 后需确认 {@code inject(Minecraft, List)} 仍然存在。
 */
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