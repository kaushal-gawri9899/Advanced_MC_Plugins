package org.jpwilliamson.multiplex.model.monster;

import io.github.bananapuncher714.cartographer.core.Cartographer;
import io.github.bananapuncher714.cartographer.core.api.WorldCursor;
import io.github.bananapuncher714.cartographer.core.api.map.WorldCursorProvider;
import io.github.bananapuncher714.cartographer.core.map.Minimap;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCursor;
import org.jpwilliamson.multiplex.model.*;
import org.jpwilliamson.multiplex.model.blackOps.BlackOpsScoreboard;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.RandomUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple monster arena
 */
public class MobArena extends Arena {

	/**
	 * The arena unique identification type
	 */
	public static final String TYPE = "monster";

	private MobArenaScoreboard scoreboard;

	private Minimap minimap = null;

	private List<WorldCursorProvider> providers = new ArrayList<>(); // map providers
	/**
	 * Create a new monster arena
	 *
	 * @param name
	 */
	public MobArena(final String name) {
		super(TYPE, name);
		this.scoreboard = getGameScoreboard();
	}

	/**
	 * Create new arena settings
	 */
	@Override
	protected ArenaSettings createSettings() {
		return new MobArenaSettings(this);
	}

	/**
	 * @see Arena#createHeartbeat()
	 */
	@Override
	protected ArenaHeartbeat createHeartbeat() {
		return new MobArenaHeartbeat(this);
	}

	/**
	 * @see Arena#createScoreboard()
	 */
//	@Override
//	protected ArenaScoreboard createScoreboard() {
//		return new ArenaScoreboard(this);
//	}


	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Game logic
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * @see Arena#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();

		Location location = getSettings().getEntranceLocation();
//		MythicMobs.inst().getMobManager().spawnMob("Mammoth", location, 1);
//		broadcast("Mob Spawned");
		if (getState() != ArenaState.EDITED)
			forEachInAllModes((player) -> teleport(player, getSettings().getEntranceLocation()));

		this.minimap = Cartographer.getInstance().getMapManager().constructNewMinimap("BlackOps");

		WorldCursorProvider provider = (player, minimap, playerSetting) -> {
			Set<WorldCursor> cursors = new HashSet<>();

			// Marking Player Locations
			for(Player player1: getPlayers(ArenaJoinMode.PLAYING)){
				WorldCursor cursor = new WorldCursor(player1.getLocation());

				cursor.setType(MapCursor.Type.GREEN_POINTER);

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

	public void handleWorldProviders(){
		for(WorldCursorProvider provider: providers)
			minimap.unregister(provider);
	}

	public void giveMap(Player player){
		ItemStack map = Cartographer.getInstance().getMapManager().getItemFor(minimap);

		player.getInventory().addItem(map);
	}

	/**
	 * @see Arena#getRespawnLocation(org.bukkit.entity.Player)
	 */
	@Override
	protected Location getRespawnLocation(final Player player) {
		return getSettings().getEntranceLocation();
	}

	@Override
	protected void onPlayerDamage(Player attacker, Entity victim, EntityDamageByEntityEvent event) {
		//super.onPlayerDamage(attacker, victim, event);
		if(victim instanceof Monster){
			String name = victim.getName();
            broadcast("Entity being hit " + name);
            attacker.getInventory().addItem(new ItemStack(Material.IRON_INGOT));
		}
	}

	@Override
	protected void onStop() {
		for(Player player: getPlayersInAllModes()) {
			scoreboard.stopShowing(player);
			player.getInventory().remove(Cartographer.getInstance().getMapManager().getItemFor(minimap));
		}
		super.onStop();

		handleWorldProviders();
		providers.clear();
		Cartographer.getInstance().getMapManager().remove(minimap);
	}

	/**
	 * @see Arena#onPlayerKill(org.bukkit.entity.Player, org.bukkit.entity.LivingEntity)
	 */
	@Override
	protected void onPlayerKill(final Player killer, final LivingEntity victim) {
		super.onPlayerKill(killer, victim);

		if (victim instanceof Monster) {
			final ArenaPlayer cache = ArenaPlayer.getCache(killer);
			final double points = MathUtil.formatTwoDigitsD(RandomUtil.nextBetween(10, 20) + Math.random());

			cache.giveArenaPoints(killer, points);
			Messenger.warn(killer, "You received " + points + " points for killing " + Common.article(ItemUtil.bountifyCapitalized(victim.getType()))
					+ " and now have " + cache.getArenaPoints() + " points!");
		}
	}

	/**
	 * @see Arena#onSpectateStart(org.bukkit.entity.Player, ArenaLeaveReason)
	 */
	@Override
	protected void onSpectateStart(final Player player, final ArenaLeaveReason reason) {
		super.onSpectateStart(player, reason);

		final ArenaPlayer cache = ArenaPlayer.getCache(player);

		if (!hasPlayerComeLater(player) && cache.getPlayedToWave() == 0)
			cache.setPlayedToWave(getHeartbeat().getWave());
	}

	/**
	 * @see Arena#onReward(org.bukkit.entity.Player, ArenaPlayer)
	 */
	@Override
	protected void onReward(final Player player, final ArenaPlayer cache) {
		//super.onReward(player, cache); Disable the generic points message

		final int wave = cache.getPlayedToWave() != 0 ? cache.getPlayedToWave() : getHeartbeat().getWave();

		// Only give points if survived 5 waves or more
		// If survived 20 or more waves, multiply by 2, otherwise multiply by 2.5
		final double points = wave >= 20 ? wave * 2 : wave >= 5 ? wave * 2.5 : 0;
		final double totalArenaPoints = points + cache.getArenaPoints();

		if (totalArenaPoints > 0) {
			cache.giveArenaPoints(player, points);

			Messenger.warn(player, "You received " + MathUtil.formatTwoDigits(points) + " points for surviving " + Common.plural(wave, "wave")
					+ ". Points gained in arena: " + totalArenaPoints + " and overall: " + cache.getTotalPoints() + " points.");
		}
	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Pluggable
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * @see Arena#hasLives()
	 */
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

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Misc
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * @see Arena#getHeartbeat()
	 */
	@Override
	public MobArenaHeartbeat getHeartbeat() {
		return (MobArenaHeartbeat) super.getHeartbeat();
	}

	/**
	 * @see Arena#getSettings()
	 */
	@Override
	public MobArenaSettings getSettings() {
		return (MobArenaSettings) super.getSettings();
	}

	/**
	 * @see Arena#getScoreboard()
	 */

	public MobArenaScoreboard getGameScoreboard() {
		return new MobArenaScoreboard(this);
	}
}
