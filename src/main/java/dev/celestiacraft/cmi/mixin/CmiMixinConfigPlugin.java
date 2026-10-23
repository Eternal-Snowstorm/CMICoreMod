package dev.celestiacraft.cmi.mixin;

import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * 为所有 KubeJS 相关修补提供守卫。
 * <p>
 * kubejs 包下的混入目标全部来自 dev.latvian.mods.kubejs.*，只有安装了 KubeJS 才能加载；
 * 未安装时混入这些目标类会直接 NoClassDefFoundError。其余 mixin 一律照常应用。
 */
public class CmiMixinConfigPlugin implements IMixinConfigPlugin {
	private static final String KUBEJS_MIXIN_PREFIX = "dev.celestiacraft.cmi.mixin.kubejs.";

	@Override
	public void onLoad(String mixinPackage) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (mixinClassName.startsWith(KUBEJS_MIXIN_PREFIX)) {
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