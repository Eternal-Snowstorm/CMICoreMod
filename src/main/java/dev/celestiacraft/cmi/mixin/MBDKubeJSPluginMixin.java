package dev.celestiacraft.cmi.mixin;

import com.lowdragmc.mbd2.integration.kubejs.MBDKubeJSPlugin;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * MBD2 1.0.40 的 KubeJS 集成回归修补。
 * <p>
 * MBD2 的 MBDKubeJSPlugin#afterInit() 会无条件 new CustomRendererEventJS()，
 * 而该构造器的首条字节码是 getstatic com/lowdragmc/mbd2/client/renderer/KubeJSRenderer.renderFunctions，
 * 服务端解析这个纯客户端类时会被 Forge 的 RuntimeDistCleaner 直接拒绝：
 * <pre>
 * java.lang.RuntimeException: Attempted to load class com/lowdragmc/mbd2/client/renderer/KubeJSRenderer
 *     for invalid dist DEDICATED_SERVER
 *   at com.lowdragmc.mbd2.integration.kubejs.events.CustomRendererEventJS.&lt;init&gt;(CustomRendererEventJS.java:16)
 *   at com.lowdragmc.mbd2.integration.kubejs.MBDKubeJSPlugin.afterInit(MBDKubeJSPlugin.java:88)
 * </pre>
 * afterInit() 的全部内容只有「向 KubeJS 提交一次客户端自定义渲染器事件」这一件事，服务端毫无意义，
 * 因此整体取消。对比 1.0.38.a / 1.0.39 的同名方法，字节码里并不存在 KubeJSRenderer 引用，
 * 可确认这是 1.0.40 引入的回归；若上游修复后本 mixin 依然生效（服务端本就不需要该事件），
 * 但若日后 afterInit() 里加入了服务端必需逻辑，请一并删除本类。
 */
@Mixin(value = MBDKubeJSPlugin.class, remap = false)
public class MBDKubeJSPluginMixin {
	@Inject(method = "afterInit", at = @At("HEAD"), cancellable = true, remap = false)
	private void cmi$skipClientOnlyAfterInit(CallbackInfo ci) {
		if (FMLEnvironment.dist.isDedicatedServer()) {
			ci.cancel();
		}
	}
}
