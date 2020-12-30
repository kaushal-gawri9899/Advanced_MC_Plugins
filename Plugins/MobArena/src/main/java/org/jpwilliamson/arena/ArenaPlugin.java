package org.jpwilliamson.arena;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import lombok.SneakyThrows;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.jpwilliamson.arena.command.ArenaCommandGroup;
import org.jpwilliamson.arena.model.buildbattle.BuildBattleArena;
import org.jpwilliamson.arena.model.dm.DeathmatchArena;
import org.jpwilliamson.arena.model.eggwars.EggWarsArena;
import org.jpwilliamson.arena.model.monster.MobArena;
import org.jpwilliamson.arena.model.team.ctf.CaptureTheFlagArena;
import org.jpwilliamson.arena.model.team.tdm.TeamDeathmatchArena;
import org.jpwilliamson.arena.mysql.ArenaDatabase;
import org.jpwilliamson.arena.settings.Settings;
import org.jpwilliamson.arena.model.ArenaClass;
import org.jpwilliamson.arena.model.ArenaListener;
import org.jpwilliamson.arena.model.ArenaManager;
import org.jpwilliamson.arena.model.ArenaPlayer;
import org.jpwilliamson.arena.model.ArenaReward;
import org.jpwilliamson.arena.model.ArenaStopReason;
import org.jpwilliamson.arena.model.ArenaTeam;
import org.jpwilliamson.arena.task.EscapeTask;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.settings.YamlConfig;
import org.mineacademy.fo.settings.YamlSectionConfig;
import org.mineacademy.fo.settings.YamlStaticConfig;

import lombok.Getter;

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
		ArenaManager.registerArenaType(MobArena.class);
		ArenaManager.registerArenaType(DeathmatchArena.class);
		ArenaManager.registerArenaType(TeamDeathmatchArena.class);
		ArenaManager.registerArenaType(CaptureTheFlagArena.class);
		ArenaManager.registerArenaType(EggWarsArena.class);
		ArenaManager.registerArenaType(BuildBattleArena.class);
	}

	/**
	 * Load the plugin and its configuration
	 */
	@SneakyThrows
	@Override
	protected void onPluginStart() {
		Common.log("Plugin Started");
		// Connect to MySQL
//		ArenaDatabase.start("mysql57.websupport.sk", 3311, "projectorion", "projectorion", "Te7=cXvxQI");

		// Enable messages prefix
		Common.ADD_TELL_PREFIX = true;

		// Use themed messages in commands
		SimpleCommand.USE_MESSENGER = true;

		Common.runLater(() -> ArenaManager.loadArenas()); // Uncomment this line if your arena world is loaded by a third party plugin such as Multiverse
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
	@SneakyThrows
	@Override
	protected void onPluginStop() {
		Common.log("Plugin Stops");
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
