package org.jpwilliamson.arena.model.paintBall;

import lombok.Getter;
import org.bukkit.Location;
import org.jpwilliamson.arena.model.ArenaTeam;
import org.jpwilliamson.arena.model.ArenaTeamPoints;
import org.jpwilliamson.arena.model.team.TeamArenaSettings;
import org.mineacademy.fo.collection.SerializedMap;

import java.util.List;

public class PaintBallSettings extends TeamArenaSettings {

	@Getter
	private int crystalHitThreshold;
	@Getter
	private int maxNeutralBases;

	/**
	 * The crystal spawn point for each team
	 */
	private ArenaTeamPoints crystalPoints;

	/**
	 * Create new arena settings
	 *
	 * @param arena
	 */
	public PaintBallSettings(final PaintBallArena arena) {
		super(arena);
	}

	/*
	 * @see ArenaSettings#onLoadFinish()
	 * Load the values of Threshold, crystal spawnpoint and max Neutral Bases in game
	 */
	@Override
	protected void onLoadFinish() {
		super.onLoadFinish();

		this.crystalPoints = new ArenaTeamPoints(this, getMap("Neutral_Crystals"));
		this.crystalHitThreshold = getInteger("Crystal_Hit_Threshold", 20);
		this.maxNeutralBases = getInteger("Max_Neutral_Bases",8);
	}

	/**
	 * Set a new crystal hit threshold
	 *
	 * @param crystalHitThreshold the crystalHitThreshold to set
	 */
	public void setCrystalHitThreshold(final int crystalHitThreshold) {
		this.crystalHitThreshold = crystalHitThreshold;

		save();
	}

	/**
	 * Set the crystal spawn point at the given location, removing the old point
	 * since we only enable 1 crystal per team
	 *
	 * @param location
	 */
	public void setCrystal(final ArenaTeam team, final Location location) {
		crystalPoints.setPoint(team, location);
	}

	/**
	 * Add a new crystal
	 *
	 * @param location
	 */
	public void addCrystal(final ArenaTeam team, final Location location) {
		crystalPoints.addPoint(team, location);
	}

	/**
	 * Remove a crystal
	 *
	 * @param location
	 */
	public void removeCrystal(final ArenaTeam team, final Location location) {
		crystalPoints.removePoint(team, location);
	}

	/**
	 * Return true if the given crystal location exists
	 *
	 * @param location
	 * @return
	 */
	public boolean hasCrystal(final Location location) {
		return crystalPoints.hasPoint(location);
	}

	/**
	 * Return what team owns the crystal at the given location
	 *
	 * @param location
	 * @return
	 */
	public ArenaTeam findCrystalTeam(final Location location) {
		return crystalPoints.findTeam(location);
	}

	/**
	 * Get the crystal for the given team
	 *
	 * @param team
	 * @return
	 */
	public Location findCrystal(final ArenaTeam team) {
		return crystalPoints.findPoint(team);
	}

	/**
	 * Get a list of all crystal locations
	 *
	 * @return
	 */
	public List<Location> getCrystals() {
		return crystalPoints.getLocations();
	}

	/*
	 * @see ArenaSettings#serialize()
	 * Put the Config Values in a Map and Use it to Load when game is rejoined
	 */
	@Override
	public SerializedMap serialize() {
		final SerializedMap map = super.serialize();

		map.putArray(
				"Neutral_Crystals", crystalPoints,
				"Crystal_Hit_Threshold", crystalHitThreshold,
				"Max_Neutral_Bases", maxNeutralBases);

		return map;
	}
}
