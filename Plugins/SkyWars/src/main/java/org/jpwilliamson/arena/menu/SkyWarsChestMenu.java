package org.jpwilliamson.arena.menu;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jpwilliamson.arena.model.skyWars.SkyWarsArena;
import org.jpwilliamson.arena.model.skyWars.SkyWarsSettings;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.ReflectionUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.conversation.SimplePrompt;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.model.MenuClickLocation;
import org.mineacademy.fo.remain.CompMaterial;

import lombok.RequiredArgsConstructor;

/**
 * Enable to purchase items from the villager when playing egg wars
 * or edit the items that are offered
 */
public class SkyWarsChestMenu extends Menu {

	/**
	 * The arena we display items in
	 */
	private final SkyWarsArena arena;

	/**
	 * Is the menu displayed for editing or purchasing?
	 */
	private final Mode viewMode;

	/**
	 * The button to change how the menu is shown
	 */
	private final Button modeButton;

	/**
	 * Create a new SkyWarsChest menu
	 *
	 * @param arena
	 * @param viewMode
	 */
	private SkyWarsChestMenu(SkyWarsArena arena, Mode viewMode) {
		this.arena = arena;
		this.viewMode = viewMode;

		setSize(9 * 5);
		setTitle(viewMode.menuTitle);
		setInfo("Select items you want to",
				"purchase to use in the arena!");

		this.modeButton = new Button() {

			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType click) {
				player.closeInventory();

				new SkyWarsChestMenu(arena, viewMode == Mode.EDIT_PRICES ? Mode.EDIT_ITEMS : Mode.EDIT_PRICES).displayTo(player);
			}

			@Override
			public ItemStack getItem() {
				final boolean editingPrices = viewMode == Mode.EDIT_PRICES;

				return ItemCreator.of(
						editingPrices ? CompMaterial.STRING : CompMaterial.GOLD_INGOT,
						"Editing " + (editingPrices ? "prices" : "items"),
						"",
						"Click to edit " + (editingPrices ? "items" : "prices"))
						.build().make();
			}
		};
	}

	/**
	 * Show edit price prompt or purchase items
	 *
	 * @see org.mineacademy.fo.menu.Menu#onMenuClick(org.bukkit.entity.Player, int, org.bukkit.inventory.ItemStack)
	 */
	@Override
	protected void onMenuClick(Player player, int slot, ItemStack clicked) {
		final SkyWarsSettings.CustomItem item = arena.getSettings().getItem(slot);

		if (item == null)
			return;

		if (viewMode == Mode.EDIT_PRICES)
			SimplePrompt.show(player, new PricePrompt(item));

		else if (viewMode == Mode.PURCHASE) {
			final int price = item.getPrice();
			final CompMaterial material = item.getCurrency().getMaterial();

			if (!PlayerUtil.containsAtLeast(player, price, material)) {
				restartMenu("&4Lacking funds!");

				return;
			}

			PlayerUtil.take(player, material, price);
			PlayerUtil.addItems(player.getInventory(), item.getItem());

			restartMenu("&2Purchase made!");
		}
	}

	/**
	 * Return the item at the given slot location, in this case
	 * the items being sold/edited or our buttons
	 *
	 * @see org.mineacademy.fo.menu.Menu#getItemAt(int)
	 */
	@Override
	public ItemStack getItemAt(int slot) {
		final SkyWarsSettings.CustomItem item = arena.getSettings().getItem(slot);

		if (item != null) {
			final ItemCreator.ItemCreatorBuilder builder = ItemCreator.of(item.getItem());

			if (viewMode != Mode.EDIT_ITEMS) {
				builder.lore("");
				builder.lore("&6Price: " + item.getPriceFormatted());

				if (viewMode == Mode.EDIT_PRICES)
					builder.lore("&6Click to edit");
			}

			return builder.build().make();
		}

		if (slot == getSize() - 5 && viewMode != Mode.PURCHASE)
			return modeButton.getItem();

		return null;
	}

	/**
	 * Save items when edit menu is closed
	 *
	 * @see org.mineacademy.fo.menu.Menu#onMenuClose(org.bukkit.entity.Player, org.bukkit.inventory.Inventory)
	 */
	@Override
	protected void onMenuClose(Player player, Inventory inventory) {
		if (viewMode == Mode.EDIT_ITEMS) {
			final ItemStack[] items = getContent(0, 9 * 4);

			for (int index = 0; index < items.length; index++)
				arena.getSettings().setItem(index, items[index]);
		}
	}

	/**
	 * Allow dragging items to the menu while edited
	 *
	 * @see org.mineacademy.fo.menu.Menu#isActionAllowed(org.mineacademy.fo.menu.model.MenuClickLocation, int, org.bukkit.inventory.ItemStack, org.bukkit.inventory.ItemStack)
	 */
	@Override
	protected boolean isActionAllowed(MenuClickLocation location, int slot, ItemStack clicked, ItemStack cursor) {
		return viewMode == Mode.EDIT_ITEMS && (location == MenuClickLocation.PLAYER_INVENTORY || (location == MenuClickLocation.MENU && slot < getSize() - 9));
	}

	/**
	 * Return to this menu after setting the price
	 *
	 * @see org.mineacademy.fo.menu.Menu#newInstance()
	 */
	@Override
	public Menu newInstance() {
		return new SkyWarsChestMenu(arena, viewMode);
	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Static access (for usability)
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * Open the menu for editing items
	 *
	 * @param arena
	 * @param player
	 */
	public static void openEditMenu(SkyWarsArena arena, Player player) {
		new SkyWarsChestMenu(arena, Mode.EDIT_ITEMS).displayTo(player);
	}

	/**
	 * Open the menu for playing
	 *
	 * @param arena
	 * @param player
	 */
	public static void openPurchaseMenu(SkyWarsArena arena, Player player) {
		new SkyWarsChestMenu(arena, Mode.PURCHASE).displayTo(player);
	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Classes
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * How this menu is displayd
	 */
	@RequiredArgsConstructor
	private enum Mode {

		/**
		 * Menu is opened for editing items
		 */
		EDIT_ITEMS("Editing Shop Items"),

		/**
		 * Menu is opened for setting up prices
		 */
		EDIT_PRICES("Editing Shop Prices"),

		/**
		 * Menu is opened in game for purchasing
		 */
		PURCHASE("Villager Shop");

		/**
		 * The title of the menu in this mode
		 */
		private final String menuTitle;
	}

	/**
	 * A simple prompt to change an item's price
	 */
	@RequiredArgsConstructor
	private class PricePrompt extends SimplePrompt {

		/**
		 * The item
		 */
		private final SkyWarsSettings.CustomItem item;

		/**
		 * @see org.mineacademy.fo.conversation.SimplePrompt#getPrompt(org.bukkit.conversations.ConversationContext)
		 */
		@Override
		protected String getPrompt(ConversationContext ctx) {
			return "&6Enter the price for this item. Currently: " + item.getPriceFormatted();
		}

		/**
		 * @see org.mineacademy.fo.conversation.SimplePrompt#isInputValid(org.bukkit.conversations.ConversationContext, java.lang.String)
		 */
		@Override
		protected boolean isInputValid(ConversationContext context, String input) {
			final String[] split = input.split(" ");

			try {
				ReflectionUtil.lookupEnum(SkyWarsSettings.ItemCurrency.class, split[1]);

			} catch (final Throwable ex) {
				return false;
			}

			return split.length == 2 && Valid.isInteger(split[0]);
		}

		/**
		 * @see org.mineacademy.fo.conversation.SimplePrompt#getFailedValidationText(org.bukkit.conversations.ConversationContext, java.lang.String)
		 */
		@Override
		protected String getFailedValidationText(ConversationContext context, String invalidInput) {
			return "&cPlease use the format: <price> " + Common.convert(SkyWarsSettings.ItemCurrency.values(), currency -> currency.toString().toLowerCase());
		}

		/**
		 * @see org.bukkit.conversations.ValidatingPrompt#acceptValidatedInput(org.bukkit.conversations.ConversationContext, java.lang.String)
		 */
		@Override
		protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
			final String[] split = input.split(" ");
			final int price = Integer.parseInt(split[0]);
			final SkyWarsSettings.ItemCurrency currency = ReflectionUtil.lookupEnum(SkyWarsSettings.ItemCurrency.class, split[1]);

			item.setPrice(price, currency);
			tell(context, "&2Item price has been set to " + item.getPriceFormatted() + ".");

			return Prompt.END_OF_CONVERSATION;
		}
	}
}
