package org.jpwilliamson.sumoarena.model.sumo;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.sumoarena.model.ArenaHeartbeat;
import org.jpwilliamson.sumoarena.model.ArenaJoinMode;

public class SumoHeartBeat extends ArenaHeartbeat {

	private boolean semifinal = true;
	private boolean finale = true;

	public SumoHeartBeat(final SumoArena arena){
		super(arena);
	}

	@Override
	protected void onTick() {
		if (getArena().getHeartbeat().getTimeLeft() % 10 == 0)
			getArena().broadcastInfo("Visit our Website https://www.jpwilliamson.org/ for more exciting stuff!");

		if(!((SumoArena)getArena()).isFighting){

			if (getArena().getPlayers(ArenaJoinMode.PLAYING).size() == 3 && semifinal){
				getArena().broadcastInfo("This is the Semifinal Round");
				semifinal = false;
			}

			if (getArena().getPlayers(ArenaJoinMode.PLAYING).size() == 2 && finale){
				getArena().broadcastInfo("This is the Final Match");
				finale = false;
			}

			if(getArena().getPlayers(ArenaJoinMode.PLAYING).size() == 1){
				Player player = getArena().getPlayers(ArenaJoinMode.PLAYING).get(0);
				player.sendMessage("Congratulations " + player.getName() + "! You won this tournament.");
			}
		}

		if(((SumoArena)getArena()).isFighting){
			SumoArena arena = (SumoArena) getArena();

			Player player = arena.fightingPlayers.get(0);
			Player player1 = arena.fightingPlayers.get(1);

			Location location = player.getLocation();
			Location location1 = player1.getLocation();

			if(location.getY() < arena.getSettings().getLevel().getY()){
				player.sendMessage("You fall out of the arena.");
				player1.sendMessage("You won this round.");

				arena.playerRoundInfo.replace(player.getUniqueId(), 0);
				arena.playerRoundInfo.replace(player1.getUniqueId(), 1);
				player1.getInventory().addItem(new ItemStack(Material.IRON_INGOT,2));
				player1.getInventory().addItem(new ItemStack(Material.GOLD_INGOT,1));
				player1.getInventory().addItem(new ItemStack(Material.DIAMOND,1));
				player.teleport(arena.getSettings().getSumoStadium());
				player1.teleport(arena.getSettings().getSumoStadium());
				arena.isFighting = false;
			}

		}
	}
}
