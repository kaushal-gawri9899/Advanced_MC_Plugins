package org.jpwilliamson.multiplex.model.skyWars.tools;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import org.jpwilliamson.multiplex.model.blackOps.BlackOpsSettings;
import org.jpwilliamson.multiplex.model.skyWars.SkyWarsArena;
import org.jpwilliamson.multiplex.model.skyWars.SkyWarsSettings;
import org.jpwilliamson.multiplex.model.skyWars.menus.SkyWarsChestMenu;
import org.mineacademy.fo.Messenger;
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

//	/**
//	 * @see VisualTool#handleAirClick(org.bukkit.entity.Player, Arena, org.bukkit.event.inventory.ClickType)
//	 */
	@Override
	protected void handleAirClick(Player player, SkyWarsArena arena, ClickType click) {
		SkyWarsChestMenu.openEditMenu(arena, player);
	}

	@Override
	protected void handleBlockClick(Player player, SkyWarsArena arena, ClickType click, Block block) {
		super.handleBlockClick(player, arena, click, block);
		final SkyWarsSettings settings = arena.getSettings();
		final Location location = block.getLocation();

		location.setYaw(player.getLocation().getYaw());
		location.setPitch(0);



		Messenger.success(player, "Set the arena entrance point for players.");
	}




	//	/**
//	 * @see ToolEggWars#getLocations(EggWarsSettings)
//	 */
	@Override
	protected LocationList getLocations(SkyWarsSettings settings) {
		//this.settings = settings;
		return settings.getChests();
	}
}
