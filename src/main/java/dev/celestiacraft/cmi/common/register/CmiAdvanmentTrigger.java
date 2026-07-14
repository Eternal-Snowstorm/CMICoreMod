package dev.celestiacraft.cmi.common.register;

import dev.celestiacraft.cmi.common.advancement.academic_fraud.AcademicFraudTrigger;
import dev.celestiacraft.cmi.common.advancement.electronic_blast_furnace.ElectronicBlastFurnaceTrigge;
import net.minecraft.advancements.CriteriaTriggers;

public class CmiAdvanmentTrigger {
	public static final AcademicFraudTrigger ACADEMIC_FRAUD = new AcademicFraudTrigger();
	public static final ElectronicBlastFurnaceTrigge ELECTRONIC_BLAST_FURNACE = new ElectronicBlastFurnaceTrigge();

	public static void register() {
		CriteriaTriggers.register(ACADEMIC_FRAUD);
	}
}