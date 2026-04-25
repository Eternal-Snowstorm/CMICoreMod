package dev.celestiacraft.cmi.mixin;

import dev.celestiacraft.cmi.client.menu.jei.InternalDrawable;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientToggleState;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.common.input.IInternalKeyMappings;
import mezz.jei.gui.overlay.ConfigButton;
import mezz.jei.gui.overlay.IngredientListOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BooleanSupplier;

@Mixin(value = IngredientListOverlay.class, remap = false)
public abstract class IngredientListOverlayMixin {
	@Redirect(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lmezz/jei/gui/overlay/ConfigButton;create(Ljava/util/function/BooleanSupplier;Lmezz/jei/common/config/IClientToggleState;Lmezz/jei/common/input/IInternalKeyMappings;)Lmezz/jei/gui/overlay/ConfigButton;"),
			remap = false
	)
	private ConfigButton redirectConstructor(BooleanSupplier isListDisplayed, IClientToggleState toggleState, IInternalKeyMappings keyBindings) {
		try {
			Constructor<ConfigButton> constructor = ConfigButton.class.getDeclaredConstructor(
					IDrawable.class,
					IDrawable.class,
					BooleanSupplier.class,
					IClientToggleState.class,
					IInternalKeyMappings.class
			);
			constructor.setAccessible(true);
			Textures textures = Internal.getTextures();
			return constructor.newInstance(
					textures.getConfigButtonIcon(),
					new InternalDrawable(16, 16),
					isListDisplayed,
					toggleState,
					keyBindings
			);
		} catch (NoSuchMethodException
		         | InvocationTargetException
		         | InstantiationException
		         | IllegalAccessException exception) {
			throw new RuntimeException(exception);
		}
	}
}