package org.jpwilliamson.sumoarena.model.monster;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.jpwilliamson.sumoarena.model.Arena;
import org.jpwilliamson.sumoarena.model.ArenaHeartbeat;
import org.jpwilliamson.sumoarena.model.ArenaLeaveReason;
import org.jpwilliamson.sumoarena.model.ArenaPlayer;
import org.jpwilliamson.sumoarena.model.ArenaScoreboard;
import org.jpwilliamson.sumoarena.model.ArenaSettings;
import org.jpwilliamson.sumoarena.model.ArenaState;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.RandomUtil;

/**
 * A simple monster arena
 */
public class MobArena extends Arena {

	/**
	 * The arena unique identification type
	 */
	public static final String TYPE = "monster";

	/**
	 * Create a new monster arena
	 *
	 * @param name
	 */
	public MobArena(final String name) {
		super(TYPE, name);
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
	@Override
	protected ArenaScoreboard createScoreboard() {
		return new MobArenaScoreboard(this);
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

		if (getState() != ArenaState.EDITED)
			forEachInAllModes((player) -> teleport(player, getSettings().getEntranceLocation()));
	}

	/**
	 * @see Arena#getRespawnLocation(org.bukkit.entity.Player)
	 */
	@Override
	protected Location getRespawnLocation(final Player player) {
		return getSettings().getEntranceLocation();
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
	@Override
	public MobArenaScoreboard getScoreboard() {
		return (MobArenaScoreboard) super.getScoreboard();
	}
}
