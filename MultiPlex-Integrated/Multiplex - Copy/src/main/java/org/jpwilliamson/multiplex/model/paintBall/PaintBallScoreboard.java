package org.jpwilliamson.multiplex.model.paintBall;


import be.maximvdw.featherboard.api.FeatherBoardAPI;
import be.maximvdw.placeholderapi.PlaceholderAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jpwilliamson.multiplex.ArenaPlugin;
import org.jpwilliamson.multiplex.model.ArenaJoinMode;
import org.jpwilliamson.multiplex.model.ArenaPlayer;
import org.jpwilliamson.multiplex.model.ArenaTeam;
import org.jpwilliamson.multiplex.model.team.TeamArena;
import org.jpwilliamson.multiplex.model.team.TeamArenaScoreboard;
import org.jpwilliamson.multiplex.model.tntTag.TntTagArena;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;

public class PaintBallScoreboard{

	/**
	 * Create a new mob arena scoreboard
	 *
	 * @param arena
	 */
//	public PaintBallScoreboard(TeamArena arena) {
//		super(arena);
//	}
	@Getter
	private PaintBallArena arena;

	public PaintBallScoreboard(final PaintBallArena arena){
		this.arena = arena;
	}

	public void displayCustomLobbyBoard(Player player){
		FeatherBoardAPI.showScoreboard(player, "paint-ball-lobby");
	}

	public void displayCustomGameBoard(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player, "paint-ball-lobby");
		FeatherBoardAPI.showScoreboard(player, "paint-ball-game");
	}

	public void removeBoard(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player, "paint-ball-game");
	}

	public void onLobby(){

		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "paint_ball_players",
					placeholderReplaceEvent -> {
						int players = arena.getPlayersInGame().size();
						return Integer.toString(players);
					});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "red_team_size",
					placeholderReplaceEvent -> {
				     int countRed =0;
				     for(Player player : arena.getPlayers(ArenaJoinMode.PLAYING)){
				     	ChatColor team = ArenaPlayer.getCache(player).getArenaTeam().getColor();
				     	if(team.equals(ChatColor.RED)){
				     		countRed++;
						}
					 }
				     return Integer.toString(countRed);
					});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "blue_team_size",
					placeholderReplaceEvent -> {
						int countBlue =0;
						for(Player player : arena.getPlayers(ArenaJoinMode.PLAYING)){
							ChatColor team = ArenaPlayer.getCache(player).getArenaTeam().getColor();
							if(team.equals(ChatColor.BLUE)){
								countBlue++;
							}
						}
						return Integer.toString(countBlue);
					});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "yellow_team_size",
					placeholderReplaceEvent -> {
						int countYellow =0;
						for(Player player : arena.getPlayers(ArenaJoinMode.PLAYING)){
							ChatColor team = ArenaPlayer.getCache(player).getArenaTeam().getColor();
							if(team.equals(ChatColor.YELLOW)){
								countYellow++;
							}
						}
						return Integer.toString(countYellow);
					});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "green_team_size",
					placeholderReplaceEvent -> {
						int countGreen =0;
						for(Player player : arena.getPlayers(ArenaJoinMode.PLAYING)){
							ChatColor team = ArenaPlayer.getCache(player).getArenaTeam().getColor();
							if(team.equals(ChatColor.GREEN)){
								countGreen++;
							}
						}
						return Integer.toString(countGreen);
					});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "paint_ball_time",
					placeholderReplaceEvent -> Common.plural(arena.getStartCountdown().getTimeLeft(), "second"));


		}
	}

	public void onStart(){

		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "paint_ball_game_players",
					placeholderReplaceEvent -> {
						int players = arena.getPlayersInGame().size();
						return Integer.toString(players);
					});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "paint_ball_max_bases",
					placeholderReplaceEvent -> Integer.toString(arena.getSettings().getMaxNeutralBases()));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "red_team_points",
					placeholderReplaceEvent -> Integer.toString(arena.getDamageRed())
			);

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "blue_team_points",
					placeholderReplaceEvent -> Integer.toString(arena.getDamageBlue())
			);

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "green_team_points",
					placeholderReplaceEvent -> Integer.toString(arena.getDamageGreen())
			);

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "yellow_team_points",
					placeholderReplaceEvent -> Integer.toString(arena.getDamageYellow())
			);

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "paint_ball_game_duration", placeholderReplaceEvent ->
					Integer.toString(arena.getSettings().getGameDuration().getTimeSeconds()));
		}
	}
}
