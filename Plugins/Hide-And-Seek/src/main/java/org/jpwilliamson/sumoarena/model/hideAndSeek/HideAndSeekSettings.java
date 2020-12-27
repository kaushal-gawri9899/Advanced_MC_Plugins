package org.jpwilliamson.sumoarena.model.hideAndSeek;

import lombok.Getter;
import org.bukkit.Location;
import org.jpwilliamson.sumoarena.model.Arena;
import org.jpwilliamson.sumoarena.model.ArenaSettings;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.SimpleTime;

public class HideAndSeekSettings extends ArenaSettings {

	/**
	 * Create new arena settings
	 *
	 * @param arena
	 */
	@Getter
	private LocationList entrances;

	@Getter
	private boolean gameRunning;

	@Getter
	private int hideTime;

	@Getter
	private boolean isShrinking;

	@Getter
	private Location stadiumLocation;

	public HideAndSeekSettings(Arena arena) {
		super(arena);
		this.gameRunning=false;
	}

	@Override
	protected void onLoadFinish() {
		super.onLoadFinish();
		this.entrances = getLocations("Entrance_Locations");
		this.isShrinking = getBoolean("Enable_Shrinking",false);
		this.hideTime = getInteger("Hiding_Duration",10);

	}

	@Override
	public boolean isSetup() {
		return super.isSetup() && entrances.size() >= getMaxPlayers();
	}


	public void setIsShrinking(boolean value){
		this.isShrinking = value;

	}

	@Override
	public SerializedMap serialize() {
		final SerializedMap map = super.serialize();

		map.putArray(
				"Entrance_Locations", entrances,
				"Enable_Shrinking", isShrinking,
				"Hiding_Duration", hideTime
				);

		return map;
	}

}
