package org.jpwilliamson.multiplex.model.tntrun;

import io.github.bananapuncher714.cartographer.core.Cartographer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.multiplex.model.*;
import org.jpwilliamson.multiplex.model.blackOps.MonsterRound;
import org.jpwilliamson.multiplex.model.tntTag.TntTagScoreboard;
import org.mineacademy.fo.model.RandomNoRepeatPicker;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.ArrayList;
import java.util.List;

/*
 * Most of the game logic is in TNTRunHeartbeat
 * Comments provided for better understanding
 * */
public class TNTRunArena extends Arena {

	public static final String TYPE = "tnt_run";

	private TNTRunScoreboard scoreboard;
	/**
	 * Create a new arena. If the arena settings do not yet exist,
	 * they are created automatically.
	 * @param name
	 */
	public TNTRunArena(final String name) {
		super(TYPE, name);
		this.scoreboard = getGameScoreboard();
	}

	@Override
	protected void onStart() {
		super.onStart();

		List<Player> inGamePlayers = getPlayers(ArenaJoinMode.PLAYING);

		this.scoreboard.onStart();
		//int position=0;
		for(Player player : inGamePlayers){
				//teleport(player, getSettings().getEntrances().getLocations().get(position));
				//position++;
			player.setAllowFlight(false);
			player.setFlying(false);
		}

		//Using randomNoRepeatPicker instead of logic used above
		// to randomly assign positions to players rather than an ordered one
		final RandomNoRepeatPicker<Location> locationPicker = RandomNoRepeatPicker.newPicker(Location.class);
		locationPicker.setItems(getSettings().getEntrances().getLocations());

		if (getState() != ArenaState.EDITED)
			forEachInAllModes((player) -> {
				final Location location = locationPicker.pickRandom(player);
				teleport(player, location);
				this.scoreboard.showGameBoard(player);
			});

	}

	@Override
	protected void onJoin(Player player, ArenaJoinMode joinMode) {
		super.onJoin(player, joinMode);

		getScoreboard().onPlayerLeave(player);
		scoreboard.onLobby();
		scoreboard.showLobbyBoard(player);
	}

	public void teleportToStadium(Player player){
		teleport(player, getSettings().getTNTStadium());
	}

	@Override
	protected void onStop() {
		super.onStop();

		for(Player players : getPlayers(ArenaJoinMode.PLAYING)){

			if(players.getLocation().getY()!=getSettings().getLevelLocation().getY()){
				players.sendMessage(ChatColor.BOLD + "You won this round" + ChatColor.DARK_GREEN + ChatColor.RESET);
				broadcast(ChatColor.MAGIC + "The Game has ended, players who survived will receive rewards now");
				players.getInventory().addItem(new ItemStack(Material.BAKED_POTATO,2));
				players.getInventory().addItem(new ItemStack(Material.SWEET_BERRIES,3));
				players.getInventory().addItem(new ItemStack(Material.GOLD_INGOT,5));
				leavePlayer(players, ArenaLeaveReason.LAST_STANDING);
				this.scoreboard.stopShowing(players);
			}
			else {
				players.sendMessage(ChatColor.BOLD + "You didn't survive! Better Luck Next Time!" + ChatColor.DARK_RED + ChatColor.RESET );
				leavePlayer(players, ArenaLeaveReason.CANT_SURVIVE);
				this.scoreboard.stopShowing(players);
			}
		//	teleport(players, getSettings().getTNTStadium());

		}
	}

	@Override
	public TNTRunSettings getSettings() {
		return (TNTRunSettings) super.getSettings();
	}

	@Override
	public TNTRunHeartbeat getHeartbeat() {
		return (TNTRunHeartbeat)super.getHeartbeat();
	}

	@Override
	protected ArenaSettings createSettings() {
		return new TNTRunSettings(this);
	}

	@Override
	protected ArenaHeartbeat createHeartbeat() {
		return new TNTRunHeartbeat(this);
	}

//	@Override
//	protected ArenaScoreboard createScoreboard() {
//		return new TNTRunScoreboard(this);
//	}

	public TNTRunScoreboard getGameScoreboard(){
		return new TNTRunScoreboard(this);

	}
	@Override
	protected void onLeave(final Player player, final ArenaLeaveReason reason) {
		super.onLeave(player, reason);

		checkLastStanding();
	}

	private void checkLastStanding() {
		if (getPlayers(ArenaJoinMode.PLAYING).size() == 1 && !isStopping()) {
			final Player winner = getPlayers(ArenaJoinMode.PLAYING).get(0);

			leavePlayer(winner, ArenaLeaveReason.LAST_STANDING);
		}
	}

}
