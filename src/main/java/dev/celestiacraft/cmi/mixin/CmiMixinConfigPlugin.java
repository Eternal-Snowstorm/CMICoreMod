package dev.celestiacraft.cmi.mixin;

import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * 仅为 MBD2 的 KubeJS 修补提供守卫。
 * <p>
 * MBDKubeJSPlugin 的父类是 dev.latvian.mods.kubejs.KubeJSPlugin，只有安装了 KubeJS 才能加载；
 * 未安装时混入该目标类会直接 NoClassDefFoundError。其余 mixin 一律照常应用。
 */
public class CmiMixinConfigPlugin implements IMixinConfigPlugin {
	private static final String MBD_KUBEJS_MIXIN = "dev.celestiacraft.cmi.mixin.MBDKubeJSPluginMixin";

	@Override
	public void onLoad(String mixinPackage) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (MBD_KUBEJS_MIXIN.equals(mixinClassName)) {
			try {
				return LoadingModList.get().getModFileById("kubejs") != null;
			} catch (Throwable ignored) {
				return true;
			}
		}
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
}
