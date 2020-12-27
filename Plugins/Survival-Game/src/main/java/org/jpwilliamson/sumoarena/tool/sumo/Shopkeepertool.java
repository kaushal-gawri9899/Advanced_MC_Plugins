package org.jpwilliamson.sumoarena.tool.sumo;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.sumoarena.menu.ShopKeeperMenu;
import org.jpwilliamson.sumoarena.model.sumo.SumoArena;
import org.jpwilliamson.sumoarena.model.sumo.SumoSettings;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.YamlConfig;

public class Shopkeepertool extends ToolSumo{

	@Getter
	private static final Tool instance = new Shopkeepertool();

	private Shopkeepertool() {
		super("Shopkeeper", CompMaterial.BROWN_MUSHROOM_BLOCK);
	}

	@Override
	public ItemStack getItem() {
		return ItemCreator.of(
				CompMaterial.VILLAGER_SPAWN_EGG,
				"ShopKeeper Spawnpoint",
				"",
				"Shopkeeper will appear",
				"or click air edit their shop items").build().make();
	}

	@Override
	protected void handleAirClick(Player player, SumoArena arena, ClickType click) {
		ShopKeeperMenu.openEditMenu(arena, player);
	}

	@Override
	protected YamlConfig.LocationList getLocations(SumoSettings settings) {
		return settings.getShopKeepers();
	}
}
