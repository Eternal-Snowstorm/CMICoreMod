package dev.celestiacraft.cmi.common.register;

import dev.celestiacraft.cmi.Cmi;
import dev.celestiacraft.cmi.common.advancement.academic_fraud.AcademicFraudTrigger;
import dev.celestiacraft.cmi.common.advancement.electronic_blast_furnace.ElectronicBlastFurnaceTrigge;
import net.minecraft.advancements.CriteriaTriggers;

public class CmiAdvanmentTrigger {
	public static final AcademicFraudTrigger ACADEMIC_FRAUD = new AcademicFraudTrigger();
	public static final ElectronicBlastFurnaceTrigge ELECTRONIC_BLAST_FURNACE = new ElectronicBlastFurnaceTrigge();

	public static void register() {
		Cmi.LOGGER.info("{} Advancements Triggers Registered!", Cmi.NAME);
		CriteriaTriggers.register(ACADEMIC_FRAUD);
		CriteriaTriggers.register(ELECTRONIC_BLAST_FURNACE);
	}
}