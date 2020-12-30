package org.jpwilliamson.arena.tool.skywars;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.arena.menu.SkyWarsChestMenu;
import org.jpwilliamson.arena.model.Arena;
import org.jpwilliamson.arena.model.skyWars.SkyWarsArena;
import org.jpwilliamson.arena.model.skyWars.SkyWarsSettings;
import org.jpwilliamson.arena.tool.VisualTool;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.YamlConfig.LocationList;

import lombok.Getter;

/**
 *
 */
public class ToolChests extends ToolSkyWars {

	@Getter
	private static final Tool instance = new ToolChests();
	private SkyWarsSettings settings;
	private ToolChests() {
		super("Reward Chests", CompMaterial.ENDER_CHEST);


	}

	/**
	 * @see org.mineacademy.fo.menu.tool.Tool#getItem()
	 */
	@Override
	public ItemStack getItem() {
				return ItemCreator.of(
				CompMaterial.DRAGON_EGG,
				"CHEST SPAWNPOINT",
				"Click to toggle where",
				"chests are placed",
				"or click air to",
				"edit their shop items")
				.build().make();
	}

	/**
	 * @see VisualTool#handleAirClick(org.bukkit.entity.Player, Arena, org.bukkit.event.inventory.ClickType)
	 */
	@Override
	protected void handleAirClick(Player player, SkyWarsArena arena, ClickType click) {
		//EggWarsVillagerMenu.openEditMenu(arena, player);
		SkyWarsChestMenu.openEditMenu(arena, player);
	}

//	@Override
//	protected void handleBlockClick(Player player, SkyWarsArena arena, ClickType click, Block block) {
//		super.handleBlockClick(player, arena, click, block);
//		//SkyWarsChestMenu.openEditMenu(arena, player);
//	}
//
//	@Override
//	public boolean hasToolInHand(Player player)
//	{
//
//		//SkyWarsArena arena = player.getArena();
//		//SkyWarsChestMenu.openEditMenu(, player);
//		ArenaPlayer player1 = ArenaPlayer.getCache(player);
//
//		//SkyWarsArena arena = settings.getArena();
//		//SkyWarsChestMenu.openEditMenu((SkyWarsArena) player1.getArena(), player);
//		return super.hasToolInHand(player);
//	}



	//	/**
//	 * @see ToolEggWars#getLocations(EggWarsSettings)
//	 */
	@Override
	protected LocationList getLocations(SkyWarsSettings settings) {
		//this.settings = settings;
		return settings.getChests();
	}
}
