package org.jpwilliamson.multiplex.model.skyWars;

import java.util.*;

import io.github.bananapuncher714.cartographer.core.Cartographer;
import io.github.bananapuncher714.cartographer.core.api.WorldCursor;
import io.github.bananapuncher714.cartographer.core.api.map.WorldCursorProvider;
import io.github.bananapuncher714.cartographer.core.map.Minimap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCursor;
import org.jpwilliamson.multiplex.model.*;
import org.jpwilliamson.multiplex.model.skyWars.menus.SkyWarsChestMenu;
import org.jpwilliamson.multiplex.model.skyWars.menus.SpinRewardMenu;
import org.jpwilliamson.multiplex.util.Constants;
import org.mineacademy.fo.*;
import org.mineacademy.fo.model.RandomNoRepeatPicker;


/**
 * Skywars arena is a game where players spawn on a
 * island situated way above the ground level in the sky
 * where they collect items spawned on the ground
 * in exchange for items.
 *
 * The aim is to kill every player in the arena using the weapons,
 * which can be exchanged for the collected items through a menu
 * which is displayed on clicking the Ender Chest
 * Last man standing wins.
 */
public class SkyWarsArena extends Arena {


	@Getter
	public Map<UUID,Double> playerPoints = new HashMap<>();
	/**
	 * The arena unique identification type
	 */
	public static final String TYPE = "skywars";

	private SkyWarsScoreboard scoreboard;
	private Minimap minimap = null;
	private List<WorldCursorProvider> providers = new ArrayList<>(); // map providers
	/**
	 * Create a new arena
	 *
	 * @param name
	 */
	public SkyWarsArena(final String name) {
		super(TYPE, name);
		this.scoreboard=getGameScoreboard();
	}

	/**
	 * Create new arena settings
	 */
	@Override
	protected SkyWarsSettings createSettings() {
		return new SkyWarsSettings(this);
	}

	/**
	 * Create new arena heartbeat
	 */
	@Override
	protected ArenaHeartbeat createHeartbeat() {
		return new SkyWarsHeartbeat(this);
	}

	public SkyWarsScoreboard getGameScoreboard(){
		return new SkyWarsScoreboard(this);
	}

	/**
	 * Return a custom scoreboard for this arena
	 */
//	@Override
//	protected ArenaScoreboard createScoreboard() {
//		return new SkyWarsScoreboard(this);
//	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Game logic
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * Teleport players
	 */
	@Override
	protected void onStart() {
		super.onStart();

		if (isEdited())
			return;

		List<Player> players= this.getPlayers(ArenaJoinMode.PLAYING);
		for(Player p1 : players)
		{
			//Initialize the player points map with zero points for each player
			playerPoints.put(p1.getUniqueId(), 0.0);
		}
		// Use no repeat picker to ensure no 2 players will spawn at one spot
		final RandomNoRepeatPicker<Location> locationPicker = RandomNoRepeatPicker.newPicker(Location.class);
		locationPicker.setItems(getSettings().getEntrances());

		for (final ArenaPlayer arenaPlayer : getArenaPlayersInAllModes()) {
			final Player player = arenaPlayer.getPlayer();
			final Location location = locationPicker.pickRandom(player);

			// Teleport to arena
			// Open The Spin Menu As the Game Starts
			teleport(player, location);
			Bukkit.broadcastMessage("This is skywars");
			SpinRewardMenu.openSpinMenu(this);
			SpinRewardMenu.openSpinInventory(player);
			// Save their location for respawns
			setPlayerTag(arenaPlayer, Constants.Tag.ENTRANCE_LOCATION, location);
		}

		this.minimap = Cartographer.getInstance().getMapManager().constructNewMinimap("skywars");

		WorldCursorProvider provider = (player, minimap, playerSetting) -> {
			Set<WorldCursor> cursors = new HashSet<>();

			// Marking Player Locations
			for(Player player1: getPlayers(ArenaJoinMode.PLAYING)){
				WorldCursor cursor = new WorldCursor(player1.getLocation());

				cursor.setType(MapCursor.Type.GREEN_POINTER);

				cursors.add(cursor);
			}

			// Marking Chest Locations
			for(Location location: getSettings().getChests()){
				WorldCursor cursor = new WorldCursor(location);

				cursor.setType(MapCursor.Type.RED_X);
				cursors.add(cursor);
			}

			return cursors;
		};

		minimap.register(provider);

		providers.add(provider);

		scoreboard.onStart();

		for(Player player: getPlayers(ArenaJoinMode.PLAYING)){
			scoreboard.showGameBoard(player);
			giveMap(player);
		}

	}

	public void handleProviders(){
		for(WorldCursorProvider provider: providers)
			minimap.unregister(provider);
	}


	public void giveMap(Player player){
		ItemStack map = Cartographer.getInstance().getMapManager().getItemFor(minimap);

		player.getInventory().addItem(map);
	}


	@Override
	protected void onStop() {
		for(Player player: getPlayersInAllModes()) {
			scoreboard.stopShowing(player);
			player.getInventory().remove(Cartographer.getInstance().getMapManager().getItemFor(minimap));
		}

		super.onStop();

		handleProviders();

		providers.clear();
		Cartographer.getInstance().getMapManager().remove(minimap);
	}

	/**
	 * @see Arena#getRespawnLocation(org.bukkit.entity.Player)
	 */
	@Override
	protected Location getRespawnLocation(final Player player) {
		final ArenaPlayer cache = ArenaPlayer.getCache(player);
		final Location location = getPlayerTag(cache, Constants.Tag.ENTRANCE_LOCATION);
		Valid.checkNotNull(location, "Player " + player.getName() + " is missing entrance location!");

		return location;
	}

	/**
	 * Kick players without a crystal
	 */
	@Override
	protected void onPlayerRespawn(Player player, ArenaPlayer cache) {
		super.onPlayerRespawn(player, cache);
	}


	/**
	 * Handle clicking on EnderChests
	 *
	 * @see Arena#onEntityClick(org.bukkit.entity.Player, org.bukkit.entity.Entity, org.bukkit.event.player.PlayerInteractAtEntityEvent)
	 */
	@Override
	protected void onEntityClick(Player player, Entity clicked, PlayerInteractAtEntityEvent event) {
		super.onEntityClick(player, clicked, event);

		if (clicked instanceof EnderChest){
			SkyWarsChestMenu.openPurchaseMenu(this, player);
		}

	}

	/*
	* Assign points to the players on every kill that they make
	* Can be used for leaderboard
	* */
	@Override
	protected void onPlayerKill(Player killer, LivingEntity victim) {

		super.onPlayerKill(killer, victim);
		if (victim instanceof Player) {
			final ArenaPlayer killerCache = ArenaPlayer.getCache(killer);
			final double points = MathUtil.formatTwoDigitsD(RandomUtil.nextBetween(20, 30) + Math.random());

			killerCache.giveArenaPoints(killer, points);

			final ArenaPlayer victimCache = ArenaPlayer.getCache((Player) victim);
			final List<String> killVerbs = Arrays.asList("slayed", "assassinated", "murdered", "killed", "annihilated");

			broadcastExcept((Player) victim, Common.format("&8[&4&lx&8] &c%s %s %s (%s/%s)", killer, RandomUtil.nextItem(killVerbs), victim, victimCache.getRespawns() + 1, getSettings().getLives()));
			Messenger.warn(killer, "You received " + points + " points for killing " + victim.getName() + " and now have " + killerCache.getArenaPoints() + " points!");

			for (Map.Entry<UUID, Double> player : playerPoints.entrySet()) {
				if (player.getKey().equals(killer.getUniqueId())) {
					double updatePoints = player.getValue() + points;
					player.setValue(updatePoints);
				}
			}
		}
	}

	/**
	 * Prevent explosions from damaging safezone blocks
	 *
	 * @see Arena#onExplosion(org.bukkit.Location, java.util.List, org.bukkit.event.Cancellable)
	 */
	@Override
	protected void onExplosion(Location centerLocation, List<Block> blocks, Cancellable event) {
		super.onExplosion(centerLocation, blocks, event);

		//final EggWarsSettings settings = getSettings();
		final SkyWarsSettings settings = getSettings();
		final List<Location> locations = Common.joinArrays(
				settings.getIron(),
				settings.getGold(),
				settings.getDiamonds());

		for (final Entity entity : settings.getRegion().getEntities())
			if (entity instanceof EnderCrystal || entity instanceof Villager)
				locations.add(entity.getLocation());

		for (final Iterator<Block> it = blocks.iterator(); it.hasNext();) {
			final Block block = it.next();
			final Location blockLocation = block.getLocation();
			final Location closestSafezone = BlockUtil.findClosestLocation(blockLocation, locations);

			if (closestSafezone.distance(blockLocation) < 3)
				it.remove();
		}
	}

	/**
	 * @see Arena#onLeave(org.bukkit.entity.Player, ArenaLeaveReason)
	 */
	@Override
	protected void onLeave(final Player player, final ArenaLeaveReason reason) {
		super.onLeave(player, reason);

		checkLastStanding();
	}

	/**
	 * Run the last standing check when a player enters spectate mode, stop
	 * arena if only 1 player is left playing
	 */
	@Override
	protected void onSpectateStart(final Player player, final ArenaLeaveReason reason) {
		super.onSpectateStart(player, reason);

		checkLastStanding();
	}

	/*
	 * Check if there is only one last player in the playing mode then stop the arena
	 * and announce winner
	 */
	private void checkLastStanding() {
		if (getPlayers(ArenaJoinMode.PLAYING).size() == 1 && !isStopping()) {
			final Player winner = getPlayers(ArenaJoinMode.PLAYING).get(0);
			leavePlayer(winner, ArenaLeaveReason.LAST_STANDING);
		}
	}

	/**
	 * @see Arena#canSpectateOnLeave(org.bukkit.entity.Player)
	 */
	@Override
	protected boolean canSpectateOnLeave(final Player player) {
		return getPlayers(ArenaJoinMode.PLAYING).size() > 1;
	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Pluggable
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * @see Arena#hasPvP()
	 */
	@Override
	protected boolean hasPvP() {
		return true;
	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Overrides
	// ------–------–------–------–------–------–------–------–------–------–------–------–

		@Override
		public SkyWarsSettings getSettings() {
			return (SkyWarsSettings) super.getSettings();
		}


}

