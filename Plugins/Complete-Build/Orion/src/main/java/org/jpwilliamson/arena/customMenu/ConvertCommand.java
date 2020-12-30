//package org.jpwilliamson.arena.customMenu;
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
//import org.bukkit.plugin.Plugin;
//import org.jetbrains.annotations.NotNull;
//import org.jpwilliamson.arena.command.ArenaSubCommand;
//import org.jpwilliamson.arena.model.Arena;
//import org.jpwilliamson.arena.model.ArenaJoinMode;
//import org.jpwilliamson.arena.model.ArenaPlayer;
//
////public class ConvertCommand implements CommandExecutor {
////
////	Plugin plugin;
////
////	public ConvertCommand(Plugin plugin){
////
////		this.plugin = plugin;
////		Bukkit.getPluginCommand("guiconvert").setExecutor(this);
////	}
////
////	@Override
////	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
////
////		if(strings.length!=1){
////			commandSender.sendMessage("Please specify player name . /guiconvert <player>");
////			return false;
////		}
////
////		Player player = Bukkit.getPlayer(strings[0]);
////
////		if(player==null){
////			commandSender.sendMessage("Cannot find Player" + strings[0]);
////			return false;
////
////		}
////
////		if(player.getInventory().getType() == InventoryType.CRAFTING)
////		{
////			commandSender.sendMessage("No open inventory, please open an inventory and execute the command");
////			return false;
////		}
////
////		FontImageWrapper text = new FontImageWrapper("mcguis:blank_menu");
////		if(!text.exists())
////		{
////			commandSender.sendMessage("Can't find the text, make sure you have the relevant file in Items_Adder");
////			return  false;
////		}
////
////		TexturedInventoryWrapper.setPlayerInventoryTexture(player, text, ChatColor.BLACK+"Test");
////		return true;
////	}
////}
//
//public class ConvertCommand extends ArenaSubCommand{
//
//	protected ConvertCommand() {
//		super("guiconvert|gc", "ConvertThemes");
//	}
//
//	@Override
//	protected void onCommand() {
//		checkConsole();
//
//		final Player player = getPlayer();
//		final ArenaPlayer cache = ArenaPlayer.getCache(player);
//		final ArenaJoinMode mode = cache.getMode();
//
//
//
//		if(player==null){
//			player.sendMessage("Cannot find Player");
//		}
//
//		if(player.getInventory().getType() == InventoryType.CRAFTING)
//		{
//			player.sendMessage("No open inventory, please open an inventory and execute the command");
//		}
//
//		FontImageWrapper text = new FontImageWrapper("mcguis:blank_menu");
//		if(!text.exists())
//		{
//			player.sendMessage("Can't find the text, make sure you have the relevant file in Items_Adder");
//		}
//
//		TexturedInventoryWrapper.setPlayerInventoryTexture(player, text, ChatColor.BLACK+"Test");
//
//	}
//}