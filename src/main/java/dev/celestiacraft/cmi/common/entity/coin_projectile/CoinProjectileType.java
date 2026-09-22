package dev.celestiacraft.cmi.common.entity.coin_projectile;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.celestiacraft.cmi.Cmi;
import lombok.Getter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 单个 coin 弹射物类型
 *
 * <p>
 * 由数据包 {@code data/<namespace>/cmi/coin_projectile/<name>.json} 定义, 决定某个(或某一类)
 * coin 物品被 {@link dev.celestiacraft.cmi.common.item.mechanism.CoilItem} 发射出去之后的表现
 * </p>
 *
 * <p>
 * 重力 / 可拾取 / 击退这些行为不再由独立字段控制, 而是由 {@link CoinFireType} 决定,
 * 各类型的专属参数如下:
 * </p>
 *
 * <h2>字段</h2>
 *
 * <pre>{@code
 * {
 *   "item": "#forge:coins",          // 必填: 物品 id / "#标签" / ["a", "b"] / {"item": "..."} / {"tag": "..."}
 *   "fire_type": "knockback",        // knockback / pierce / scatter
 *
 *   "knockback_strength": 0,         // 仅 knockback: 击退强度
 *   "pierce_level": 1,               // 仅 pierce: 可贯穿的实体数量(同时无视重力)
 *   "bullet_count": 3,               // 仅 scatter: 同时射出的子弹数(相邻两发相差 10°, 只消耗一枚硬币)
 *
 *   "velocity": 3.0,                 // 发射初速度 (格/tick)
 *   "damage": 2.0,                   // 伤害系数: 实际伤害 = 命中瞬间速度 * damage
 *   "inaccuracy": 1.0,               // 发射散布
 *   "render_mode": "toward_motion",  // billboard / tumble / toward_motion
 *   "scale": 1.0,                    // 渲染缩放
 *   "spin": 1.0,                     // toward_motion 的自旋速度
 *   "cooldown": 8,                   // 使用冷却 (tick)
 *   "shoot_sound": "immersiveengineering:railgun_fire",  // 默认音效, 缺少该模组时回退原版箭矢音效
 *   "shoot_volume": 0.5,             // 发射音效音量
 *   "hit_sound": "minecraft:entity.arrow.hit"
 * }
 * }</pre>
 */
public class CoinProjectileType {
	/**
	 * 没有任何数据包匹配时使用的内置默认值
	 */
	private static final double DEFAULT_VELOCITY = 2.0D;
	private static final double DEFAULT_DAMAGE = 2.0D;
	private static final int DEFAULT_KNOCKBACK_STRENGTH = 0;
	private static final int DEFAULT_PIERCE_LEVEL = 1;
	private static final int DEFAULT_BULLET_COUNT = 3;
	private static final ResourceLocation DEFAULT_SHOOT_SOUND_ID = ResourceLocation.fromNamespaceAndPath("immersiveengineering", "railgun_fire");
	private static final float DEFAULT_SHOOT_VOLUME = 0.5F;

	/**
	 * 原版箭矢能承载的最大贯穿等级
	 */
	public static final int MAX_PIERCE_LEVEL = 127;
	/**
	 * 散射子弹数量的上限, 防止数据包写出夸张的数字拖垮服务器
	 */
	public static final int MAX_BULLET_COUNT = 32;

	@Getter
	private final ResourceLocation id;
	/**
	 * 命中判定: 任意一个匹配即视为该类型
	 */
	private final List<Ingredient> ingredients;
	@Getter
	private final CoinFireType fireType;
	@Getter
	private final int knockbackStrength;
	@Getter
	private final int pierceLevel;
	@Getter
	private final int bulletCount;
	@Getter
	private final double velocity;
	@Getter
	private final double damage;
	@Getter
	private final float inaccuracy;
	@Getter
	private final CoinProjectileRenderMode renderMode;
	@Getter
	private final float scale;
	@Getter
	private final float spin;
	@Getter
	private final int cooldown;
	@Getter
	private final SoundEvent shootSound;
	@Getter
	private final float shootVolume;
	@Getter
	private final SoundEvent hitSound;

	private CoinProjectileType(ResourceLocation id, List<Ingredient> ingredients, CoinFireType fireType, int knockbackStrength, int pierceLevel, int bulletCount, double velocity, double damage, float inaccuracy, CoinProjectileRenderMode renderMode, float scale, float spin, int cooldown, SoundEvent shootSound, float shootVolume, SoundEvent hitSound) {
		this.id = id;
		this.ingredients = ingredients;
		this.fireType = fireType;
		this.knockbackStrength = knockbackStrength;
		this.pierceLevel = pierceLevel;
		this.bulletCount = bulletCount;
		this.velocity = velocity;
		this.damage = damage;
		this.inaccuracy = inaccuracy;
		this.renderMode = renderMode;
		this.scale = scale;
		this.spin = spin;
		this.cooldown = cooldown;
		this.shootSound = shootSound;
		this.shootVolume = shootVolume;
		this.hitSound = hitSound;
	}

	/**
	 * 该类型是否匹配这个物品
	 */
	public boolean matches(ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}

		for (Ingredient ingredient : ingredients) {
			if (ingredient.test(stack)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 内置默认类型: 匹配不到任何数据包时兜底, 保证没有数据包也能正常发射
	 */
	public static @NotNull CoinProjectileType fallback() {
		return new CoinProjectileType(
				Cmi.loadResource("fallback"),
				List.of(),
				CoinFireType.DEFAULT,
				DEFAULT_KNOCKBACK_STRENGTH,
				DEFAULT_PIERCE_LEVEL,
				DEFAULT_BULLET_COUNT,
				DEFAULT_VELOCITY,
				DEFAULT_DAMAGE,
				1.0F,
				CoinProjectileRenderMode.DEFAULT,
				1.0F,
				1.0F,
				8,
				defaultShootSound(),
				DEFAULT_SHOOT_VOLUME,
				SoundEvents.ARROW_HIT
		);
	}

	public static @NotNull CoinProjectileType fromJson(ResourceLocation id, JsonObject json) {
		if (!json.has("item")) {
			throw new JsonParseException(String.format("coin_projectile %s 缺少必填字段 item", id));
		}

		return new CoinProjectileType(
				id,
				parseIngredients(json.get("item")),
				CoinFireType.byName(GsonHelper.getAsString(json, "fire_type", CoinFireType.DEFAULT.getSerializedName())),
				Math.max(0, GsonHelper.getAsInt(json, "knockback_strength", DEFAULT_KNOCKBACK_STRENGTH)),
				Mth.clamp(GsonHelper.getAsInt(json, "pierce_level", DEFAULT_PIERCE_LEVEL), 0, MAX_PIERCE_LEVEL),
				Mth.clamp(GsonHelper.getAsInt(json, "bullet_count", DEFAULT_BULLET_COUNT), 1, MAX_BULLET_COUNT),
				GsonHelper.getAsDouble(json, "velocity", DEFAULT_VELOCITY),
				GsonHelper.getAsDouble(json, "damage", DEFAULT_DAMAGE),
				GsonHelper.getAsFloat(json, "inaccuracy", 1.0F),
				CoinProjectileRenderMode.byName(GsonHelper.getAsString(json, "render_mode", CoinProjectileRenderMode.DEFAULT.getSerializedName())),
				GsonHelper.getAsFloat(json, "scale", 1.0F),
				GsonHelper.getAsFloat(json, "spin", 1.0F),
				GsonHelper.getAsInt(json, "cooldown", 8),
				parseSound(json, "shoot_sound", defaultShootSound()),
				GsonHelper.getAsFloat(json, "shoot_volume", DEFAULT_SHOOT_VOLUME),
				parseSound(json, "hit_sound", SoundEvents.ARROW_HIT)
		);
	}

	/**
	 * 默认发射音效: 沉浸工程的磁轨炮开火
	 *
	 * <p>
	 * 没装该模组时回退到原版箭矢音效, 不会因为缺少可选依赖而变成静音
	 * </p>
	 */
	private static SoundEvent defaultShootSound() {
		SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(DEFAULT_SHOOT_SOUND_ID);

		return sound == null ? SoundEvents.ARROW_SHOOT : sound;
	}

	private static List<Ingredient> parseIngredients(JsonElement element) {
		List<Ingredient> ingredients = new ArrayList<>();

		if (element.isJsonArray()) {
			for (JsonElement child : element.getAsJsonArray()) {
				ingredients.addAll(parseIngredients(child));
			}

			if (ingredients.isEmpty()) {
				throw new JsonParseException("coin_projectile 的 item 数组不能为空");
			}

			return ingredients;
		}

		ingredients.add(parseIngredient(element));

		return ingredients;
	}

	private static Ingredient parseIngredient(JsonElement element) {
		if (element.isJsonObject()) {
			JsonObject object = element.getAsJsonObject();

			if (object.has("tag")) {
				return Ingredient.of(parseTag(GsonHelper.getAsString(object, "tag")));
			}

			if (object.has("item")) {
				return parseIngredient(object.get("item"));
			}

			throw new JsonParseException("coin_projectile 的 item 对象只支持 item / tag 字段");
		}

		String value = element.getAsString();
		if (value.startsWith("#")) {
			return Ingredient.of(parseTag(value.substring(1)));
		}

		ResourceLocation itemId = ResourceLocation.tryParse(value);
		Item item = itemId == null ? null : ForgeRegistries.ITEMS.getValue(itemId);

		if (item == null) {
			throw new JsonParseException(String.format("coin_projectile 引用了未知物品: %s", value));
		}

		return Ingredient.of(item);
	}

	private static TagKey<Item> parseTag(String value) {
		ResourceLocation tagId = ResourceLocation.tryParse(value);

		if (tagId == null) {
			throw new JsonParseException(String.format("coin_projectile 引用了非法标签: %s", value));
		}

		return TagKey.create(Registries.ITEM, tagId);
	}

	private static SoundEvent parseSound(JsonObject json, String key, SoundEvent fallback) {
		if (!json.has(key)) {
			return fallback;
		}

		ResourceLocation soundId = ResourceLocation.tryParse(GsonHelper.getAsString(json, key));

		if (soundId == null) {
			return fallback;
		}

		SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(soundId);

		return sound == null ? fallback : sound;
	}
}
