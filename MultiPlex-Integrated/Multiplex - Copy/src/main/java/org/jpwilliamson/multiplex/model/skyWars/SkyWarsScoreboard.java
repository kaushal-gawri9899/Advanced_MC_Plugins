package org.jpwilliamson.multiplex.model.skyWars;

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
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.plugin.SimplePlugin;

public class SkyWarsScoreboard
{
//	public SkyWarsScoreboard(final Arena arena) {
//		super(arena);
//	}
//
//	/**
//	 * Replace variables on the table
//	 */
//	@Override
//	protected String replaceVariablesLate(final Player player, String message) {
//		final SkyWarsSettings settings = getArena().getSettings();
//
//		message = Replacer.of(message).replaceAll(
//				"spawnpoints", settings.getEntrances().size(),
//				"chests", settings.getChests().size(),
//				"iron", settings.getIron().size(),
//				"gold", settings.getGold().size(),
//				"diamonds", settings.getDiamonds().size());
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
//				"Player spawnpoints: {spawnpoints}",
//				"Chests Locations: {chests}",
//				"Iron spawners: {iron}",
//				"Gold spawners: {gold}",
//				"Diamond spawners: {diamonds}");
//	}
//
//	/**
//	 * @see ArenaScoreboard#getArena()
//	 */
//	@Override
//	public SkyWarsArena getArena() {
//		return (SkyWarsArena) super.getArena();
//	}

	@Getter
	private SkyWarsArena arena;

	public SkyWarsScoreboard(final SkyWarsArena arena){
		this.arena = arena;
	}

	// use to show the lobby scoreboard
	public void showLobbyBoard(Player player){
		FeatherBoardAPI.showScoreboard(player,"sky-wars-lobby");
	}

	// shows the game scoreboard
	public void showGameBoard(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player, "sky-wars-lobby");

		FeatherBoardAPI.showScoreboard(player,"sky-wars-game");
	}

	// stops showing any board
	public void stopShowing(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player,"sky-wars-game");
	}

	// handles the placeholder required during the lobby
	public void onLobby(){
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "sky-wars_players",
					placeholderReplaceEvent -> {
						int players = getArena().getPlayers(ArenaJoinMode.PLAYING).size();
						return Integer.toString(players);
					});


			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "sky-wars_time",
					placeholderReplaceEvent -> Common.plural(arena.getStartCountdown().getTimeLeft(), "second"));

		}
	}

	// handles placeholders required during the game
	public void onStart(){
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "sky-wars_players",
					placeholderReplaceEvent -> Integer.toString(arena.playerPoints.size()));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "sky-wars_rewards", placeholderReplaceEvent -> {
				Player player = placeholderReplaceEvent.getPlayer();
				return Integer.toString(arena.getSettings().getChests().size());
			});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "sky-wars_game_time", placeholderReplaceEvent ->
					Integer.toString(arena.getSettings().getGameDuration().getTimeSeconds()));
		}
	}
}
