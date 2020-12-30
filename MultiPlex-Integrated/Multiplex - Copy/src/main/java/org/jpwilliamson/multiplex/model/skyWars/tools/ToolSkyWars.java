package org.jpwilliamson.multiplex.model.skyWars.tools;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jpwilliamson.multiplex.model.skyWars.SkyWarsArena;
import org.jpwilliamson.multiplex.model.skyWars.SkyWarsSettings;
import org.jpwilliamson.multiplex.tool.VisualTool;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.List;

public abstract class ToolSkyWars extends VisualTool<SkyWarsArena> {
	private final String blockName;
	private final CompMaterial blockMask;
	private boolean limitToPlayerMaximum = false;
	private int MAX_ITEM = 8;

	protected ToolSkyWars(String blockName, CompMaterial blockMask) {
		super(SkyWarsArena.class);

		this.blockName = blockName;
		this.blockMask = blockMask;
	}

	@Override
	protected String getBlockName(final Block block, final Player player, final SkyWarsArena arena) {
		return blockName;
	}

	@Override
	protected CompMaterial getBlockMask(final Block block, final Player player, final SkyWarsArena arena) {
		return blockMask;
	}

	@Override
	protected void handleBlockClick(final Player player, final SkyWarsArena arena, final ClickType click, final Block block) {
		final SkyWarsSettings settings = arena.getSettings();
		final YamlConfig.LocationList locations = getLocations(settings);
		final String name = blockName.toLowerCase();

		int left = MathUtil.range(settings.getMaxPlayers() - locations.size(), 0, Integer.MAX_VALUE);

		if (limitToPlayerMaximum && !locations.hasLocation(block.getLocation()) && left == 0) {
			Messenger.warn(player, "Cannot place more " + name + " than max player count. Remove some.");

			return;
		}

		final boolean added = locations.toggle(block.getLocation());
		left = added ? left - 1 : left + 1;

		Messenger.success(player, "Successfully " + (added ? "&2added&7" : "&cremoved&7") + " " + name + " spawn point."
				+ (limitToPlayerMaximum ? (left > 0 ? " Create " + left + " more to match the max player count." : " All necessary " + name + " set.") : ""));
	}

	protected abstract YamlConfig.LocationList getLocations(SkyWarsSettings settings);

	@Override
	protected List<Location> getVisualizedPoints(SkyWarsArena arena) {
		return getLocations(arena.getSettings()).getLocations();
	}

	@Override
	protected boolean autoCancel() {
		return true; // Cancel the event so that we don't destroy blocks when selecting them
	}
}
