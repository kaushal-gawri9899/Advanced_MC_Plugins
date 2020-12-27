package org.jpwilliamson.arena.model.monster;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.commands.mobs.SpawnCommand;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jpwilliamson.arena.model.Arena;
import org.jpwilliamson.arena.model.ArenaHeartbeat;
import org.jpwilliamson.arena.model.monster.MobArenaSettings.MobSpawnpoint;

import lombok.Getter;

/**
 * The ticking system for mob arenas
 */
public class MobArenaHeartbeat extends ArenaHeartbeat {

	/**
	 * The current arena wave, 0 when the arena is stopped
	 */
	@Getter
	private int wave = 0;

	/**
	 * Create a new heart beat
	 *
	 * @param arena
	 */
	protected MobArenaHeartbeat(final Arena arena) {
		super(arena);
	}

	/**
	 * @see ArenaHeartbeat#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();

		wave = 1;
		tickSpawnpoints();
	}

	/**
	 * @see ArenaHeartbeat#onTick()
	 */
	@Override
	protected void onTick() {
		super.onTick();

		final int elapsedSeconds = getCountdownSeconds() - getTimeLeft();
		final int waveDuration = getArena().getSettings().getWaveDuration().getTimeSeconds();

		// Run next wave on every n-th wave, except the last two seconds before the arena finishes
		if (elapsedSeconds % waveDuration == 0 && elapsedSeconds + 1 < getCountdownSeconds()) {
			wave++;

			onNextWave();
		}
	}

	/*
	 * Called automatically when arena enters the next wave
	 */
	private void onNextWave() {
		getArena().broadcastInfo("Arena entered the " + wave + " wave");

		tickSpawnpoints();
	}

	/*
	 * Run through all spawn points and spawn monsters
	 */
	private void tickSpawnpoints() {
		for (final MobSpawnpoint point : getArena().getSettings().getMobSpawnpoints()) {
			final Location location = point.getLocation().clone().add(0.5 * Math.random(), 1, 0.5 * Math.random()); // Spawns on the top of the block

			for (int i = 0; i < Math.round(point.getMultiplier() * wave); i++) {
				//location.getWorld().spawnEntity(location, point.getEntity());
				MythicMobs.inst().getMobManager().spawnMob("Mammoth", location, 1);
			}
		}
	}

	/**
	 * @see ArenaHeartbeat#onEnd()
	 */
	@Override
	protected void onEnd() {
		super.onEnd();

		this.wave = 0;
	}

	/**
	 * @see ArenaHeartbeat#getArena()
	 */
	@Override
	public MobArena getArena() {
		return (MobArena) super.getArena();
	}
}
