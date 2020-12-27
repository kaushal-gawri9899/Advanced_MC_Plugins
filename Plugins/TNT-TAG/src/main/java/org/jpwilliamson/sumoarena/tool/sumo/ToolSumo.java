//package org.jpwilliamson.sumoarena.tool.sumo;
//
//import lombok.AccessLevel;
//import lombok.Setter;
//import org.bukkit.Location;
//import org.bukkit.block.Block;
//import org.bukkit.entity.Player;
//import org.bukkit.event.inventory.ClickType;
//import org.jpwilliamson.sumoarena.model.sumo.SumoArena;
//import org.jpwilliamson.sumoarena.model.sumo.SumoSettings;
//import org.jpwilliamson.sumoarena.tool.VisualTool;
//import org.mineacademy.fo.MathUtil;
//import org.mineacademy.fo.remain.CompMaterial;
//import org.mineacademy.fo.settings.YamlConfig;
//
//import java.util.List;
//
//@Setter(value = AccessLevel.PROTECTED)
//public abstract class ToolSumo extends VisualTool<SumoArena> {
//
//	private final String blockName;
//	private final CompMaterial blockMask;
//
//	protected ToolSumo(String blockName, CompMaterial blockMask){
//		super(SumoArena.class);
//
//		this.blockMask = blockMask;
//		this.blockName = blockName;
//	}
//
//	@Override
//	protected String getBlockName(Block block, Player player, SumoArena arena) {
//		return blockName;
//	}
//
//	@Override
//	protected CompMaterial getBlockMask(Block block, Player player, SumoArena arena) {
//		return blockMask;
//	}
//
//	@Override
//	protected void handleBlockClick(Player player, SumoArena arena, ClickType click, Block block) {
//		final SumoSettings settings = arena.getSettings();
//		final YamlConfig.LocationList locations = getLocations(settings);
//		final String name = blockName.toLowerCase();
//
//		int left = MathUtil.range(settings.getMaxPlayers() - locations.size(), 0, Integer.MAX_VALUE);
//		final boolean added = locations.toggle(block.getLocation());
//	}
//
//	protected abstract YamlConfig.LocationList getLocations(SumoSettings settings);
//
//	@Override
//	protected List<Location> getVisualizedPoints(SumoArena arena) {
//		return getLocations(arena.getSettings()).getLocations();
//	}
//
//	@Override
//	protected boolean autoCancel() {
//		return true;
//	}
//}
