package org.jpwilliamson.arena.command;

import java.util.ArrayList;
import java.util.List;

import org.jpwilliamson.arena.model.ArenaManager;
import org.jpwilliamson.arena.model.Arena;
import org.jpwilliamson.arena.model.ArenaLeaveReason;

/**
 * The command to leave arenas when playing
 */
public class LeaveCommand extends ArenaSubCommand {

	protected LeaveCommand() {
		super("leave|l", "Leave an arena.");
	}

	@Override
	protected void onCommand() {
		checkConsole();
		checkInArena();

		final Arena arena = ArenaManager.findArena(getPlayer());
		arena.leavePlayer(getPlayer(), ArenaLeaveReason.COMMAND);
	}

	/**
	 * @see ArenaSubCommand#tabComplete()
	 */
	@Override
	protected List<String> tabComplete() {
		return new ArrayList<>();
	}
}
