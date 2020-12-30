package org.jpwilliamson.sumoarena.tool.eggwars;

import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.sumoarena.model.eggwars.EggWarsSettings;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.YamlConfig.LocationList;

import lombok.Getter;

/**
 *
 */
public class ToolDiamonds extends ToolEggWars {

	@Getter
	private static final Tool instance = new ToolDiamonds();

	private ToolDiamonds() {
		super("Diamond", CompMaterial.DIAMOND_BLOCK);
	}

	/**
	 * @see org.mineacademy.fo.menu.tool.Tool#getItem()
	 */
	@Override
	public ItemStack getItem() {
		return ItemCreator.of(
				CompMaterial.DIAMOND_ORE,
				"DIAMOND SPAWNPOINT",
				"Click to toggle where",
				"diamonds will appear.")
				.build().make();
	}

	/**
	 * @see ToolEggWars#getLocations(EggWarsSettings)
	 */
	@Override
	protected LocationList getLocations(EggWarsSettings settings) {
		return settings.getDiamonds();
	}
}
