package top.nebula.cmi.utils;

import com.simibubi.create.foundation.utility.LangBuilder;
import top.nebula.cmi.Cmi;

public class ModLang {
	public static LangBuilder builder() {
		return new LangBuilder(Cmi.MODID);
	}
}