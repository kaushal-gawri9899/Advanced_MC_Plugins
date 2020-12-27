package org.jpwilliamson.sumoarena.tool;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.sumoarena.model.Arena;
import org.jpwilliamson.sumoarena.model.hideAndSeek.HideAndSeekSettings;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;

import lombok.Getter;

/**
 * Represents a tool to create/remove arena entrance points
 */
public class ToolEntrancePoints extends VisualTool<Arena> {

	@Getter
	private static final Tool instance = new ToolEntrancePoints();

	/**
	 * Create new tool for the arena type
	 */
	private ToolEntrancePoints() {
		super(Arena.class);
	}

	/**
	 * @see VisualTool#getBlockName(org.bukkit.block.Block, org.bukkit.entity.Player, Arena)
	 */
	@Override
	protected String getBlockName(final Block block, final Player player, final Arena arena) {
		return "[&4Spawnpoint&f]";
	}

	/**
	 * @see VisualTool#getBlockMask(org.bukkit.block.Block, org.bukkit.entity.Player, Arena)
	 */
	@Override
	protected CompMaterial getBlockMask(final Block block, final Player player, final Arena arena) {
		return CompMaterial.REDSTONE_BLOCK;
	}

	/**
	 * @see org.mineacademy.fo.menu.tool.Tool#getItem()
	 */
	@Override
	public ItemStack getItem() {
		return ItemCreator.of(
				CompMaterial.ENDER_PEARL,
				"&lSPAWNPOINT TOOL",
				"",
				"Right click to place",
				"a player spawn point.")
				.build().makeMenuTool();
	}

	/**
	 * @see VisualTool#handleBlockClick(org.bukkit.entity.Player, Arena, org.bukkit.event.inventory.ClickType, org.bukkit.block.Block)
	 */
	@Override
	protected void handleBlockClick(final Player player, final Arena arena, final ClickType click, final Block block) {
		if(arena.getType().equalsIgnoreCase("hideAndSeek")){
			final HideAndSeekSettings settings = (HideAndSeekSettings) arena.getSettings();
			final Location old = settings.getEntrances().find(block.getLocation());
			int left = MathUtil.range(settings.getMaxPlayers() - settings.getEntrances().size(), 0, Integer.MAX_VALUE);

			if (old == null && left == 0) {
				Messenger.warn(player, "Cannot place more spawnpoints than max player count. Remove some.");

				return;
			}

			final boolean added = settings.getEntrances().toggle(block.getLocation());
			left = added ? left - 1 : left + 1;

			Messenger.success(player, "Successfully " + (added ? "&2added&7" : "&cremoved&7") + " a spawn point." + (left > 0 ? " Create " + left + " more to match the max player count." : " All points set."));
		}
	}

	/**
	 * @see VisualTool#getVisualizedPoints(Arena)
	 */
	@Override
	protected List<Location> getVisualizedPoints(final Arena arena) {
		//if(arena.getType().equalsIgnoreCase("hideAndSeek"))
			return ((HideAndSeekSettings)arena.getSettings()).getEntrances().getLocations();
	}

	/**
	 * @see ArenaTool#isApplicable(Arena)
	 */
	@Override
	public boolean isApplicable(Arena arena) {
		//if(arena.getType().equalsIgnoreCase("hideAndSeek"))
			return super.isApplicable(arena) && arena.getSettings() instanceof HideAndSeekSettings;
	}

	@Override
	protected boolean autoCancel() {
		return true; // Cancel the event so that we don't destroy blocks when selecting them
	}
}
