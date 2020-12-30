//package org.jpwilliamson.sumoarena.tool.sumo;
//
//import org.bukkit.Location;
//import org.bukkit.block.Block;
//import org.bukkit.entity.Player;
//import org.bukkit.event.inventory.ClickType;
//import org.bukkit.inventory.ItemStack;
//import org.jpwilliamson.sumoarena.model.sumo.SumoArena;
//import org.jpwilliamson.sumoarena.model.sumo.SumoSettings;
//import org.jpwilliamson.sumoarena.tool.VisualTool;
//import org.mineacademy.fo.menu.model.ItemCreator;
//import org.mineacademy.fo.remain.CompMaterial;
//
//import java.util.Collections;
//import java.util.List;
//
//public class LevelTool extends VisualTool<SumoArena> {
//
//
//	@Override
//	protected void handleBlockClick(Player player, SumoArena arena, ClickType click, Block block) {
//		SumoSettings settings = arena.getSettings();
//		Location location = block.getLocation();
//
//		if(settings.setLevel(location))
//			player.sendMessage("Location added as the Standard Level.");
//		else
//			player.sendMessage("Location removed.");
//	}
//
//	@Override
//	protected List<Location> getVisualizedPoints(SumoArena arena) {
//		return Collections.singletonList(arena.getSettings().getLevel());
//	}
//
//	@Override
//	protected String getBlockName(Block block, Player player, SumoArena arena) {
//		return "[&7Level]&6";
//	}
//
//	@Override
//	protected CompMaterial getBlockMask(Block block, Player player, SumoArena arena) {
//		return CompMaterial.BRAIN_CORAL_BLOCK;
//	}
//
//	@Override
//	public ItemStack getItem() {
//		return ItemCreator.of(CompMaterial.BAMBOO,
//				"Level Setter Tool","",
//				"Use this Tool to",
//				"set the level").build().make();
//	}
//}
