package org.jpwilliamson.arena.model.duels;

import org.bukkit.entity.Player;
import org.jpwilliamson.arena.model.ArenaScoreboard;
import org.mineacademy.fo.model.Replacer;

public class DuelsScoreboard extends ArenaScoreboard {
	/**
	 * Create a new scoreboard
	 *
	 * @param arena
	 */
	public DuelsScoreboard(DuelsArena arena) {
		super(arena);
	}

	/**
	 * Replace variables on the table
	 */
	@Override
	protected String replaceVariablesLate(final Player player, String message) {
		final DuelsSettings settings = getArena().getSettings();

		message = Replacer.of(message).replaceAll(
				"stadium_location", settings.getStadiumLocation(),
				"duels_fight_location_set", settings.getEntrances() != null,
				"valid_Lives", settings.getLives(),
				"game_duration", settings.getGameDuration());

		return message;
	}

	/**
	 * @see ArenaScoreboard#addEditRows()
	 */
	@Override
	protected void addEditRows() {
		addRows(
				"Duels Fight Location: _{duels_fight_location_set}",
				"Duels Stadium Location: {stadium_location}"
				);
	}

	/**
	 * @see ArenaScoreboard#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();

		addRows("Available Lives: {valid_Lives}" +
				"Game Duration: {game_duration}");
	}

	/**
	 * @see ArenaScoreboard#getArena()
	 */
	@Override
	public DuelsArena getArena() {
		return (DuelsArena) super.getArena();
	}
}
