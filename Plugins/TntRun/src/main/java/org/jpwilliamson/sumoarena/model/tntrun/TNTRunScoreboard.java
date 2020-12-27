package org.jpwilliamson.sumoarena.model.tntrun;

import org.bukkit.entity.Player;
import org.jpwilliamson.sumoarena.model.Arena;
import org.jpwilliamson.sumoarena.model.ArenaScoreboard;
import org.jpwilliamson.sumoarena.model.monster.MobArena;
import org.jpwilliamson.sumoarena.model.monster.MobArenaSettings;
import org.mineacademy.fo.model.Replacer;

public class TNTRunScoreboard extends ArenaScoreboard {
	/**
	 * Create a new scoreboard
	 *
	 * @param arena
	 */
	public TNTRunScoreboard(Arena arena) {
		super(arena);
	}

	/**
	 * Replace variables on the table
	 */
	@Override
	protected String replaceVariablesLate(final Player player, String message) {
		final TNTRunSettings settings = getArena().getSettings();

		message = Replacer.of(message).replaceAll(
				"speed_reduction", getArena().getSettings().getSpeedReductionFactor(),
				"player_entrance_set", settings.getEntrances()!=null,
				"stadium_location", settings.getTNTStadium()!= null,
				"game_duration", settings.getGameDuration(),
				"max_player_available", settings.getMaxPlayers());

		return message;
	}

	/**
	 * @see ArenaScoreboard#addEditRows()
	 */
	@Override
	protected void addEditRows() {
		addRows(
				"Spawn-point: {player_entrance_set}",
				"Speed Reduction Factor: {speed_reduction}," +
						"Game Duration: {game_duration}");
	}

	/**
	 * @see ArenaScoreboard#getArena()
	 */
	@Override
	public TNTRunArena getArena() {
		return (TNTRunArena) super.getArena();
	}
}
