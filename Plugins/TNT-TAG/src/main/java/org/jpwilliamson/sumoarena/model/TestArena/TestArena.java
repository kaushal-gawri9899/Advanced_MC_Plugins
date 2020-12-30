package org.jpwilliamson.sumoarena.model.TestArena;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jpwilliamson.sumoarena.model.*;
import org.jpwilliamson.sumoarena.model.dm.DeathmatchSettings;
import org.mineacademy.fo.model.RandomNoRepeatPicker;

import java.util.List;

public class TestArena extends Arena {

	public static String TYPE = "test";
//	/**
//	 * Create a new arena. If the arena settings do not yet exist,
//	 * they are created automatically.
//	 *
//	 * @param type
//	 * @param name
//	 */
	public TestArena(final String name) {
		super(TYPE, name);
	}

	@Override
	public TestArenaSettings createSettings() {
		return new TestArenaSettings(this);
	}

	@Override
	protected void onStart() {

		List<Player> players = getPlayers(ArenaJoinMode.PLAYING);

		super.onStart();

		// Use no repeat picker to ensure no 2 players will spawn at one spot
		final RandomNoRepeatPicker<Location> locationPicker = RandomNoRepeatPicker.newPicker(Location.class);
		locationPicker.setItems(getSettings().getEntrances().getLocations());

		if (getState() != ArenaState.EDITED)
			forEachInAllModes((player) -> {
				final Location location = locationPicker.pickRandom(player);

				teleport(player, location);
			});


		for(Player player : players){
			//Testing the sound
			player.sendMessage("Sound will be played now");
			player.playSound(player.getLocation(),"itemsadder:ambient.creepy" , 1, 1);
		}
	}

	@Override
	public TestArenaSettings getSettings() {
		return (TestArenaSettings) super.getSettings();
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

	@Override
	protected boolean hasPvP() {
		return true;
	}


}

