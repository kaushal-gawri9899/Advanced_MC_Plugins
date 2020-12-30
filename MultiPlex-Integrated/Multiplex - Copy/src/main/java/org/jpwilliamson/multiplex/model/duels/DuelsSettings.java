package org.jpwilliamson.multiplex.model.duels;

import lombok.Getter;
import org.bukkit.Location;
import org.jpwilliamson.multiplex.model.ArenaSettings;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.SimpleEnchant;
import org.mineacademy.fo.model.SimpleTime;

@Getter
public class DuelsSettings extends ArenaSettings {

	@Getter
	private LocationList entrances;

	@Getter
	private Location stadiumLocation;


	/**
	 * Create new arena settings
	 *
	 * @param arena
	 */
	public DuelsSettings(final DuelsArena arena) {
		super(arena);
	}

	@Override
	protected void onLoadFinish() {
		super.onLoadFinish();
		setLobbyDuration(SimpleTime.fromSeconds(25));
		setGameDuration(SimpleTime.fromSeconds(600));
		this.entrances = getLocations("Entrance_Locations");
		this.stadiumLocation = getLocation("Stadium_Location");

	}

	public final boolean toggleEntranceLocation(final Location location){
		for(Location locationList : entrances){
			if(Valid.locationEquals(location, locationList)){
				entrances.remove(location);
				save();
				return false;
			}
		}
		entrances.add(location);
		return true;
	}

	@Override
	public boolean isSetup() {
		return super.isSetup() && entrances.size() == 2 && stadiumLocation!=null;
	}

	public void setStadiumLocation(Location location){
		this.stadiumLocation = location;
		save();
	}

	public void setEntrance(LocationList location){
		this.entrances = location;
		save();
	}

	@Override
	public SerializedMap serialize() {
		SerializedMap serializedMap = super.serialize();

		serializedMap.putArray("Entrance_Locations",entrances,"Stadium_Location",stadiumLocation);
		return serializedMap;
	}
}
