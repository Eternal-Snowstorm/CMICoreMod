package dev.celestiacraft.cmi.client.ponder.scene.tconstruct;

import dev.celestiacraft.libs.client.ponder.NebulaSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;
import slimeknights.tconstruct.smeltery.block.entity.CastingTankBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.ProxyTankBlockEntity;

public class TankScene {
	public static void tank(SceneBuilder builder, SceneBuildingUtil util) {
		NebulaSceneBuilder scene = new NebulaSceneBuilder(builder);
		scene.title("tank", "Use Fluid Containers");

		NebulaSceneBuilder.init5x5(scene, util);

		BlockPos center = util.grid().at(2, 2, 2);

		BlockPos gauge = util.grid().at(2, 1, 1);
		BlockPos tank = util.grid().at(2, 1, 3);
		BlockPos proxy = util.grid().at(1, 1, 2);
		BlockPos cast = util.grid().at(3, 1, 2);

		scene.idle(10);
		scene.world().showSection(util.select().layer(1), Direction.NORTH);
		scene.idle(15);

		scene.overlay().showText(45)
				.colored(PonderPalette.MEDIUM)
				.text("Tinker's fluid containers roughly divided into 4 categories");
		scene.idle(55);

		scene.overlay().showOutline(PonderPalette.GREEN, gauge, util.select().position(gauge), 50);
		scene.overlay().showText(55)
				.colored(PonderPalette.GREEN)
				.text("Gauge: holds 48 ingots of fluid, retains liquid when broken")
				.pointAt(util.vector().topOf(gauge))
				.attachKeyFrame();
		scene.idle(60);

		NebulaSceneBuilder.rotate(scene, 35, 180);

		scene.overlay().showOutline(PonderPalette.GREEN, tank, util.select().position(tank), 50);
		scene.overlay().showText(55)
				.colored(PonderPalette.GREEN)
				.text("Tank: holds 48 ingots of fluid, retains liquid when broken")
				.pointAt(util.vector().topOf(tank))
				.attachKeyFrame();
		scene.idle(60);

		NebulaSceneBuilder.rotate(scene, 35, -90);

		scene.overlay().showOutline(PonderPalette.GREEN, cast, util.select().position(cast), 50);
		scene.overlay().showText(55)
				.colored(PonderPalette.GREEN)
				.text("Casting Tank: interacts with items, retains liquid when broken")
				.pointAt(util.vector().topOf(cast))
				.attachKeyFrame();
		scene.idle(60);

		scene.idle(15);
		scene.world().modifyBlockEntity(cast, CastingTankBlockEntity.class, (entity) -> {
			entity.updateFluidTo(new FluidStack(TinkerFluids.moltenIron.get(), 4000));
		});

		scene.idle(30);
		scene.overlay().showControls(util.vector().topOf(cast), Pointing.DOWN, 20)
				.withItem(new ItemStack(TinkerSmeltery.searedTank.get(SearedTankBlock.TankType.INGOT_TANK)))
				.rightClick();

		scene.world().modifyBlockEntity(cast, CastingTankBlockEntity.class, (entity) -> {
			entity.updateFluidTo(FluidStack.EMPTY);
			ItemStack stack = new ItemStack(TinkerSmeltery.searedTank.get(SearedTankBlock.TankType.INGOT_TANK));
			CompoundTag fluid =  new CompoundTag();
			fluid.putString("FluidName", "tconstruct:molten_iron");
			fluid.putInt("Amount", 4000);
			stack.getOrCreateTag().put("tank", fluid);
			entity.setItem(1, stack);
		});

		scene.idle(30);

		NebulaSceneBuilder.rotate(scene, 35, 180);

		scene.overlay().showOutline(PonderPalette.GREEN, proxy, util.select().position(proxy), 50);
		scene.overlay().showText(55)
				.colored(PonderPalette.GREEN)
				.text("Proxy Tank: interacts with items, agents item inside")
				.pointAt(util.vector().topOf(proxy))
				.attachKeyFrame();
		scene.idle(60);

		scene.idle(15);
		scene.overlay().showControls(util.vector().topOf(proxy), Pointing.DOWN, 20)
				.withItem(new ItemStack(TinkerSmeltery.searedTank.get(SearedTankBlock.TankType.INGOT_TANK)))
				.rightClick();

		scene.world().modifyBlockEntity(proxy, ProxyTankBlockEntity.class, (entity) -> {
			ItemStack stack = new ItemStack(TinkerSmeltery.searedTank.get(SearedTankBlock.TankType.INGOT_TANK));
			CompoundTag fluid =  new CompoundTag();
			fluid.putString("FluidName", "tconstruct:molten_iron");
			fluid.putInt("Amount", 4000);
			stack.getOrCreateTag().put("tank", fluid);
			entity.getItemTank().setStack(stack);
		});

		scene.idle(60);
		scene.markAsFinished();
	}
}