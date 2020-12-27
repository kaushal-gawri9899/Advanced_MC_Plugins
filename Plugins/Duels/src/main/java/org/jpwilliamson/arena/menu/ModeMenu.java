package org.jpwilliamson.arena.menu;

import com.bekvon.bukkit.residence.utils.GetTime;
import javafx.beans.property.SimpleObjectProperty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.arena.model.*;
import org.jpwilliamson.arena.model.buildbattle.BuildBattleArena;
import org.jpwilliamson.arena.model.team.TeamArena;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuPagged;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompSound;

import java.util.ArrayList;
import java.util.List;

public class ModeMenu extends MenuPagged<ArenaPlayingMode> {

	private final Button practiceButton;
	private final Button soloButton;
	private final Button teamButton;
//	private SimpleTime newTime;

	private BuildBattleArena arena;
	private final ArenaPlayer cache;

	private ModeMenu(final Menu parent, final TeamArena arena, final Player player){
		//super(parent, compile(player, arena));
		super(parent,compile(player, arena));
		this.cache = ArenaPlayer.getCache(player);

		setSize(9*4);
		setTitle("Mode Selection Menu");

		this.practiceButton = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
				arena.setGameMode(ArenaPlayingMode.PRACTICE.name());

				((BuildBattleArena)arena).getVoteMenu().openVoteMenu((BuildBattleArena) arena, null);
				((BuildBattleArena)arena).getVoteMenu().openInventory(player);
				((BuildBattleArena)arena).getSettings().setMaxPlayers(1);
				((BuildBattleArena)arena).getSettings().setMaximumTeamImbalance(1);
				((BuildBattleArena) arena).getSettings().setBuildDuration(((BuildBattleArena)arena).getSettings().getPracticeDuration());
				((BuildBattleArena) arena).getSettings().setVotingDuration(((BuildBattleArena)arena).getSettings().getVotingPracticeDuration());
				((BuildBattleArena)arena).getSettings().setGameDuration(((BuildBattleArena)arena).getSettings().getPracticeGameDuration());
				//((BuildBattleArena)arena).getSettings().setBuildDuration(SimpleTime.from("2 minutes"));
				//((BuildBattleArena)arena).getSettings().setBuildDuration(((BuildBattleArena)arena).getSettings().getPracticeDuration());

				//((BuildBattleArena)arena).getSettings().setVotingDuration(SimpleTime.from("0 minutes"));
//							buildArena.getSettings().setVotingDuration(SimpleTime.from("0 seconds"));

//							//VoteMenu.openVoteMenu(buildArena, null, player);
//							broadcast("SOLO MODE");
//							buildArena.getVoteMenu().openVoteMenu(buildArena,null);
//							buildArena.getVoteMenu().openInventory(player);

			}

			@Override
			public ItemStack getItem() {
				return ItemCreator.of(CompMaterial.ACACIA_DOOR, "Practice Mode", "You're choosing Practice Mode").build().make();
			}
		};

		this.soloButton = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
				arena.setGameMode(ArenaPlayingMode.SOLO.name());
				((BuildBattleArena)arena).getVoteMenu().openVoteMenu((BuildBattleArena) arena, null);
				((BuildBattleArena)arena).getVoteMenu().openInventory(player);
				((BuildBattleArena)arena).getSettings().setMaximumTeamImbalance(1);
				((BuildBattleArena)arena).getSettings().setMaxPlayers(4);
				((BuildBattleArena) arena).getSettings().setBuildDuration(((BuildBattleArena)arena).getSettings().getTeamDuration());
				((BuildBattleArena) arena).getSettings().setVotingDuration(((BuildBattleArena)arena).getSettings().getTeamVotingDuration());
				((BuildBattleArena)arena).getSettings().setGameDuration(((BuildBattleArena)arena).getSettings().getTeamGameDuration());
				//Bukkit.broadcastMessage("You're competing with " +(((BuildBattleArena)arena).getSettings().getMaxPlayers()-1) + " players");

			}

			@Override
			public ItemStack getItem() {
				//return null;
				return ItemCreator.of(CompMaterial.IRON_DOOR, "Solo Mode", "You're choosing Solo Mode").build().make();
			}
		};

		this.teamButton = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
				arena.setGameMode(ArenaPlayingMode.TEAM.name());
				TeamSelectionMenu.openSelectMenu(player, arena);
				((BuildBattleArena) arena).getSettings().setBuildDuration(((BuildBattleArena)arena).getSettings().getTeamDuration());
				((BuildBattleArena) arena).getSettings().setVotingDuration(((BuildBattleArena)arena).getSettings().getTeamVotingDuration());
				((BuildBattleArena)arena).getSettings().setGameDuration(((BuildBattleArena)arena).getSettings().getTeamGameDuration());
			}

			@Override
			public ItemStack getItem() {
				//return null;
				return ItemCreator.of(CompMaterial.DARK_OAK_DOOR, "Team Mode", "You're choosing Team Mode").build().make();
			}
		};

	}

	private static Iterable<ArenaPlayingMode> compile(final Player player, final Arena arena) {

		final List<ArenaPlayingMode> modes = new ArrayList<>();
		for(final ArenaPlayingMode mode : ArenaPlayingMode.values()){
			modes.add(mode);
		}

		return modes;
	}


	@Override
	protected ItemStack convertToItemStack(ArenaPlayingMode arenaPlayingMode) {
		return null;
		//return ItemCreator.of(CompMaterial.D)
	}

	@Override
	protected void onPageClick(Player player, ArenaPlayingMode arenaPlayingMode, ClickType clickType) {

//		if(arena.getGameMode()!=null) {
//			if (arena.getGameMode().equalsIgnoreCase("Practice")) {
//				arena.getVoteMenu().openVoteMenu(arena, null);
//				arena.getVoteMenu().openInventory(player);
//				//arena.getSettings()
//			}
//			if (arena.getGameMode().equalsIgnoreCase("Solo")) {
//				player.closeInventory();
//				arena.getVoteMenu().openVoteMenu(arena, null);
//				arena.getVoteMenu().openInventory(player);
//			}
//			if (arena.getGameMode().equalsIgnoreCase("Teams")) {
//				//TeamSelectionMenu.openSelectMenu(player, arena);
//			}
//		}
	}

	@Override
	public ItemStack getItemAt(final int slot) {

		if (slot == 9 * 1 + 3)
			return practiceButton.getItem();

		if (slot == 9 * 2 + 3)
			return soloButton.getItem();

		if (slot == 9 * 3 + 3)
			return teamButton.getItem();

		return null;
	}

	@Override
	protected String[] getInfo() {
		return new String[] {
				"Configure the",
				"game-mode here"
		};
	}

	public static void openModeMenu(final Player player, final BuildBattleArena arena) {
		new ModeMenu(null,arena, player).displayTo(player);
	}
}