package org.jpwilliamson.multiplex.command;

import org.bukkit.entity.Player;
import org.jpwilliamson.multiplex.menu.RewardMenu;
import org.jpwilliamson.multiplex.model.ArenaJoinMode;
import org.jpwilliamson.multiplex.model.ArenaPlayer;

/**
 * Class selection menu during lobby
 */
public class RewardCommand extends ArenaSubCommand {

	protected RewardCommand() {
		super("reward|rewards|r", "Claim your rewards");

		setUsage("[edit]");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		final Player player = getPlayer();
		final ArenaPlayer cache = ArenaPlayer.getCache(player);

		checkBoolean(!cache.hasArena() && cache.getMode() != ArenaJoinMode.EDITING, "You may only claim rewards when not playing arenas.");

		if (args.length == 1) {
			final String parameter = args[0];

			if ("edit".equals(parameter) || "e".equals(parameter)) {
				checkPerm("arena.edit.rewards");

				RewardMenu.openEditMenu(player);
			}

			else
				returnTell("Invalid parameter. Type /{label} ? for help.");

		} else
			RewardMenu.openPurchaseMenu(player);
	}
}
