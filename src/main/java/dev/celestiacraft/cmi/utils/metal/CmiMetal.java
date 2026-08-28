package dev.celestiacraft.cmi.utils.metal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
public class CmiMetal {
	private final String id;
	private int meltingPoint;
	private String namespace;
	private String byProduct;
}