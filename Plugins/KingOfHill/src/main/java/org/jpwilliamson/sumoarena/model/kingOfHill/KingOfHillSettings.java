package org.jpwilliamson.sumoarena.model.kingOfHill;
import lombok.Getter;
//import org.jpwilliamson.sumoarena.model.ArenaTeamPoints;
//import org.jpwilliamson.sumoarena.model.team.TeamArenaSettings;
import org.bukkit.Location;
import org.jpwilliamson.sumoarena.model.team.TeamArenaSettings;
import org.mineacademy.fo.collection.SerializedMap;
//import org.mineacademy.arena.model.ArenaTeam;
//import org.mineacademy.arena.model.ArenaTeamPoints;
//import org.mineacademy.arena.model.team.TeamArena;
//import org.mineacademy.arena.model.team.TeamArenaSettings;
//import org.mineacademy.fo.collection.SerializedMap;

import org.jpwilliamson.sumoarena.model.*;

import java.util.List;

public class KingOfHillSettings extends TeamArenaSettings {

	@Getter
	private int damageThreshold;

	private ArenaTeamPoints crystalPoints;
	//private Location crystalPoints;

	/**
	 * Create new arena settings
	 *
	 * @param arena
	 */
	public KingOfHillSettings(final KingOfHillArena arena) {
		super(arena);
	}

	@Override
	protected void onLoadFinish() {
		super.onLoadFinish();

		this.crystalPoints = new ArenaTeamPoints(this, getMap("Team_Crystals"));
		this.damageThreshold = getInteger("Crystal_Hit_Threshold", 30);
	}

	public void setCrystalHitThreshold(final int crystalHitThreshold) {
		this.damageThreshold = crystalHitThreshold;

		save();
	}

	public void setCrystal(final ArenaTeam team, final Location location) {
		crystalPoints.setPoint(team, location);
	}


	public void addCrystal(final ArenaTeam team, final Location location) {
		crystalPoints.addPoint(team, location);
	}

	public void removeCrystal(final ArenaTeam team, final Location location) {
		crystalPoints.removePoint(team, location);
	}

	public boolean hasCrystal(final Location location) {
		return crystalPoints.hasPoint(location);
	}

	public ArenaTeam findCrystalTeam(final Location location) {
		return crystalPoints.findTeam(location);
	}


	public Location findCrystal(final ArenaTeam team) {
		return crystalPoints.findPoint(team);
	}


	public List<Location> getCrystals() {
		return crystalPoints.getLocations();
	}


	@Override
	public SerializedMap serialize() {
		final SerializedMap map = super.serialize();

		map.putArray(
				"Team_Crystals", crystalPoints,
				"Crystal_Hit_Threshold", damageThreshold);

		return map;
	}



}
