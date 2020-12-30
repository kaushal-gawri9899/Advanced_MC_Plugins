package org.jpwilliamson.multiplex.model.duels;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import be.maximvdw.placeholderapi.PlaceholderAPI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.multiplex.ArenaPlugin;
import org.jpwilliamson.multiplex.model.ArenaJoinMode;
import org.jpwilliamson.multiplex.model.ArenaScoreboard;
import org.jpwilliamson.multiplex.model.blackOps.BlackOpsArena;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.plugin.SimplePlugin;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.Collections;

public class DuelsScoreboard{
//	/**
//	 * Create a new scoreboard
//	 *
//	 * @param arena
//	 */
//	public DuelsScoreboard(DuelsArena arena) {
//		super(arena);
//	}
//
//	/**
//	 * Replace variables on the table
//	 */
//	@Override
//	protected String replaceVariablesLate(final Player player, String message) {
//		final DuelsSettings settings = getArena().getSettings();
//
//		message = Replacer.of(message).replaceAll(
//				"stadium_location", settings.getStadiumLocation(),
//				"duels_fight_location_set", settings.getEntrances() != null,
//				"valid_Lives", settings.getLives(),
//				"game_duration", settings.getGameDuration());
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
//				"Duels Fight Location: _{duels_fight_location_set}",
//				"Duels Stadium Location: {stadium_location}"
//				);
//	}
//
//	/**
//	 * @see ArenaScoreboard#onStart()
//	 */
//	@Override
//	public void onStart() {
//		super.onStart();
//
//		addRows("Available Lives: {valid_Lives}" +
//				"Game Duration: {game_duration}");
//	}
//
//	/**
//	 * @see ArenaScoreboard#getArena()
//	 */
//	@Override
//	public DuelsArena getArena() {
//		return (DuelsArena) super.getArena();
//	}

	@Getter
	private DuelsArena arena;

	public DuelsScoreboard(final DuelsArena arena){
		this.arena = arena;
	}

	public void displayCustomLobbyBoard(Player player){
		FeatherBoardAPI.showScoreboard(player, "duels-lobby");
	}

	public void displayCustomGameBoard(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player, "duels-lobby");
		FeatherBoardAPI.showScoreboard(player, "duels-game");
	}

	public void removeBoard(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player, "duels-game");
	}

	public void onLobby(){
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "duels_total_players",
					placeholderReplaceEvent -> {
						int players = arena.getPlayers(ArenaJoinMode.PLAYING).size();
						return Integer.toString(players);
					});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "current_players_in_battle",
					placeholderReplaceEvent -> {
						int playerSize = arena.playersInDuelFight.size();
						return Integer.toString(playerSize);
					});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "duels_lobby_time",
					placeholderReplaceEvent -> Common.plural(arena.getSettings().getLobbyDuration().getTimeSeconds(), "second"));
		}
	}

	public void onStart(){
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class),"duels_spawnpoints",
					placeholderReplaceEvent -> Integer.toString(arena.getSettings().getEntrances().size()));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "duels_max_hits",
					placeholderReplaceEvent -> Integer.toString(Collections.max(arena.manageHits.values())));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "duels_reward_1",
					placeholderReplaceEvent -> String.valueOf(Material.APPLE));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "duels_reward_2",
					placeholderReplaceEvent -> String.valueOf(Material.IRON_SWORD));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "duels_reward_3",
					placeholderReplaceEvent -> String.valueOf(Material.DIAMOND_CHESTPLATE));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "duels_reward_4",
					placeholderReplaceEvent -> String.valueOf(Material.ENCHANTED_GOLDEN_APPLE));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "duels_game_duration", placeholderReplaceEvent ->
					Integer.toString(arena.getSettings().getGameDuration().getTimeSeconds()));
		}
	}

}
