package org.jpwilliamson.multiplex.model.skyWars.menus;


import org.jpwilliamson.multiplex.model.skyWars.SkyWarsArena;
import org.mineacademy.fo.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.model.InventoryDrawer;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;


import java.util.ArrayList;
import java.util.List;

public class SpinRewardMenu extends Menu {

	private static List<CompMaterial> randomList = new ArrayList<CompMaterial>();
	private static List<CompMaterial> rewardList = new ArrayList<CompMaterial>();
	private static Inventory spinInventory;
	private String inventoryTitle = "Spin And Win";
	private SkyWarsArena arena;


	//Constructor is responsible for creating an empty inventory and invokes the function responsible for creating initial spin menu
	public SpinRewardMenu(SkyWarsArena arena)
	{
	super();
	this.arena = arena;
	setSize(9*5);
	setTitle(inventoryTitle);
	spinInventory = Bukkit.createInventory(null, 9*5,"Spin and Win");
	fillRandomList();
	createInitialSpinMenu();
	}

	public static void openSpinInventory(Player player){
	player.openInventory(spinInventory);
	}

	/*
	* This is the logic of how menu is created
	* Each column of the custom inventory is filled with Some Coloured Plates
	* The centre of the inventory has a Diamond Sword already placed at it, User interacts with it to start the spinning of menu
	* Everything else is validated via ArenaListener
	* */
	public static void createInitialSpinMenu(){

	for(int i=0; i<45; i++)
	{
		int val = i+1;
		if((val+1)/2==23)
		{
			int newVal = (val+1)/2;
			ItemStack stack =ItemCreator.of(CompMaterial.DIAMOND_SWORD, "Click to spin", "Get Your Reward!").build().make();
			spinInventory.setItem(newVal,new ItemStack(stack));
		}

		else{
			if(val%9==1)
			{
				ItemStack stack = ItemCreator.of(CompMaterial.RED_STAINED_GLASS_PANE).build().make();
				spinInventory.setItem(val,new ItemStack(stack));
			}
			else if(val%9==2)
			{
				ItemStack stack = ItemCreator.of(CompMaterial.GREEN_STAINED_GLASS_PANE).build().make();
				spinInventory.setItem(val,new ItemStack(stack));
			}
			else if(val%9==3)
			{
				ItemStack stack = ItemCreator.of(CompMaterial.ORANGE_STAINED_GLASS_PANE).build().make();
				spinInventory.setItem(val,new ItemStack(stack));
			}
			else if(val%9==4)
			{
				ItemStack stack = ItemCreator.of(CompMaterial.BLUE_STAINED_GLASS_PANE).build().make();
				spinInventory.setItem(val,new ItemStack(stack));
			}
			else if(val%9==5 && (val+1)/2!=23)
			{
				ItemStack stack = ItemCreator.of(CompMaterial.ORANGE_STAINED_GLASS_PANE).build().make();
				spinInventory.setItem(val,new ItemStack(stack));
			}
			else if(val%9==6)
			{
				ItemStack stack = ItemCreator.of(CompMaterial.YELLOW_STAINED_GLASS_PANE).build().make();
				spinInventory.setItem(val,new ItemStack(stack));
			}
			else if(val%9==7)
			{
				ItemStack stack = ItemCreator.of(CompMaterial.CYAN_STAINED_GLASS_PANE).build().make();
				spinInventory.setItem(val,new ItemStack(stack));
			}
			else if(val%9==8)
			{
				ItemStack stack = ItemCreator.of(CompMaterial.PURPLE_STAINED_GLASS_PANE).build().make();
				spinInventory.setItem(val,new ItemStack(stack));
			}
			else if(val%9==0)
			{
				ItemStack stack = ItemCreator.of(CompMaterial.LIME_STAINED_GLASS_PANE).build().make();
				spinInventory.setItem(val,new ItemStack(stack));
			}
		}
	}

	}


	/*
	* This function is responsible for handling the state update on every click by a player
	* This function is invoked whenever a user clicks on the centre item, used as a button, and the state of our menu is changed for the next 30 seconds
	* At the 30th second, the state is saved and the item at the centre is added to player's inventory
	* See ArenaListener for function call
	* */
	public static void updateState(Player player){

		for(int i=0; i<spinInventory.getSize(); i++)
		{
			int val = i+1;
			if((val+1)/2==23)
			{
				int newVal = (val+1)/2;
				int min=0;
				int max = 14;
				int seed = (int) (Math.random()*(max-min+1) + min);
				ItemStack stack = ItemCreator.of(rewardList.get(seed),"You Recieved A Reward!").build().make();
				spinInventory.setItem(newVal,new ItemStack(stack));
			}
			else{
				int min=0;
				int max = 7;
				int seed = (int) (Math.random()*(max-min+1) + min);
				ItemStack stack = ItemCreator.of(randomList.get(seed)).build().make();
				spinInventory.setItem(val,new ItemStack(stack));
			}
		}
	}

	/*
	* Returns the item at the center of the inventory, which is the reward of spin and win
	* */
	public static ItemStack getItemAtCentre(){
		if(spinInventory.getItem(23)!=null)
		return spinInventory.getItem(23);
		else
			return ItemCreator.of(CompMaterial.DIAMOND_SWORD, "Click to spin", "Get Your Reward!").build().make();
	}

	/*
	* Creates a new Menu for the arena
	* Used in other classes to open the Menu when player joins
	* */
	public static void openSpinMenu(SkyWarsArena arena)
	{
		new SpinRewardMenu(arena);
	}

	/*
	* ------------------------- Overides-------------------------------------------------------------------
	* */
	@Override
	protected void onDisplay(InventoryDrawer drawer) {
		super.onDisplay(drawer);
	}

	/*
	* Returns the item at a particular slot
	* */
	@Override
	public ItemStack getItemAt(int slot) {
		return super.getItemAt(slot);
	}

	@Override
	protected void onMenuClick(Player player, int slot, InventoryAction action, ClickType click, ItemStack cursor, ItemStack clicked, boolean cancelled) {
		super.onMenuClick(player, slot, action, click, cursor, clicked, cancelled);
	}

	@Override
	protected void onMenuClose(Player player, Inventory inventory) {
		super.onMenuClose(player, inventory);
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	protected String[] getInfo() {
		return super.getInfo();
	}


	/*
	* Fills the list maintaining the items used to create the menu and the items used as a reward
	* Can be later substituted to either get an element through a database
	* */
	public void fillRandomList(){
		randomList.add(CompMaterial.RED_STAINED_GLASS_PANE);
		randomList.add(CompMaterial.GREEN_STAINED_GLASS_PANE);
		randomList.add(CompMaterial.ORANGE_STAINED_GLASS_PANE);
		randomList.add(CompMaterial.BLUE_STAINED_GLASS_PANE);
		randomList.add(CompMaterial.YELLOW_STAINED_GLASS_PANE);
		randomList.add(CompMaterial.CYAN_STAINED_GLASS);
		randomList.add(CompMaterial.PURPLE_STAINED_GLASS);
		randomList.add(CompMaterial.LIME_STAINED_GLASS);

		rewardList.add(CompMaterial.ARROW);
		rewardList.add(CompMaterial.SPECTRAL_ARROW);
		rewardList.add(CompMaterial.TIPPED_ARROW);
		rewardList.add(CompMaterial.BOW);
		rewardList.add(CompMaterial.CROSSBOW);
		rewardList.add(CompMaterial.STONE_SWORD);
		rewardList.add(CompMaterial.GOLDEN_SWORD);
		rewardList.add(CompMaterial.DIAMOND_SWORD);
		rewardList.add(CompMaterial.WOODEN_SWORD);
		rewardList.add(CompMaterial.TRIDENT);
		rewardList.add(CompMaterial.APPLE);
		rewardList.add(CompMaterial.GOLDEN_APPLE);
		rewardList.add(CompMaterial.ENCHANTED_GOLDEN_APPLE);
		rewardList.add(CompMaterial.CARROT);
		rewardList.add(CompMaterial.MILK_BUCKET);
		rewardList.add(CompMaterial.SHIELD);

	}
}
