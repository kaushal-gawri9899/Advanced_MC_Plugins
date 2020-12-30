package org.jpwilliamson.multiplex.model.buildbattle;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jpwilliamson.multiplex.model.ArenaHeartbeat;
import org.jpwilliamson.multiplex.model.ArenaJoinMode;
import org.mineacademy.fo.TimeUtil;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.*;

public class BuildBattleHeartBeat extends ArenaHeartbeat {

	BuildBattleSettings settings = null;
	List<Location> locations = new ArrayList<>();
	Map<String, Location> teleportLocations = new HashMap<>();
	List<Player> players = new ArrayList<>();
	Map<String, Integer> results = new HashMap<>();
	List<String> TeamOrder = new ArrayList<>();

	public BuildBattleHeartBeat(final BuildBattleArena arena){
		super(arena);
	}

	@Override
	protected void onTick() {
		final List<Integer> broadcastTimes = Arrays.asList(80, 90, 120);

		if(getTimeLeft() == 359){

			locations.clear();
			teleportLocations.clear();
			players.clear();
			results.clear();
			TeamOrder.clear();

			settings = (BuildBattleSettings) getArena().getSettings();
			locations = settings.getVotingPoints();
			players = getArena().getPlayers(ArenaJoinMode.PLAYING);

			for(Location location: locations){
				teleportLocations.put(settings.findVotingTeam(location).getName(), location);
			}
		}

		if (getTimeLeft() % 120 == 0 || (getTimeLeft() <= 70 && getTimeLeft() >= 60) || broadcastTimes.contains(getTimeLeft()))
			getArena().broadcastWarn("Build Time ends in less than " + TimeUtil.formatTimeGeneric(getTimeLeft() - 60));

		else if(getTimeLeft() == 59){

			for(Player player: players){
				player.getInventory().clear();
				giveVotingTools(player);
			}

			for(Player player : players){
				player.sendMessage(ChatColor.RED + "Voting Time Started For Red Team");
				player.teleport(teleportLocations.get("Red"));
			}
		}

		else if(getTimeLeft() == 49){
			for(Player player : players){
				player.sendMessage(ChatColor.GREEN + "Voting Time Started For Green Team");
				player.teleport(teleportLocations.get("Green"));
			}
		}

		else if(getTimeLeft() == 39){
			for(Player player : players){
				player.sendMessage(ChatColor.BLUE + "Voting Time Started For Blue Team");
				player.teleport(teleportLocations.get("Blue"));
			}
		}

		else if(getTimeLeft() == 29){
			for(Player player : players){
				player.sendMessage(ChatColor.YELLOW + "Voting Time Started For Yellow Team");
				player.teleport(teleportLocations.get("Yellow"));
			}
		}

		else if(getTimeLeft() == 19){
			for(Player player: players){
				player.sendMessage(ChatColor.LIGHT_PURPLE + "Voting Time is Over. Time For the Results");

				BuildBattleArena arena = (BuildBattleArena)getArena();

				results = sortByValue(arena.TeamScore);
			}

			for(Map.Entry<String,Integer> entry : results.entrySet()){
				TeamOrder.add(entry.getKey());
			}

		}

		else if(getTimeLeft() == 15){
			for(Player player: players){
				player.sendMessage("Team at 4th Position: " + TeamOrder.get(0));
			}
		}

		else if(getTimeLeft() == 12){
			for(Player player: players){
				player.sendMessage("Team at 3rd Position: " + TeamOrder.get(1));
			}
		}

		else if(getTimeLeft() == 9){
			for(Player player: players){
				player.sendMessage("Team at 2nd Position: " + TeamOrder.get(2));
			}
		}

		else if(getTimeLeft() == 6){
			for(Player player: players){
				player.sendMessage("Team at 1st Position: " + TeamOrder.get(3));
			}
		}
	}

	public static Map sortByValue(Map unsortMap) {
		List list = new LinkedList(unsortMap.entrySet());
		list.sort((o1, o2) -> ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue()));
		Map sortedMap = new LinkedHashMap();
		for (Object aList : list) {
			Map.Entry entry = (Map.Entry) aList;
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	private void giveVotingTools(Player player){
		player.getInventory().addItem(ItemCreator.of(CompMaterial.RED_GLAZED_TERRACOTTA,
				"Poop","","Worst Build").build().make());

		player.getInventory().addItem(ItemCreator.of(CompMaterial.BLUE_GLAZED_TERRACOTTA,
				"Ok","","Ok Build").build().make());

		player.getInventory().addItem(ItemCreator.of(CompMaterial.ORANGE_GLAZED_TERRACOTTA,
				"Good","","Good Build").build().make());

		player.getInventory().addItem(ItemCreator.of(CompMaterial.GREEN_GLAZED_TERRACOTTA,
				"Best","","Best Build").build().make());
	}
}
