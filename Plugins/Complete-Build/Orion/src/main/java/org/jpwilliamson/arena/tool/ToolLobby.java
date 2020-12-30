package org.jpwilliamson.arena.tool;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.arena.model.ArenaSettings;
import org.jpwilliamson.arena.model.Arena;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a generic arena tool used to set the lobby point
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ToolLobby extends VisualTool<Arena> {

	@Getter
	private static final Tool instance = new ToolLobby();

	/**
	 * @see VisualTool#getBlockName(org.bukkit.block.Block, org.bukkit.entity.Player, Arena)
	 */
	@Override
	protected String getBlockName(final Block block, final Player player, final Arena arena) {
		return "[&6Lobby point&f]";
	}

	/**
	 * @see VisualTool#getBlockMask(org.bukkit.block.Block, org.bukkit.entity.Player, Arena)
	 */
	@Override
	protected CompMaterial getBlockMask(final Block block, final Player player, final Arena arena) {
		return CompMaterial.GOLD_BLOCK;
	}

	/**
	 * @see org.mineacademy.fo.menu.tool.Tool#getItem()
	 */
	@Override
	public ItemStack getItem() {
		return ItemCreator.of(
				CompMaterial.GOLD_INGOT,
				"&lLOBBY TOOL",
				"",
				"Right click to set an",
				"arena lobby point.")
				.build().makeMenuTool();
	}

	/**
	 * @see VisualTool#handleBlockClick(org.bukkit.entity.Player, Arena, org.bukkit.event.inventory.ClickType, org.bukkit.block.Block)
	 */
	@Override
	protected void handleBlockClick(final Player player, final Arena arena, final ClickType click, final Block block) {
		final ArenaSettings settings = arena.getSettings();
		final Location location = block.getLocation();

		location.setYaw(player.getLocation().getYaw());
		location.setPitch(0);

		settings.setLobbyLocation(location);

		Messenger.success(player, "Set the lobby arena point.");
	}

	/**
	 * @see VisualTool#getVisualizedPoints(Arena)
	 */
	@Override
	protected List<Location> getVisualizedPoints(final Arena arena) {
		return Arrays.asList(arena.getSettings().getLobbyLocation());
	}

	@Override
	protected boolean autoCancel() {
		return true; // Cancel the event so that we don't destroy blocks when selecting them
	}
}
