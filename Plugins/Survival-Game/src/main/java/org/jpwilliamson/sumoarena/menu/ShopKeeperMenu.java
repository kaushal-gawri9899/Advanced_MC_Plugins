package org.jpwilliamson.sumoarena.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jpwilliamson.sumoarena.model.sumo.SumoArena;
import org.jpwilliamson.sumoarena.model.sumo.SumoSettings;
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

public class ShopKeeperMenu extends Menu {

	private final SumoArena arena;

	private final ViewMode viewMode;

	private final Button modeButton;

	private ShopKeeperMenu(SumoArena arena, ViewMode viewMode){
		this.arena = arena;
		this.viewMode = viewMode;

		setSize(9*5);
		setTitle(viewMode.menuTitle);
		setInfo("Select items you want to",
				"purchase for fun!");

		this.modeButton = new Button() {

			@Override
			public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
				player.closeInventory();

				new ShopKeeperMenu(arena, viewMode == ViewMode.EDIT_PRICES ?
						ViewMode.EDIT_ITEMS : ViewMode.EDIT_PRICES).displayTo(player);
			}

			@Override
			public ItemStack getItem() {
				final boolean editingPrices = viewMode == ViewMode.EDIT_PRICES;

				return ItemCreator.of(
						editingPrices ? CompMaterial.STRING : CompMaterial.GOLD_INGOT,
						"Editing" + (editingPrices ? "prices" : "items"),
						"",
						"Click to edit " + (editingPrices ? "items" : "prices"))
						.build().make();
			}
		};
	}

	@Override
	protected void onMenuClick(Player player, int slot, ItemStack clicked) {
		final SumoSettings.RestaurantItem item = arena.getSettings().getItem(slot);

		if(item == null)
			return;

		if(viewMode == ViewMode.EDIT_PRICES)
			SimplePrompt.show(player,new PricePrompt(item));

		else if(viewMode == ViewMode.PURCHASE) {
			final int price = item.getPrice();
			final CompMaterial material = item.getCurrency().getMaterial();

			if(!PlayerUtil.containsAtLeast(player, price, material)){
				restartMenu("&4Lacking funds!");

				return;
			}

			PlayerUtil.take(player, material, price);
			PlayerUtil.addItems(player.getInventory(), item.getItem());

			restartMenu("&2Purchase made!");
		}
	}

	@Override
	public ItemStack getItemAt(int slot) {
		final SumoSettings.RestaurantItem item = arena.getSettings().getItem(slot);

		if (item != null) {
			final ItemCreator.ItemCreatorBuilder builder = ItemCreator.of(item.getItem());

			if (viewMode != ViewMode.EDIT_ITEMS) {
				builder.lore("");
				builder.lore("&6Price: " + item.getPriceFormatted());

				if (viewMode == ViewMode.EDIT_PRICES)
					builder.lore("&6Click to edit");
			}

			return builder.build().make();
		}

		if (slot == getSize() - 5 && viewMode != ViewMode.PURCHASE)
			return modeButton.getItem();

		return null;
	}

	@Override
	protected void onMenuClose(Player player, Inventory inventory) {
		if (viewMode == ViewMode.EDIT_ITEMS) {
			final ItemStack[] items = getContent(0, 9 * 4);

			for (int index = 0; index < items.length; index++)
				arena.getSettings().setRestaurantItems(index, items[index]);
		}
	}

	@Override
	protected boolean isActionAllowed(MenuClickLocation location, int slot, ItemStack clicked, ItemStack cursor) {
		return viewMode == ViewMode.EDIT_ITEMS && (location == MenuClickLocation.PLAYER_INVENTORY || (location == MenuClickLocation.MENU && slot < getSize() - 9));
	}

	@Override
	public Menu newInstance() {
		return new ShopKeeperMenu(arena, viewMode);
	}

	public static void openEditMenu(SumoArena arena, Player player) {
		new ShopKeeperMenu(arena, ViewMode.EDIT_ITEMS).displayTo(player);
	}

	public static void openPurchaseMenu(SumoArena arena, Player player){
		new ShopKeeperMenu(arena, ViewMode.PURCHASE).displayTo(player);
	}

	@RequiredArgsConstructor
	private enum ViewMode{

		EDIT_ITEMS("Editing the Shop Items"),

		EDIT_PRICES("Editing the Prices"),

		PURCHASE("Restaurant");

		private final String menuTitle;
	}

	@RequiredArgsConstructor
	private class PricePrompt extends  SimplePrompt{

		private final SumoSettings.RestaurantItem item;

		@Override
		protected String getPrompt(ConversationContext conversationContext) {
			return "&6Enter the price for this item. Currently: " + item.getPriceFormatted();
		}

		@Override
		protected boolean isInputValid(ConversationContext context, String input) {
			final String[] split = input.split(" ");

			try {
				ReflectionUtil.lookupEnum(SumoSettings.RItemCurrency.class, split[1]);

			} catch (final Throwable ex) {
				return false;
			}

			return split.length == 2 && Valid.isInteger(split[0]);
		}

		@Override
		protected String getFailedValidationText(ConversationContext context, String invalidInput) {
			return "&cPlease use the format: <price> " + Common.convert(SumoSettings.RItemCurrency.values(), currency -> currency.toString().toLowerCase());
		}

		@Override
		protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
			final String[] split = input.split(" ");
			final int price = Integer.parseInt(split[0]);
			final SumoSettings.RItemCurrency currency = ReflectionUtil.lookupEnum(SumoSettings.RItemCurrency.class, split[1]);

			item.setPrice(price, currency);
			tell(context, "&2Item price has been set to " + item.getPriceFormatted() + ".");

			return Prompt.END_OF_CONVERSATION;
		}
	}
}
