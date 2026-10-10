package dev.celestiacraft.cmi.mixin.kubejs;

import dev.celestiacraft.cmi.compat.kubejs.modify.event.ILangEventJSUtils;
import dev.latvian.mods.kubejs.client.LangEventJS;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = LangEventJS.class, remap = false)
public class LangEventJSMixin implements ILangEventJSUtils {
}