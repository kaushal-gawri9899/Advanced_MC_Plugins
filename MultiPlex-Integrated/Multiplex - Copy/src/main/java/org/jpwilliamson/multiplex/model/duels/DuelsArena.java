package org.jpwilliamson.multiplex.model.duels;

import io.github.bananapuncher714.cartographer.core.Cartographer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.multiplex.model.*;
import org.jpwilliamson.multiplex.model.blackOps.MonsterRound;
import org.jpwilliamson.multiplex.model.tntTag.TntTagScoreboard;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.RandomUtil;


import java.util.*;

public class DuelsArena extends Arena {

	public static final String TYPE = "duels";

	public Map<UUID, Integer> playersInGame = new HashMap<>();

	public List<Player> playersInDuelFight = new ArrayList<>();

	public Map<UUID, Integer> manageHits = new HashMap<>();

	public boolean isDuels = false;

	private DuelsScoreboard scoreboard;

//	/**
//	 * Create a new arena. If the arena settings do not yet exist,
//	 * they are created automatically.
//	 *
//	 * @param type
//	 * @param name
//	 */
	public DuelsArena(final String name) {
		super(TYPE, name);
		this.scoreboard = getDueslsScoreboard();
	}

	@Override
	protected DuelsSettings createSettings() {
		return new DuelsSettings(this);
	}

	/**
	 * Create new arena heartbeat
	 */
	@Override
	protected ArenaHeartbeat createHeartbeat() {
		return new DuelsHeartbeat(this);
	}

//	@Override
//	protected ArenaScoreboard createScoreboard() {
//		return new DuelsScoreboard(this);
//	}

	public DuelsScoreboard getDueslsScoreboard(){
		return new DuelsScoreboard(this);
	}

	@Override
	public DuelsHeartbeat getHeartbeat() {
		return (DuelsHeartbeat)super.getHeartbeat();
	}


	@Override
	public DuelsSettings getSettings() {
		return (DuelsSettings) super.getSettings();
	}

	@Override
	protected void onStart() {
		super.onStart();

		scoreboard.onStart();

		if (!isEdited())
			return;

		List<Player> players = this.getPlayers(ArenaJoinMode.PLAYING);

		//Initially teleport all the players at a single location
		for (Player player : players) {
			playersInGame.put(player.getUniqueId(), -1);
			scoreboard.displayCustomGameBoard(player);
			teleport(player, getSettings().getStadiumLocation());
		}

	}

	public void manageDuelsFight() {

		//specify that duel fight is started
		isDuels = true;

		List<Player> players = this.getPlayers(ArenaJoinMode.PLAYING);
		List<Player> inDuelsPlayers = new ArrayList<>();

		for (Player player : players) {
			if (playersInGame.get(player.getUniqueId()) == -1)
				inDuelsPlayers.add(player);
		}

		Player firstPlayer = inDuelsPlayers.get(0);
		Player secondPlayer = inDuelsPlayers.get(1);

		this.broadcastInfo(firstPlayer.getDisplayName() + "Fighting against" + secondPlayer.getDisplayName());
		firstPlayer.sendMessage("Get Ready to Fight");
		secondPlayer.sendMessage("Get Ready to Fight");

		firstPlayer.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
		firstPlayer.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
		secondPlayer.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
		secondPlayer.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));

		playersInDuelFight.add(firstPlayer);
		playersInDuelFight.add(secondPlayer);

		manageHits.put(firstPlayer.getUniqueId(), 0);
		manageHits.put(secondPlayer.getUniqueId(), 0);

		teleport(firstPlayer, getSettings().getEntrances().getLocations().get(0));
		teleport(secondPlayer, getSettings().getEntrances().getLocations().get(1));

	}


	@Override
	protected void onPvP(Player attacker, Player victim, EntityDamageByEntityEvent event) {
		super.onPvP(attacker, victim, event);

		//Manages the hit that a player makes on another players
		//Using it to provide special items to the players for specific hits
		for(Map.Entry<UUID,Integer> entry : manageHits.entrySet()){
			if(entry.getKey().equals(attacker.getUniqueId())){
				int hits = entry.getValue();
				manageHits.replace(attacker.getUniqueId(), hits+1);
			}
		}

		for(Map.Entry<UUID,Integer> entry : manageHits.entrySet()){
			if(entry.getKey().equals(attacker.getUniqueId())){
				if(entry.getValue()>=5 && entry.getValue()<10){
					ItemStack stack = new ItemStack(Material.APPLE, 3);
					attacker.getInventory().addItem(stack);
				}
				if(entry.getValue()>=10 && entry.getValue()<15){
					ItemStack stack = new ItemStack(Material.IRON_SWORD, 1);
					attacker.getInventory().addItem(stack);
				}
				if(entry.getValue()>=15 && entry.getValue()<20){
					ItemStack stack = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
					ItemStack item = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
					attacker.getInventory().addItem(stack);
					attacker.getInventory().addItem(item);
				}
			}
		}

	}

	@Override
	protected Location getRespawnLocation(final Player player) {
		return RandomUtil.nextItem(getSettings().getEntrances());
	}

	@Override
	protected void onJoin(Player player, ArenaJoinMode joinMode) {
		super.onJoin(player, joinMode);

		getScoreboard().onPlayerLeave(player);
		scoreboard.onLobby();
		scoreboard.displayCustomLobbyBoard(player);

	}

	@Override
	protected void onPlayerKill(final Player killer, final LivingEntity victim) {
		super.onPlayerKill(killer, victim);

		if (victim instanceof Player) {
			final ArenaPlayer killerCache = ArenaPlayer.getCache(killer);
			final double points = MathUtil.formatTwoDigitsD(RandomUtil.nextBetween(45, 50) + Math.random());

			killerCache.giveArenaPoints(killer, points);

			final ArenaPlayer victimCache = ArenaPlayer.getCache((Player) victim);

			broadcastExcept((Player) victim, Common.format("&8[&4&lx&8] &c%s %s %s (%s/%s)", killer, "killed" , victim, victimCache.getRespawns() + 1, getSettings().getLives()));
			Messenger.warn(killer, "You received " + points + " points for killing " + victim.getName() + " and now have " + killerCache.getArenaPoints() + " points!");
		}
	}


	@Override
	protected void onSpectateStart(final Player player, final ArenaLeaveReason reason) {
		super.onSpectateStart(player, reason);

		checkLastStanding();
	}

	@Override
	protected void onLeave(final Player player, final ArenaLeaveReason reason) {
		super.onLeave(player, reason);

		checkLastStanding();
	}


	public void checkLastStanding() {
		if (getPlayers(ArenaJoinMode.PLAYING).size() == 1 && !isStopping()) {
			final Player winner = getPlayers(ArenaJoinMode.PLAYING).get(0);

			leavePlayer(winner, ArenaLeaveReason.LAST_STANDING);
		}
	}

	@Override
	protected boolean canSpectateOnLeave(final Player player) {
		return getPlayers(ArenaJoinMode.PLAYING).size() > 1;
	}

	@Override
	protected boolean hasLives() {
		return true;
	}

	/**
	 * @see Arena#hasClasses()
	 */
	@Override
	protected boolean hasClasses() {
		return true;
	}

	/**
	 * @see Arena#hasPvP()
	 */
	@Override
	protected boolean hasPvP() {
		return true;
	}

	/**
	 * @see Arena#hasDeathMessages()
	 */
	@Override
	protected boolean hasDeathMessages() {
		return false;
	}

	@Override
	protected void onStop() {
		for(Player player: getPlayersInAllModes()) {
			scoreboard.removeBoard(player);

		}
		super.onStop();
	}

}
