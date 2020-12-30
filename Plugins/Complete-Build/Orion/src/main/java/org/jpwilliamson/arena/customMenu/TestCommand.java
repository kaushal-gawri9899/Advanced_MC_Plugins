//package org.jpwilliamson.arena.customMenu;
//
////import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
////import dev.lone.itemsadder.api.FontImages.TexturedInventoryWrapper;
//
//import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
//import dev.lone.itemsadder.api.FontImages.TexturedInventoryWrapper;
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//import org.bukkit.event.inventory.InventoryType;
//import org.bukkit.inventory.Inventory;
//import org.bukkit.plugin.Plugin;
//import org.jetbrains.annotations.NotNull;
//import org.jpwilliamson.arena.command.ArenaSubCommand;
//import org.jpwilliamson.arena.model.ArenaJoinMode;
//import org.jpwilliamson.arena.model.ArenaPlayer;
//
////public class TestCommand implements CommandExecutor {
////
////	Plugin plugin;
////
////	public TestCommand(Plugin plugin){
////		this.plugin = plugin;
////		Bukkit.getPluginCommand("guiexample").setExecutor(this);
////	}
////
////	@Override
////	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
////
////		if(!(commandSender instanceof Player))
////			return false;
////
////		Player player = (Player) commandSender;
////
////		TexturedInventoryWrapper customInventory = new TexturedInventoryWrapper(null,54, ChatColor.BLACK + "Test" , new FontImageWrapper("mcguis:blank_menu"));
////
////		customInventory.showInventory(player);
////		return true;
////	}
////}
//
//
//public class TestCommand extends ArenaSubCommand
//{
//	protected TestCommand() {
//		super("guiexample|ge", "Open Gui");
//	}
//
//	@Override
//	protected void onCommand() {
//
//		final Player player = getPlayer();
//		final ArenaPlayer cache = ArenaPlayer.getCache(player);
//		final ArenaJoinMode mode = cache.getMode();
//
//		TexturedInventoryWrapper customInventory = new TexturedInventoryWrapper(null,54, ChatColor.BLACK + "Test" , new FontImageWrapper("mcguis:crafting"));
//		//TexturedInventoryWrapper customInventory = new TexturedInventoryWrapper(null, InventoryType.CRAFTING,ChatColor.BLACK+"Test",new FontImageWrapper("mcguis:blank_menu"),54);
//		customInventory.showInventory(player);
//	}
//
//
//}