package org.jpwilliamson.multiplex.model.kingOfHill;

import io.github.bananapuncher714.cartographer.core.Cartographer;
import io.github.bananapuncher714.cartographer.core.api.WorldCursor;
import io.github.bananapuncher714.cartographer.core.api.map.WorldCursorProvider;
import io.github.bananapuncher714.cartographer.core.map.Minimap;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCursor;
import org.jpwilliamson.multiplex.model.*;
import org.jpwilliamson.multiplex.model.team.TeamArena;
import org.jpwilliamson.multiplex.model.tntTag.TntTagScoreboard;
import org.jpwilliamson.multiplex.util.Constants;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.SimpleEquipment;
import org.mineacademy.fo.remain.CompColor;
import org.mineacademy.fo.remain.CompEquipmentSlot;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompSound;

import java.util.*;

public class KingOfHillArena extends TeamArena {

	/**
	 * The arena unique identification type
	 */
	public static final String TYPE = "king_of_hill";

	@Getter
	public int timeCrystalDamage=0;
//	@Getter
//	public Location locationCrystalDamage=getSettings().getCrystals().get(0);
    @Getter
	public Location crystalLocation  = getSettings().getResetLocation();

	private KingOfHillScoreboard scoreboard;

	private Minimap minimap = null;

	private List<WorldCursorProvider> providers = new ArrayList<>(); // map providers

	/**
	 * Create a new arena type
	 *
	 * @param name
	 */
	public KingOfHillArena(final String name) {
		super(TYPE, name);
		this.scoreboard = getGameScoreBoard();
	}


//	/**
//	 * @see org.mineacademy.arena.model.team.TeamArena#createSettings()
//	 */
	@Override
	protected ArenaSettings createSettings() {
		return new KingOfHillSettings(this);
	}

	@Override
	public KingOfHillHeartBeat getHeartbeat() {
		return (KingOfHillHeartBeat) super.getHeartbeat();
	}

	public void setCrystalLocation(Location someLocation) {
		this.crystalLocation = someLocation;
	}

//	@Override
//	public KingOfHillScoreboard getScoreboard() {
//		return (KingOfHillScoreboard) super.getScoreboard();
//	}

	public KingOfHillScoreboard getGameScoreBoard(){
		return new KingOfHillScoreboard(this);

	}
	@Override
	protected ArenaHeartbeat createHeartbeat() {
		return new KingOfHillHeartBeat(this);
	}

	@Override
	protected void onJoin(Player player, ArenaJoinMode joinMode) {
		super.onJoin(player, joinMode);


		//player.getInventory().setHelmet(new ItemStack(Material.LANTERN));

		getScoreboard().onPlayerLeave(player);
		scoreboard.onLobby();
		scoreboard.displayCustomLobbyBoard(player);

	}


//	@Override
//	protected ArenaScoreboard createScoreboard() {
//		return new KingOfHillScoreboard(this);
//	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Game logic
	// ------–------–------–------–------–------–------–------–------–------–------–------–

//	/**
//	 * Spawns crystals when the game starts
//	 *
//	 * @see org.mineacademy.arena.model.team.TeamArena#onStart()
//	 */
	@Override
	protected void onStart() {
		super.onStart();

		for(final Location location : getSettings().getCrystals())
		{

			final ArenaTeam crystalTeam = getSettings().findCrystalTeam(location);
//			spawn the crystal at location
			final EnderCrystal crystal = location.getWorld().spawn(location.clone().add(0, 1, 0), EnderCrystal.class);
			setEntityTag(crystal, Constants.Tag.TEAM_CRYSTAL, crystalTeam.getName());

		}

		for (final ArenaPlayer cache : getArenaPlayersInAllModes()) {
			final Location teamSpawnpoint = getSettings().findSpawnpoint(cache.getArenaTeam());
			final Player player = cache.getPlayer();

			// Give the colored helmet to each team player
			new SimpleEquipment(player).set(CompEquipmentSlot.HEAD, ItemCreator
					.of(CompMaterial.LEATHER_HELMET)
					.color(CompColor.fromChatColor(cache.getArenaTeam().getColor())));

			// Teleport the player to his team spawn point
			teleport(player, teamSpawnpoint);
		}

		this.minimap = Cartographer.getInstance().getMapManager().constructNewMinimap("KingOfHill");
		WorldCursorProvider provider = (player, minimap, playerSetting) -> {
			Set<WorldCursor> cursors = new HashSet<>();


			for(Player playerInGame: getPlayers(ArenaJoinMode.PLAYING)){
				ArenaTeam team = ArenaPlayer.getCache(playerInGame).getArenaTeam();

				if(team.getColor().equals(CompColor.fromChatColor(ChatColor.RED))){
					WorldCursor cursor = new WorldCursor(playerInGame.getLocation());

					cursor.setType(MapCursor.Type.BANNER_RED);

					cursors.add(cursor);
				}
				if(team.getColor().equals(CompColor.fromChatColor(ChatColor.GREEN))){
					WorldCursor cursor = new WorldCursor(playerInGame.getLocation());

					cursor.setType(MapCursor.Type.BANNER_GREEN);

					cursors.add(cursor);
				}

				if(team.getColor().equals(CompColor.fromChatColor(ChatColor.YELLOW))){
					WorldCursor cursor = new WorldCursor(playerInGame.getLocation());

					cursor.setType(MapCursor.Type.BANNER_YELLOW);

					cursors.add(cursor);
				}

				if(team.getColor().equals(CompColor.fromChatColor(ChatColor.BLUE))){
					WorldCursor cursor = new WorldCursor(playerInGame.getLocation());

					cursor.setType(MapCursor.Type.BANNER_BLUE);

					cursors.add(cursor);
				}
			}

			return cursors;
		};

		minimap.register(provider);
		providers.add(provider);
		scoreboard.onStart();

		for(Player player: getPlayers(ArenaJoinMode.PLAYING)){
			scoreboard.displayCustomGameBoard(player);
			provideMap(player);
		}
	}

	public void provideMap(Player player){
		ItemStack map = Cartographer.getInstance().getMapManager().getItemFor(minimap);

		player.getInventory().addItem(map);
	}


	/*
	 * Remove crystals that are left when the arena stops
	 */
	private void removeCrystals() {
		final World world = getSettings().getRegion().getWorld();

		for (final EnderCrystal crystal : world.getEntitiesByClass(EnderCrystal.class))
			if (hasEntityTag(crystal, Constants.Tag.TEAM_CRYSTAL))
				crystal.remove();
	}

//	/**
//	 * Prevent custom player kill logic
//	 *
//	 * @see org.mineacademy.arena.model.team.TeamArena#onPlayerKill(org.bukkit.entity.Player, org.bukkit.entity.LivingEntity)
//	 */
	@Override
	protected void onPlayerKill(Player killer, LivingEntity victim) {
		// Prevent override to disable rewards for killing players
		super.onPlayerKill(killer, victim);

		if(victim instanceof  Player)
		{
			final ArenaTeam attackTeam = ArenaPlayer.getCache(killer).getArenaTeam();
			final ArenaTeam defendTeam = ArenaPlayer.getCache((Player)victim).getArenaTeam();

			final ArenaPlayer killerCache = ArenaPlayer.getCache(killer);
			killerCache.giveArenaPoints(killer, 30);

			final Location respawnLoc = getSettings().findSpawnpoint(defendTeam);
			victim.teleport(respawnLoc);
		}
	}

//	/**
//	 * Handle crystal damage
//	 *
//	 * @see org.mineacademy.arena.model.Arena#onPlayerDamage(org.bukkit.entity.Player, org.bukkit.entity.Entity, org.bukkit.event.entity.EntityDamageByEntityEvent)
//	 */
	@Override
	protected void onPlayerDamage(Player attacker, Entity victim, EntityDamageByEntityEvent event) {

		if(victim instanceof Player){

			final ArenaTeam attackTeam = ArenaPlayer.getCache(attacker).getArenaTeam();
			final ArenaTeam defendTeam = ArenaPlayer.getCache((Player)victim).getArenaTeam();


			if(attackTeam.getName().equalsIgnoreCase(defendTeam.getName())) {
				event.setCancelled(true);
				returnHandled();
			}
			event.setCancelled(false);
		}

		if(victim instanceof EnderCrystal)
		{
			final ArenaTeam attackTeam = ArenaPlayer.getCache(attacker).getArenaTeam();
			final ArenaPlayer attackerCache = ArenaPlayer.getCache(attacker);

			int crystalDamage = getNumericEntityTag(victim, Constants.Tag.CRYSTAL_DAMAGE, 0);
			final int threshold =getSettings().getDamageThreshold();

			if(++crystalDamage>=threshold)
			{
				timeCrystalDamage = getHeartbeat().getCountdownSeconds() - getHeartbeat().getTimeLeft();
				//locationCrystalDamage = victim.getLocation();
				Location locationCrystalDamage = victim.getLocation();
				setCrystalLocation(locationCrystalDamage);
				victim.remove();
				event.setCancelled(true);
				attackerCache.giveArenaPoints(attacker, 20);
				Messenger.warn(attacker, "You received 20 points for destroying the crystal" );
			}

			if(crystalDamage % 4 ==0){
				for(final Player player : getPlayers(ArenaJoinMode.PLAYING)){
						if(player.equals(attacker)){
							Messenger.info(player, Common.format("You damaged the Hill Point",crystalDamage, threshold));
							CompSound.SUCCESSFUL_HIT.play(player);
							continue;
						}

						else {
							Messenger.info(player, Common.format("Hill Point Got Attacked, Rush now!",crystalDamage,threshold));
							CompSound.AMBIENCE_THUNDER.play(player);
						}
				}
			}
		}
	}

//	/**
//	 * Prevent any form of crystal damage from non players
//	 *
//	 * @see org.mineacademy.arena.model.Arena#onDamage(org.bukkit.entity.Entity, org.bukkit.entity.Entity, org.bukkit.event.entity.EntityDamageByEntityEvent)
//	 */
	@Override
	protected void onDamage(Entity attacker, Entity victim, EntityDamageByEntityEvent event) {
		if (victim instanceof EnderCrystal) {
			event.setCancelled(true);

			returnHandled();
		}
	}

//	/**
//	 * @see org.mineacademy.arena.model.team.TeamArena#onLeave(org.bukkit.entity.Player, org.mineacademy.arena.model.ArenaLeaveReason)
//	 */
	@Override
	protected void onLeave(Player player, ArenaLeaveReason reason) {
		super.onLeave(player, reason);

		checkStop(reason);
	}

//	/**
//	 * @see org.mineacademy.arena.model.Arena#onSpectateStart(org.bukkit.entity.Player, org.mineacademy.arena.model.ArenaLeaveReason)
//	 */
	@Override
	protected void onSpectateStart(Player player, ArenaLeaveReason reason) {
		super.onSpectateStart(player, reason);

		checkStop(reason);
	}

	/*
	 * Stop the arena if only 1 team is left
	 */
	private void checkStop(ArenaLeaveReason reason) {
		final ArenaTeam lastTeam = getLastTeamStanding();

		//It means if only last team is left in game
		if(lastTeam!=null)
		{
			for(Player players : getPlayers(ArenaJoinMode.PLAYING)){
				players.sendMessage("Only Your Team left, Team " + lastTeam.getName() + " Wins!");
			}
			stopArena(ArenaStopReason.PLUGIN);
		}
	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Pluggables
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * Players can respawn indefinitely
	 */
	@Override
	protected boolean hasLives() {
		return false;
	}

	/**
	 * Custom handling of teams leaving
	 */
	@Override
	protected boolean stopIfLastStanding() {
		return false;
	}

//	/**
//	 * Do not broadcast that the playerl left if their crystal got destroyed
//	 *
//	 * @see org.mineacademy.arena.model.Arena#canBroadcastLeave(org.mineacademy.arena.model.ArenaLeaveReason)
//	 */
	@Override
	protected boolean canBroadcastLeave(ArenaLeaveReason reason) {
		return reason != ArenaLeaveReason.CRYSTAL_DESTROYED;
	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Overrides
	// ------–------–------–------–------–------–------–------–------–------–------–------–

//	/**
//	 * @see org.mineacademy.arena.model.team.TeamArena#getSettings()
//	 */
	@Override
	public KingOfHillSettings getSettings() {
		return (KingOfHillSettings) super.getSettings();
	}

	@Override
	protected void onStop() {

		for(Player player: getPlayersInAllModes()) {
			scoreboard.removeBoard(player);
			player.getInventory().remove(Cartographer.getInstance().getMapManager().getItemFor(minimap));
		}
		super.onStop();

		removeCrystals();
		handleWorldProviders();
		providers.clear();
		Cartographer.getInstance().getMapManager().remove(minimap);
	}

	public void handleWorldProviders(){
		for(WorldCursorProvider provider: providers)
			minimap.unregister(provider);
	}
}

