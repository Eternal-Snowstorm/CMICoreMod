package dev.celestiacraft.cmi.common.entity.coin_projectile;

import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

/**
 * coin 弹射物实体
 *
 * <p>
 * 直接继承原版 {@link AbstractArrow}, 因此天然具备"以速度计算伤害"的箭矢命中逻辑:
 * 伤害 = 命中瞬间速度 × baseDamage, 而 baseDamage 由数据包字段 {@code damage} 决定
 * </p>
 *
 * <p>
 * 重力 / 可拾取 / 击退等行为由 {@link CoinFireType} 决定(见 {@link #setCoin(ItemStack, CoinProjectileType)}),
 * 与箭的差异:
 * </p>
 *
 * <ul>
 *     <li>飞行中渲染成被消耗的那个 coin 物品(见 {@link CoinProjectileRenderer})</li>
 *     <li>物品栈与姿态数据通过 Forge 的 spawn data 同步到客户端, 不占用 entity data 槽位</li>
 *     <li>创造模式玩家拾取时只清除实体, 不把 coin 放进背包</li>
 *     <li>散射弹无法拾取, 触地立即消失</li>
 * </ul>
 */
public class CoinProjectileEntity extends AbstractArrow implements IEntityAdditionalSpawnData {
	private static final String COIN_TAG = "Coin";
	private static final String FIRE_TYPE_TAG = "FireType";
	private static final String RENDER_MODE_TAG = "RenderMode";
	private static final String RENDER_SCALE_TAG = "RenderScale";
	private static final String RENDER_SPIN_TAG = "RenderSpin";

	/**
	 * 被发射出去的 coin 物品(数量恒为 1), 客户端用于渲染与拾取
	 */
	@Getter
	private ItemStack coin = ItemStack.EMPTY;
	@Getter
	private CoinFireType fireType = CoinFireType.DEFAULT;
	@Getter
	private CoinProjectileRenderMode renderMode = CoinProjectileRenderMode.DEFAULT;
	@Getter
	private float renderScale = 1.0F;
	@Getter
	private float renderSpin = 1.0F;
	/**
	 * 数据包类型, 只在服务端有效(客户端不加载数据包, 会被兜底成 fallback)
	 *
	 * <p>
	 * 注意字段名不能叫 type —— 那会和 {@link net.minecraft.world.entity.Entity#getType()} 冲突
	 * </p>
	 */
	@Getter
	private CoinProjectileType projectileType = CoinProjectileTypes.fallback();
	/**
	 * 姿态时钟: 只在飞行中推进
	 *
	 * <p>
	 * 硬币卡在方块上之后(以及下坠前的静止状态)不再累加, 于是渲染时不会原地转圈
	 * </p>
	 */
	private float spinTime;

	public CoinProjectileEntity(EntityType<? extends CoinProjectileEntity> type, Level level) {
		super(type, level);
	}

	public CoinProjectileEntity(EntityType<? extends CoinProjectileEntity> type, Level level, LivingEntity shooter) {
		super(type, shooter, level);
	}

	/**
	 * 设置本次发射的 coin 与参数
	 *
	 * <p>
	 * 三种射击类型在这里分道扬镳:
	 * </p>
	 *
	 * <ul>
	 *     <li>击退: 受重力, 按数据包强度击退, 落地可拾回</li>
	 *     <li>贯穿: 无视重力, 按数据包等级贯穿实体, 落地可拾回</li>
	 *     <li>散射: 受重力, 强制不可拾取(触地即消失的逻辑见 {@link #onHitBlock(BlockHitResult)})</li>
	 * </ul>
	 */
	public void setCoin(ItemStack stack, CoinProjectileType type) {
		this.coin = stack.copyWithCount(1);
		this.projectileType = type;
		this.fireType = type.getFireType();
		this.renderMode = type.getRenderMode();
		this.renderScale = type.getScale();
		this.renderSpin = type.getSpin();

		this.setBaseDamage(type.getDamage());
		this.setSoundEvent(type.getHitSound());

		switch (fireType) {
			case KNOCKBACK -> {
				this.setNoGravity(false);
				this.setKnockback(type.getKnockbackStrength());
			}
			case PIERCE -> {
				this.setNoGravity(true);
				this.setPierceLevel((byte) Mth.clamp(type.getPierceLevel(), 0, CoinProjectileType.MAX_PIERCE_LEVEL));
			}
			case SCATTER -> {
				this.setNoGravity(false);
				this.pickup = Pickup.DISALLOWED;
			}
		}
	}

	@Override
	public void tick() {
		super.tick();

		/*
		 * super.tick() 里已经完成碰撞检测并设置 inGround, 因此这里判断到的就是最新状态
		 */
		if (!inGround) {
			spinTime += 1.0F;
		}
	}

	/**
	 * 渲染用的姿态时钟, 卡在方块上时不再随时间推进
	 *
	 * @param partialTick 当前帧插值, 飞行中用于让自旋平滑; 静止时忽略它, 避免原地抖动
	 */
	public float getSpinTime(float partialTick) {
		return inGround ? spinTime : spinTime + partialTick;
	}

	/**
	 * 散射弹触地即消失: 不卡在地上, 也不掉落硬币
	 */
	@Override
	protected void onHitBlock(@NotNull BlockHitResult result) {
		if (fireType == CoinFireType.SCATTER) {
			this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
			this.discard();
			return;
		}

		super.onHitBlock(result);
	}

	@Override
	protected @NotNull ItemStack getPickupItem() {
		return coin.copy();
	}

	/**
	 * 拾取判定
	 *
	 * <p>
	 * 创造模式玩家只是把硬币"收掉"(实体消失), 不往背包里塞东西;
	 * 散射弹在 {@link #setCoin(ItemStack, CoinProjectileType)} 里就已被标记为不可拾取
	 * </p>
	 */
	@Override
	protected boolean tryPickup(@NotNull Player player) {
		if (pickup == Pickup.DISALLOWED) {
			return false;
		}

		if (player.isCreative()) {
			return true;
		}

		return super.tryPickup(player);
	}

	@Override
	public void addAdditionalSaveData(@NotNull CompoundTag tag) {
		super.addAdditionalSaveData(tag);

		if (!coin.isEmpty()) {
			tag.put(COIN_TAG, coin.serializeNBT());
		}

		tag.putString(FIRE_TYPE_TAG, fireType.getSerializedName());
		tag.putString(RENDER_MODE_TAG, renderMode.getSerializedName());
		tag.putFloat(RENDER_SCALE_TAG, renderScale);
		tag.putFloat(RENDER_SPIN_TAG, renderSpin);
	}

	@Override
	public void readAdditionalSaveData(@NotNull CompoundTag tag) {
		super.readAdditionalSaveData(tag);

		if (tag.contains(COIN_TAG, CompoundTag.TAG_COMPOUND)) {
			coin = ItemStack.of(tag.getCompound(COIN_TAG));
			projectileType = CoinProjectileTypes.getOrFallback(coin);
		}

		fireType = CoinFireType.byName(tag.getString(FIRE_TYPE_TAG));
		renderMode = CoinProjectileRenderMode.byName(tag.getString(RENDER_MODE_TAG));
		renderScale = tag.contains(RENDER_SCALE_TAG) ? tag.getFloat(RENDER_SCALE_TAG) : 1.0F;
		renderSpin = tag.contains(RENDER_SPIN_TAG) ? tag.getFloat(RENDER_SPIN_TAG) : 1.0F;
	}

	@Override
	public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		buffer.writeItem(coin);
		buffer.writeEnum(fireType);
		buffer.writeEnum(renderMode);
		buffer.writeFloat(renderScale);
		buffer.writeFloat(renderSpin);
	}

	@Override
	public void readSpawnData(FriendlyByteBuf buffer) {
		coin = buffer.readItem();
		fireType = buffer.readEnum(CoinFireType.class);
		renderMode = buffer.readEnum(CoinProjectileRenderMode.class);
		renderScale = buffer.readFloat();
		renderSpin = buffer.readFloat();
	}
}
