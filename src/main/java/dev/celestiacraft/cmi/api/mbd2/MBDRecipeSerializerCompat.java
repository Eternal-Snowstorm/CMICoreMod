package dev.celestiacraft.cmi.api.mbd2;

import com.google.gson.JsonObject;
import com.lowdragmc.mbd2.api.recipe.MBDRecipe;
import com.lowdragmc.mbd2.api.recipe.MBDRecipeSerializer;
import com.lowdragmc.mbd2.api.recipe.MBDRecipeType;
import com.lowdragmc.mbd2.api.registry.MBDRegistries;
import dev.celestiacraft.cmi.Cmi;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 把 MB2 的配方类型补注册进 Forge 注册表 (RECIPE_TYPES + RECIPE_SERIALIZERS)。
 * <p>
 * MB2 的 MBDRecipeType 只存在自己的 MBDRegistries 里, 不进 ForgeRegistries.RECIPE_TYPES;
 * 所有配方又共用一个序列化器 (mbd2:mbd_recipe_serializer)。这会带来两个问题:
 * <ul>
 *   <li>KubeJS 的配方体系按"配方类型 id"找序列化器 -> 报 "Serializer for type xxx is not found",
 *       配方无法用 ServerEvents.recipes 编写;</li>
 *   <li>配方进了 RecipeManager 后, 其它 mod 用 BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType())
 *       会得到 null (例如 CreateDragonsPlus 遍历配方建 ImmutableMap 时报 null key 崩溃)。</li>
 * </ul>
 * 这里在 RegisterEvent 阶段为每个已注册的 MBD 配方类型补一份 Forge 条目 (类型 + 委托序列化器)。
 */
@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MBDRecipeSerializerCompat {
	@SubscribeEvent
	public static void onRegister(RegisterEvent event) {
		ResourceKey<? extends Registry<?>> key = event.getRegistryKey();

		if (key.equals(ForgeRegistries.Keys.RECIPE_TYPES)) {
			for (MBDRecipeType type : MBDRegistries.RECIPE_TYPES) {
				ResourceLocation id = type.getRegistryName();
				if (!ForgeRegistries.RECIPE_TYPES.containsKey(id)) {
					event.register(ForgeRegistries.Keys.RECIPE_TYPES, id, () -> {
						return (RecipeType<?>) type;
					});
				}
			}
			return;
		}

		if (key.equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
			for (MBDRecipeType type : MBDRegistries.RECIPE_TYPES) {
				ResourceLocation id = type.getRegistryName();
				if (!ForgeRegistries.RECIPE_SERIALIZERS.containsKey(id)) {
					event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, id, DelegatingSerializer::new);
				}
			}
		}
	}

	public static class DelegatingSerializer implements RecipeSerializer<MBDRecipe> {
		@Override
		public @NotNull MBDRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
			return MBDRecipeSerializer.SERIALIZER.fromJson(id, json);
		}

		@Override
		public MBDRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
			return MBDRecipeSerializer.SERIALIZER.fromNetwork(id, buf);
		}

		@Override
		public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull MBDRecipe recipe) {
			MBDRecipeSerializer.SERIALIZER.toNetwork(buf, recipe);
		}
	}
}