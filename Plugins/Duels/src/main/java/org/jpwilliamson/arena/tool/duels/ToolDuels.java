package org.jpwilliamson.arena.tool.duels;

import lombok.AccessLevel;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jpwilliamson.arena.model.duels.DuelsArena;
import org.jpwilliamson.arena.model.duels.DuelsSettings;
import org.jpwilliamson.arena.model.eggwars.EggWarsSettings;
import org.jpwilliamson.arena.tool.VisualTool;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.remain.CompMaterial;

import org.mineacademy.fo.settings.YamlConfig;
import org.mineacademy.fo.settings.YamlConfig.LocationList;

import java.util.List;

@Setter(value = AccessLevel.PROTECTED)
public abstract class ToolDuels extends VisualTool<DuelsArena> {

	private final String blockName;
	private final CompMaterial blockMask;

	protected ToolDuels(String blockName,CompMaterial blockMask){
		super(DuelsArena.class);
		this.blockMask=blockMask;
		this.blockName=blockName;
	}
	@Override
	protected boolean autoCancel() {
		return true;
	}

	@Override
	protected void handleBlockClick(Player player, DuelsArena arena, ClickType click, Block block) {
	final DuelsSettings settings = arena.getSettings();
	final LocationList locations = getLocations(settings);
	final String name = blockName.toLowerCase();

	int left = MathUtil.range(settings.getMaxPlayers()-locations.size(), 0, Integer.MAX_VALUE);
	final boolean added = locations.toggle(block.getLocation());

	}
	protected abstract LocationList getLocations(DuelsSettings settings);

	@Override
	protected List<Location> getVisualizedPoints(DuelsArena arena) {
		return getLocations(arena.getSettings()).getLocations();
	}

	@Override
	protected String getBlockName(Block block, Player player, DuelsArena arena) {
		return blockName;
	}

	@Override
	protected CompMaterial getBlockMask(Block block, Player player, DuelsArena arena) {
		return blockMask;
	}
}
