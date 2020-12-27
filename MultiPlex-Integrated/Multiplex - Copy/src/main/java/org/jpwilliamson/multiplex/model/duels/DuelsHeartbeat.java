package org.jpwilliamson.multiplex.model.duels;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.multiplex.model.Arena;
import org.jpwilliamson.multiplex.model.ArenaHeartbeat;


public class DuelsHeartbeat extends ArenaHeartbeat {
	/**
	 * Create a new countdown
	 *
	 * @param arena
	 */
	protected DuelsHeartbeat(Arena arena) {
		super(arena);
	}

	@Override
	protected void onTick() {

		if(getArena().getHeartbeat().getTimeLeft()%60==0)
			getArena().broadcastInfo("Visit our Website https://www.jpwilliamson.org/ for more exciting stuff!");

		if(!getArena().isDuels){

			if(getArena().playersInGame.size()>=2){
				getArena().manageDuelsFight();
			}

			if(getArena().playersInGame.size()==1){
				getArena().playersInDuelFight.clear();
				getArena().checkLastStanding();
			}
		}

		if(( getArena().isDuels)){

			Bukkit.broadcastMessage("Duesls fight started between" +
							getArena().playersInDuelFight.get(0) + " and " +
							getArena().playersInDuelFight.get(1));

			//Provide items to the players in Duels fight
			//Doing it in heartbeat so that player inventory never gets empty and fight can remain until one dies

			ItemStack crossBow = new ItemStack(Material.CROSSBOW,1);
			ItemStack sword = new ItemStack(Material.GOLDEN_SWORD,1);
			ItemStack arrow = new ItemStack(Material.TIPPED_ARROW,5);

			getArena().playersInDuelFight.get(0).getInventory().addItem(crossBow);
			getArena().playersInDuelFight.get(0).getInventory().addItem(sword);
			getArena().playersInDuelFight.get(0).getInventory().addItem(arrow);


			getArena().playersInDuelFight.get(1).getInventory().addItem(crossBow);
			getArena().playersInDuelFight.get(1).getInventory().addItem(sword);
			getArena().playersInDuelFight.get(1).getInventory().addItem(arrow);
		}
	}

	@Override
	public DuelsArena getArena() {
		return (DuelsArena) super.getArena();
	}
}
