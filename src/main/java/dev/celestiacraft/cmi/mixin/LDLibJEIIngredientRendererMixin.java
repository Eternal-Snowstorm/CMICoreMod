package dev.celestiacraft.cmi.mixin;

import com.lowdragmc.lowdraglib.gui.ingredient.IRecipeIngredientSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 修复: 所有涉及 MBD(LDLib) 的 JEI 分类里, 鼠标指向物品时不显示物品名称 / 注册 ID.
 * <p>
 * LDLib 在 ModularUIRecipeCategory#addJEISlot 里通过
 * IRecipeSlotBuilder#setCustomRenderer 给每个槽位换上了自己的物品渲染器
 * (ModularUIRecipeCategory$2). 而 JEI 取 tooltip 走的是
 * IIngredientRenderer#getTooltip(ITooltipBuilder, T, Player, TooltipFlag)} 这个带
 * Player 的四参重载, LDLib 1.0.52 只实现了旧 API 的三参重载
 * getTooltip(ITooltipBuilder, T, TooltipFlag), 于是调用会沿 default 实现一路退化:
 * 四参 -> 三参(List, 带 Player) -> 两参抽象方法, 而两参方法被 LDLib 覆盖成了
 * Collections.emptyList(). 结果物品自身那部分 tooltip(名称/注册ID/耐久/燃烧时间等)
 * 全是空的, 只剩 JEI 在渲染器之外补的 "接受标签" 行和 mod 名.
 * <p>
 * 这里把两参方法的返回值换回 LDLib 本来要用的 slot.getFullTooltipTexts(),
 * 与它自己的三参实现语义完全一致.
 * <p>
 * 注意: 这里不能用 @Shadow 取 val$slot 字段.
 * 目标类是匿名内部类, 只能用 @Mixin(targets = ...), 而 Mixin 注解处理器走 ASM
 * 句柄校验 @Shadow 字段时会把字段描述符当成方法描述符丢给
 * SignaturePrinter/Type#getReturnType, 直接抛 StringIndexOutOfBoundsException
 * 打断编译(报错信息只有 "String index out of range: N"). 故改用反射取字段.
 */
@Mixin(targets = "com.lowdragmc.lowdraglib.jei.ModularUIRecipeCategory$2", remap = false)
public class LDLibJEIIngredientRendererMixin {
	@Unique
	private static final String CMI$SLOT_FIELD_NAME = "val$slot";

	@Unique
	private static Field cmi$slotField;

	@Inject(
			method = "getTooltip(Ljava/lang/Object;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;",
			at = @At("HEAD"),
			cancellable = true,
			remap = false
	)
	private void cmi$restoreIngredientTooltip(Object ingredient, TooltipFlag flag, CallbackInfoReturnable<List<Component>> returnable) {
		IRecipeIngredientSlot slot = cmi$getSlot(this);

		if (slot == null) {
			return;
		}

		returnable.setReturnValue(slot.getFullTooltipTexts());
	}

	@Unique
	private static IRecipeIngredientSlot cmi$getSlot(Object renderer) {
		try {
			Field field = cmi$slotField;

			if (field == null) {
				field = renderer.getClass().getDeclaredField(CMI$SLOT_FIELD_NAME);
				field.setAccessible(true);
				cmi$slotField = field;
			}

			return (IRecipeIngredientSlot) field.get(renderer);
		} catch (ReflectiveOperationException | ClassCastException exception) {
			return null;
		}
	}
}