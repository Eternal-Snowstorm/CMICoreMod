package dev.celestiacraft.cmi.event;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.usb_socket.UsbSocketBlockEntity;
import dev.celestiacraft.cmi.common.register.CmiBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Cmi.MODID,bus = Mod.EventBusSubscriber.Bus.FORGE)
public class UsbSocket extends Block {
    public UsbSocket(Properties pProperties) {
        super(pProperties);
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {

        // 孩子们这太好崩了所以try一下
        try{

            // 申必变量
            Level level = event.getLevel();
            BlockPos pos = event.getPos();
            Player player = event.getEntity();
            InteractionHand hand = event.getHand();
            ItemStack item = player.getItemInHand(hand);

            // 服务端
            if (level.isClientSide()){
                return;
            }

            // 不许副手
            if (event.getHand() == InteractionHand.OFF_HAND) {
                return;
            }

            // 只插U盘
            if (!item.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("cmi","mechanism_flash_drives")))) {
                return;
            }
            if (!event.getLevel().getBlockState(pos).is(CmiBlock.USB_SOCKET.get())) {
                return;
            }

            // 缓存NBT
            CompoundTag socketNbt = new CompoundTag();

            // 不许已经插过的
            if (hasUsb(item.getItem().toString(),level,pos)) {
                return;
            }

            // 存
            int[] nbt;
            for (int i =0;i< allMechanismTypes.length;i++){
                nbt = level.getBlockEntity(pos).serializeNBT().getIntArray("savedDrives");
                if (item.getItem().toString().equals(allMechanismTypes[i]+"_mechanism_flash_drive")){
                    nbt[i] = 1;
                    socketNbt.putIntArray("savedDrives",nbt);
                }
            }
            if (level.getBlockEntity(pos) instanceof UsbSocketBlockEntity entity) {
                entity.load(socketNbt);
                entity.saveAdditional(socketNbt);
                entity.setChanged();
            }

            // 消耗物品+挥手
            if (!player.isCreative()) {
                item.shrink(1);
            }
            player.swing(InteractionHand.MAIN_HAND);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {

        // 方块撸之后掉物品
        try {
            // 申必变量
            LevelAccessor level = event.getLevel();
            BlockPos pos = event.getPos();
            SimpleContainer container = new SimpleContainer(64);
            for (int i=0;i<level.getBlockEntity(pos).serializeNBT().getIntArray("savedDrives").length;i++){
                if (level.getBlockEntity(pos).serializeNBT().getIntArray("savedDrives")[i] == 1){
                    container.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("cmi",allMechanismTypes[i]+"_mechanism_flash_drive"))));
                }
            }

            Containers.dropContents((Level) level,pos,container);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    // 获取有没有插过
    // itemId形参用namespace:an_id中的an_id部分
    public static boolean hasUsb(String itemId,Level level,BlockPos pos){
        if (level.getBlockEntity(pos) instanceof UsbSocketBlockEntity usbSocketEntity){
            int[] drives = usbSocketEntity.serializeNBT().getIntArray("savedDrives");
            for (int i =0;i< allMechanismTypes.length;i++){
                if (itemId.equals(allMechanismTypes[i]+"_mechanism_flash_drive")&&drives[i] == 1){
                    return true;
                }
            }
        }
        return false;
    }

    public static final String[] allMechanismTypes = new String[]{
            "wooden",
            "stone",
            "iron",
            "nature",
            "pig_iron",
            "potion",
            "colorful",
            "enchanted",
            "nether",
            "sculk",
            "ender",
            "copper",
            "andesite",
            "bronze",
            "railway",
            "light_engineering",
            "heavy_engineering",
            "coil",
            "smart",
            "cobalt",
            "photosensitive",
            "thermal",
            "reinforced",
            "gold",
            "basic_mekanism",
            "advanced_mekanism",
            "elite_mekanism",
            "ultimate_mekanism",
            "computing",
            "air_tight",
            "tier_1_aviation",
            "tier_2_aviation",
            "tier_3_aviation",
            "tier_4_aviation",
            "nuclear",
            "antimatter",
            "creative"
    };
}
