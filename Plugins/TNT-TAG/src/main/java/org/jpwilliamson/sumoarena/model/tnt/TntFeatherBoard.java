package org.jpwilliamson.sumoarena.model.tnt;

//import be.maximvdw.featherboard.api.FeatherBoardAPI;
//import be.maximvdw.placeholderapi.PlaceholderAPI;
//import be.maximvdw.placeholderapi.PlaceholderAPI;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jpwilliamson.sumoarena.ArenaPlugin;
import org.jpwilliamson.sumoarena.model.ArenaPlayer;
import org.mineacademy.fo.plugin.SimplePlugin;

public class TntFeatherBoard {

	@Getter
	private TntTagArena arena;

	public TntFeatherBoard(final TntTagArena arena){
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

	public void onLobbyStart(){
		if(Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){

		}
	}

}
