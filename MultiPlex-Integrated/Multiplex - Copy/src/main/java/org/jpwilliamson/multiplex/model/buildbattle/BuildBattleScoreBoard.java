package org.jpwilliamson.multiplex.model.buildbattle;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import be.maximvdw.placeholderapi.PlaceholderAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jpwilliamson.multiplex.ArenaPlugin;
import org.jpwilliamson.multiplex.model.ArenaJoinMode;
import org.jpwilliamson.multiplex.model.blackOps.BlackOpsArena;
import org.jpwilliamson.multiplex.model.buildbattle.menus.VoteMenu;
import org.jpwilliamson.multiplex.model.team.TeamArenaScoreboard;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.plugin.SimplePlugin;

public class BuildBattleScoreBoard{
	@Getter
	private BuildBattleArena arena;

	public BuildBattleScoreBoard(final BuildBattleArena arena){
		this.arena = arena;
	}

	// use to show the lobby scoreboard
	public void showLobbyBoard(Player player){
		FeatherBoardAPI.showScoreboard(player,"build-battle-lobby");
	}

	// shows the game scoreboard
	public void showGameBoard(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player, "build-battle-lobby");

		FeatherBoardAPI.showScoreboard(player,"build-battle-game");
	}

	// stops showing any board
	public void stopShowing(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player,"build-battle-game");
	}

	// handles the placeholder required during the lobby
	public void onLobby(){
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "build_battle_players",
					placeholderReplaceEvent -> {
						int players = getArena().getPlayers(ArenaJoinMode.PLAYING).size();
						return Integer.toString(players);
					});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "build_battle_time",
					placeholderReplaceEvent -> Common.plural(arena.getStartCountdown().getTimeLeft(), "second"));

		}
	}

	// handles placeholders required during the game
	public void onStart(){
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "build_battle_alive_players",
					placeholderReplaceEvent -> Integer.toString(arena.getPlayers(ArenaJoinMode.PLAYING).size()));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "build_battle_remaining_build", placeholderReplaceEvent -> {
				String message = getArena().getHeartbeat().getTimeLeft() <= 59 ?
						Common.plural(0,"second"):
						Common.plural(getArena().getHeartbeat().getTimeLeft()-60, "second");
				return message;
			});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "build_battle_highest_voted_theme", placeholderReplaceEvent ->
					String.valueOf(VoteMenu.getHighestVotedTheme()));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "build_battle_voting_time", placeholderReplaceEvent ->
					Integer.toString(arena.getSettings().getVotingDuration().getTimeSeconds()));


			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "build_battle_game_round", placeholderReplaceEvent ->
					Integer.toString(arena.getSettings().getGameDuration().getTimeSeconds()));
		}
	}
}
