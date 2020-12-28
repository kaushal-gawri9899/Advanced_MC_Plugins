package org.jpwilliamson.multiplex;

import lombok.Getter;
import org.jpwilliamson.multiplex.command.ArenaCommandGroup;
import org.jpwilliamson.multiplex.model.*;
import org.jpwilliamson.multiplex.model.blackOps.BlackOpsArena;
import org.jpwilliamson.multiplex.model.duels.DuelsArena;
import org.jpwilliamson.multiplex.model.kingOfHill.KingOfHillArena;
import org.jpwilliamson.multiplex.model.paintBall.PaintBallArena;
import org.jpwilliamson.multiplex.model.survival.SurvivalArena;
import org.jpwilliamson.multiplex.model.tntTag.TntTagArena;
import org.jpwilliamson.multiplex.settings.Settings;
import org.jpwilliamson.multiplex.task.EscapeTask;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.settings.YamlStaticConfig;

import java.util.Arrays;
import java.util.List;

/**
 * The core plugin class for Arena
 */
public final class ArenaPlugin extends SimplePlugin {

	/**
	 * The main arena command
	 */
	@Getter
	private final SimpleCommandGroup mainCommand = new ArenaCommandGroup();

	/**
	 * Register arena types early
	 */
	@Override
	protected void onPluginPreStart() {
		// Register our arenas
		ArenaManager.registerArenaType(TntTagArena.class);
		ArenaManager.registerArenaType(DuelsArena.class);
		ArenaManager.registerArenaType(KingOfHillArena.class);
		ArenaManager.registerArenaType(PaintBallArena.class);
		ArenaManager.registerArenaType(SurvivalArena.class);
		ArenaManager.registerArenaType(BlackOpsArena.class);
	}

	/**
	 * Load the plugin and its configuration
	 */
	@Override
	protected void onPluginStart() {
		// Enable messages prefix
		Common.ADD_TELL_PREFIX = true;

		// Use themed messages in commands
		SimpleCommand.USE_MESSENGER = true;

		Common.runLater(ArenaManager::loadArenas); // Uncomment this line if your arena world is loaded by a third party plugin such as Multiverse
	}

	/**
	 * Called on startup and reload, load arenas
	 */
	@Override
	protected void onReloadablesStart() {
		//ArenaManager.loadArenas(); // Comment this line if your arena world is loaded by a third party plugin such as Multiverse
		ArenaClass.loadClasses();
		ArenaTeam.loadTeams();

		ArenaReward.getInstance(); // Loads the file
		ArenaPlayer.clearAllData();

		registerEvents(new ArenaListener());

		Common.runTimer(20, new EscapeTask());
	}

	/**
	 * Stop arenas on server stop
	 */
	@Override
	protected void onPluginStop() {
		ArenaManager.stopArenas(ArenaStopReason.PLUGIN);
	}

	/**
	 * Stop arenas on reload
	 */
	@Override
	protected void onPluginReload() {
		ArenaManager.stopArenas(ArenaStopReason.RELOAD);
		ArenaManager.loadArenas(); // Uncomment this line if your arena world is loaded by a third party plugin such as Multiverse
	}

	/**
	 * Load the global settings classes
	 */
	@Override
	public List<Class<? extends YamlStaticConfig>> getSettings() {
		return Arrays.asList(Settings.class);
	}
}
