package org.jpwilliamson.arena.tool.eggwars;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.arena.model.Arena;
import org.jpwilliamson.arena.tool.VisualTool;
import org.jpwilliamson.arena.menu.EggWarsVillagerMenu;
import org.jpwilliamson.arena.model.eggwars.EggWarsArena;
import org.jpwilliamson.arena.model.eggwars.EggWarsSettings;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.YamlConfig.LocationList;

import lombok.Getter;

/**
 *
 */
public class ToolVillagers extends ToolEggWars {

	@Getter
	private static final Tool instance = new ToolVillagers();

	private ToolVillagers() {
		super("Villager", CompMaterial.BROWN_TERRACOTTA);
	}

	/**
	 * @see org.mineacademy.fo.menu.tool.Tool#getItem()
	 */
	@Override
	public ItemStack getItem() {
		return ItemCreator.of(
				CompMaterial.VILLAGER_SPAWN_EGG,
				"VILLAGER SPAWNPOINT",
				"Click to toggle where",
				"villagers will appear",
				"or click air to",
				"edit their shop items")
				.build().make();
	}

	/**
	 * @see VisualTool#handleAirClick(org.bukkit.entity.Player, Arena, org.bukkit.event.inventory.ClickType)
	 */
	@Override
	protected void handleAirClick(Player player, EggWarsArena arena, ClickType click) {
		EggWarsVillagerMenu.openEditMenu(arena, player);
	}

	/**
	 * @see ToolEggWars#getLocations(EggWarsSettings)
	 */
	@Override
	protected LocationList getLocations(EggWarsSettings settings) {
		return settings.getVillagers();
	}
}
