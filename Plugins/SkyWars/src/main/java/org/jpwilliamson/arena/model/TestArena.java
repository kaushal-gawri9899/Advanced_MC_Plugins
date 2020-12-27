package org.jpwilliamson.arena.model;

public class TestArena extends Arena {

	private static final String TYPE = "test";

	/**
	 * Create a new arena. If the arena settings do not yet exist,
	 * they are created automatically.
	 *
	 * @param type
	 * @param name
	 */
	protected TestArena(String name) {
		super(TYPE, name);
	}

}
