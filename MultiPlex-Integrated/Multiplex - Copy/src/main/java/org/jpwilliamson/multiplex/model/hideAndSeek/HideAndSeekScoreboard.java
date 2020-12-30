package org.jpwilliamson.multiplex.model.hideAndSeek;


import be.maximvdw.featherboard.api.FeatherBoardAPI;
import be.maximvdw.placeholderapi.PlaceholderAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jpwilliamson.multiplex.ArenaPlugin;
import org.jpwilliamson.multiplex.model.ArenaJoinMode;
import org.jpwilliamson.multiplex.model.blackOps.BlackOpsArena;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;

public class HideAndSeekScoreboard{

	@Getter
	private HideAndSeekArena arena;

	public HideAndSeekScoreboard(final HideAndSeekArena arena){
		this.arena = arena;
	}

	// use to show the lobby scoreboard
	public void showLobbyBoard(Player player){
		FeatherBoardAPI.showScoreboard(player,"hide-and-seek-lobby");
	}

	// shows the game scoreboard
	public void showGameBoard(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player, "hide-and-seek-lobby");

		FeatherBoardAPI.showScoreboard(player,"hide-and-seek-game");
	}

	// stops showing any board
	public void stopShowing(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player,"hide-and-seek-game");
	}

	// handles the placeholder required during the lobby
	public void onLobby(){
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "hide-and-seek_players",
					placeholderReplaceEvent -> {
						int players = getArena().getPlayers(ArenaJoinMode.PLAYING).size();
						return Integer.toString(players);
					});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "hide-and-seek_time",
					placeholderReplaceEvent -> Common.plural(arena.getStartCountdown().getTimeLeft(), "second"));

		}
	}

	// handles placeholders required during the game
	public void onStart(){
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "hide-and-seek_players",
					placeholderReplaceEvent -> Integer.toString(arena.getPlayersInGame().size()));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "hide-and-seek_seeker", placeholderReplaceEvent -> {
				Player player = placeholderReplaceEvent.getPlayer();
				if(player.getUniqueId().equals(arena.getSeeker().getUniqueId()))
				return player.getDisplayName() + ": Seeker";
				else
					return player.getDisplayName() + ": Non Seeker";
			});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "hide-and-seek_shrink_time", placeholderReplaceEvent ->
					Integer.toString(arena.getHeartbeat().getShrinkTime()));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "hide-and-seek_game_duration", placeholderReplaceEvent ->
					Integer.toString(arena.getSettings().getGameDuration().getTimeSeconds()));
		}
	}
}
