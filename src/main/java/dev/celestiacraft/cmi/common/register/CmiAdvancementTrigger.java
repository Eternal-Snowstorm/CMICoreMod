package dev.celestiacraft.cmi.common.register;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.advancement.academic_fraud.AcademicFraudTrigger;
import dev.celestiacraft.cmi.common.advancement.electronic_blast_furnace.ElectronicBlastFurnaceTrigger;
import net.minecraft.advancements.CriteriaTriggers;

public class CmiAdvancementTrigger {
	public static final AcademicFraudTrigger ACADEMIC_FRAUD = new AcademicFraudTrigger();
	public static final ElectronicBlastFurnaceTrigger ELECTRONIC_BLAST_FURNACE = new ElectronicBlastFurnaceTrigger();

	public static void register() {
		Cmi.LOGGER.info("{} Advancements Triggers Registered!", Cmi.NAME);
		CriteriaTriggers.register(ACADEMIC_FRAUD);
		CriteriaTriggers.register(ELECTRONIC_BLAST_FURNACE);
	}
}