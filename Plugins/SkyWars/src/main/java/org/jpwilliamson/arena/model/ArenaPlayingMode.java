package org.jpwilliamson.arena.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ArenaPlayingMode {

	/**
	 * The player is playing the arena
	 */
	PRACTICE("Practice"),

	/**
	 * The player is editing the arena
	 */
	SOLO("Solo"),

	/**
	 * The player is dead and now spectating the arena
	 */
	TEAM("Team");

	@Getter
	private final String mode;
}

