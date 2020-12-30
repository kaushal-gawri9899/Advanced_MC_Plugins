package org.jpwilliamson.multiplex.model.blackOps;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.multiplex.model.ArenaSettings;
import org.mineacademy.fo.collection.SerializedMap;

import java.util.List;

@Getter
public class BlackOpsSettings extends ArenaSettings {

	/* settings info
	* Entrance Location -> Player will be spawned up here at the start of the game
	* InitialItems -> Player will get these items at the begin of the game
	* ChestLocations -> Locations for the chests.
	* Rounds -> No. of Rounds in the Game before boss fight
	* Monsters -> List Containing Different type of Monsters present in the game
	* BossMonster -> Monster will be selected randomly from this list for the boss fight
	* BossMonsterLocation -> List of location slected randomly to spawn the boss*/

	private Location entranceLocation;

	private List<ItemStack> initialItems;

	private LocationList chestLocations;

	private LocationList monsterSpawnLocation;

	private List<ItemStack> rareItems;

	private List<ItemStack> dropItems;

	//-----------------------------------------------------------------------------------

	public BlackOpsSettings(final BlackOpsArena arena){
		super(arena);
	}

	@Override
	protected void onLoadFinish() {
		super.onLoadFinish();

		this.entranceLocation = getLocation("Entrance_Location");
		this.initialItems = getList("Initial_Items", ItemStack.class, this);
		this.chestLocations = getLocations("Chest_Locations");
		this.monsterSpawnLocation = getLocations("Monster_Spawn_Location");
		this.rareItems = getList("Rare_Items",ItemStack.class,this);
		this.dropItems = getList("Drop_Items",ItemStack.class, this);
	}

	public void setInitialItems(ItemStack item, int slot) {
		if(slot < initialItems.size())
			initialItems.set(slot,item);
		else
			initialItems.add(item);

		save();
	}

	public void setDropItems(ItemStack item, int slot) {
		if(slot < dropItems.size())
			dropItems.set(slot,item);
		else
			dropItems.add(item);

		save();
	}

	public void setRareItems(ItemStack item, int slot) {
		if(slot < rareItems.size())
			rareItems.set(slot,item);
		else
			rareItems.add(item);

		save();
	}

	public ItemStack getItem(int slot){
		return slot < initialItems.size() ? initialItems.get(slot) : null;
	}

	public ItemStack getDropItem(int slot){
		return slot < dropItems.size() ? dropItems.get(slot): null;
	}

	public ItemStack getRareItem(int slot){
		return slot < rareItems.size() ? rareItems.get(slot) : null;
	}

	public void setEntranceLocation(Location location){
		entranceLocation = location;

		save();
	}

	@Override
	public SerializedMap serialize() {
		SerializedMap map = super.serialize();
		map.putArray("Entrance_Location", entranceLocation,
				"Initial_Items", initialItems,
				"Chest_Locations", chestLocations,
				"Monster_Spawn_Location", monsterSpawnLocation,
				"Rare_Items", rareItems,
				"Drop_Items", dropItems);
		return map;
	}
}
