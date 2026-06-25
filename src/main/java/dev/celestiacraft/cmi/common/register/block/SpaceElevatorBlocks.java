package dev.celestiacraft.cmi.common.register.block;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.SpaceElevatorBaseConsoleBlock;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.SpaceElevatorBaseConsoleBlockItem;
import dev.celestiacraft.cmi.common.block.space_elevator_base_console.io.SpaceElevatorIoPortBlock;
import dev.celestiacraft.cmi.common.block.space_elevator_top.SpaceElevatorTopBlock;
import dev.celestiacraft.cmi.common.block.space_elevator_top.SpaceElevatorTopBlockItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SpaceElevatorBlocks {
	public static final BlockEntry<SpaceElevatorBaseConsoleBlock> SPACE_ELEVATOR_BASE_CONSOLE;
	public static final BlockEntry<SpaceElevatorIoPortBlock> SPACE_ELEVATOR_IO_PORT;
	public static final BlockEntry<SpaceElevatorTopBlock> SPACE_ELEVATOR_TOP;

	static {
		SPACE_ELEVATOR_BASE_CONSOLE = Cmi.REGISTRATE.block("space_elevator_base_console", SpaceElevatorBaseConsoleBlock::new)
				.initialProperties(SharedProperties::stone)
				.properties(BlockBehaviour.Properties::noOcclusion)
				.item(SpaceElevatorBaseConsoleBlockItem::new)
				.model(SpaceElevatorBaseConsoleBlockItem.genItemModel())
				.build()
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_IRON_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate(SpaceElevatorBaseConsoleBlock.genBlockState())
				.register();

		SPACE_ELEVATOR_IO_PORT = Cmi.REGISTRATE.block("space_elevator_io_port", SpaceElevatorIoPortBlock::new)
				.initialProperties(SharedProperties::stone)
				.blockstate(SpaceElevatorIoPortBlock.genBlockState())
				.register();

		SPACE_ELEVATOR_TOP = Cmi.REGISTRATE.block("space_elevator_top", SpaceElevatorTopBlock::new)
				.initialProperties(SharedProperties::stone)
				.properties(BlockBehaviour.Properties::noOcclusion)
				.item(SpaceElevatorTopBlockItem::new)
				.model(SpaceElevatorTopBlock.genItemModel())
				.build()
				.tag(BlockTags.MINEABLE_WITH_PICKAXE)
				.tag(BlockTags.NEEDS_IRON_TOOL)
				.tag(AllTags.AllBlockTags.WRENCH_PICKUP.tag)
				.blockstate(SpaceElevatorTopBlock.genBlockState())
				.register();
	}

	public static void register() {
		Cmi.LOGGER.info("{} Space Elevator Blocks Registered!", Cmi.MODID);
	}
}