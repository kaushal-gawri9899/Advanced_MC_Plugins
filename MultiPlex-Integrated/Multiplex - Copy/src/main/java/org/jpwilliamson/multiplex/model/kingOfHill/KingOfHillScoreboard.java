package org.jpwilliamson.multiplex.model.kingOfHill;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import be.maximvdw.placeholderapi.PlaceholderAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import org.jpwilliamson.multiplex.ArenaPlugin;
import org.jpwilliamson.multiplex.model.ArenaJoinMode;
import org.jpwilliamson.multiplex.model.ArenaPlayer;
import org.jpwilliamson.multiplex.model.ArenaTeam;
import org.jpwilliamson.multiplex.model.duels.DuelsArena;
import org.jpwilliamson.multiplex.model.team.TeamArenaScoreboard;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.util.Collections;


public class KingOfHillScoreboard{
//	/**
//	 * Create a new scoreboard
//	 *
//	 * @param arena
//	 */
//	public KingOfHillScoreboard(final KingOfHillArena arena) {
//		super(arena);
//	}
//
//	@Override
//	protected String replaceVariablesLate(Player player, String message) {
//		final KingOfHillSettings settings = getArena().getSettings();
//		final ArenaPlayer cache = ArenaPlayer.getCache(player);
//		final ArenaTeam team = cache.getArenaTeam();
//
//		message = Replacer.of(message).replaceAll(
//				"team", team != null ? team.getName() : "No team",
//				"team_players", team != null ? team.getPlayers(getArena()).size() : "-");
//
//		return message;
//	}
//
//	@Override
//	protected void addEditRows() {
//		super.addEditRows();
//	}
//
//	@Override
//	public void onLobbyStart() {
//		super.onLobbyStart();
//
//		addRows(
//				"Team: {team}",
//				"Team players: {team_players}");
//	}
//
//	@Override
//	public KingOfHillArena getArena() {
//		return (KingOfHillArena)super.getArena();
//	}

	@Getter
	private KingOfHillArena arena;

	public KingOfHillScoreboard(final KingOfHillArena arena){
		this.arena = arena;
	}

	public void displayCustomLobbyBoard(Player player){
		FeatherBoardAPI.showScoreboard(player, "koh-lobby");
	}

	public void displayCustomGameBoard(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player, "koh-lobby");
		FeatherBoardAPI.showScoreboard(player, "koh-game");
	}

	public void removeBoard(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player, "koh-game");
	}

	public void onLobby(){
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "koh_total_players",
					placeholderReplaceEvent -> {
						int players = arena.getPlayers(ArenaJoinMode.PLAYING).size();
						return Integer.toString(players);
					});


			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "koh_lobby_time",
					placeholderReplaceEvent -> Common.plural(arena.getSettings().getLobbyDuration().getTimeSeconds(), "second"));
		}
	}

	public void onStart(){
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class),"koh_spawnpoints",
					placeholderReplaceEvent -> Integer.toString(arena.getArenaPlayers(ArenaJoinMode.PLAYING).size()));

			//DoubtFull
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "koh_crystal_Location",
					placeholderReplaceEvent -> String.valueOf(arena.getCrystalLocation()));


			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "koh_game_duration", placeholderReplaceEvent ->
					Integer.toString(arena.getSettings().getGameDuration().getTimeSeconds()));
		}
	}
}
