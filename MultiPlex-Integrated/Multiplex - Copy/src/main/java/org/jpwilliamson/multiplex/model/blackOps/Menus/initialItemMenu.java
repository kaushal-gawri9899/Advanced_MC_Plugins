package org.jpwilliamson.multiplex.model.blackOps.Menus;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.multiplex.model.blackOps.BlackOpsArena;
import org.jpwilliamson.multiplex.model.blackOps.BlackOpsSettings;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.model.MenuClickLocation;

public class initialItemMenu extends Menu {

	@Getter
	private BlackOpsArena arena;

	private initialItemMenu(BlackOpsArena arena){
		this.arena = arena;

		setSize(9*5);
		setTitle("Initial Item Menu");
		setInfo("add those items which you want",
				" to give to players",
				"at the start of the game");
	}

	@Override
	protected void onMenuClose(Player player, Inventory inventory) {
		BlackOpsSettings settings = arena.getSettings();

		ItemStack[] items = getContent(0,9*4);

		for(int index = 0; index < items.length; ++index)
			settings.setInitialItems(items[index], index);
	}

	@Override
	public ItemStack getItemAt(int slot) {
		final ItemStack item = arena.getSettings().getItem(slot);

		if (item != null) {
			final ItemCreator.ItemCreatorBuilder builder = ItemCreator.of(item);

			return builder.build().make();
		}

		return null;
	}

	@Override
	protected boolean isActionAllowed(MenuClickLocation location, int slot, ItemStack clicked, ItemStack cursor) {
		return (location == MenuClickLocation.PLAYER_INVENTORY || (location == MenuClickLocation.MENU && slot < getSize()));
	}

	@Override
	public Menu newInstance() {
		return new initialItemMenu(arena);
	}

	public static void openEditMenu(BlackOpsArena arena, Player player){
		new initialItemMenu(arena).displayTo(player);
	}
}
