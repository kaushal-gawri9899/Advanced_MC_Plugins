package org.jpwilliamson.sumoarena.command;

import org.jpwilliamson.sumoarena.model.Arena;
import org.jpwilliamson.sumoarena.model.ArenaJoinMode;
import org.jpwilliamson.sumoarena.model.ArenaManager;

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
