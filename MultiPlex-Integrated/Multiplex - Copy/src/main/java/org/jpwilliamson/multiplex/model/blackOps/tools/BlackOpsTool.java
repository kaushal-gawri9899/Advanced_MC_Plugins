package org.jpwilliamson.multiplex.model.blackOps.tools;

import lombok.AccessLevel;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jpwilliamson.multiplex.model.blackOps.BlackOpsArena;
import org.jpwilliamson.multiplex.model.blackOps.BlackOpsSettings;
import org.jpwilliamson.multiplex.tool.VisualTool;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.YamlConfig.LocationList;

import java.util.List;

@Setter(value = AccessLevel.PROTECTED)
public abstract class BlackOpsTool extends VisualTool<BlackOpsArena> {

	private final String blockName;
	private final CompMaterial blockMask;
	private boolean limitToPlayerMaximum = false;

	protected BlackOpsTool(String blockName, CompMaterial blockMask){
		super(BlackOpsArena.class);

		this.blockMask = blockMask;
		this.blockName = blockName;
	}

	@Override
	protected String getBlockName(Block block, Player player, BlackOpsArena arena) {
		return blockName;
	}

	@Override
	protected CompMaterial getBlockMask(Block block, Player player, BlackOpsArena arena) {
		return blockMask;
	}

	@Override
	protected void handleBlockClick(Player player, BlackOpsArena arena, ClickType click, Block block) {
		final BlackOpsSettings settings = arena.getSettings();
		final LocationList locations = getLocations(settings);
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

	protected abstract LocationList getLocations(BlackOpsSettings settings);

	@Override
	protected List<Location> getVisualizedPoints(BlackOpsArena arena) {
		return getLocations(arena.getSettings()).getLocations();
	}

	@Override
	protected boolean autoCancel() {
		return true; // Cancel the event so that we don't destroy blocks when selecting them
	}
}
