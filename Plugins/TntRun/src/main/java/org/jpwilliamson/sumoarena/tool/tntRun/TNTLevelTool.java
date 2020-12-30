package org.jpwilliamson.sumoarena.tool.tntRun;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.sumoarena.model.tntrun.TNTRunArena;
import org.jpwilliamson.sumoarena.model.tntrun.TNTRunSettings;
import org.jpwilliamson.sumoarena.tool.VisualTool;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.Collections;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TNTLevelTool extends VisualTool<TNTRunArena> {


	@Getter
	private static final Tool instance = new TNTLevelTool();

	@Override
	protected void handleBlockClick(Player player, TNTRunArena arena, ClickType click, Block block) {
		TNTRunSettings settings = arena.getSettings();
		Location location = block.getLocation();

		if(settings.setLevelLocation(location))
			player.sendMessage("Location added as the Bottom Level.");
		else
			player.sendMessage("Location removed.");
	}

	@Override
	protected List<Location> getVisualizedPoints(TNTRunArena arena) {
		return Collections.singletonList(arena.getSettings().getLevelLocation());
	}

	@Override
	protected String getBlockName(Block block, Player player, TNTRunArena arena) {
		return "[&7Level]&6";
	}

	@Override
	protected CompMaterial getBlockMask(Block block, Player player, TNTRunArena arena) {
		return CompMaterial.BRAIN_CORAL_BLOCK;
	}

	@Override
	public ItemStack getItem() {
		return ItemCreator.of(CompMaterial.BAMBOO,
				"Level Setter Tool","",
				"Use this Tool to",
				"set the level").build().make();
	}
}
