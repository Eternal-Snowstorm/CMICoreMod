package dev.celestiacraft.cmi.common.block.usb_socket;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.register.CmiBlock;
import dev.celestiacraft.cmi.common.register.CmiBlockEntityTypes;
import dev.celestiacraft.cmi.tag.ModItemTags;
import dev.celestiacraft.cmi.utils.Mechanisms;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = Cmi.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class UsbSocketBlock extends BaseEntityBlock {
	public UsbSocketBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state1, boolean pIsMoving) {
		super.onRemove(state, level, pos, state1, pIsMoving);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new UsbSocketBlockEntity(CmiBlockEntityTypes.USB_SOCKET.get(), pos, state);
	}

	@SubscribeEvent
	public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		Level level = event.getLevel();
		BlockPos pos = event.getPos();
		Player player = event.getEntity();
		InteractionHand hand = event.getHand();
		ItemStack item = player.getItemInHand(hand);

		if (level.isClientSide()) {
			return;
		}

		if (event.getHand() == InteractionHand.OFF_HAND) {
			return;
		}

		if (!item.is(ModItemTags.MECHANISM_FLASH_DRIVES)) {
			return;
		}
		if (!event.getLevel().getBlockState(pos).is(CmiBlock.USB_SOCKET.get())) {
			return;
		}

		// 缓存NBT
		CompoundTag tag = new CompoundTag();
		// 不许已经插过的
		if (hasUsb(item.getItem().toString(), level, pos)) {
			return;
		}

		// 存入
		int[] nbt;
		for (int i = 0; i < Mechanisms.ALL_MECHANISM_TYPES.size(); i++) {
			nbt = level.getBlockEntity(pos).serializeNBT().getIntArray("savedDrives");
			if (item.getItem().toString().equals(Mechanisms.ALL_MECHANISM_TYPES.get(i) + "_mechanism_flash_drive")) {
				nbt[i] = 1;
				tag.putIntArray("savedDrives", nbt);
			}
		}
		if (level.getBlockEntity(pos) instanceof UsbSocketBlockEntity entity) {
			entity.load(tag);
			entity.saveAdditional(tag);
			entity.setChanged();
		}

		// 消耗物品+挥手
		if (!player.isCreative()) {
			item.shrink(1);
		}
		player.swing(InteractionHand.MAIN_HAND);
	}

	@SubscribeEvent
	public static void onBreak(BlockEvent.BreakEvent event) {
		LevelAccessor level = event.getLevel();
		BlockPos pos = event.getPos();
		SimpleContainer container = new SimpleContainer(64);

		for (int i = 0; i < level.getBlockEntity(pos).serializeNBT().getIntArray("savedDrives").length; i++) {
			if (level.getBlockEntity(pos).serializeNBT().getIntArray("savedDrives")[i] == 1) {
				ResourceLocation drive = Cmi.loadResource(String.format("%s_mechanism_flash_drive", Mechanisms.ALL_MECHANISM_TYPES.get(i)));
				container.addItem(ForgeRegistries.ITEMS.getValue(drive).getDefaultInstance());
			}
		}
	}

	/**
	 * 获取有没有插过
	 * itemId形参用namespace:an_id中的an_id部分
	 *
	 * @param itemId
	 * @param level
	 * @param pos
	 * @return
	 */
	public static boolean hasUsb(String itemId, Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof UsbSocketBlockEntity usbSocketEntity) {
			int[] drives = usbSocketEntity.serializeNBT().getIntArray("savedDrives");
			for (int i = 0; i < Mechanisms.ALL_MECHANISM_TYPES.size(); i++) {
				String drive = String.format("%s_mechanism_flash_drive", Mechanisms.ALL_MECHANISM_TYPES.get(i));
				if (itemId.equals(drive) && drives[i] == 1) {
					return true;
				}
			}
		}
		return false;
	}
}