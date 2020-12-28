package org.jpwilliamson.multiplex.model.survival;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import be.maximvdw.placeholderapi.PlaceholderAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jpwilliamson.multiplex.ArenaPlugin;
import org.jpwilliamson.multiplex.model.ArenaJoinMode;
import org.jpwilliamson.multiplex.model.ArenaScoreboard;
import org.jpwilliamson.multiplex.model.tntTag.TntTagArena;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;

public class SurvivalArenaScoreboard{
	/**
	 * Create a new scoreboard
	 *
	 * @param arena
	 */
	@Getter
	private SurvivalArena arena;

	public SurvivalArenaScoreboard(final SurvivalArena arena){
		this.arena = arena;
	}

	public void displayCustomLobbyBoard(Player player){
		FeatherBoardAPI.showScoreboard(player, "survival-lobby");
	}

	public void displayCustomGameBoard(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player, "survival-lobby");
		FeatherBoardAPI.showScoreboard(player, "survival-game");
	}

	public void removeBoard(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player, "survival-game");
	}

	public void onLobby(){
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "survival_players",
					placeholderReplaceEvent -> {
						int players = getArena().getPlayers(ArenaJoinMode.PLAYING).size();
						return Integer.toString(players);
					});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "survival_lobby_time",
					placeholderReplaceEvent -> Common.plural(arena.getStartCountdown().getTimeLeft(), "second"));
		}
	}

	public void onStart(){
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class),"survival_spawnpoints",
					placeholderReplaceEvent -> Integer.toString(arena.getSettings().getEntranceLocation().size()));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "survival_mob_spawnpoints",
					placeholderReplaceEvent -> Integer.toString(arena.getSettings().getMobSpawnpoints().size()));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "survival_waves",
					placeholderReplaceEvent -> Integer.toString(arena.getHeartbeat().getWave()));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "survival_shrinking_factor",
					placeholderReplaceEvent ->
					{
						double shrink = arena.getHeartbeat().getShrinkingFactor() * 100;
						return String.valueOf(Double.toString(shrink).endsWith("%"));
					});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class
			), "survival_shrinking_time",
					placeholderReplaceEvent -> Integer.toString(arena.getHeartbeat().getShrinkTime()));


			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "survival_game_duration", placeholderReplaceEvent ->
					Integer.toString(arena.getSettings().getGameDuration().getTimeSeconds()));


		}
	}


}
