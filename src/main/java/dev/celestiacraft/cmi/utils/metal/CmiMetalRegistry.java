package dev.celestiacraft.cmi.utils.metal;

import java.util.HashMap;
import java.util.Map;

public class CmiMetalRegistry {
	public static final Map<String, CmiMetal> METALS = new HashMap<>();

	public static CmiMetal register(String id, int meltingPoint, String namespace, String byProduct) {
		CmiMetal metal = METALS.computeIfAbsent(id, CmiMetal::new);

		metal.setMeltingPoint(meltingPoint);
		metal.setNamespace(namespace);
		metal.setByProduct(byProduct);

		return metal;
	}

	public static Iterable<CmiMetal> getAll() {
		return METALS.values();
	}

	public static CmiMetal get(String id) {
		return METALS.computeIfAbsent(id, CmiMetal::new);
	}

	public static CmiMetal getMetal(String id) {
		return get(id);
	}
}