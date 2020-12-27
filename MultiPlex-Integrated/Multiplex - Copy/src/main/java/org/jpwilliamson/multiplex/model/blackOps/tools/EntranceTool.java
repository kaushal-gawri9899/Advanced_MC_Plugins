package org.jpwilliamson.multiplex.model.blackOps.tools;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.multiplex.model.blackOps.BlackOpsArena;
import org.jpwilliamson.multiplex.model.blackOps.BlackOpsSettings;
import org.jpwilliamson.multiplex.model.blackOps.Menus.DropItemMenu;
import org.jpwilliamson.multiplex.tool.VisualTool;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.Collections;
import java.util.List;

public class EntranceTool extends VisualTool<BlackOpsArena> {

	@Getter
	private static final Tool instance = new EntranceTool();

	private EntranceTool() {
		super(BlackOpsArena.class);
	}

	@Override
	protected String getBlockName(final Block block, final Player player, final BlackOpsArena arena) {
		return "[&bEntrance point&f]";
	}

	@Override
	protected CompMaterial getBlockMask(final Block block, final Player player, final BlackOpsArena arena) {
		return CompMaterial.DIAMOND_BLOCK;
	}

	@Override
	public ItemStack getItem() {
		return ItemCreator.of(
				CompMaterial.DIAMOND,
				"&lENTRANCE TOOL",
				"",
				"Right click to place",
				"arena entrance point.")
				.build().makeMenuTool();
	}

	@Override
	protected void handleAirClick(Player player, BlackOpsArena arena, ClickType click) {
		DropItemMenu.openEditMenu(arena,player);
	}

	@Override
	protected void handleBlockClick(final Player player, final BlackOpsArena arena, final ClickType click, final Block block) {
		final BlackOpsSettings settings = arena.getSettings();
		final Location location = block.getLocation();

		location.setYaw(player.getLocation().getYaw());
		location.setPitch(0);

		settings.setEntranceLocation(location);

		Messenger.success(player, "Set the arena entrance point for players.");
	}

	@Override
	protected List<Location> getVisualizedPoints(final BlackOpsArena arena) {
		return Collections.singletonList(arena.getSettings().getEntranceLocation());
	}

	@Override
	protected boolean autoCancel() {
		return true; // Cancel the event so that we don't destroy blocks when selecting them
	}
}
