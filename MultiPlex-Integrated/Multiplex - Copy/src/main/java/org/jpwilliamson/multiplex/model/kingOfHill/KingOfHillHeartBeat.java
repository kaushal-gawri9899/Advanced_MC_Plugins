package org.jpwilliamson.multiplex.model.kingOfHill;

import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.jpwilliamson.multiplex.model.Arena;
import org.jpwilliamson.multiplex.model.ArenaHeartbeat;


public class KingOfHillHeartBeat extends ArenaHeartbeat {

	private KingOfHillArena arena;
	/**
	 * Create a new countdown
	 *
	 * @param arena
	 */
	protected KingOfHillHeartBeat(Arena arena) {
		super(arena);
	}

	@Override
	protected void onTick() {
		super.onTick();

		final KingOfHillSettings settings = getArena().getSettings();
		final int elapsedSeconds = getCountdownSeconds() - getTimeLeft();

		if(elapsedSeconds - getArena().getTimeCrystalDamage() == 5){
			for(final Location location : getArena().getSettings().getCrystals())
			{
				if(location.equals(getArena().getCrystalLocation())) {
					//spawn the crystal at location again after 5 seconds delay
					location.getWorld().spawn(location.clone().add(0, 1, 0), EnderCrystal.class);
				}
			}
		}

	}


	@Override
	public KingOfHillArena getArena() {
		return (KingOfHillArena) super.getArena();
	}

}
