package org.jpwilliamson.multiplex.model.blackOps.Commands;

import org.bukkit.entity.Player;
import org.jpwilliamson.multiplex.command.ArenaSubCommand;
import org.jpwilliamson.multiplex.model.ArenaJoinMode;
import org.jpwilliamson.multiplex.model.ArenaPlayer;
import org.jpwilliamson.multiplex.model.blackOps.Menus.RoundSetupMenu;

public class RoundCommand extends ArenaSubCommand {

	protected RoundCommand() {
		super("round|ro", "edit Game rounds");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		final Player player = getPlayer();
		final ArenaPlayer cache = ArenaPlayer.getCache(player);
		final ArenaJoinMode mode = cache.getMode();

		if(cache.hasArena() && mode == ArenaJoinMode.EDITING){
			checkBoolean(player.isOp(),"You don't have the permission to edit rounds");

			RoundSetupMenu.openEditMenu(null, getPlayer());
		}
	}
}
