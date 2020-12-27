package org.jpwilliamson.sumoarena.command;

import org.jpwilliamson.sumoarena.model.Arena;
import org.jpwilliamson.sumoarena.model.ArenaJoinMode;
import org.jpwilliamson.sumoarena.model.ArenaLeaveReason;
import org.jpwilliamson.sumoarena.model.ArenaManager;
import org.jpwilliamson.sumoarena.model.ArenaPlayer;
import org.mineacademy.fo.Common;

/**
 * The command to edit and setup arenas
 */
public class EditCommand extends ArenaSubCommand {

	protected EditCommand() {
		super("edit|e", "Edit an existing arena.");

		setUsage("[arena]");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		final ArenaPlayer arenaPlayer = ArenaPlayer.getCache(getPlayer());
		Arena arena = null;

		// Automatically find already edited arena or arena by players location
		// if he only types /arena edit
		if (args.length == 0) {
			if (arenaPlayer.hasArena())
				arena = arenaPlayer.getArena();
			else
				arena = ArenaManager.findArena(getPlayer().getLocation());

			checkNotNull(arena, "Please specify what arena to edit.");
		} else
			arena = findArena(args[0]);

		setArg(0, arena.getName());

		if (arenaPlayer.hasArena()) {
			if (arenaPlayer.getArena().equals(arena) && arenaPlayer.getMode() == ArenaJoinMode.EDITING) {
				arena.leavePlayer(getPlayer(), ArenaLeaveReason.EDIT_STOP);

				return;
			}

			returnTell("Stop " + arenaPlayer.getMode().getLocalized() + " the arena " + arenaPlayer.getArena().getName() + " before you edit this.");
		}

		if (arena.joinPlayer(getPlayer(), ArenaJoinMode.EDITING))
			tellInfo("You are now editing arena {0}." + (arena.getPlayersInAllModes().size() < 2 ? "" : " Other editors: " + Common.joinPlayersExcept(arena.getPlayersInAllModes(), sender.getName())));
	}
}
