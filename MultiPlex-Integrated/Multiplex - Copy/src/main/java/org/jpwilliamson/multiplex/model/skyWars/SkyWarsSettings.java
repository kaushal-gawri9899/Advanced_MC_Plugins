package org.jpwilliamson.multiplex.model.skyWars;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.multiplex.model.dm.DeathmatchSettings;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.remain.CompMaterial;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents settings used in eggwars arenas
 */
@Getter
public class SkyWarsSettings extends DeathmatchSettings {

	/**
	 * The shop items
	 */
	private List<CustomItem> shopItems;

	private final SkyWarsArena arena;
	/**
	 * Represents where to spawn chests
	 */
	private LocationList chests;

	private Location arenaLocation;

	private LocationList iron;

	private LocationList gold;

	private LocationList diamonds;

	/**
	 * Create new arena settings
	 *
	 * @param arena
	 */
	public SkyWarsSettings(final SkyWarsArena arena) {
		super(arena);
		this.arena = arena;
		serialize();
	}
//	/**
//	 * @see ArenaSettings#onLoadFinish()
//	 */
	@Override
	protected void onLoadFinish() {
		super.onLoadFinish();

		this.shopItems = getList("Shop_Items", CustomItem.class, this);
		this.chests = getLocations("Chests");
		this.iron = getLocations("Iron");
		this.gold = getLocations("Gold");
		this.diamonds = getLocations("Diamonds");
		this.arenaLocation = getLocation("SkyWars_Arena");
	}

	public SkyWarsArena getArena(){
		return arena;

	}
	/**
	 * Set the item at the given slot, overriding the old one, preserving the price
	 *
	 * @param slot
	 * @param item
	 */
	public void setItem(int slot, ItemStack item) {
		CustomItem newItem = slot < shopItems.size() ? shopItems.get(slot) : null;

		if (item == null)
			newItem = null;

		else {
			if (newItem == null)
				newItem = new CustomItem(this, item, 1, ItemCurrency.IRON);
			else
				newItem.item = item;
		}

		if (slot < shopItems.size())
			shopItems.set(slot, newItem);
		else
			shopItems.add(newItem);

		save();
	}

	/**
	 * Set the price for the item at the given location
	 *
	 * @param slot
	 * @param price
	 * @param currency
	 */
	public void setPrice(int slot, int price, ItemCurrency currency) {
		Valid.checkBoolean(slot < shopItems.size(), "Cannot set price for non existing item!");

		final CustomItem item = shopItems.get(slot);

		item.price = price;
		item.currency = currency;

		shopItems.set(slot, item);
		save();
	}

	/**
	 * Return the item at the given location or null if not set
	 *
	 * @param slot
	 * @return
	 */
	public CustomItem getItem(int slot) {
		return slot < shopItems.size() ? shopItems.get(slot) : null;
	}

//	/**
//	 * @see ArenaSettings#isSetup()
//	 */
	@Override
	public boolean isSetup() {
		return super.isSetup()
				&& chests.size() > 0
				&& iron.size() > 0
				&& gold.size() > 0
				&& diamonds.size() > 0;
	}


//	/**
//	 * @see ArenaSettings#serialize()
//	 */
	@Override
	public SerializedMap serialize() {
		final SerializedMap map = super.serialize();

		map.putArray(
				"Custom_Items", shopItems,
				"Chests", chests,
				"Iron", iron,
				"Gold", gold,
				"Diamonds", diamonds);

		return map;
	}

	/**
	 * Represents a single purchaseable item
	 */
	@Getter
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public final static class CustomItem implements ConfigSerializable {

		/**
		 * The parent class
		 */
		private final SkyWarsSettings settings;

		/**
		 * The item
		 */
		private ItemStack item;

		/**
		 * The numeral price
		 */
		private int price;

		/**
		 * What currency is this item being offered for (gold, iron etc.)
		 */
		private ItemCurrency currency;

		/**
		 * Set the item
		 *
		 * @param item the item to set
		 */
		public void setItem(ItemStack item) {
			this.item = item;

			settings.save();
		}

		/**
		 * Set the price
		 *
		 * @param price the price to set
		 */
		public void setPrice(int price, ItemCurrency currency) {
			this.price = price;
			this.currency = currency;

			settings.save();
		}

		/**
		 * Return formatted price in {@link #getPrice()} {@link #getCurrency()} format
		 *
		 * @return
		 */
		public String getPriceFormatted() {
			return price + " " + ItemUtil.bountify(currency);
		}

		/**
		 * @see org.mineacademy.fo.model.ConfigSerializable#serialize()
		 */
		@Override
		public SerializedMap serialize() {
			return SerializedMap.ofArray(
					"Item", item,
					"Price", price,
					"Currency", currency);
		}

		/**
		 * Convert a config section to this class
		 *
		 * @param map
		 * @param settings
		 * @return
		 */
		public static CustomItem deserialize(SerializedMap map, SkyWarsSettings settings) {
			final CustomItem item = new CustomItem(settings);

			item.item = map.getItem("Item");
			item.price = map.getInteger("Price");
			item.currency = map.get("Currency", ItemCurrency.class);

			return item;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return item.getType() + " " + getPriceFormatted();
		}
	}

	/**
	 * Represents item currency
	 */
	@RequiredArgsConstructor
	public enum ItemCurrency {

		/**
		 * Iron
		 */
		IRON(CompMaterial.IRON_INGOT),

		/**
		 * Gold
		 */
		GOLD(CompMaterial.GOLD_INGOT),

		/**
		 * Diamond
		 */
		DIAMONDS(CompMaterial.DIAMOND);

		/**
		 * What material this currency needs
		 */
		@Getter
		private final CompMaterial material;
	}
}
