package org.jpwilliamson.multiplex.model.paintBall;

import io.github.bananapuncher714.cartographer.core.Cartographer;
import io.github.bananapuncher714.cartographer.core.api.WorldCursor;
import io.github.bananapuncher714.cartographer.core.api.map.WorldCursorProvider;
import io.github.bananapuncher714.cartographer.core.map.Minimap;
import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
//import org.jpwilliamson.arena.model.*;
//import org.jpwilliamson.arena.model.team.TeamArena;
//import org.jpwilliamson.arena.util.Constants;
import org.bukkit.map.MapCursor;
import org.jpwilliamson.multiplex.model.*;
import org.jpwilliamson.multiplex.model.team.TeamArena;
import org.jpwilliamson.multiplex.util.Constants;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.SimpleEquipment;
import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.remain.CompColor;
import org.mineacademy.fo.remain.CompEquipmentSlot;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompSound;

import java.util.*;

public class PaintBallArena extends TeamArena {

	public static final String TYPE = "paintball";

	@Getter
	private int damageRed,damageBlue ,damageGreen ,damageYellow;
	@Getter
	private Map<UUID,Double> playersInGame = new HashMap<>();

	private PaintBallScoreboard scoreboard;

	private Minimap minimap = null;
	private List<WorldCursorProvider> providers = new ArrayList<>(); // map providers

	/*
	 * Create a new team arena
	 *
	 * @param type
	 * @param name
	 */

	public PaintBallArena(final String name) {
		super(TYPE, name);
		damageRed =0;
		damageBlue=0;
		damageGreen=0;
		damageYellow=0;
		this.scoreboard = getGameScoreboard();
		getSettings().setLobbyDuration(SimpleTime.fromSeconds(90));
		getSettings().setGameDuration(SimpleTime.fromSeconds(300));
		getSettings().setMinPlayers(1);
	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Overrides
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * @see TeamArena#createSettings()
	 */
	@Override
	protected PaintBallSettings createSettings() {
		return new PaintBallSettings(this);
	}

//	@Override
//	protected PaintBallScoreboard createScoreboard() {
//		return new PaintBallScoreboard(this);
//	}

	@Override
	protected PaintBallHeartbeat createHeartbeat() {
		return new PaintBallHeartbeat(this);
	}

	@Override
	public PaintBallHeartbeat getHeartbeat() {
		return (PaintBallHeartbeat) super.getHeartbeat();
	}

	@Override
	public PaintBallSettings getSettings() {
		return (PaintBallSettings) super.getSettings();
	}

	public PaintBallScoreboard getGameScoreboard(){
		return new PaintBallScoreboard(this);
	}

	//	@Override
//	public PaintBallScoreboard getScoreboard() {
//		return (PaintBallScoreboard) super.getScoreboard();
//	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Game logic
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * Validate all the players in Playing Mode
	 * We currently have two paint ball guns, one is red and other is blue
	 * If player Team is red/Yellow, add Red PaintBall gun to inventory
	 * Else If Player Team is blue/Green, add Blue PaintBall gun to inventory
	 *
	 * Spawns neutral base crystals when the game starts
	 *
	 * @see TeamArena#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();

		List<Player> players = this.getPlayers(ArenaJoinMode.PLAYING);

		List<String> lore = new ArrayList<String>();
		lore.add("Right Click Air");
		lore.add("To shoot");

		ItemStack redGun = new ItemStack(Material.CARROT_ON_A_STICK);
		ItemMeta redGunMeta = redGun.getItemMeta();
		//1000015 is the tag for red paint ball gun, can be found in PaintBall class
		redGunMeta.setCustomModelData(1000015);
		redGunMeta.setDisplayName("PaintBall Red");
		redGunMeta.setLore(lore);
		redGun.setItemMeta(redGunMeta);

		ItemStack blueGun = new ItemStack(Material.CARROT_ON_A_STICK);
		ItemMeta blueGunMeta = blueGun.getItemMeta();
		//1000016 is the tag for blue paint ball gun, can be found in PaintBall class
		blueGunMeta.setCustomModelData(1000016);
		blueGunMeta.setDisplayName("PaintBall Blue");
		blueGunMeta.setLore(lore);
		blueGun.setItemMeta(blueGunMeta);

		for(Player player : players){
			playersInGame.put(player.getUniqueId(), 0.0);
			ArenaTeam playerTeam = ArenaPlayer.getCache(player).getArenaTeam();
			if((playerTeam.getName().equalsIgnoreCase("red"))||(playerTeam.getName().equalsIgnoreCase("yellow")))
				player.getInventory().addItem(redGun);
			else
				player.getInventory().addItem(blueGun);
		}


		// Spawn crystals to the arena
		for (final Location crystalLocation : getSettings().getCrystals()) {
			final ArenaTeam crystalTeam = getSettings().findCrystalTeam(crystalLocation);

			final EnderCrystal crystal = crystalLocation.getWorld().spawn(crystalLocation.clone().add(0, 1, 0), EnderCrystal.class);

			// Put the team name as an invisible flag to them so that we know
			// what team got their crystal (flag) destroyed
			setEntityTag(crystal, Constants.Tag.TEAM_CRYSTAL, crystalTeam.getName());

			// Set team data to have this crystal alive
			setTeamTag(crystalTeam, Constants.Tag.CRYSTAL_ALIVE, true);
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

		this.minimap = Cartographer.getInstance().getMapManager().constructNewMinimap("PaintBall");
		WorldCursorProvider provider = (player, minimap, playerSetting) -> {
			Set<WorldCursor> cursors = new HashSet<>();
		for (final Location crystalLocation : getSettings().getCrystals()) {
			final ArenaTeam crystalTeam = getSettings().findCrystalTeam(crystalLocation);

			if(crystalTeam.getColor().equals(ChatColor.RED)){
				WorldCursor cursor = new WorldCursor(crystalLocation);
				cursor.setType(MapCursor.Type.BANNER_RED);
				cursors.add(cursor);
			}
			if(crystalTeam.getColor().equals(ChatColor.YELLOW)){
				WorldCursor cursor = new WorldCursor(crystalLocation);
				cursor.setType(MapCursor.Type.BANNER_YELLOW);
				cursors.add(cursor);
			}
			if(crystalTeam.getColor().equals(ChatColor.BLUE)){
				WorldCursor cursor = new WorldCursor(crystalLocation);
				cursor.setType(MapCursor.Type.BANNER_BLUE);
				cursors.add(cursor);
			}
			if(crystalTeam.getColor().equals(ChatColor.GREEN)){
				WorldCursor cursor = new WorldCursor(crystalLocation);
				cursor.setType(MapCursor.Type.BANNER_GREEN);
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

	/**
	 * Prevent custom player kill logic
	 *
	 * Validate if the victim being killed is a player
	 * If so, get the team spawn location of that player and respawn him at its team's spawn location (Team Bases)
	 * Provide points(10 points) to the killer for killing a player from different team
	 * @see TeamArena#onPlayerKill(org.bukkit.entity.Player, org.bukkit.entity.LivingEntity)
	 */
	@Override
	protected void onPlayerKill(Player killer, LivingEntity victim) {
		// Prevent override to disable rewards for killing players

		if(victim instanceof Player){
			final ArenaTeam victimTeam = ArenaPlayer.getCache((Player)victim).getArenaTeam();
			final Location reSpawn = getSettings().findSpawnpoint(victimTeam);
			victim.teleport(reSpawn);
			for(Map.Entry<UUID,Double> entry : playersInGame.entrySet()){
				if(entry.getKey().equals(killer.getUniqueId())){
					double currentPoints = entry.getValue();
					double updatedValue = currentPoints + 10;
					entry.setValue(updatedValue);
				}
			}
		}
	}

	/**
	 * Handle crystal damage
	 * Whole Game Logic Lies here
	 * Validates if a Player is not damaging his team member, if so, cancel the event
	 *
	 * Validates if a Player is damaging a crystal, if so find the team the crystal is associated with (in this case each crystal has a neutral tag(specifying different neutral bases))
	 * Validates if the damage exceeds the threshold value, if yes
	 * 			Check the team of the attacker and spawn a new block related to his team,
	 * 			also increase the count of bases being captured by that team
	 *
	 * Validates if a Player is damaging a team block
	 * Validates if the damage exceeds the threshold value, if yes
	 * 			Check the team of the attacker and spawn a new block related to his team
	 * 		    Increase the count of bases being captured by attacker's team and
	 * 		    reduce the count of the team the block was associated to
	 *
	 * Game Logic Ends Here
	 * @see Arena#onPlayerDamage(org.bukkit.entity.Player, org.bukkit.entity.Entity, org.bukkit.event.entity.EntityDamageByEntityEvent)
	 */
	@Override
	protected void onPlayerDamage(Player attacker, Entity victim, EntityDamageByEntityEvent event) {

		if(victim instanceof  Player)
		{
			final ArenaTeam attackerTeam = ArenaPlayer.getCache(attacker).getArenaTeam();
			final ArenaTeam victimTeam = ArenaPlayer.getCache((Player)victim).getArenaTeam();

			if(attackerTeam.equals(victimTeam)){
				Messenger.error(attacker, "You cannot damage your own teammates");
				event.setCancelled(true);
			}
			event.setCancelled(false);
		}
		if (victim instanceof EnderCrystal) {

			final ArenaTeam attackerTeam = ArenaPlayer.getCache(attacker).getArenaTeam();
			final ArenaTeam crystalTeam = ArenaTeam.findTeam(getEntityTag(victim, Constants.Tag.TEAM_CRYSTAL));

			if (attackerTeam.equals(crystalTeam)) {
				Messenger.error(attacker, "You cannot damage your own crystal!");

			} else {
				int damage = getNumericEntityTag(victim, Constants.Tag.CRYSTAL_DAMAGE, 0);
				final int threshold = getSettings().getCrystalHitThreshold();

				if (++damage >= threshold) {
					//This is made so that if ENDER_CRYSTAL is DAMAGED, Add a block there and increase the count
					//Remove Ender-Crystal finally but before store its location
					Location loc = victim.getLocation();
					victim.remove();

					if(attackerTeam.getName().equalsIgnoreCase("red")) {
						Entity changeBlock = (Entity) ItemCreator.of(CompMaterial.RED_CONCRETE).build().make();
						changeBlock.teleport(loc);
						damageRed++;
					}
					if(attackerTeam.getName().equalsIgnoreCase("blue")) {
						Entity changeBlock = (Entity) ItemCreator.of(CompMaterial.BLUE_CONCRETE).build().make();
						changeBlock.teleport(loc);
						damageBlue++;
					}
					if(attackerTeam.getName().equalsIgnoreCase("green")) {
						Entity changeBlock = (Entity) ItemCreator.of(CompMaterial.GREEN_CONCRETE).build().make();
						changeBlock.teleport(loc);
						damageGreen++;
					}
					if(attackerTeam.getName().equalsIgnoreCase("yellow")) {
						Entity changeBlock = (Entity) ItemCreator.of(CompMaterial.YELLOW_CONCRETE).build().make();
						changeBlock.teleport(loc);
						damageYellow++;
					}
					event.setCancelled(true);
					returnHandled();
				}

				// Broadcast every fourth hit only
				if (damage % 4 == 0) {
					for (final Player player : getPlayersInAllModes()) {
						if (player.equals(attacker)) {
							Messenger.info(player, Common.format("Damaged %s team's crystal! (%s/%s)", crystalTeam.getName(), damage, threshold));
							CompSound.ANVIL_LAND.play(player);
							continue;
						}
						final ArenaTeam playerTeam = ArenaPlayer.getCache(player).getArenaTeam();

						if (playerTeam != null && playerTeam.equals(crystalTeam)) {
							Messenger.info(player, Common.format("&eYour crystal got damaged! (%s/%s)", damage, threshold));

							CompSound.SUCCESSFUL_HIT.play(player);

						} else
							Messenger.info(player, Common.format("%s team's crystal just got damaged! (%s/%s)", crystalTeam.getName(), damage, threshold));
					}
				}

				setNumericEntityTag(victim, Constants.Tag.CRYSTAL_DAMAGE, damage);
			}

			event.setCancelled(true);
			returnHandled();
		}
		Entity e1 = (Entity) ItemCreator.of(CompMaterial.RED_CONCRETE).build().make();
		Entity e2 = (Entity) ItemCreator.of(CompMaterial.BLUE_CONCRETE).build().make();
		Entity e3 = (Entity) ItemCreator.of(CompMaterial.GREEN_CONCRETE).build().make();
		Entity e4 = (Entity) ItemCreator.of(CompMaterial.YELLOW_CONCRETE).build().make();

		if(victim.equals(e1)){
			final ArenaTeam attackerTeam = ArenaPlayer.getCache(attacker).getArenaTeam();
			final ArenaTeam crystalTeam = ArenaTeam.findTeam("red");
			if (attackerTeam.equals(crystalTeam)) {
				Messenger.error(attacker, "You cannot damage your own crystal!");

			} else {
				int damage = getNumericEntityTag(victim, Constants.Tag.CRYSTAL_DAMAGE, 0);
				final int threshold = getSettings().getCrystalHitThreshold();

				if (++damage >= threshold) {
					//This is made so that if ENDER_CRYSTAL is DAMAGED, Add a block there and increase the count
					//Remove Ender-Crystal finally but before store its location
					Location loc = victim.getLocation();
					victim.remove();
					if(attackerTeam.getName().equalsIgnoreCase("blue")) {
						damageRed--;
						Entity changeBlock = (Entity) ItemCreator.of(CompMaterial.BLUE_CONCRETE).build().make();
						changeBlock.teleport(loc);
						damageBlue++;
					}
					if(attackerTeam.getName().equalsIgnoreCase("green")) {
						damageRed--;
						Entity changeBlock = (Entity) ItemCreator.of(CompMaterial.GREEN_CONCRETE).build().make();
						changeBlock.teleport(loc);
						damageGreen++;
					}
					if(attackerTeam.getName().equalsIgnoreCase("yellow")) {
						damageRed--;
						Entity changeBlock = (Entity) ItemCreator.of(CompMaterial.YELLOW_CONCRETE).build().make();
						changeBlock.teleport(loc);
						damageYellow++;
					}

					event.setCancelled(true);
					returnHandled();
				}
				setNumericEntityTag(victim, Constants.Tag.CRYSTAL_DAMAGE, damage);
			}
			event.setCancelled(true);
			returnHandled();
		}
		else if(victim.equals(e2)){
			final ArenaTeam attackerTeam = ArenaPlayer.getCache(attacker).getArenaTeam();
			final ArenaTeam crystalTeam = ArenaTeam.findTeam("blue");
			if (attackerTeam.equals(crystalTeam)) {
				Messenger.error(attacker, "You cannot damage your own crystal!");

			} else {
				int damage = getNumericEntityTag(victim, Constants.Tag.CRYSTAL_DAMAGE, 0);
				final int threshold = getSettings().getCrystalHitThreshold();

				if (++damage >= threshold) {

					//This is made so that if ENDER_CRYSTAL is DAMAGED, Add a block there and increase the count
					//Remove Ender-Crystal finally but before store its location
					Location loc = victim.getLocation();
					victim.remove();

					if(attackerTeam.getName().equalsIgnoreCase("red")) {
						damageBlue--;
						Entity changeBlock = (Entity) ItemCreator.of(CompMaterial.RED_CONCRETE).build().make();
						changeBlock.teleport(loc);
						damageRed++;
					}
					if(attackerTeam.getName().equalsIgnoreCase("green")) {
						damageBlue--;
						Entity changeBlock = (Entity) ItemCreator.of(CompMaterial.GREEN_CONCRETE).build().make();
						changeBlock.teleport(loc);
						damageGreen++;
					}
					if(attackerTeam.getName().equalsIgnoreCase("yellow")) {
						damageBlue--;
						Entity changeBlock = (Entity) ItemCreator.of(CompMaterial.YELLOW_CONCRETE).build().make();
						changeBlock.teleport(loc);
						damageYellow++;
					}

					event.setCancelled(true);
					returnHandled();
				}
				setNumericEntityTag(victim, Constants.Tag.CRYSTAL_DAMAGE, damage);
			}
			event.setCancelled(true);
			returnHandled();
		}
		else if(victim.equals(e3)){
			final ArenaTeam attackerTeam = ArenaPlayer.getCache(attacker).getArenaTeam();
			final ArenaTeam crystalTeam = ArenaTeam.findTeam("green");
			if (attackerTeam.equals(crystalTeam)) {
				Messenger.error(attacker, "You cannot damage your own crystal!");

			} else {
				int damage = getNumericEntityTag(victim, Constants.Tag.CRYSTAL_DAMAGE, 0);
				final int threshold = getSettings().getCrystalHitThreshold();

				if (++damage >= threshold) {
					//This is made so that if ENDER_CRYSTAL is DAMAGED, Add a block there and increase the count
					//Remove Ender-Crystal finally but before store its location
					Location loc = victim.getLocation();
					victim.remove();

					if(attackerTeam.getName().equalsIgnoreCase("red")) {
						damageGreen--;
						Entity changeBlock = (Entity) ItemCreator.of(CompMaterial.RED_CONCRETE).build().make();
						changeBlock.teleport(loc);
						damageRed++;
					}
					if(attackerTeam.getName().equalsIgnoreCase("blue")) {
						damageGreen--;
						Entity changeBlock = (Entity) ItemCreator.of(CompMaterial.BLUE_CONCRETE).build().make();
						changeBlock.teleport(loc);
						damageBlue++;
					}
					if(attackerTeam.getName().equalsIgnoreCase("yellow")) {
						damageGreen--;
						Entity changeBlock = (Entity) ItemCreator.of(CompMaterial.YELLOW_CONCRETE).build().make();
						changeBlock.teleport(loc);
						damageYellow++;
					}

					event.setCancelled(true);
					returnHandled();
				}
				setNumericEntityTag(victim, Constants.Tag.CRYSTAL_DAMAGE, damage);
			}
			event.setCancelled(true);
			returnHandled();
		}
		else if(victim.equals(e4)){
			final ArenaTeam attackerTeam = ArenaPlayer.getCache(attacker).getArenaTeam();
			final ArenaTeam crystalTeam = ArenaTeam.findTeam("yellow");
			if (attackerTeam.equals(crystalTeam)) {
				Messenger.error(attacker, "You cannot damage your own crystal!");

			} else {
				int damage = getNumericEntityTag(victim, Constants.Tag.CRYSTAL_DAMAGE, 0);
				final int threshold = getSettings().getCrystalHitThreshold();

				if (++damage >= threshold) {
					//This is made so that if ENDER_CRYSTAL is DAMAGED, Add a block there and increase the count
					//Remove Ender-Crystal finally but before store its location
					Location loc = victim.getLocation();
					victim.remove();

					if(attackerTeam.getName().equalsIgnoreCase("red")) {
						damageYellow--;
						Entity changeBlock = (Entity) ItemCreator.of(CompMaterial.RED_CONCRETE).build().make();
						changeBlock.teleport(loc);
						damageRed++;
					}
					if(attackerTeam.getName().equalsIgnoreCase("blue")) {
						damageYellow--;
						Entity changeBlock = (Entity) ItemCreator.of(CompMaterial.BLUE_CONCRETE).build().make();
						changeBlock.teleport(loc);
						damageBlue++;
					}
					if(attackerTeam.getName().equalsIgnoreCase("green")) {
						damageYellow--;
						Entity changeBlock = (Entity) ItemCreator.of(CompMaterial.GREEN_CONCRETE).build().make();
						changeBlock.teleport(loc);
						damageGreen++;
					}

					event.setCancelled(true);
					returnHandled();
				}
				setNumericEntityTag(victim, Constants.Tag.CRYSTAL_DAMAGE, damage);
			}
			event.setCancelled(true);
			returnHandled();
		}
	}

	/**
	 * Prevent any form of crystal damage from non players
	 *
	 * @see Arena#onDamage(org.bukkit.entity.Entity, org.bukkit.entity.Entity, org.bukkit.event.entity.EntityDamageByEntityEvent)
	 */
	@Override
	protected void onDamage(Entity attacker, Entity victim, EntityDamageByEntityEvent event) {
		if (victim instanceof EnderCrystal) {
			event.setCancelled(true);

			returnHandled();
		}
	}

	/**
	 * @see TeamArena#onLeave(org.bukkit.entity.Player, ArenaLeaveReason)
	 */
	@Override
	protected void onLeave(Player player, ArenaLeaveReason reason) {
		super.onLeave(player, reason);

		checkStop(reason);
	}

	/**
	 * @see Arena#onSpectateStart(org.bukkit.entity.Player, ArenaLeaveReason)
	 */
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

		// Stop the arena if there is only 1 team left
		if (!isStopped() && !isStopping() && lastTeam != null) {
			boolean allOtherCrystalsDestroyed = true;

			for (final ArenaTeam team : getTeamTags().keySet()) {
				if (team.equals(lastTeam))
					continue;

				final boolean crystalAlive = getTeamTag(team, Constants.Tag.CRYSTAL_ALIVE);

				if (crystalAlive) {
					allOtherCrystalsDestroyed = false;

					break;
				}
			}

			leaveTeamPlayers(lastTeam, allOtherCrystalsDestroyed && reason == ArenaLeaveReason.CRYSTAL_DESTROYED ? ArenaLeaveReason.LAST_TEAM_STANDING : ArenaLeaveReason.OTHER_TEAMS_LEFT);

			if (!isStopped())
				stopArena(ArenaStopReason.PLUGIN);
		}
	}

	/**
	 * Players can respawn indefinitely
	 */
	@Override
	protected boolean hasLives() {
		return false;
	}

	/**
	 * Custom handling of teams leaving, Validates if Only a single team is left in the arena while game is running
	 */
	@Override
	protected boolean stopIfLastStanding() {
		return false;
	}

	@Override
	protected boolean canBroadcastLeave(ArenaLeaveReason reason) {
		return reason != ArenaLeaveReason.CRYSTAL_DESTROYED;
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
