package org.jpwilliamson.sumoarena.model.survival;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jpwilliamson.sumoarena.model.Arena;
import org.jpwilliamson.sumoarena.model.ArenaHeartbeat;
import org.jpwilliamson.sumoarena.model.ArenaJoinMode;
import org.jpwilliamson.sumoarena.model.monster.MobArena;
import org.jpwilliamson.sumoarena.model.monster.MobArenaSettings;

public class SurvivalArenaHeartBeat extends ArenaHeartbeat {

	private final double shrinkingFactor;
	private final int shrinkingDuration;
	private final SurvivalArena arena;
	/**
	 * Create a new countdown
	 *
	 * @param arena
	 */
	protected SurvivalArenaHeartBeat(final SurvivalArena arena) {
		super(arena);
		shrinkingFactor=0.80;
		shrinkingDuration=10;
		this.arena = arena;
	}


	/**
	 * The current arena wave, 0 when the arena is stopped
	 * wave is a private variable used to keep a track of mob waves in the arena
	 */
	@Getter
	private int wave = 0;

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
		final int waveDuration = arena.getSettings().getWaveDuration().getTimeSeconds();
		// Run next wave on every n-th wave, except the last two seconds before the arena finishes
		if (elapsedSeconds % waveDuration == 0 && elapsedSeconds + 1 < getCountdownSeconds()) {
			wave++;
			onNextWave();
		}

		if(elapsedSeconds % 30 == 0){
			arena.broadcastInfo(ChatColor.WHITE + "" + ChatColor.ITALIC + "The Arena Border will shrink to" + (int)(this.shrinkingFactor*100) + "% in" + this.shrinkingDuration + " seconds!");
			shrinkArenaBorder();
		}


	}

	public void shrinkArenaBorder(){
		for(Player player : getArena().getPlayers(ArenaJoinMode.PLAYING)){
			player.getWorld().getWorldBorder().setSize(player.getWorld().getWorldBorder().getSize()*this.shrinkingFactor,shrinkingDuration);
		}
	}

	/*
	 * Called automatically when arena enters the next wave
	 */
	private void onNextWave() {
		arena.broadcastInfo("Arena entered the " + wave + " wave");

		tickSpawnpoints();
	}

	/*
	 * Run through all spawn points and spawn monsters
	 */
	private void tickSpawnpoints() {
		for (final SurvivalArenaSettings.MobSpawnpoint point : arena.getSettings().getMobSpawnpoints()){
			final Location location = point.getLocation().clone().add(0.5 * Math.random(), 1, 0.5 * Math.random()); // Spawns on the top of the block

			for (int i = 0; i < Math.round(point.getMultiplier() * wave); i++)
				location.getWorld().spawnEntity(location, point.getEntity());
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
