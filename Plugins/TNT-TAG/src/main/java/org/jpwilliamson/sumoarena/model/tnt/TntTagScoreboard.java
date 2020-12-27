package org.jpwilliamson.sumoarena.model.tnt;

import org.bukkit.entity.Player;
import org.jpwilliamson.sumoarena.model.Arena;
import org.jpwilliamson.sumoarena.model.ArenaScoreboard;
import org.jpwilliamson.sumoarena.model.eggwars.EggWarsArena;
import org.jpwilliamson.sumoarena.model.eggwars.EggWarsSettings;
import org.mineacademy.fo.model.Replacer;

public class TntTagScoreboard extends ArenaScoreboard {
	/**
	 * Create a new scoreboard
	 *
	 * @param arena
	 */
	public TntTagScoreboard(final TntTagArena arena) {
		super(arena);
	}
	@Override
	protected String replaceVariablesLate(final Player player, String message) {
		final TntTagSettings settings = getArena().getSettings();

		message = Replacer.of(message).replaceAll(
				"spawnpoints", settings.getEntrances().size(),
				"taggers", getArena().getHeartbeat().getActiveTaggers(),
				"nextExplosion", getArena().getHeartbeat().getExplosionTime(),
				"playersAlive", getArena().playersTag.size(),
				"gameDuration", settings.getGameDuration());

		return message;
	}

	/**
	 * @see ArenaScoreboard#addEditRows()
	 */
	@Override
	protected void addEditRows() {
		addRows(
				"Player spawnpoints: {spawnpoints}",
				"Active Taggers: {taggers}",
				"Explosion Time Left: {nextExplosion}",
				"Players Alive: {playersAlive}",
				"Game Duration: {gameDuration}");
	}

	@Override
	public void onStart() {
		//super.onStart();
		addRows(
				"Player spawnpoints: {spawnpoints}",
				"Active Taggers: {taggers}",
				"Explosion Time Left: {nextExplosion}",
				"Players Alive: {playersAlive}",
				"Game Duration: {gameDuration}");
	}

	/**
	 * @see ArenaScoreboard#getArena()
	 */
	@Override
	public TntTagArena getArena() {
		return (TntTagArena) super.getArena();
	}
}
