package org.jpwilliamson.arena.menu;

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
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompSound;

import java.util.ArrayList;
import java.util.List;

public class ModeMenu extends MenuPagged<ArenaPlayingMode> {

	private final Button practiceButton;
	private final Button soloButton;
	private final Button teamButton;

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
				Bukkit.broadcastMessage("Mode is" + arena.getGameMode());
			}

			@Override
			public ItemStack getItem() {
				arena.setGameMode(ArenaPlayingMode.PRACTICE.name());
				return ItemCreator.of(CompMaterial.ACACIA_DOOR, "Practice Mode", "You're choosing Practice Mode").build().make();
			}
		};

		this.soloButton = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
				arena.setGameMode(ArenaPlayingMode.SOLO.name());
				Bukkit.broadcastMessage("Mode is" + arena.getGameMode());
				((BuildBattleArena)arena).getVoteMenu().openVoteMenu((BuildBattleArena) arena, null);
				((BuildBattleArena)arena).getVoteMenu().openInventory(player);
				Bukkit.broadcastMessage("Mode is" + arena.getGameMode());

			}

			@Override
			public ItemStack getItem() {
				//return null;
				arena.setGameMode(ArenaPlayingMode.SOLO.name());
				return ItemCreator.of(CompMaterial.IRON_DOOR, "Solo Mode", "You're choosing Solo Mode").build().make();
			}
		};

		this.teamButton = new Button() {
			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
				arena.setGameMode(ArenaPlayingMode.TEAM.name());
				Bukkit.broadcastMessage("Mode is" + arena.getGameMode());
				//TeamSelectionMenu.openSelectMenu(player, arena);
			}

			@Override
			public ItemStack getItem() {
				//return null;
				arena.setGameMode(ArenaPlayingMode.TEAM.name());
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

		if(arena.getGameMode()!=null) {
			if (arena.getGameMode().equalsIgnoreCase("Practice")) {
				arena.getVoteMenu().openVoteMenu(arena, null);
				arena.getVoteMenu().openInventory(player);
				//arena.getSettings()
			}
			if (arena.getGameMode().equalsIgnoreCase("Solo")) {
				player.closeInventory();
				arena.getVoteMenu().openVoteMenu(arena, null);
				arena.getVoteMenu().openInventory(player);
			}
			if (arena.getGameMode().equalsIgnoreCase("Teams")) {
				//TeamSelectionMenu.openSelectMenu(player, arena);
			}
		}

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