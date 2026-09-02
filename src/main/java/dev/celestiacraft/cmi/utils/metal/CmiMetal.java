package dev.celestiacraft.cmi.utils.metal;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class CmiMetal {
	private final String id;
	private int meltingPoint;
	private String namespace;
	private String byProduct;

	public static final Map<String, CmiMetal> METALS = new HashMap<>();

	public CmiMetal(String id) {
		this.id = id;
	}

	public static CmiMetal register(String id, int meltingPoint, String namespace, String byProduct) {
		CmiMetal metal = METALS.computeIfAbsent(id, CmiMetal::new);

		metal.setMeltingPoint(meltingPoint);
		metal.setNamespace(namespace);
		metal.setByProduct(byProduct);

		return metal;
	}

	public static Collection<CmiMetal> getAll() {
		return METALS.values();
	}

	public static CmiMetal getMetal(String id) {
		return METALS.computeIfAbsent(id, CmiMetal::new);
	}
}