package org.jpwilliamson.multiplex.command;

import org.jpwilliamson.multiplex.model.Arena;
import org.jpwilliamson.multiplex.model.ArenaStopReason;

/**
 * A command to stop running arenas
 */
public class StopCommand extends ArenaSubCommand {

	protected StopCommand() {
		super("stop|st", 1, "<arena>", "Stop a running arena.");
	}

	@Override
	protected void onCommand() {
		final String name = args[0];
		final Arena arena = findArena(name);

		checkBoolean(!arena.isStopped(), "Arena " + arena.getName() + " is already stopped");
		arena.stopArena(ArenaStopReason.PLUGIN);

		tellSuccess("Stopped arena " + arena.getName() + " in the " + arena.getState().getLocalized() + " state.");
	}
}
