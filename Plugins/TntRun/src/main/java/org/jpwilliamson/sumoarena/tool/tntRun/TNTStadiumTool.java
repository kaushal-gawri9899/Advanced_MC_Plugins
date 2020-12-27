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
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TNTStadiumTool extends VisualTool<TNTRunArena> {

	@Getter
	private static final Tool instance = new TNTStadiumTool();

	@Override
	protected CompMaterial getBlockMask(Block block, Player player, TNTRunArena arena) {
		return CompMaterial.BONE_BLOCK;
	}

	@Override
	protected String getBlockName(Block block, Player player, TNTRunArena arena) {
		return "[&6TNT Stadium&f]";
	}

	@Override
	public ItemStack getItem() {
		return ItemCreator.of(CompMaterial.BONE,
				"&lTNT Stadium TOOL",
				"",
				"Right Click to set",
				"TNT RUN Stadium").build().makeMenuTool();
	}

	@Override
	protected void handleBlockClick(Player player, TNTRunArena arena, ClickType click, Block block) {
		final TNTRunSettings settings = arena.getSettings();
		final Location location = block.getLocation();

		location.setYaw(player.getLocation().getYaw());
		location.setPitch(0);

		settings.setTNTStadium(location);

		Messenger.success(player, "Set the TNT Stadium point.");
	}

	@Override
	protected List<Location> getVisualizedPoints(TNTRunArena arena) {
		return Arrays.asList((arena.getSettings()).getTNTStadium());
	}

	@Override
	protected boolean autoCancel() {
		return true;
	}
}
