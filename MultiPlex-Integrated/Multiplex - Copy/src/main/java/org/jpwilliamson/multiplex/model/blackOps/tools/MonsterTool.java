package org.jpwilliamson.multiplex.model.blackOps.tools;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.multiplex.model.blackOps.BlackOpsArena;
import org.jpwilliamson.multiplex.model.blackOps.BlackOpsSettings;
import org.jpwilliamson.multiplex.model.blackOps.Menus.RareItemMenu;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.YamlConfig.LocationList;

public class MonsterTool extends BlackOpsTool{

	@Getter
	private final static Tool instance = new MonsterTool();

	private MonsterTool() {
		super("Monster Spawn Point", CompMaterial.RED_TERRACOTTA);
	}

	@Override
	public ItemStack getItem() {
		return ItemCreator.of(CompMaterial.CREEPER_HEAD,
				"Monster Spawn Tool",
				"Click to toggle Where",
				"Monster will spawn").build().make();
	}

	@Override
	protected void handleAirClick(Player player, BlackOpsArena arena, ClickType click) {
		RareItemMenu.openEditMenu(arena,player);
	}

	@Override
	protected LocationList getLocations(BlackOpsSettings settings) {
		return settings.getMonsterSpawnLocation();
	}
}
