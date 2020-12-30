package org.jpwilliamson.multiplex.model.team;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jpwilliamson.multiplex.model.*;
import org.mineacademy.fo.*;
import org.mineacademy.fo.collection.StrictMap;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.RandomNoRepeatPicker;
import org.mineacademy.fo.model.SimpleEquipment;
import org.mineacademy.fo.remain.CompColor;
import org.mineacademy.fo.remain.CompEquipmentSlot;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.*;
import java.util.function.Consumer;

/**
 * A simple team arena
 */
public abstract class TeamArena extends Arena {

	/**
	 * Holds data for different teams, removed when arena stops
	 */
	@Getter(value = AccessLevel.PROTECTED)
	private final StrictMap<ArenaTeam, StrictMap<String, Object>> teamTags = new StrictMap<>();

	@Getter@Setter
	private String InGameMode;

	/**
	 * Create a new team arena
	 *
	 * @param name
	 */
	protected TeamArena(final String type, final String name) {
		super(type, name);
	}

	/**
	 * Create new arena settings
	 */
	@Override
	protected ArenaSettings createSettings() {
		return new TeamArenaSettings(this);
	}

	/**
	 * @see Arena#createScoreboard()
	 */
	@Override
	protected ArenaScoreboard createScoreboard() {
		return new TeamArenaScoreboard(this);
	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Game logic
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * @see Arena#canJoin(org.bukkit.entity.Player, ArenaJoinMode)
	 */
	@Override
	protected boolean canJoin(final Player player, final ArenaJoinMode joinMode) {
		final int applicableTeams = getApplicableTeams().size();

		if (joinMode != ArenaJoinMode.EDITING) {
			if (ArenaTeam.getTeams().size() < 2) {
				Messenger.error(player, "Arena requires at least 2 teams to be setup. If you are an admin, create new teams with '/arena teams'.");

				return false;
			}

			if (applicableTeams < 2) {
				Messenger.error(player, "At least 2 teams must be applicable for this arena. If you are an admin, edit this with '/arena teams'.");

				return false;
			}
		}

		else {
			if (applicableTeams < 2)
				Common.runLater(5, () -> Messenger.warn(player, "There is not enough teams that can play in this arena. Please select at least 2 with '/arena teams'."));
		}

		return super.canJoin(player, joinMode);
	}

	/**
	 * @see Arena#onPreStart()
	 */
	@Override
	protected void onPreStart() {
		super.onPreStart();

		teamTags.clear();

		if (isStopped())
			return;

		// Kick players without team
		final List<ArenaPlayer> players = new ArrayList<>(this.getArenaPlayersInAllModes());

		// Picker to pick a random class for players who did not select any
		final RandomNoRepeatPicker<ArenaTeam> teamPicker = RandomNoRepeatPicker.newPicker((player, team) -> isBalancedJoin(team, true) && team.canAssign(player, this));

		for (final ArenaPlayer cache : players) {
			// Stopped in the meanwhile
			if (isStopped())
				return;

			final Player player = cache.getPlayer();

			if (cache.getArenaTeam() == null) {
				final ArenaTeam picked = teamPicker.pickFromFor(ArenaTeam.getTeams(), player);

				if (picked != null)
					picked.assignTo(cache.getPlayer());

				else
					leavePlayer(cache.getPlayer(), ArenaLeaveReason.NO_TEAM);
			}
		}
	}

	/**
	 * @see Arena#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();

		if (getState() == ArenaState.EDITED)
			return;

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
	}

	/**
	 * @see Arena#getRespawnLocation(org.bukkit.entity.Player)
	 */
	@Override
	protected Location getRespawnLocation(final Player player) {
		final ArenaPlayer cache = ArenaPlayer.getCache(player);

		return getSettings().findSpawnpoint(cache.getArenaTeam());
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
			Messenger.warn(killer, "You received " + points + " points for killing " + victim.getName() + " and now have " + killerCache.getArenaPoints() + " points!");

			final ArenaPlayer victimCache = ArenaPlayer.getCache((Player) victim);
			final List<String> killVerbs = Arrays.asList("murdered", "killed", "slayed", "terminated", "assassinated", "annihilated", "evaporated");

			broadcastExcept((Player) victim, Common.format("&8[&4x&8] &c%s %s %s (%s/%s)", killer, RandomUtil.nextItem(killVerbs), victim, victimCache.getRespawns() + 1, getSettings().getLives()));
		}
	}

	/**
	 * @see Arena#onLeave(org.bukkit.entity.Player, ArenaLeaveReason)
	 */
	@Override
	protected void onLeave(final Player player, final ArenaLeaveReason reason) {
		super.onLeave(player, reason);

		// Stop the arena if there is only 1 team left
		if (stopIfLastStanding() && !isStopped() && !isStopping() && getLastTeamStanding() != null)
			stopArena(reason == ArenaLeaveReason.NO_LIVES_LEFT ? ArenaStopReason.LAST_TEAM_STANDING : ArenaStopReason.OTHER_TEAMS_LEFT);
	}

	/**
	 * @see Arena#onPvP(org.bukkit.entity.Player, org.bukkit.entity.Player, org.bukkit.event.entity.EntityDamageByEntityEvent)
	 */
	@Override
	protected void onPvP(final Player attacker, final Player victim, final EntityDamageByEntityEvent event) {
		super.onPvP(attacker, victim, event);

		final ArenaPlayer attackerCache = ArenaPlayer.getCache(attacker);
		final ArenaPlayer victimCache = ArenaPlayer.getCache(victim);

		// Disallow attacking team mates
		if (attackerCache.getArenaTeam().equals(victimCache.getArenaTeam())) {
			Messenger.error(attacker, "Friendly fire is disallowed.");

			event.setCancelled(true);
			returnHandled();
		}
	}

	/**
	 * @see Arena#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();

		teamTags.clear();
	}

	/**
	 * @see Arena#canSpectateOnLeave(org.bukkit.entity.Player)
	 */
	@Override
	protected boolean canSpectateOnLeave(final Player player) {
		return getLastTeamStanding() == null;
	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Team methods
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * Return if by joining this team the team player balance will remain
	 *
	 * @param teamToJoin
	 * @param strict if the teams have equal amount of players and the threshold is 1,
	 * 				 return false
	 * @return
	 */
	public final boolean isBalancedJoin(final ArenaTeam teamToJoin, final boolean strict) {
		final int lowestTeamPlayers = getTeamWithLowestPlayers().getPlayers(this).size();
		final int joinTeamPlayers = teamToJoin.getPlayers(this).size();

		final int imbalance = joinTeamPlayers - lowestTeamPlayers;

		// how much the difference between our team and lowest player team can be
		final int threshold = strict ? 1 : getSettings().getMaximumTeamImbalance();

		if (strict && threshold == 1 && imbalance == 0 && lowestTeamPlayers > 0)
			return false;

		return imbalance < threshold;
	}

	/**
	 * Return the last team alive, or null if there are 2+ teams alive
	 * or no team alive
	 *
	 * @return
	 */
	protected final ArenaTeam getLastTeamStanding() {
		ArenaTeam lastTeam = null;

		for (final ArenaPlayer cache : getArenaPlayers(ArenaJoinMode.PLAYING)) {
			final ArenaTeam playerTeam = cache.getArenaTeam();

			// If the player has another team it means there are 2+
			// teams left, so we just return null
			if (lastTeam != null && !playerTeam.equals(lastTeam))
				return null;

			lastTeam = playerTeam;
		}

		return lastTeam;
	}

	/**
	 * Run a function for each team member
	 *
	 * @param team
	 * @param consumer
	 */
	protected final void forEachTeam(final ArenaTeam team, final Consumer<Player> consumer) {
		for (final ArenaPlayer cache : getTeamPlayers(team))
			consumer.accept(cache.getPlayer());
	}

	/**
	 * Kick all players for the given team off of the arena
	 *
	 * @param team
	 * @param reason
	 */
	protected final void leaveTeamPlayers(final ArenaTeam team, final ArenaLeaveReason reason) {
		for (final ArenaPlayer cache : new ArrayList<>(getTeamPlayers(team)))
			leavePlayer(cache.getPlayer(), reason);
	}

	/**
	 * Return all teams with their players
	 *
	 * @return
	 */
	protected final Map<ArenaTeam, List<ArenaPlayer>> getTeams() {
		final StrictMap<ArenaTeam, List<ArenaPlayer>> map = new StrictMap<>();

		for (final ArenaPlayer player : getArenaPlayers(ArenaJoinMode.PLAYING)) {
			final ArenaTeam team = player.getArenaTeam();
			final List<ArenaPlayer> teamPlayers = map.getOrDefault(team, new ArrayList<>());

			teamPlayers.add(player);
			map.override(team, teamPlayers);
		}

		return map.getSource();
	}

	/**
	 * Find a team with lowest player count
	 *
	 * @return
	 */
	protected final ArenaTeam getTeamWithLowestPlayers() {
		final Map<Integer, ArenaTeam> teamsByPlayers = new TreeMap<>();

		for (final ArenaTeam team : getApplicableTeams())
			teamsByPlayers.put(getTeamPlayers(team).size(), team);

		return teamsByPlayers.isEmpty() ? null : teamsByPlayers.values().iterator().next();
	}

	/**
	 * Return a list of team members
	 *
	 * @param team
	 * @return
	 */
	public final List<ArenaPlayer> getTeamPlayers(final ArenaTeam team) {
		final List<ArenaPlayer> teamPlayers = new ArrayList<>();

		for (final ArenaPlayer cache : getArenaPlayers(ArenaJoinMode.PLAYING)) {
			final ArenaTeam playerTeam = cache.getArenaTeam();

			if (getState() == ArenaState.PLAYED && !isStarting())
				Valid.checkNotNull(playerTeam, "Found player " + cache.getPlayer().getName() + " without a team!");

			if (playerTeam != null && playerTeam.equals(team))
				teamPlayers.add(cache);
		}

		return teamPlayers;
	}

	/**
	 * Get all teams that can be applied in this arena
	 *
	 * @return
	 */
	protected final List<ArenaTeam> getApplicableTeams() {
		final List<ArenaTeam> applicableTeams = new ArrayList<>();

		for (final ArenaTeam team : ArenaTeam.getTeams())
			if (team.getApplicableArenas().contains(getName()) || team.getApplicableArenas().isEmpty())
				applicableTeams.add(team);

		return applicableTeams;
	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Team data
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * Return true if the given team has data at the given key
	 *
	 * @param team
	 * @param key
	 * @return
	 */
	protected final boolean hasTeamTag(final ArenaTeam team, final String key) {
		return getTeamTag(team, key) != null;
	}

	/**
	 * Return the a value at the given key for the team, null if not set
	 *
	 * @param <T>
	 * @param team
	 * @param key
	 * @return
	 */
	protected final <T> T getTeamTag(final ArenaTeam team, final String key) {
		if (teamTags.contains(team)) {
			final Object value = teamTags.get(team).get(key);

			return value != null ? (T) value : null;
		}

		return null;
	}

	/**
	 * Sets the team a key-value data pair that is persistent until the arena finishes
	 * even if the team gets kicked out
	 *
	 * @param team
	 * @param key
	 * @param value
	 */
	protected final void setTeamTag(final ArenaTeam team, final String key, final Object value) {
		final StrictMap<String, Object> teamData = teamTags.getOrDefault(team, new StrictMap<>());

		teamData.override(key, value);
		teamTags.override(team, teamData);
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
	 * @see Arena#hasTeams()
	 */
	@Override
	protected final boolean hasTeams() {
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

	/**
	 * Stop the arena automatically if the last team is alive only
	 *
	 * @return
	 */
	protected boolean stopIfLastStanding() {
		return true;
	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Misc
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * @see Arena#getSettings()
	 */
	@Override
	public TeamArenaSettings getSettings() {
		return (TeamArenaSettings) super.getSettings();
	}

	/**
	 * @see Arena#getScoreboard()
	 */
	@Override
	public TeamArenaScoreboard getScoreboard() {
		return (TeamArenaScoreboard) super.getScoreboard();
	}
}
