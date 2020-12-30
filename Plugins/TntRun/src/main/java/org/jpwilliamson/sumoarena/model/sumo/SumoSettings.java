package org.jpwilliamson.sumoarena.model.sumo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.sumoarena.model.ArenaSettings;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.List;

public class SumoSettings extends ArenaSettings {

	// Locations for the two players selected to fight in the arena
	@Getter
	private LocationList entranceLocations;

	// Location for the sumoStadium
	@Getter
	private Location sumoStadium;

	// Restaurant Items
	private List<RestaurantItem> restaurantItems;

	@Getter
	private Location Level;

	// ShopKeepers
	@Getter
	private LocationList shopKeepers;

	// Settings Constructor
	public SumoSettings(final SumoArena arena){
		super(arena);
	}

	// After loading the settings file
	@Override
	protected void onLoadFinish() {
		super.onLoadFinish();

		this.entranceLocations = getLocations("Entrance_Locations");
		this.sumoStadium = getLocation("SumoStadium_Location");
		this.restaurantItems = getList("Restaurant_Items",RestaurantItem.class, this);
		this.shopKeepers = getLocations("ShopKeepers");
		this.Level = getLocation("Level");
	}

	public void setRestaurantItems(int slot, ItemStack item){
		RestaurantItem newItem = slot < restaurantItems.size() ? restaurantItems.get(slot) : null;

		if(item == null)
			newItem = null;

		else {
			if(newItem == null)
				newItem = new RestaurantItem(this, item,1, RItemCurrency.IRON);
			else
				newItem.item = item;
		}

		if(slot < restaurantItems.size())
			restaurantItems.set(slot, newItem);
		else
			restaurantItems.add(newItem);

		save();
	}

	public void setPrice(int slot, int price, RItemCurrency currency) {
		Valid.checkBoolean(slot < restaurantItems.size(), "Cannot set price for non existing item!");

		final SumoSettings.RestaurantItem item = restaurantItems.get(slot);

		item.price = price;
		item.currency = currency;

		restaurantItems.set(slot, item);
		save();
	}

	public SumoSettings.RestaurantItem getItem(int slot) {
		return slot < restaurantItems.size() ? restaurantItems.get(slot) : null;
	}

	// adding or removing the spawn location
	public final boolean toggleEntranceLocation(final Location location){
		for(Location location1: entranceLocations){
			if(Valid.locationEquals(location,location1)){
				entranceLocations.remove(location);

				save();
				return false;
			}
		}

		entranceLocations.add(location);
		save();

		return true;
	}

	public boolean setLevel(Location location){
		if(Valid.locationEquals(Level, location)){
			Level = null;
			save();
			return false;
		}
		Level = location;
		save();

		return true;
	}

	@Override
	public boolean isSetup() {
		return super.isSetup()
				&& entranceLocations.size() == 2
				&& sumoStadium != null
				&& shopKeepers.size() > 0;
	}

	public void setSumoStadium(final Location location){
		this.sumoStadium = location;

		save();
	}

	@Override
	public SerializedMap serialize() {
		SerializedMap map = super.serialize();

		map.putArray("Entrance_Locations", entranceLocations,
				"SumoStadium_Location", sumoStadium,
				"Restaurant_Items", restaurantItems,
				"ShopKeepers", shopKeepers,
				"Level", Level);

		return map;
	}

	@Getter
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public final static class RestaurantItem implements ConfigSerializable{

		private final SumoSettings settings;

		private ItemStack item;

		private int price;

		private RItemCurrency currency;

		public void setItem(ItemStack item) {
			this.item = item;

			settings.save();
		}

		public void setPrice(int price, RItemCurrency currency) {
			this.price = price;
			this.currency = currency;

			settings.save();
		}

		public String getPriceFormatted(){
			return price + " " + ItemUtil.bountify(currency);
		}

		@Override
		public SerializedMap serialize() {
			return SerializedMap.ofArray("Item", item,
					"Price", price,
					"Currency", currency);
		}

		public static SumoSettings.RestaurantItem deserialize(SerializedMap map, SumoSettings settings) {
			final SumoSettings.RestaurantItem item = new SumoSettings.RestaurantItem(settings);

			item.item = map.getItem("Item");
			item.price = map.getInteger("Price");
			item.currency = map.get("Currency", RItemCurrency.class);

			return item;
		}

		@Override
		public String toString(){
			return item.getType() + " " + getPriceFormatted();
		}
	}

	@RequiredArgsConstructor
	public enum RItemCurrency{

		IRON(CompMaterial.IRON_INGOT),

		GOLD(CompMaterial.GOLD_INGOT),

		DIAMONDS(CompMaterial.DIAMOND);

		@Getter
		private final CompMaterial material;
	}

}
