package org.jpwilliamson.arena.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.arena.model.buildbattle.BuildBattleArena;
import org.jpwilliamson.arena.util.ArenaUtil;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.model.InventoryDrawer;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.*;

public class VoteMenu extends Menu {

	//private static Map<Integer,String> themeMap = new HashMap<Integer, String>();
	private static Map<String,Integer> themeMap = new HashMap<String,Integer>();
	private static Map<UUID,String> playerMap = new HashMap<UUID,String>();
	private static String highestTheme;
	MenuItem menuInventory;
	private static Inventory myInventory;
	private String menuTitle = "Theme Voting Menu";
	private BuildBattleArena arena;
	private Themes themeSelect;
	private static boolean notClick=false;






//	public VoteMenu(BuildBattleArena arena, MenuItem menuInventory, Player player) {
//
//		super();
//		loadThemes();
//		this.arena = arena;
//		this.menuInventory = menuInventory;
//
//		setSize(9*6);
//		setTitle(menuTitle);
//
//		myInventory = Bukkit.createInventory(null, 9*6,"Vote Inventory");
////		customizeInventory();
//
//		customizeInventory();
//		player.openInventory(myInventory);
//
//		//getViewer().openInventory(myInventory);
//
//		playerMap.put(player.getUniqueId(), " ");
//		//Update Inventory To initial Value
//		//updateMenu();
//	}

	public VoteMenu(BuildBattleArena arena, MenuItem menuInventory) {

		super();
		loadThemes();
		this.arena = arena;
		this.menuInventory = menuInventory;

		setSize(9*6);
		setTitle(menuTitle);

		myInventory = Bukkit.createInventory(null, 9*6,"Vote Inventory");
//		customizeInventory();

		customizeInventory();
		//player.openInventory(myInventory);

		//getViewer().openInventory(myInventory);

		//playerMap.put(player.getUniqueId(), " ");
		//Update Inventory To initial Value
		//updateMenu();
	}

	public  void openInventory(Player player){

		player.openInventory(myInventory);
		playerMap.put(player.getUniqueId(), " ");

	}

	public static Map<String,Integer> getThemeMap(){
		return themeMap;
	}


	public static void changeInventory(int slot, Player player){

		List value = new ArrayList(themeMap.keySet());
		customizeInventory();

			if(!playerMap.get(player.getUniqueId()).equalsIgnoreCase(" ")){
			int oldvalue = themeMap.get(playerMap.get(player.getUniqueId()));
			themeMap.replace((String)(playerMap.get(player.getUniqueId())),oldvalue,oldvalue-1);
		}
		for(int i=0; i<6; i++){
			if(slot==(9*(i+1)-1)){
				int oldValue = themeMap.get((String)value.get(i));
				Bukkit.broadcastMessage("Old Value of Theme "+ oldValue);
				int newValue = oldValue+1;
				notClick = true;
				themeMap.replace((String)(value.get(i)),oldValue,newValue);
				playerMap.put(player.getUniqueId(),(String)value.get(i));
				for(int j=7;j>0;j--){
					//myInventory.setItem();

					//removing the break will make all panes green once Paper is clicked
					if(myInventory.getItem((9*(i+1)-1-(j+1))).getType().equals(Material.ACACIA_SIGN) && myInventory.getItem((9*(i+1)-1-(j))).getType().equals(Material.RED_STAINED_GLASS_PANE)){
						myInventory.setItem(9*(i+1)-1-j,ItemCreator.of(CompMaterial.GREEN_STAINED_GLASS_PANE).build().make());
						increaseVotes();
					//	return;
						//break;
						return;
					}
					if(myInventory.getItem((9*(i+1)-1-(j+1))).getType().equals(Material.GREEN_STAINED_GLASS_PANE)){
						myInventory.setItem(9*(i+1)-1-j,ItemCreator.of(CompMaterial.GREEN_STAINED_GLASS_PANE).build().make());
						increaseVotes();
						return;
					//	break;
					}
					else{
						return;
						//break;
					}
				}

			}
		}
		increaseVotes();

	}

	public static void increaseVotes(){

		List value = new ArrayList(themeMap.keySet());
		for(int i=0; i<54; i++){
			int k=i+1;
			if((k-1)%9==0){
				if(k!=-1) {
					ItemStack stack = ItemCreator.of(CompMaterial.ACACIA_SIGN,
							//String.valueOf(VoteMenu.themeMap.get((k - 1) / 9)),
							(String) value.get((k-1)/9),
							"A build Theme with value" + themeMap.get((String)value.get((k-1)/9)))
							.build().make();
					myInventory.setItem(k-1, stack);
					//themeMap.get()
				}
				else{
					ItemStack stack = ItemCreator.of(CompMaterial.ACACIA_SIGN,
							String.valueOf(VoteMenu.themeMap.get(0)),
							"A Theme")
							.build().make();
					myInventory.setItem(0, stack);
				}
			}
		}
		sortByValue();
	}

	public static void sortByValue(){

		List<Map.Entry<String,Integer>> myThemes = new LinkedList<Map.Entry<String, Integer>>(themeMap.entrySet());

		Collections.sort(myThemes, new Comparator<Map.Entry<String,Integer>>(){
			public int compare(Map.Entry<String,Integer> list1, Map.Entry<String, Integer> list2){
				return (list1.getValue()).compareTo(list2.getValue());
			}
		});

		HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
		for(Map.Entry<String,Integer> element : myThemes){
			temp.put(element.getKey(), element.getValue());
		}
		//themeMap.replaceAll(temp);
//		themeMap.clear();
//		themeMap.putAll(temp);
		List<String> value = new ArrayList<>(temp.keySet());
		highestTheme = value.get(value.size()-1);

	}
	public static String getHighestVotedTheme(){
		return highestTheme;
	}

	public static void customizeInventory(){

		List value = new ArrayList(themeMap.keySet());
		myInventory.clear();
		for(int i=0; i<54; i++){
			int k = i+1;
//			ItemStack item = ItemCreator.of(CompMaterial.ACACIA_SIGN,
//					String.valueOf(VoteMenu.themeMap.get(0)),
//					"A Theme")
//					.build().make();

			//Trying to hardcode and set the value of first item in inventory
			//Does not work for some reason

			ItemStack item = ItemCreator.of(CompMaterial.ACACIA_SIGN,
					"ROLLER_COASTER",
					"A Theme")
					.build().make();
			//myInventory.remove(Material.IRON_SWORD);
//			myInventory.clear();
			myInventory.setItem(0, item);

			if(k%9==0){
				ItemStack stack = ItemCreator.of(CompMaterial.PAPER,"Vote Here!", "Click this to vote").build().make();
				myInventory.setItem(k-1, new ItemStack(stack));
			}
			else if((k-1)%9==0){
				// When i is 1,10,19,28,37...
				// (i-1)/9 will either return 0,1,2,3...
//				ItemStack item = ItemCreator.of(CompMaterial.ACACIA_SIGN,
//						String.valueOf(VoteMenu.themeMap.get(0)),
//						"A Theme")
//						.build().make();
//				myInventory.setItem(0, item);
				if(k!=-1) {
					ItemStack stack = ItemCreator.of(CompMaterial.ACACIA_SIGN,
							//String.valueOf(VoteMenu.themeMap.get((k - 1) / 9)),
							(String) value.get((k-1)/9),
							"A build Theme with value" + themeMap.get((String)value.get((k-1)/9)))
							.build().make();
					myInventory.setItem(k-1, stack);
					//themeMap.get()
				}
				else{
					ItemStack stack = ItemCreator.of(CompMaterial.ACACIA_SIGN,
							String.valueOf(VoteMenu.themeMap.get(0)),
							"A Theme")
							.build().make();
					myInventory.setItem(0, stack);
				}
				//super.getItemAt(i).setType(Material.LEGACY_SIGN);
//				super.getItemAt(i).setType(stack.getType());
				//getItemAt(i).setType(stack.getType());
			}
			else {
				//if (k != 54) {
//				super.getItemAt(i).setType(Material.RED_STAINED_GLASS_PANE);
				//getItemAt(i).setType(Material.RED_STAINED_GLASS_PANE);
				//	if(!notClick)
				myInventory.setItem(k-1, ItemCreator.of(CompMaterial.RED_STAINED_GLASS_PANE).build().make());
				//}
			}
		}

	}



//	public static void openVoteMenu(BuildBattleArena arena, MenuItem menuInventory, Player player){
//
//		new VoteMenu(arena, menuInventory,player);
////		loadThemes();
////		myInventory = Bukkit.createInventory(null, 9*6,"Vote Inventory");
//////		customizeInventory();
////
////		customizeInventory();
////		player.openInventory(myInventory);
////
////		playerMap.put(player.getUniqueId(), " ");
//	}

	public  void openVoteMenu(BuildBattleArena arena, MenuItem menuInventory){

		//new VoteMenu(arena, menuInventory,player);
		new VoteMenu(arena,menuInventory);
//		loadThemes();
//		myInventory = Bukkit.createInventory(null, 9*6,"Vote Inventory");
////		customizeInventory();
//
//		customizeInventory();
//		player.openInventory(myInventory);
//
//		playerMap.put(player.getUniqueId(), " ");
	}

	//Initially Sets the Menu
	private void updateMenu(){

		//Not required anymore
	}

	//Override
	public ItemStack setItemAtPos(final int slot) {

//		if (slot == 9 * 1 + 3)
//			return monstersButton.getItem();
//
//		if (slot == 9 * 1 + 5)
//			return intensityButton.getItem();
//
//		return null;
		return new ItemStack(Material.RED_STAINED_GLASS_PANE);

	}




	public void resetThemes(){
		//TODO

	}

	public static MenuItem getMenuItems(){
		return new MenuItem();
	}

	public void loadThemes(){

		//Add the themes to the map

		themeMap.put(Themes.CAR.name(), 0);
		themeMap.put(Themes.CASTLE.name(), 0);
		themeMap.put(Themes.FOOTBALL_FIELD.name(), 0);
		themeMap.put(Themes.ROLLER_COASTER.name(), 0);
		themeMap.put(Themes.HOUSE.name(), 0);
		themeMap.put(Themes.TRUCK.name(), 0);


	}

	@Override
	protected void onDisplay(InventoryDrawer drawer) {
		super.onDisplay(drawer);
	}

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

	//Not using this class anymore
	private static class MenuItem{

		public static Inventory myInventory = Bukkit.createInventory(null, 9*6, "Voting Inventory");


		//static{
		public MenuItem(){
			for(int i=1; i<=54; i++){
				if(i%9==0){
					//super.getItemAt(i).setType(Material.PAPER);
					//	getItemAt(i).setType(Material.PAPER);
					ItemStack stack = ItemCreator.of(CompMaterial.SIGN,"Vote Here!", "Click this to vote").build().make();
					myInventory.setItem(i, new ItemStack(stack));
				}
				else if((i-1)%9==0 ){
					// When i is 1,10,19,28,37...
					// (i-1)/9 will either return 0,1,2,3...
//					ItemStack stack = ItemCreator.of(CompMaterial.SIGN,
//							VoteMenu.themeMap.get((i-1)/9),
//							"A build Theme")
//							.build().make();
					myInventory.setItem(i, null);
					//super.getItemAt(i).setType(Material.LEGACY_SIGN);
//				super.getItemAt(i).setType(stack.getType());
					//getItemAt(i).setType(stack.getType());
				}
				else {
//				super.getItemAt(i).setType(Material.RED_STAINED_GLASS_PANE);
					//getItemAt(i).setType(Material.RED_STAINED_GLASS_PANE);
					myInventory.setItem(i, ItemCreator.of(CompMaterial.RED_STAINED_GLASS_PANE).build().make());
				}
			}
		}
	}

	@RequiredArgsConstructor
	private enum Themes {

		CAR("CAR"),

		CASTLE("CASTLE"),

		HOUSE("HOUSE"),

		ROLLER_COASTER("ROLLER COASTER"),

		FOOTBALL_FIELD("FOOTBALL FIELD"),

		TRUCK("TRUCK");

		/**
		 * The title of the menu in this mode
		 */
		private final String themeName;
	}
}
