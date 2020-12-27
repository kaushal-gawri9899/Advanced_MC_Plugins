//package org.jpwilliamson.arena.command;
//
//import org.bukkit.entity.Player;
//import org.jpwilliamson.arena.model.*;
//
//public class VoteCommand extends ArenaSubCommand {
//
//	protected VoteCommand() {
//		super("vote|v", "Vote for themes to be Build");
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
//		final Arena arena = cache.getArena();
//		//final VoteMenu voteMenu = new VoteMenu((BuildBattleArena)arena, null, player);
//
//		if (cache.hasArena() && mode != ArenaJoinMode.EDITING) {
//
//
//			checkBoolean(mode == ArenaJoinMode.PLAYING && arena.getState() == ArenaState.LOBBY, "You may only select themes in lobby");
//			checkBoolean(arena instanceof BuildBattleArena, "You cannot vote for themes in this arena.");
//
//			//voteMenu.openVoteMenu((BuildBattleArena)arena, null, player);
//			//final VoteMenu voteMenu = new VoteMenu((BuildBattleArena)arena, null, player);
//		//	VoteMenu.openVoteMenu((BuildBattleArena) arena, null,player);
//			BuildBattleArena buildArena = (BuildBattleArena) arena;
//			buildArena.getVoteMenu().openVoteMenu(buildArena, null);
//			buildArena.getVoteMenu().openInventory(player);
//		}
//		//VoteMenu.openVoteMenu((BuildBattleArena) arena, null, player);
//		BuildBattleArena buildArena = (BuildBattleArena) arena;
//		buildArena.getVoteMenu().openVoteMenu(buildArena, null);
//		buildArena.getVoteMenu().openInventory(player);
//	}
//
//}
