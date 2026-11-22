package com.timwoodcreates.roost;

import com.timwoodcreates.roost.api.ChickenType;
import com.timwoodcreates.roost.data.VanillaChickenType;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RoostChickens {
	private static final Map<Identifier, ChickenType> TYPES = new LinkedHashMap<>();

	private RoostChickens() {
	}

	/** Registers a chicken kind. The first registration for an id wins. */
	public static void register(ChickenType type) {
		ChickenType previous = TYPES.putIfAbsent(type.id(), type);
		if (previous != null) {
			Roost.LOGGER.warn("A chicken with the id {} is already registered, ignoring the duplicate", type.id());
		}
	}

	public static ChickenType get(Identifier id) {
		return TYPES.get(id);
	}

	public static ChickenType get(String id) {
		Identifier identifier = Identifier.tryParse(id);
		return identifier == null ? null : get(identifier);
	}

	public static Collection<ChickenType> all() {
		return Collections.unmodifiableCollection(TYPES.values());
	}

	public static List<ChickenType> sorted() {
		List<ChickenType> types = new ArrayList<>(TYPES.values());
		types.sort(Comparator.comparing(type -> type.id().toString()));
		return types;
	}

	public static void bootstrap() {
		register(VanillaChickenType.INSTANCE);
	}
}
