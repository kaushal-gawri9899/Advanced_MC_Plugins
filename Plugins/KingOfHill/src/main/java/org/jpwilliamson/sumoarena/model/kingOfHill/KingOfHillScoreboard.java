package org.jpwilliamson.sumoarena.model.kingOfHill;

import org.bukkit.entity.Player;
//import org.mineacademy.arena.model.ArenaPlayer;
//import org.mineacademy.arena.model.ArenaTeam;
//import org.mineacademy.arena.model.team.TeamArena;
//import org.mineacademy.arena.model.team.TeamArenaScoreboard;
import org.jpwilliamson.sumoarena.model.team.TeamArena;
import org.jpwilliamson.sumoarena.model.team.TeamArenaScoreboard;
import org.mineacademy.fo.model.Replacer;
import org.jpwilliamson.sumoarena.model.*;

public class KingOfHillScoreboard extends TeamArenaScoreboard {
	/**
	 * Create a new scoreboard
	 *
	 * @param arena
	 */
	public KingOfHillScoreboard(final KingOfHillArena arena) {
		super(arena);
	}

	@Override
	protected String replaceVariablesLate(Player player, String message) {
		final KingOfHillSettings settings = getArena().getSettings();
		final ArenaPlayer cache = ArenaPlayer.getCache(player);
		final ArenaTeam team = cache.getArenaTeam();

		message = Replacer.of(message).replaceAll(
				"team", team != null ? team.getName() : "No team",
				"team_players", team != null ? team.getPlayers(getArena()).size() : "-");

		return message;
	}

	@Override
	protected void addEditRows() {
		super.addEditRows();
	}

	@Override
	public void onLobbyStart() {
		super.onLobbyStart();

		addRows(
				"Team: {team}",
				"Team players: {team_players}");
	}

	@Override
	public KingOfHillArena getArena() {
		return (KingOfHillArena)super.getArena();
	}
}
