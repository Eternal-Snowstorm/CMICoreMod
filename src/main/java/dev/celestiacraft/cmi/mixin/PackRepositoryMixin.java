package dev.celestiacraft.cmi.mixin;

import dev.celestiacraft.cmi.client.autoglow.AutoGlowPack;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * 把 AutoGlow 的虚拟资源包插到 pack 列表最前面(优先级最高)
 * <p>
 * 和 KubeJS 插 GeneratedClientResourcePack 是同一个位置
 *
 * @author CelestiaCraft
 */
@Mixin(PackRepository.class)
public class PackRepositoryMixin {
	@Inject(method = "openAllSelected", at = @At("RETURN"), cancellable = true)
	private void cmi$openAllSelected(CallbackInfoReturnable<List<PackResources>> callback) {
		List<PackResources> packs = new ArrayList<>(callback.getReturnValue());
		packs.add(0, AutoGlowPack.create());
		callback.setReturnValue(packs);
	}
}