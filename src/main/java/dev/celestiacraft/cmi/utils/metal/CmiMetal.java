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

	public CmiMetal meltingPoint(int val) {
		this.meltingPoint = val;
		return this;
	}

	public CmiMetal namespace(String val) {
		this.namespace = val;
		return this;
	}

	public CmiMetal byProduct(String val) {
		this.byProduct = val;
		return this;
	}
}