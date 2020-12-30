package org.jpwilliamson.multiplex.model.tntrun;

import lombok.Getter;
import org.bukkit.Location;
import org.jpwilliamson.multiplex.model.ArenaSettings;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.SimpleTime;

public class TNTRunSettings extends ArenaSettings {

	@Getter
	private LocationList entrances;

	@Getter
	private Location levelLocation;

	@Getter
	private Location TNTStadium;

	@Getter
	private double speedReductionFactor;
	/**
	 * Create new arena settings
	 *
	 * @param arena
	 */
	public TNTRunSettings(final TNTRunArena arena) {
		super(arena);
		this.setGameDuration(SimpleTime.fromSeconds(300));
		this.setLobbyDuration(SimpleTime.fromSeconds(30));
		this.setMinPlayers(1);
		this.setMaxPlayers(4);
	}

	public boolean setLevelLocation(Location levelLoc) {
		if(Valid.locationEquals(levelLocation, levelLoc)){
			levelLocation = null;
			save();
			return false;
		}
		levelLocation = levelLoc;
		save();

		return true;
	}

	public void setTNTStadium(final Location location){
		this.TNTStadium = location;

		save();
	}

	@Override
	protected void onLoadFinish() {
		super.onLoadFinish();

		this.levelLocation = getLocation("Level_Locations");
		this.entrances = getLocations("Entrance_Locations");
		this.TNTStadium	= getLocation("TNT_Stadium");
		this.speedReductionFactor = getDouble("Speed_Reduction",0.5);
	}

	@Override
	public boolean isSetup() {
		return super.isSetup() && entrances.size()>0;
	}


	public final boolean toggleEntranceLocation(final Location location){
		for(Location location1: entrances){
			if(Valid.locationEquals(location,location1)){
				entrances.remove(location);

				save();
				return false;
			}
		}

		entrances.add(location);
		save();

		return true;
	}

	@Override
	public SerializedMap serialize() {
		SerializedMap map = super.serialize();

		map.putArray("Level_Locations",levelLocation,
				"Entrance_Locations",entrances,
				"TNT_Stadium",TNTStadium,
				"Speed_Reduction", speedReductionFactor);

		return map;
	}
}
