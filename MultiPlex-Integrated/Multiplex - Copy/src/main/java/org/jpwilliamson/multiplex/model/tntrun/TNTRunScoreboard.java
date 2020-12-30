package org.jpwilliamson.multiplex.model.tntrun;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import be.maximvdw.placeholderapi.PlaceholderAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import org.jpwilliamson.multiplex.ArenaPlugin;
import org.jpwilliamson.multiplex.model.Arena;
import org.jpwilliamson.multiplex.model.ArenaJoinMode;
import org.jpwilliamson.multiplex.model.ArenaScoreboard;
import org.jpwilliamson.multiplex.model.blackOps.BlackOpsArena;
import org.jpwilliamson.multiplex.model.tntTag.TntTagArena;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.plugin.SimplePlugin;

public class TNTRunScoreboard{
//	/**
//	 * Create a new scoreboard
//	 *
//	 * @param arena
//	 */
//	public TNTRunScoreboard(Arena arena) {
//		super(arena);
//	}
//
//	/**
//	 * Replace variables on the table
//	 */
//	@Override
//	protected String replaceVariablesLate(final Player player, String message) {
//		final TNTRunSettings settings = getArena().getSettings();
//
//		message = Replacer.of(message).replaceAll(
//				"speed_reduction", getArena().getSettings().getSpeedReductionFactor(),
//				"player_entrance_set", settings.getEntrances()!=null,
//				"stadium_location", settings.getTNTStadium()!= null,
//				"game_duration", settings.getGameDuration(),
//				"max_player_available", settings.getMaxPlayers());
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
//				"Spawn-point: {player_entrance_set}",
//				"Speed Reduction Factor: {speed_reduction}," +
//						"Game Duration: {game_duration}");
//	}
//
//	/**
//	 * @see ArenaScoreboard#getArena()
//	 */
//	@Override
//	public TNTRunArena getArena() {
//		return (TNTRunArena) super.getArena();
//	}

	@Getter
	private TNTRunArena arena;

	public TNTRunScoreboard(final TNTRunArena arena){
		this.arena = arena;
	}

	// use to show the lobby scoreboard
	public void showLobbyBoard(Player player){
		FeatherBoardAPI.showScoreboard(player,"tnt-run-lobby");
	}

	// shows the game scoreboard
	public void showGameBoard(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player, "tnt-run-lobby");

		FeatherBoardAPI.showScoreboard(player,"tnt-run-game");
	}

	// stops showing any board
	public void stopShowing(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player,"tnt-run-game");
	}

	// handles the placeholder required during the lobby
	public void onLobby(){
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "tnt-run_players",
					placeholderReplaceEvent -> {
						int players = getArena().getPlayers(ArenaJoinMode.PLAYING).size();
						return Integer.toString(players);
					});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "tnt-run_time",
					placeholderReplaceEvent -> Common.plural(arena.getStartCountdown().getTimeLeft(), "second"));

		}
	}

	// handles placeholders required during the game
	public void onStart(){
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "tnt-run_total_players",
					placeholderReplaceEvent -> Integer.toString(arena.getSettings().getEntrances().size()));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "tnt_run_speed_decrease", placeholderReplaceEvent -> {
				Player player = placeholderReplaceEvent.getPlayer();
				return Double.toString(arena.getSettings().getSpeedReductionFactor());
			});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "tnt_run_game_duration", placeholderReplaceEvent ->
					Integer.toString(arena.getSettings().getGameDuration().getTimeSeconds()));
		}
	}
}
