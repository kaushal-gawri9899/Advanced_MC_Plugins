package org.jpwilliamson.sumoarena.model.TestArena;

import lombok.Getter;
import org.jpwilliamson.sumoarena.model.Arena;
import org.jpwilliamson.sumoarena.model.ArenaSettings;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.SimpleTime;

public class TestArenaSettings extends ArenaSettings {
	/**
	 * The list of all entrance locations in the arena
	 */
	@Getter
	private LocationList entrances;

	/**
	 * Create new arena settings
	 *
	 * @param arena
	 */
	public TestArenaSettings(final Arena arena) {
		super(arena);
		this.setMaxPlayers(4);
		this.setMinPlayers(1);
		this.setGameDuration(SimpleTime.fromSeconds(300));
		this.setLobbyDuration(SimpleTime.fromSeconds(15));
	}

	/**
	 * @see ArenaSettings#onLoadFinish()
	 */
	@Override
	protected void onLoadFinish() {
		super.onLoadFinish();

		this.entrances = getLocations("Entrance_Locations");
	}

	/**
	 * @see ArenaSettings#isSetup()
	 */
	@Override
	public boolean isSetup() {
		return super.isSetup() && entrances.size() >= getMaxPlayers();
	}

	/**
	 * @see ArenaSettings#serialize()
	 */
	@Override
	public SerializedMap serialize() {
		final SerializedMap map = super.serialize();

		map.putArray("Entrance_Locations", entrances);

		return map;
	}
}
