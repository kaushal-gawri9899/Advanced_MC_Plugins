package org.jpwilliamson.sumoarena.model.tntrun;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.sumoarena.model.Arena;
import org.jpwilliamson.sumoarena.model.ArenaHeartbeat;
import org.jpwilliamson.sumoarena.model.ArenaJoinMode;
import org.jpwilliamson.sumoarena.model.ArenaLeaveReason;
import org.jpwilliamson.sumoarena.model.monster.MobArena;

import java.util.List;


public class TNTRunHeartbeat extends ArenaHeartbeat {

	/**
	 * Create a new countdown
	 *
	 * @param arena
	 */
	protected TNTRunHeartbeat(final TNTRunArena arena) {
		super(arena);
	}

	@Override
	protected void onTick() {

		List<Player> inGamePlayers = getArena().getPlayers(ArenaJoinMode.PLAYING);
		final int elapsedSeconds = getCountdownSeconds() - getTimeLeft();


		for(Player player : inGamePlayers){
			//This breaks the blocks present under the feet of players
			//Basically replace the block under player's feet with air
			Block block = player.getLocation().subtract(0, 1, 0).getBlock();
			//If the blocks are not situated at the bottom most level
			//then only set type to AIR
			if(block.getY()!=getArena().getSettings().getLevelLocation().getY()){
				block.setType(Material.AIR);
			}
		}

		for(Player player : inGamePlayers){

			//As in TNT run map we have different blocks level situated at different height
			//If the y coordinate of the player location is equal to the y coordinate
			//of the bottom most level
			//The Player loses the game and is teleported back to the stadium
			if(player.getLocation().getY() == getArena().getSettings().getLevelLocation().getY()){

				inGamePlayers.remove(player);

				getArena().broadcast(player.getDisplayName() + " is ejected! We have "
						+ inGamePlayers.size() + " more players left");

				getArena().teleportToStadium(player);
				getArena().leavePlayer(player, ArenaLeaveReason.CANT_SURVIVE);
			}
		}

		float playerSpeed = 0;
		//On every 30 seconds, reduce player speed to half
		if(elapsedSeconds%60==0){
			for (Player player : inGamePlayers){
				getArena().broadcast(ChatColor.RED + "Your speed is being " +
						"reduced to half for 10 seconds" + ChatColor.RESET);
				playerSpeed = player.getWalkSpeed();
				player.setWalkSpeed((float) (player.getWalkSpeed()
						*getArena().getSettings().getSpeedReductionFactor()));
				player.setSprinting(false);
			}
		}
		//This verifies that player speed is reduced only for 10 seconds
		if(elapsedSeconds%60==10){
			for(Player player : inGamePlayers){

				getArena().broadcast(ChatColor.GREEN + "Your speed is being set to " +
						"Normal" + ChatColor.RESET);
				player.setWalkSpeed(playerSpeed);
				player.setSprinting(true);
			}
		}


	}

	@Override
	public TNTRunArena getArena() {
		return (TNTRunArena) super.getArena();
	}
}
