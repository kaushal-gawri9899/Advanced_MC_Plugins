package org.jpwilliamson.multiplex.model.paintBall;

import org.bukkit.ChatColor;

import org.bukkit.entity.Player;
import org.jpwilliamson.multiplex.model.*;


import java.util.List;

public class PaintBallHeartbeat extends ArenaHeartbeat {

	/**
	 * Create a new countdown
	 *
	 * @param arena
	 */
	protected PaintBallHeartbeat(Arena arena) {
		super(arena);
	}
	/**
	 * @see ArenaHeartbeat#onTick()
	 * Validate If Any team has captured all the 8 bases
	 * If True, Declare the team as winner and Teleport to lobby
	 * Else
	 * 	Declare Round End and Teleport to lobby
	 */
	@Override
	protected void onTick() {
		super.onTick();

		final PaintBallSettings settings = getArena().getSettings();
		final int elapsedSeconds = getCountdownSeconds() - getTimeLeft();
		List<Player> inGame = getArena().getPlayers(ArenaJoinMode.PLAYING);

		if(getArena().getDamageBlue()==settings.getMaxNeutralBases()){
			for(Player player : inGame){
				final ArenaTeam team = ArenaPlayer.getCache(player).getArenaTeam();
				if(team.getName().equalsIgnoreCase("blue"))
				{
					player.sendMessage("Congratulations" + player.getName() +"! Your team won the round.");
				}
				else {
					player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Round Ended! Spawning back to Lobby" + ChatColor.RESET);
				}
				player.teleport(getArena().getSettings().getLobbyLocation());
			}
		}
		if(getArena().getDamageRed()==settings.getMaxNeutralBases()){
			for(Player player : inGame){
				final ArenaTeam team = ArenaPlayer.getCache(player).getArenaTeam();
				if(team.getName().equalsIgnoreCase("red"))
				{
					player.sendMessage("Congratulations" + player.getName() +"! Your team won the round.");
				}
				else {
					player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Round Ended! Spawning back to Lobby" + ChatColor.RESET);
				}
				player.teleport(getArena().getSettings().getLobbyLocation());
			}

		}
		if(getArena().getDamageYellow()==settings.getMaxNeutralBases()){
			for(Player player : inGame){
				final ArenaTeam team = ArenaPlayer.getCache(player).getArenaTeam();
				if(team.getName().equalsIgnoreCase("yellow"))
				{
					player.sendMessage("Congratulations" + player.getName() +"! Your team won the round.");
				}
				else {
					player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Round Ended! Spawning back to Lobby" + ChatColor.RESET);
				}
				player.teleport(getArena().getSettings().getLobbyLocation());
			}
		}
		if(getArena().getDamageGreen()==getArena().getSettings().getMaxNeutralBases()){
			for(Player player : inGame){
				final ArenaTeam team = ArenaPlayer.getCache(player).getArenaTeam();
				if(team.getName().equalsIgnoreCase("green"))
				{
					player.sendMessage("Congratulations" + player.getName() +"! Your team won the round.");
				}
				else {
					player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Round Ended! Spawning back to Lobby" + ChatColor.RESET);
				}
				player.teleport(getArena().getSettings().getLobbyLocation());
			}
		}
	}


	/**
	 * @see ArenaHeartbeat#getArena()
	 */
	@Override
	public PaintBallArena getArena() {
		return (PaintBallArena) super.getArena();
	}
}
