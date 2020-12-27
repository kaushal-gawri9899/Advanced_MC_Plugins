package org.jpwilliamson.arena.util;

import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;
import org.jpwilliamson.arena.model.ArenaManager;
import org.jpwilliamson.arena.model.Arena;
import org.mineacademy.fo.EntityUtil;

import lombok.experimental.UtilityClass;

/**
 * Tracking utils that have to do with flying blocks
 */
@UtilityClass
public final class ArenaUtil {

	/**
	 * Track falling block and remove automatically once crosses arena border
	 *
	 * @param arena
	 * @param falling
	 */
	public void preventArenaLeave(Arena arena, FallingBlock falling) {
		final Runnable tracker = () -> {
			final Location newLocation = falling.getLocation();
			final Arena newArena = ArenaManager.findArena(newLocation);

			if (newArena == null || !newArena.equals(arena))
				falling.remove();
		};

		EntityUtil.track(falling, 10 * 20, tracker, tracker);
	}
}
