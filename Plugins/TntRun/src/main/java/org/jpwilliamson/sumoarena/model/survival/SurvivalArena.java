package org.jpwilliamson.sumoarena.model.survival;

import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.jpwilliamson.sumoarena.model.*;
import org.mineacademy.fo.*;
import org.mineacademy.fo.model.RandomNoRepeatPicker;

import java.util.*;


public class SurvivalArena extends Arena {

	public static final String TYPE = "survival";

	@Getter
	public Map<UUID,Double> playerPoints = new HashMap<>();

	/*
	 * Create a new arena. If the arena settings do not yet exist,
	 * they are created automatically.
	 *
	 * @param type
	 * @param name
	 */
	protected SurvivalArena(final String name) {
		super(TYPE, name);
	}

	@Override
	protected ArenaSettings createSettings() {
		return new SurvivalArenaSettings(this);
	}



	/**
	 * @see Arena#createHeartbeat()
	 */
	@Override
	protected ArenaHeartbeat createHeartbeat() {
		return new SurvivalArenaHeartBeat(this);
	}

	/**
	 * @see Arena#createScoreboard()
	 */
	@Override
	protected ArenaScoreboard createScoreboard() {
		return new SurvivalArenaScoreboard(this);
	}


	@Override
	protected void onStart() {
		super.onStart();


		List<Player> players= this.getPlayers(ArenaJoinMode.PLAYING);
		for(Player p1 : players)
		{
			//Initialize the player points map with zero points for each player
			playerPoints.put(p1.getUniqueId(), 0.0);
		}

		final RandomNoRepeatPicker<Location> locationPicker = RandomNoRepeatPicker.newPicker(Location.class);
		locationPicker.setItems(getSettings().getEntranceLocation().getLocations());

		if (getState() != ArenaState.EDITED)
			forEachInAllModes((player) -> {
				final Location location = locationPicker.pickRandom(player);

				teleport(player, location);
			});
	}

	@Override
	protected void onPlayerKill(final Player killer, final LivingEntity victim) {
		super.onPlayerKill(killer, victim);

		if (victim instanceof Player) {
			final ArenaPlayer killerCache = ArenaPlayer.getCache(killer);
			final double points = MathUtil.formatTwoDigitsD(RandomUtil.nextBetween(20, 30) + Math.random());

			killerCache.giveArenaPoints(killer, points);

			final ArenaPlayer victimCache = ArenaPlayer.getCache((Player) victim);
			final List<String> killVerbs = Arrays.asList("slayed","assassinated", "murdered","killed", "annihilated");

			broadcastExcept((Player) victim, Common.format("&8[&4&lx&8] &c%s %s %s (%s/%s)", killer, RandomUtil.nextItem(killVerbs), victim, victimCache.getRespawns() + 1, getSettings().getLives()));
			Messenger.warn(killer, "You received " + points + " points for killing " + victim.getName() + " and now have " + killerCache.getArenaPoints() + " points!");

			for(Map.Entry<UUID,Double> player : playerPoints.entrySet()){
				if(player.getKey().equals(killer.getUniqueId())){
					double updatePoints = player.getValue() + points;
					player.setValue(updatePoints);
				}
			}
			((Player) victim).setGameMode(GameMode.SPECTATOR);
			victim.teleport(killer.getLocation());
		}

		if (victim instanceof Monster) {
			final ArenaPlayer cache = ArenaPlayer.getCache(killer);
			final double points = MathUtil.formatTwoDigitsD(RandomUtil.nextBetween(10, 20) + Math.random());

			cache.giveArenaPoints(killer, points);
			Messenger.warn(killer, "You received " + points + " points for killing " + Common.article(ItemUtil.bountifyCapitalized(victim.getType()))
					+ " and now have " + cache.getArenaPoints() + " points!");

			for(Map.Entry<UUID,Double> player : playerPoints.entrySet()){
				if(player.getKey().equals(killer.getUniqueId())){
					double updatePoints = player.getValue() + points;
					player.setValue(updatePoints);
				}
			}
		}
	}

	@Override
	protected Location getRespawnLocation(final Player player) {
		return RandomUtil.nextItem(getSettings().getEntranceLocation());
	}

	private void validateLastPlayer() {
		if (getPlayers(ArenaJoinMode.PLAYING).size() == 1 && !isStopping()) {
			final Player winner = getPlayers(ArenaJoinMode.PLAYING).get(0);
			leavePlayer(winner, ArenaLeaveReason.LAST_STANDING);
		}
	}

	@Override
	protected void onLeave(final Player player, final ArenaLeaveReason reason) {
		super.onLeave(player, reason);

		validateLastPlayer();
	}

	/**
	 * Run the last standing check when a player enters spectate mode, stop
	 * arena if only 1 player is left playing
	 *
	 */
	@Override
	protected void onSpectateStart(final Player player, final ArenaLeaveReason reason) {
		super.onSpectateStart(player, reason);

		final ArenaPlayer cache = ArenaPlayer.getCache(player);

		if (!hasPlayerComeLater(player) && cache.getPlayedToWave() == 0)
			cache.setPlayedToWave(getHeartbeat().getWave());
		validateLastPlayer();
	}

	/*
	* Apart from player kill
	* Provide players with some points for surviving few waves of mob spawn
	* Double the points provided if more than 20 waves survived
	* Otherwise increase the points by 1.5 times of number of waves
	* */
	@Override
	protected void onReward(final Player player, final ArenaPlayer cache) {
		//super.onReward(player, cache); Disable the generic points message

		final int wave = cache.getPlayedToWave() != 0 ? cache.getPlayedToWave() : getHeartbeat().getWave();

		// Only give points if survived 5 waves or more
		// If survived 20 or more waves, multiply by 2, otherwise multiply by 2.5
		final double points = wave >= 20 ? wave * 2 : wave >= 5 ? wave * 1.5 : 0;
		final double totalArenaPoints = points + cache.getArenaPoints();

		if (totalArenaPoints > 0) {
			cache.giveArenaPoints(player, points);

			Messenger.warn(player, "You received " + MathUtil.formatTwoDigits(points) + " points for surviving " + Common.plural(wave, "wave")
					+ ". Points gained in arena: " + totalArenaPoints + " and overall: " + cache.getTotalPoints() + " points.");
		}

		/* Update player points for surviving mob waves */
		for(Map.Entry<UUID,Double> p : playerPoints.entrySet()){
			if(p.getKey().equals(player.getUniqueId())){
				double updatePoints = p.getValue() + points;
				p.setValue(updatePoints);
			}
		}

	}
	@Override
	protected boolean canSpectateOnLeave(final Player player) {
		return getPlayers(ArenaJoinMode.PLAYING).size() > 1;
	}

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

	@Override
	public SurvivalArenaSettings getSettings() {
		return (SurvivalArenaSettings) super.getSettings();
	}

	/**
	 * @see Arena#getHeartbeat()
	 */
	@Override
	public SurvivalArenaHeartBeat getHeartbeat() {
		return (SurvivalArenaHeartBeat) super.getHeartbeat();
	}


}
