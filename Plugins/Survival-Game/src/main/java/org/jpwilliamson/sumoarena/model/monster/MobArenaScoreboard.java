package org.jpwilliamson.sumoarena.model.monster;

import org.bukkit.entity.Player;
import org.jpwilliamson.sumoarena.model.Arena;
import org.jpwilliamson.sumoarena.model.ArenaScoreboard;
import org.mineacademy.fo.model.Replacer;

/**
 * Represents a scoreboard for mob arenas
 */
public class MobArenaScoreboard extends ArenaScoreboard {

	/**
	 * Create a new mob arena scoreboard
	 *
	 * @param arena
	 */
	public MobArenaScoreboard(final Arena arena) {
		super(arena);
	}

	/**
	 * Replace variables on the table
	 */
	@Override
	protected String replaceVariablesLate(final Player player, String message) {
		final MobArenaSettings settings = getArena().getSettings();

		message = Replacer.of(message).replaceAll(
				"wave", getArena().getHeartbeat().getWave(),
				"mob_spawnpoint_set", settings.getMobSpawnpoints().size(),
				"spawnpoint_set", settings.getEntranceLocation() != null);

		return message;
	}

	/**
	 * @see ArenaScoreboard#addEditRows()
	 */
	@Override
	protected void addEditRows() {
		addRows(
				"Spawnpoint: _{spawnpoint_set}",
				"Mob spawnpoints: {mob_spawnpoint_set}");
	}

	/**
	 * @see ArenaScoreboard#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();

		addRows("Wave: {wave}");
	}

	/**
	 * @see ArenaScoreboard#getArena()
	 */
	@Override
	public MobArena getArena() {
		return (MobArena) super.getArena();
	}
}
