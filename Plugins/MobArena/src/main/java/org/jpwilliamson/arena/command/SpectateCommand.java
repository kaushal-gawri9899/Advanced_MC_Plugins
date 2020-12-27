package org.jpwilliamson.arena.command;

import org.jpwilliamson.arena.model.ArenaManager;
import org.jpwilliamson.arena.model.Arena;
import org.jpwilliamson.arena.model.ArenaJoinMode;

/**
 * The command to spectate played arenas
 */
public class SpectateCommand extends ArenaSubCommand {

	protected SpectateCommand() {
		super("spectate|sp", "Spectate an arena.");

		setUsage("[arena]");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		Arena arena = null;

		// Automatically find arena by players location if he only types /arena join
		if (args.length == 0) {
			arena = ArenaManager.findArena(getPlayer().getLocation());

			checkNotNull(arena, "Could not find an arena to spectate, please specify its name.");
		} else
			arena = findArena(args[0]);

		arena.joinPlayer(getPlayer(), ArenaJoinMode.SPECTATING);
	}
}
