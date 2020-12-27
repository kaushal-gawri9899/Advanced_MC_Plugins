package org.jpwilliamson.multiplex.model.tntTag.tool;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
//import org.jpwilliamson.sumoarena.model.dm.DeathmatchSettings;
//import org.jpwilliamson.sumoarena.model.Arena;
//import org.jpwilliamson.sumoarena.model.sumo.SumoSettings;
//import org.jpwilliamson.sumoarena.model.tnt.TntTagArena;
//import org.jpwilliamson.sumoarena.model.tnt.TntTagSettings;
//import org.jpwilliamson.sumoarena.tool.VisualTool;
import org.jpwilliamson.multiplex.model.Arena;
import org.jpwilliamson.multiplex.model.tntTag.TntTagArena;
import org.jpwilliamson.multiplex.model.tntTag.TntTagSettings;
import org.jpwilliamson.multiplex.tool.VisualTool;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;

import lombok.Getter;

/**
 * Represents a tool to create/remove arena entrance points
 */
public class TntEntranceTool extends VisualTool<TntTagArena> {

	@Getter
	private static final Tool instance = new  TntEntranceTool();

	/**
	 * Create new tool for the arena type
	 */
	private  TntEntranceTool() {
		super(TntTagArena.class);
	}

	/**
	 * @see VisualTool#getBlockName(org.bukkit.block.Block, org.bukkit.entity.Player, Arena)
	 */
	@Override
	protected String getBlockName(final Block block, final Player player, final TntTagArena arena) {
		return "[&4Tnt Spawn-point&f]";
	}

	/**
	 * @see VisualTool#getBlockMask(org.bukkit.block.Block, org.bukkit.entity.Player, Arena)
	 */
	@Override
	protected CompMaterial getBlockMask(final Block block, final Player player, final TntTagArena arena) {
		return CompMaterial.BUBBLE_CORAL_BLOCK;
	}

	/**
	 * @see org.mineacademy.fo.menu.tool.Tool#getItem()
	 */
	@Override
	public ItemStack getItem() {
		return ItemCreator.of(
				CompMaterial.PRISMARINE_CRYSTALS,
				"&lTnt Spawn-point",
				"",
				"Right click to place",
				"a player spawn point.")
				.build().makeMenuTool();
	}

	/**
	 * @see VisualTool#handleBlockClick(org.bukkit.entity.Player, Arena, org.bukkit.event.inventory.ClickType, org.bukkit.block.Block)
	 */
	@Override
	protected void handleBlockClick(final Player player, final TntTagArena arena, final ClickType click, final Block block) {

			final TntTagSettings settings = arena.getSettings();
			final Location old = settings.getEntrances().find(block.getLocation());
			int left = MathUtil.range(settings.getMaxPlayers() - settings.getEntrances().size(), 0, Integer.MAX_VALUE);

			if (old == null && left == 0) {
				Messenger.warn(player, "Cannot place more spawn-points than max player count. Remove some.");

				return;
			}


			//final boolean added = settings.getEntrances().toggle(block.getLocation());
		    final boolean added = settings.getEntrances().toggle(block.getLocation().subtract(arena.getReferenceLocation()));
			left = added ? left - 1 : left + 1;

			Messenger.success(player, "Successfully " + (added ? "&2added&7" : "&cremoved&7") + " a spawn point." + (left > 0 ? " Create " + left + " more to match the max player count." : " All points set."));

	}

	/**
	 * @see VisualTool#getVisualizedPoints(Arena)
	 */
	@Override
	protected List<Location> getVisualizedPoints(final TntTagArena arena) {

		//return (arena.getSettings()).getEntrances().getLocations();
		List<Location> updateLocationList = (arena.getSettings()).getEntrances().getLocations();
		for(Location location : updateLocationList){
			location.add(arena.getReferenceLocation());
		}
		return updateLocationList;
	}

//	/**
//	 * @see ArenaTool#isApplicable(Arena)
//	 */
	@Override
	public boolean isApplicable(Arena arena) {
		return super.isApplicable(arena) && arena.getSettings() instanceof TntTagSettings;
	}

	@Override
	protected boolean autoCancel() {
		return true; // Cancel the event so that we don't destroy blocks when selecting them
	}
}

