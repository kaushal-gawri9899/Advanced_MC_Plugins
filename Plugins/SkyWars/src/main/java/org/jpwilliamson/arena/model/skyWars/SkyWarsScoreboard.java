package org.jpwilliamson.arena.model.skyWars;

import org.bukkit.entity.Player;
import org.jpwilliamson.arena.model.Arena;
import org.jpwilliamson.arena.model.ArenaScoreboard;
import org.mineacademy.fo.model.Replacer;

public class SkyWarsScoreboard extends ArenaScoreboard
{
	public SkyWarsScoreboard(final Arena arena) {
		super(arena);
	}

	/**
	 * Replace variables on the table
	 */
	@Override
	protected String replaceVariablesLate(final Player player, String message) {
		final SkyWarsSettings settings = getArena().getSettings();

		message = Replacer.of(message).replaceAll(
				"spawnpoints", settings.getEntrances().size(),
				"chests", settings.getChests().size(),
				"iron", settings.getIron().size(),
				"gold", settings.getGold().size(),
				"diamonds", settings.getDiamonds().size());

		return message;
	}

	/**
	 * @see ArenaScoreboard#addEditRows()
	 */
	@Override
	protected void addEditRows() {
		addRows(
				"Player spawnpoints: {spawnpoints}",
				"Chests Locations: {chests}",
				"Iron spawners: {iron}",
				"Gold spawners: {gold}",
				"Diamond spawners: {diamonds}");
	}

	/**
	 * @see ArenaScoreboard#getArena()
	 */
	@Override
	public SkyWarsArena getArena() {
		return (SkyWarsArena) super.getArena();
	}
}
