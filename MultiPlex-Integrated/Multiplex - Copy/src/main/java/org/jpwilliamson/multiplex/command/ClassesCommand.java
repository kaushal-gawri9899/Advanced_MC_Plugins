package org.jpwilliamson.multiplex.command;

import org.bukkit.entity.Player;
import org.jpwilliamson.multiplex.menu.ClassSelectionMenu;
import org.jpwilliamson.multiplex.model.ArenaJoinMode;
import org.jpwilliamson.multiplex.model.ArenaPlayer;
import org.jpwilliamson.multiplex.model.ArenaState;

/**
 * Class selection menu during lobby
 */
public class ClassesCommand extends ArenaSubCommand {

	protected ClassesCommand() {
		super("classes|cl", "Select or edit classes.");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		final Player player = getPlayer();
		final ArenaPlayer cache = ArenaPlayer.getCache(player);
		final ArenaJoinMode mode = cache.getMode();

		if (cache.hasArena() && mode != ArenaJoinMode.EDITING) {
			checkBoolean(mode == ArenaJoinMode.PLAYING && cache.getArena().getState() == ArenaState.LOBBY, "You may only select classes in the lobby.");

			ClassSelectionMenu.openSelectMenu(getPlayer(), cache.getArena());

		} else {
			checkBoolean(player.isOp(), "You don't have permission to edit classes.");

			ClassSelectionMenu.openEditMenu(null, getPlayer());
		}
	}
}
