package org.jpwilliamson.arena.model.dm;

import org.codehaus.jackson.map.ObjectMapper;
import org.jpwilliamson.arena.model.ArenaSettings;
import org.jpwilliamson.arena.model.Arena;
import org.mineacademy.fo.collection.SerializedMap;

import lombok.Getter;

import java.io.IOException;

/**
 * Represents settings used in deathmatch arenas
 */
@Getter
public class DeathmatchSettings extends ArenaSettings {

	/**
	 * The list of all entrance locations in the arena
	 */
	private LocationList entrances;

	@Getter
	private String jsonString = "";
	/**
	 * Create new arena settings
	 *
	 * @param arena
	 */
	public DeathmatchSettings(final Arena arena) {
		super(arena);
	}

	/**
	 * @see ArenaSettings#onLoadFinish()
	 */
	@Override
	protected void onLoadFinish() {
		super.onLoadFinish();
		this.entrances = getLocations("Entrance_Locations");
//		ObjectMapper Obj = new ObjectMapper();
//		try {
//
//			jsonString = Obj.writeValueAsString(this);
//		}
//		catch(IOException e){
//			e.printStackTrace();
//		}
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
