package org.jpwilliamson.multiplex.model.skyWars.tools;

import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.multiplex.model.skyWars.SkyWarsSettings;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.YamlConfig.LocationList;

import lombok.Getter;

/**
 *
 */
public class ToolGoldCurrency extends ToolSkyWars {

	@Getter
	private static final Tool instance = new ToolGoldCurrency();

	private ToolGoldCurrency() {
		super("Gold Currency", CompMaterial.GOLD_BLOCK);
	}

	/**
	 * @see org.mineacademy.fo.menu.tool.Tool#getItem()
	 */
	@Override
	public ItemStack getItem() {
		return ItemCreator.of(
				CompMaterial.GOLD_ORE,
				"GOLD Currency SPAWNPOINT",
				"Add location where",
				"gold will appear.")
				.build().make();
	}

//	/**
//	 * @see ToolEggWars#getLocations(EggWarsSettings)
//	 */
	@Override
	protected LocationList getLocations(SkyWarsSettings settings) {
		return settings.getGold();
	}
}
