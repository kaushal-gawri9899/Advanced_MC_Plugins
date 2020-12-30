package org.jpwilliamson.sumoarena.model.dm;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jpwilliamson.sumoarena.model.Arena;
import org.jpwilliamson.sumoarena.model.ArenaJoinMode;
import org.jpwilliamson.sumoarena.model.ArenaLeaveReason;
import org.jpwilliamson.sumoarena.model.ArenaPlayer;
import org.jpwilliamson.sumoarena.model.ArenaState;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.model.RandomNoRepeatPicker;

/**
 * Represents an arena where players shall kill one another
 */
public class DeathmatchArena extends Arena {

	/**
	 * The arena unique identification type
	 */
	public static final String TYPE = "deathmatch";

	/**
	 * Create a new deathmatch arena
	 *
	 * @param name
	 */
	public DeathmatchArena(final String name) {
		super(TYPE, name);
	}

	/**
	 * Create new arena settings
	 */
	@Override
	protected DeathmatchSettings createSettings() {
		return new DeathmatchSettings(this);
	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Game logic
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * @see Arena#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();

		// Use no repeat picker to ensure no 2 players will spawn at one spot
		final RandomNoRepeatPicker<Location> locationPicker = RandomNoRepeatPicker.newPicker(Location.class);
		locationPicker.setItems(getSettings().getEntrances().getLocations());

		if (getState() != ArenaState.EDITED)
			forEachInAllModes((player) -> {
				final Location location = locationPicker.pickRandom(player);

				teleport(player, location);
			});


	}

	/**
	 * @see Arena#getRespawnLocation(org.bukkit.entity.Player)
	 */
	@Override
	protected Location getRespawnLocation(final Player player) {
		return RandomUtil.nextItem(getSettings().getEntrances());
	}

	/**
	 * @see Arena#onPlayerKill(org.bukkit.entity.Player, org.bukkit.entity.LivingEntity)
	 */
	@Override
	protected void onPlayerKill(final Player killer, final LivingEntity victim) {
		super.onPlayerKill(killer, victim);

		if (victim instanceof Player) {
			final ArenaPlayer killerCache = ArenaPlayer.getCache(killer);
			final double points = MathUtil.formatTwoDigitsD(RandomUtil.nextBetween(20, 30) + Math.random());

			killerCache.giveArenaPoints(killer, points);

			final ArenaPlayer victimCache = ArenaPlayer.getCache((Player) victim);
			final List<String> killVerbs = Arrays.asList("murdered", "killed", "slayed", "terminated", "assassinated", "annihilated", "evaporated");

			broadcastExcept((Player) victim, Common.format("&8[&4&lx&8] &c%s %s %s (%s/%s)", killer, RandomUtil.nextItem(killVerbs), victim, victimCache.getRespawns() + 1, getSettings().getLives()));
			Messenger.warn(killer, "You received " + points + " points for killing " + victim.getName() + " and now have " + killerCache.getArenaPoints() + " points!");
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
	 *
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

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Misc
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * @see Arena#getSettings()
	 */
	@Override
	public DeathmatchSettings getSettings() {
		return (DeathmatchSettings) super.getSettings();
	}
}
