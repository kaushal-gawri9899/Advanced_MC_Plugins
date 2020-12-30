package org.jpwilliamson.sumoarena.tool.sumo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.sumoarena.model.sumo.SumoArena;
import org.jpwilliamson.sumoarena.model.sumo.SumoSettings;
import org.jpwilliamson.sumoarena.tool.VisualTool;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SSSpawntool extends VisualTool<SumoArena> {

	@Getter
	private static final Tool instance = new SSSpawntool();

	@Override
	protected CompMaterial getBlockMask(Block block, Player player, SumoArena arena) {
		return CompMaterial.BONE_BLOCK;
	}

	@Override
	protected String getBlockName(Block block, Player player, SumoArena arena) {
		return "[&6SSS Lobby&f]";
	}

	@Override
	public ItemStack getItem() {
		return ItemCreator.of(CompMaterial.BONE,
				"&lSSS TOOL",
				"",
				"Right Click to set",
				"sumo SSS lobby").build().makeMenuTool();
	}

	@Override
	protected void handleBlockClick(Player player, SumoArena arena, ClickType click, Block block) {
		final SumoSettings settings = (SumoSettings) arena.getSettings();
		final Location location = block.getLocation();

		location.setYaw(player.getLocation().getYaw());
		location.setPitch(0);

		settings.setSumoStadium(location);

		Messenger.success(player, "Set the SSS Lobby point.");
	}

	@Override
	protected List<Location> getVisualizedPoints(SumoArena arena) {
		return Arrays.asList(((SumoSettings)arena.getSettings()).getSumoStadium());
	}

	@Override
	protected boolean autoCancel() {
		return true;
	}
}
