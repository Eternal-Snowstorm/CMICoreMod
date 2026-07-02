package dev.celestiacraft.cmi.utils.metal;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CmiMetal {
	private final String id;

	private int meltingPoint;
	private String namespace;
	private String byProduct;

	public CmiMetal(String id) {
		this.id = id;
	}

	public CmiMetal namespace(String namespace) {
		this.namespace = namespace;
		return this;
	}

	public CmiMetal byProduct(String value) {
		byProduct = value;
		return this;
	}
}