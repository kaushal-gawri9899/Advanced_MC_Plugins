package org.jpwilliamson.multiplex.command;

import java.util.ArrayList;
import java.util.List;

import org.jpwilliamson.multiplex.item.ExplosiveBow;
import org.jpwilliamson.multiplex.model.ArenaJoinMode;
import org.jpwilliamson.multiplex.model.ArenaPlayer;

/**
 * The command to obtain arena items
 */
public class ItemsCommand extends ArenaSubCommand {

	protected ItemsCommand() {
		super("items|i", "Goodies for playing arenas.");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		final ArenaPlayer cache = ArenaPlayer.getCache(getPlayer());
		checkBoolean(!cache.hasArena() || cache.getMode() == ArenaJoinMode.EDITING, "You may only use this command while not playing, or editing an arena.");

		ExplosiveBow.getInstance().give(getPlayer());
		tell("You have been given some tools...");
	}

	@Override
	protected List<String> tabComplete() {
		return new ArrayList<>();
	}
}
