package org.jpwilliamson.multiplex.model.monster;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import be.maximvdw.placeholderapi.PlaceholderAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.jpwilliamson.multiplex.ArenaPlugin;
import org.jpwilliamson.multiplex.model.Arena;
import org.jpwilliamson.multiplex.model.ArenaJoinMode;
import org.jpwilliamson.multiplex.model.ArenaScoreboard;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.plugin.SimplePlugin;

/**
 * Represents a scoreboard for mob arenas
 */
public class MobArenaScoreboard {
//
//	/**
//	 * Create a new mob arena scoreboard
//	 *
//	 * @param arena
//	 */
//	public MobArenaScoreboard(final Arena arena) {
//		super(arena);
//	}
//
//	/**
//	 * Replace variables on the table
//	 */
//	@Override
//	protected String replaceVariablesLate(final Player player, String message) {
//		final MobArenaSettings settings = getArena().getSettings();
//
//		message = Replacer.of(message).replaceAll(
//				"wave", getArena().getHeartbeat().getWave(),
//				"mob_spawnpoint_set", settings.getMobSpawnpoints().size(),
//				"spawnpoint_set", settings.getEntranceLocation() != null);
//
//		return message;
//	}
//
//	/**
//	 * @see ArenaScoreboard#addEditRows()
//	 */
//	@Override
//	protected void addEditRows() {
//		addRows(
//				"Spawnpoint: _{spawnpoint_set}",
//				"Mob spawnpoints: {mob_spawnpoint_set}");
//	}
//
//	/**
//	 * @see ArenaScoreboard#onStart()
//	 */
//	@Override
//	public void onStart() {
//		super.onStart();
//
//		addRows("Wave: {wave}");
//	}
//
//	/**
//	 * @see ArenaScoreboard#getArena()
//	 */
//	@Override
//	public MobArena getArena() {
//		return (MobArena) super.getArena();
//	}
	@Getter
	private MobArena arena;

	public MobArenaScoreboard(final MobArena arena){
		this.arena = arena;
	}

	// use to show the lobby scoreboard
	public void showLobbyBoard(Player player){
		FeatherBoardAPI.showScoreboard(player,"monster-lobby");
	}

	// shows the game scoreboard
	public void showGameBoard(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player, "monster-lobby");

		FeatherBoardAPI.showScoreboard(player,"monster-game");
	}

	// stops showing any board
	public void stopShowing(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player,"monster-game");
	}

	// handles the placeholder required during the lobby
	public void onLobby(){
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "black_ops_players",
					placeholderReplaceEvent -> {
						int players = getArena().getPlayers(ArenaJoinMode.PLAYING).size();
						return Integer.toString(players);
					});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "black_ops_time",
					placeholderReplaceEvent -> Common.plural(arena.getStartCountdown().getTimeLeft(), "second"));

		}
	}

	// handles placeholders required during the game
	public void onStart(){
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "monster_alive_players",
					placeholderReplaceEvent -> Integer.toString(arena.getPlayers(ArenaJoinMode.PLAYING).size()));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "monster_spawns", placeholderReplaceEvent ->
					Integer.toString(arena.getSettings().getMobSpawnpoints().size()));


			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "monster_wave", placeholderReplaceEvent ->
					Integer.toString(arena.getHeartbeat().getWave()));


			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "monster_duration_wave", placeholderReplaceEvent ->
					Integer.toString(arena.getSettings().getWaveDuration().getTimeSeconds()));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "monster_game_duration", placeholderReplaceEvent ->
					Integer.toString(arena.getSettings().getGameDuration().getTimeSeconds()));


		}
	}


}
