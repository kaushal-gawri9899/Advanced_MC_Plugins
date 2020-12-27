package org.jpwilliamson.sumoarena.model.survival;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jpwilliamson.sumoarena.model.Arena;
import org.jpwilliamson.sumoarena.model.ArenaSettings;
import org.jpwilliamson.sumoarena.model.monster.MobArenaSettings;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.model.SimpleTime;

import java.util.List;

@Getter
public class SurvivalArenaSettings extends ArenaSettings {

	/**
	 * How long takes a single wave?
	 */
	private SimpleTime waveDuration;

	/**
	 * The entry spawn point for players when the arena starts
	 */
	//@Getter
	private LocationList entranceLocation;

	/**
	 * The list of monsters spawner points
	 */
	private List<MobSpawnpoint> mobSpawnpoints;

	/**
	 * Create new arena settings
	 *
	 * @param arena
	 */
	public SurvivalArenaSettings(SurvivalArena arena) {
		super(arena);
	}

	@Override
	protected void onLoadFinish() {
		super.onLoadFinish();

		this.waveDuration = getTime("Wave_Duration", "20 seconds");
		this.entranceLocation = getLocations("Entrance_Location");
		this.mobSpawnpoints = getList("Monster_Spawnpoints", MobSpawnpoint.class, this);
	}


	/**
	 * Set how long a single wave takes
	 *
	 * @param waveDuration the waveDuration to set
	 */
	public final void setWaveDuration(final SimpleTime waveDuration) {
		this.waveDuration = waveDuration;

		save();
	}

	/**
	 * Set the entrance location for this arena
	 *
	 * @param location
	 */
	public final void setEntranceLocation(final LocationList location) {
		this.entranceLocation = location;

		save();
	}

	/**
	 * Shortcut for {@link #addMobSpawnpoint(Location, EntityType)} and {@link #removeMobSpawnpoint(Location)}
	 * saving server resources
	 * <p>
	 * If the point exists it is removed and we return false, otherwise
	 * it is added and we return true
	 *
	 * @param location
	 * @param type
	 * @return
	 */
	public final boolean toggleMobSpawnpoint(final Location location, final EntityType type) {
		for (final MobSpawnpoint point : mobSpawnpoints)
			if (Valid.locationEquals(point.getLocation(), location)) {
				mobSpawnpoints.remove(point);

				save();
				return false;
			}

		mobSpawnpoints.add(new MobSpawnpoint(this, location, type, 1));
		save();

		return true;
	}

	/**
	 * Retrieves an existing spawn point or null
	 *
	 * @param location
	 * @return
	 */
	public final MobSpawnpoint findMobSpawnpoint(final Location location) {
		for (final MobSpawnpoint point : mobSpawnpoints)
			if (Valid.locationEquals(point.getLocation(), location))
				return point;

		return null;
	}

	/**
	 * Adds a new monster spawn point
	 *
	 * @param location
	 * @param type
	 */
	public final void addMobSpawnpoint(final Location location, final EntityType type) {
		Valid.checkBoolean(findMobSpawnpoint(location) == null, "Monster spawn point already exists at " + Common.shortLocation(location));

		mobSpawnpoints.add(new MobSpawnpoint(this, location, type, 1));
		save();
	}

	/**
	 * Attempts to remove an existing monster spawn point
	 *
	 * @param location
	 */
	public final void removeMobSpawnpoint(final Location location) {
		final MobSpawnpoint point = findMobSpawnpoint(location);
		Valid.checkNotNull(point, "No monster spawn point at " + Common.shortLocation(location));

		mobSpawnpoints.remove(point);
		save();
	}

	/**
	 * @see ArenaSettings#isSetup()
	 */
	@Override
	public boolean isSetup() {
		return super.isSetup() && entranceLocation.size() >= getMaxPlayers() && !mobSpawnpoints.isEmpty();
	}
	/**
	 * Represents a simple spawn point
	 */
	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class MobSpawnpoint implements ConfigSerializable {

		/**
		 * The settings associated with this class
		 */
		private final SurvivalArenaSettings settings;

		/**
		 * The location of this spawner
		 */
		private Location location;

		/**
		 * The monster being spawned
		 */
		private EntityType entity;

		/**
		 * How many times we should spawn the monster: (multiplier X the wave number)
		 */
		private double multiplier;

		/**
		 * Set a new location
		 *
		 * @param location the location to set
		 */
		public void setLocation(final Location location) {
			this.location = location;

			settings.save();
		}

		/**
		 * Set the entity
		 *
		 * @param entity the entity to set
		 */
		public void setEntity(final EntityType entity) {
			this.entity = entity;

			settings.save();
		}

		/**
		 * Set the multiplier
		 *
		 * @param multiplier the multiplier to set
		 */
		public void setMultiplier(final double multiplier) {
			this.multiplier = multiplier;

			settings.save();
		}

		/**
		 * Automatically turn saved config value into this class
		 *
		 * @param map
		 * @param settings
		 * @return
		 */
		public static MobSpawnpoint deserialize(final SerializedMap map, final SurvivalArenaSettings settings) {
			final  MobSpawnpoint  point = new  MobSpawnpoint (settings);

			point.location = map.getLocation("Location");
			point.entity = map.get("Entity", EntityType.class);
			point.multiplier = map.getDouble("Multiplier", 1D);

			return point;
		}

		/**
		 * Convert this class into a saveable config section
		 *
		 * @see org.mineacademy.fo.model.ConfigSerializable#serialize()
		 */
		@Override
		public SerializedMap serialize() {
			return SerializedMap.ofArray(
					"Location", location,
					"Entity", entity,
					"Multiplier", multiplier);
		}
	}
}
