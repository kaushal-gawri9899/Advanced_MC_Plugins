package org.jpwilliamson.sumoarena.tool;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.sumoarena.menu.MobSpawnerMenu;
import org.jpwilliamson.sumoarena.model.Arena;
import org.jpwilliamson.sumoarena.model.monster.MobArena;
import org.jpwilliamson.sumoarena.model.monster.MobArenaSettings;
import org.jpwilliamson.sumoarena.model.survival.SurvivalArena;
import org.jpwilliamson.sumoarena.model.survival.SurvivalArenaSettings;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;

import lombok.Getter;

/**
 * Represents a tool to create/remove monster spawn points
 */
public class ToolMobSpawn extends VisualTool<SurvivalArena> {

	@Getter
	private static final Tool instance = new ToolMobSpawn();

	/**
	 * Create new tool for the mob arena type
	 */
	private ToolMobSpawn() {
		super(SurvivalArena.class);
	}

	/**
	 * @see VisualTool#getBlockName(org.bukkit.block.Block, org.bukkit.entity.Player, Arena)
	 */
	@Override
	protected String getBlockName(final Block block, final Player player, final SurvivalArena arena) {
		return "[&4Mob spawnpoint&f]";
	}

	/**
	 * @see VisualTool#getBlockMask(org.bukkit.block.Block, org.bukkit.entity.Player, Arena)
	 */
	@Override
	protected CompMaterial getBlockMask(final Block block, final Player player, final SurvivalArena arena) {
		return CompMaterial.REDSTONE_BLOCK;
	}

	/**
	 * @see org.mineacademy.fo.menu.tool.Tool#getItem()
	 */
	@Override
	public ItemStack getItem() {
		return ItemCreator.of(
				CompMaterial.REDSTONE,
				"&lMOB SPAWN TOOL",
				"",
				"Right click to place",
				"a monster spawn point.")
				.build().makeMenuTool();
	}

	/**
	 * @see VisualTool#handleBlockClick(org.bukkit.entity.Player, Arena, org.bukkit.event.inventory.ClickType, org.bukkit.block.Block)
	 */
	@Override
	protected void handleBlockClick(final Player player, final SurvivalArena arena, final ClickType click, final Block block) {
		final SurvivalArenaSettings settings = arena.getSettings();
		final SurvivalArenaSettings.MobSpawnpoint point = settings.findMobSpawnpoint(block.getLocation());

		if (click == ClickType.RIGHT && point != null) {
			new MobSpawnerMenu(point).displayTo(player);

			return;
		}

		final boolean added = settings.toggleMobSpawnpoint(block.getLocation(), EntityType.ZOMBIE);

		Messenger.success(player, "Successfully " + (added ? "&2added&7" : "&cremoved&7") + " a monster spawn point. Click to configure");
	}

	/**
	 * @see VisualTool#getVisualizedPoints(Arena)
	 */
	@Override
	protected List<Location> getVisualizedPoints(final SurvivalArena arena) {
		return Common.convert(arena.getSettings().getMobSpawnpoints(), (spawner) -> spawner.getLocation());
	}

	@Override
	protected boolean autoCancel() {
		return true; // Cancel the event so that we don't destroy blocks when selecting them
	}
}
