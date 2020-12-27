package org.jpwilliamson.multiplex.model.team;

import org.bukkit.entity.Player;
import org.jpwilliamson.multiplex.model.ArenaPlayer;
import org.jpwilliamson.multiplex.model.ArenaScoreboard;
import org.jpwilliamson.multiplex.model.ArenaTeam;
import org.mineacademy.fo.model.Replacer;

/**
 * Represents a scoreboard for mob arenas
 */
public class TeamArenaScoreboard extends ArenaScoreboard {

	/**
	 * Create a new mob arena scoreboard
	 *
	 * @param arena
	 */
	public TeamArenaScoreboard(final TeamArena arena) {
		super(arena);
	}

//	/**
//	 * @see ArenaScoreboard#replaceVariablesLate(java.lang.String)
//	 */
	@Override
	protected String replaceVariablesLate(final Player player, String message) {
		final TeamArenaSettings settings = getArena().getSettings();
		final ArenaPlayer cache = ArenaPlayer.getCache(player);
		final ArenaTeam team = cache.getArenaTeam();

		message = Replacer.of(message).replaceAll(
				"setup", settings.isSetup(),
				"team", team != null ? team.getName() : "No team",
				"team_players", team != null ? team.getPlayers(getArena()).size() : "-");

		return message;
	}

	/**
	 * @see ArenaScoreboard#addEditRows()
	 */
	@Override
	protected void addEditRows() {
		addRows("Is setup: {setup}");
	}

	/**
	 * @see ArenaScoreboard#onLobbyStart()
	 */
	@Override
	public void onLobbyStart() {
		super.onLobbyStart();

		addRows(
				"Team: {team}",
				"Team players: {team_players}");
	}

	/**
	 * @see ArenaScoreboard#getArena()
	 */
	@Override
	public TeamArena getArena() {
		return (TeamArena) super.getArena();
	}
}
