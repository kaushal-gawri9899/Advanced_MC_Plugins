package org.jpwilliamson.multiplex.model.blackOps.tools;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.multiplex.model.blackOps.BlackOpsArena;
import org.jpwilliamson.multiplex.model.blackOps.BlackOpsSettings;
import org.jpwilliamson.multiplex.model.blackOps.Menus.initialItemMenu;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.YamlConfig.LocationList;

public class chestTool extends BlackOpsTool {

	@Getter
	private final static Tool instance = new chestTool();

	private chestTool() {
		super("Supplies", CompMaterial.PURPLE_TERRACOTTA);
	}

	@Override
	public ItemStack getItem() {
		return ItemCreator.of(CompMaterial.CHEST,
				"Supplies Point",
				"Click to toggle Where",
				"Chest will appear").build().make();
	}

	@Override
	protected void handleAirClick(Player player, BlackOpsArena arena, ClickType click) {
		initialItemMenu.openEditMenu(arena,player);
	}

	@Override
	protected LocationList getLocations(BlackOpsSettings settings) {
		return settings.getChestLocations();
	}
}
