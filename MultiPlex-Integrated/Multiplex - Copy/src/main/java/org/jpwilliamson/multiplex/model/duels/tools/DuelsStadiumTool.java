package org.jpwilliamson.multiplex.model.duels.tools;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.multiplex.model.duels.DuelsArena;
import org.jpwilliamson.multiplex.model.duels.DuelsSettings;
import org.jpwilliamson.multiplex.tool.VisualTool;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DuelsStadiumTool  extends VisualTool<DuelsArena> {

	@Getter
	private static final Tool instance = new DuelsStadiumTool();

	@Override
	protected void handleBlockClick(Player player, DuelsArena arena, ClickType click, Block block) {

		final DuelsSettings settings = arena.getSettings();
		final Location location = block.getLocation();

		location.setYaw(player.getLocation().getYaw());
		location.setPitch(0);

		settings.setStadiumLocation(location);
		Messenger.success(player,"Set the Duels Stadium Location");
	}

	@Override
	protected List<Location> getVisualizedPoints(DuelsArena arena) {
		return Arrays.asList((arena.getSettings()).getStadiumLocation());
	}

	@Override
	protected String getBlockName(Block block, Player player, DuelsArena arena) {
		return "[&6Duels Stadium&f]";
	}

	@Override
	protected CompMaterial getBlockMask(Block block, Player player, DuelsArena arena) {
		return CompMaterial.IRON_BLOCK;
	}

	@Override
	public ItemStack getItem() {
		return ItemCreator.of(CompMaterial.IRON_BLOCK,
				"Duels Stadium",
				"",
				"Right Click to Set",
				"Duels Stadium Location").build().makeMenuTool();
	}

	@Override
	protected boolean autoCancel() {
		return true;
	}
}
