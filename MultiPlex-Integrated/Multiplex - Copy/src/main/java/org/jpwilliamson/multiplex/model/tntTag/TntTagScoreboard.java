package org.jpwilliamson.multiplex.model.tntTag;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import be.maximvdw.placeholderapi.PlaceholderAPI;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jpwilliamson.multiplex.ArenaPlugin;
import org.jpwilliamson.multiplex.model.ArenaJoinMode;
import org.jpwilliamson.multiplex.model.tntTag.TntTagArena;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.plugin.SimplePlugin;

public class TntTagScoreboard {

	@Getter
	private TntTagArena arena;

	public TntTagScoreboard(final TntTagArena arena){
		this.arena = arena;
	}

	public void displayCustomLobbyBoard(Player player){
		FeatherBoardAPI.showScoreboard(player, "tnt-tag-lobby");
	}

	public void displayCustomGameBoard(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player, "tnt-tag-lobby");
		FeatherBoardAPI.showScoreboard(player, "tnt-tag-game");
	}

	public void removeBoard(Player player){
		FeatherBoardAPI.removeScoreboardOverride(player, "tnt-tag-game");
	}

	public void onLobby(){
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "tnt_tag_players",
					placeholderReplaceEvent -> {
						int players = getArena().getPlayers(ArenaJoinMode.PLAYING).size();
						return Integer.toString(players);
					});

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "tnt_tag_time",
					placeholderReplaceEvent -> Common.plural(arena.getStartCountdown().getTimeLeft(), "second"));
		}
	}

	public void onStart(){
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class),"tnt_tag_spawnpoints",
					placeholderReplaceEvent -> Integer.toString(arena.getSettings().getEntrances().size()));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "tnt_tag_active_taggers",
					placeholderReplaceEvent -> Integer.toString(arena.getHeartbeat().getActiveTaggers()));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "tnt_tag_next_explosion",
					placeholderReplaceEvent -> Integer.toString(arena.getHeartbeat().getExplosionTime())
			);

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "tnt_tag_players_alive", placeholderReplaceEvent ->
					Integer.toString(arena.playersTag.size()));

			PlaceholderAPI.registerPlaceholder(SimplePlugin.getPlugin(ArenaPlugin.class), "tnt_tag_game_duration", placeholderReplaceEvent ->
					Integer.toString(arena.getSettings().getGameDuration().getTimeSeconds()));
		}
	}


}
