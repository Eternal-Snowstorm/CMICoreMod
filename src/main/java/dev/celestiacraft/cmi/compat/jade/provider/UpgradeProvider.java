package dev.celestiacraft.cmi.compat.jade.provider;

import dev.celestiacraft.cmi.compat.jade.CmiType;
import mekanism.api.Upgrade;
import mekanism.common.item.ItemUpgrade;
import mekanism.common.registration.impl.ItemRegistryObject;
import mekanism.common.registries.MekanismItems;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 改编自 DevDyna 的 MekaJadeUpgrades 项目.
 * <p>
 * 感谢 DevDyna 使用 MIT
 *
 * @see <a href="https://github.com/DevDyna/MekaJadeUpgrades/tree/20.1">MekaJadeUpgrades (20.1)</a>
 */
public enum UpgradeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
	INSTANCE;

	@Override
	public ResourceLocation getUid() {
		return CmiType.COMMON;
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		CompoundTag tag = accessor.getServerData();
		List<IElement> elements = new ArrayList<>();

		int x = 0;

		for (Upgrade upgrade : Upgrade.values()) {
			if (tag.contains(upgrade.getRawName())) {
				int count = tag.getInt(upgrade.getRawName());

				elements.add(item(upgrade, x));
				elements.add(countText(count, x));
				x += 10;
			}
		}

		tooltip.add(elements);
	}

	public IElement item(Upgrade upgrade, int x) {
		try {
			return IElementHelper.get()
					.item(new ItemStack(getUpgrade(upgrade).get()), 0.55f)
					.size(new Vec2(10, 10))
					.translate(new Vec2(x, -2))
					.message(null);
		} catch (NullPointerException exception) {
			return IElementHelper.get()
					.item(new ItemStack(Items.BARRIER), 0.55f)
					.size(new Vec2(10, 10))
					.translate(new Vec2(x, -2))
					.message(null);
		}
	}

	public IElement countText(int count, int x) {
		return IElementHelper.get()
				.text(Component.literal("x" + count))
				.size(new Vec2(5, 5))
				.translate(new Vec2(x, -1))
				.message(null);
	}

	@Override
	public void appendServerData(CompoundTag data, BlockAccessor accessor) {
		TileEntityMekanism be = (TileEntityMekanism) accessor.getBlockEntity();

		for (Upgrade upgrade : Upgrade.values()) {
			if (be.getComponent() != null) {
				if (be.getComponent().isUpgradeInstalled(upgrade)) {
					data.putInt(upgrade.getRawName(), be.getComponent().getUpgrades(upgrade));
				}
			}
		}
	}

	public ItemRegistryObject<ItemUpgrade> getUpgrade(Upgrade upgrade) {
		if (Upgrade.SPEED.equals(upgrade)) {
			return MekanismItems.SPEED_UPGRADE;
		}
		if (Upgrade.ENERGY.equals(upgrade)) {
			return MekanismItems.ENERGY_UPGRADE;
		}
		if (Upgrade.GAS.equals(upgrade)) {
			return MekanismItems.GAS_UPGRADE;
		}
		if (Upgrade.ANCHOR.equals(upgrade)) {
			return MekanismItems.ANCHOR_UPGRADE;
		}
		if (Upgrade.FILTER.equals(upgrade)) {
			return MekanismItems.FILTER_UPGRADE;
		}
		if (Upgrade.MUFFLING.equals(upgrade)) {
			return MekanismItems.MUFFLING_UPGRADE;
		}
		if (Upgrade.STONE_GENERATOR.equals(upgrade)) {
			return MekanismItems.STONE_GENERATOR_UPGRADE;
		}

		return null;
	}
}