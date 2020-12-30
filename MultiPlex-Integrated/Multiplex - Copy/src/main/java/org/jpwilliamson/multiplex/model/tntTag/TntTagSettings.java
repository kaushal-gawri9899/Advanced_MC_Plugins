package org.jpwilliamson.multiplex.model.tntTag;

import github.scarsz.discordsrv.dependencies.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.jpwilliamson.multiplex.model.ArenaSettings;
import org.jpwilliamson.multiplex.model.tntTag.TntTagArena;
//import org.jpwilliamson.sumoarena.model.Arena;
//import org.jpwilliamson.sumoarena.model.ArenaSettings;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.SimpleTime;

import java.util.Map;
import java.util.UUID;

public class TntTagSettings extends ArenaSettings {

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
	public TntTagSettings(final TntTagArena arena) {
		super(arena);
		this.setGameDuration(SimpleTime.fromSeconds(360));
		this.setMaxPlayers(4);
		this.setMinPlayers(1);
		this.setLobbyDuration(SimpleTime.fromSeconds(20));

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
