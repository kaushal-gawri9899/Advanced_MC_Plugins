package org.jpwilliamson.multiplex.tool;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.multiplex.model.Arena;
import org.jpwilliamson.multiplex.model.ArenaSettings;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.region.Region;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.visual.VisualizedRegion;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents the tool used to create arena region for any arena
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ToolRegion extends VisualTool<Arena> {

	@Getter
	private static final Tool instance = new ToolRegion();

	/**
	 * @see VisualTool#getBlockName(org.bukkit.block.Block, org.bukkit.entity.Player, Arena)
	 */
	@Override
	protected String getBlockName(final Block block, final Player player, final Arena arena) {
		return "[&aRegion point&f]";
	}

	/**
	 * @see VisualTool#getBlockMask(org.bukkit.block.Block, org.bukkit.entity.Player, Arena)
	 */
	@Override
	protected CompMaterial getBlockMask(final Block block, final Player player, final Arena arena) {
		return CompMaterial.EMERALD_BLOCK;
	}

	/**
	 * @see org.mineacademy.fo.menu.tool.Tool#getItem()
	 */
	@Override
	public ItemStack getItem() {
		return ItemCreator.of(
				CompMaterial.EMERALD,
				"&lREGION TOOL",
				"",
				"Use to set region points",
				"for an edited arena.",
				"",
				"&b<< &fLeft click &7– &fPrimary",
				"&fRight click &7– &fSecondary &b>>")
				.build().makeMenuTool();
	}

	/**
	 * @see VisualTool#handleBlockClick(org.bukkit.entity.Player, Arena, org.bukkit.event.inventory.ClickType, org.bukkit.block.Block)
	 */
	@Override
	protected void handleBlockClick(final Player player, final Arena arena, final ClickType click, final Block block) {
		final ArenaSettings settings = arena.getSettings();

		final Location location = block.getLocation();
		final boolean primary = click == ClickType.LEFT;

		if (primary)
			settings.setRegion(location.subtract(arena.getReferenceLocation()), null);
		else
			settings.setRegion(null, location.subtract(arena.getReferenceLocation()));

		Messenger.success(player, "Set the " + (primary ? "primary" : "secondary") + " arena point.");
	}

	/**
	 * @see VisualTool#getVisualizedPoints(Arena)
	 */
	@Override
	protected List<Location> getVisualizedPoints(final Arena arena) {
		final List<Location> blocks = new ArrayList<>();
		final Region region = arena.getSettings().getRegion();

		if (region != null) {
			if (region.getPrimary() != null)
				blocks.add(region.getPrimary().add(arena.getReferenceLocation()));

			if (region.getSecondary() != null)
				blocks.add(region.getSecondary().add(arena.getReferenceLocation()));
		}

		return blocks;
	}

	/**
	 * @see VisualTool#getVisualizedRegion(Arena)
	 */
	@Override
	protected VisualizedRegion getVisualizedRegion(final Arena arena) {
		return arena.getSettings().getRegion();
	}

	@Override
	protected boolean autoCancel() {
		return true; // Cancel the event so that we don't destroy blocks when selecting them
	}
}
