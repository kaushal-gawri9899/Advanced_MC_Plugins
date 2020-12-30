package org.jpwilliamson.multiplex.model.blackOps;

//import be.maximvdw.featherboard.api.FeatherBoardAPI;
import be.maximvdw.featherboard.api.FeatherBoardAPI;
import be.maximvdw.placeholderapi.PlaceholderAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jpwilliamson.multiplex.ArenaPlugin;
import org.jpwilliamson.multiplex.model.ArenaJoinMode;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;

public class BlackOpsScoreboard {

	@Getter
	private BlackOpsArena arena;

	public BlackOpsScoreboard(final BlackOpsArena arena){
		this.arena = arena;
	}

	// use to show the lobby scoreboard
	public void showLobbyBoard(Player player){
		FeatherBoardAPI.showScoreboard(player,"black-ops-lobby");
	}

	// shows the game scoreboard
	public void showGameBoard(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player, "black-ops-lobby");

		FeatherBoardAPI.showScoreboard(player,"black-ops-game");
	}

	// stops showing any board
	public void stopShowing(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player,"black-ops-game");
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
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "black_ops_alive_players",
					placeholderReplaceEvent -> Integer.toString(arena.getPlayerKills().size()));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "black_ops_kills", placeholderReplaceEvent -> {
				Player player = placeholderReplaceEvent.getPlayer();
				return Integer.toString(arena.getPlayerKills().get(player));
			});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "black_ops_wave", placeholderReplaceEvent ->
					Integer.toString(arena.getHeartbeat().getHeartbeat().getWave()));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "black_ops_round", placeholderReplaceEvent ->
					Integer.toString(arena.getHeartbeat().getCurrentRound()));
		}
	}
}
