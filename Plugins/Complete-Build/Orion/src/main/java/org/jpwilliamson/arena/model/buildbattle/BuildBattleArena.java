package org.jpwilliamson.arena.model.buildbattle;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.arena.menu.VoteMenu;
import org.jpwilliamson.arena.model.*;
import org.jpwilliamson.arena.model.team.TeamArena;

import dev.lone.itemsadder.Campfire.Events.MoveItemToCampfireEvent;
import dev.lone.itemsadder.api.ItemsAdder;
import java.util.*;

public class BuildBattleArena extends TeamArena {

	public static final String TYPE = "buildBattle";

	public List<BlockData> buildBlocks = new ArrayList<>();
	public Map<UUID,Map<String,Boolean>> PlayerVote = new HashMap<>();
	public Map<String, Integer> TeamScore = new HashMap<>();
	public VoteMenu voteMenu;

	public BuildBattleArena(final String name){
		super(TYPE, name);
		voteMenu = new VoteMenu(this, null);
	}

	public VoteMenu getVoteMenu(){
		return this.voteMenu;

	}

	@Override
	protected void onStart() {
		super.onStart();
		TeamScore.clear();
		buildBlocks.clear();
		PlayerVote.clear();
		List<Player> playerList = getPlayers(ArenaJoinMode.PLAYING);
		Map<String, Boolean> mp = new HashMap<>();

		mp.put("Red",false);
		mp.put("Yellow",false);
		mp.put("Blue",false);
		mp.put("Green",false);

		TeamScore.put("Red", 0);
		TeamScore.put("Blue", 0);
		TeamScore.put("Green",0);
		TeamScore.put("Yellow", 0);
		for(Player player: playerList){
			player.setGameMode(GameMode.CREATIVE);

			PlayerVote.put(player.getUniqueId(), mp);
		}
	}

	@Override
	protected ArenaSettings createSettings() {
		return new BuildBattleSettings(this);
	}

	public void setTeamScore(Player player, int point){
		String team = null;
		int timeleft = getHeartbeat().getTimeLeft();

		if(timeleft < 60 && timeleft >= 50)
			team = "Red";
		else if(timeleft < 50 && timeleft >= 40)
			team = "Green";
		else if(timeleft < 40 && timeleft >= 30)
			team = "Blue";
		else if(timeleft < 30 && timeleft >= 20)
			team = "Yellow";

		if(PlayerVote.get(player.getUniqueId()).get(team)){
			player.sendMessage(ChatColor.DARK_RED + "You cannot Change your vote!");
		}
		else{
			ArenaPlayer cache = ArenaPlayer.getCache(player);
			if(cache.getArenaTeam().getName().equalsIgnoreCase(team)){
				player.sendMessage(ChatColor.DARK_RED + "You cannot vote to your own team!");
			}
			else{


				player.sendMessage("You give " + point + " points to " + team + " team!");
				TeamScore.replace(team,TeamScore.get(team) + point);
				PlayerVote.get(player.getUniqueId()).replace(team,true);
			}
		}
	}

	@Override
	protected ArenaScoreboard createScoreboard() {
		return new BuildBattleScoreBoard(this);
	}

	@Override
	protected ArenaHeartbeat createHeartbeat() {
		return new BuildBattleHeartBeat(this);
	}

	@Override
	public BuildBattleSettings getSettings() {
		return (BuildBattleSettings) super.getSettings();
	}


}
