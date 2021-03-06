package org.jpwilliamson.sumoarena.model.tnt;

import dev.lone.itemsadder.api.FontImages.TexturedInventoryWrapper;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jpwilliamson.sumoarena.ArenaPlugin;
import org.jpwilliamson.sumoarena.model.*;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.model.RandomNoRepeatPicker;
import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.remain.CompProperty;

import java.awt.print.Book;
import java.util.*;

public class TntTagArena extends Arena {

	public static final String TYPE = "tnt_tag";

	public Map<UUID,Boolean> playersTag = new HashMap<>();

	public int explosionTime = 20;
	/**
	 * Create a new arena. If the arena settings do not yet exist,
	 * they are created automatically.
	 *
	 * @param name
	 */
	public TntTagArena(final String name) {
		super(TYPE, name);
	}

	@Override
	public TntTagSettings getSettings() {
		return (TntTagSettings) super.getSettings();
	}

	@Override
	public TntTagHeartbeat getHeartbeat() {
		return (TntTagHeartbeat) super.getHeartbeat();
	}

	@Override
	public TntTagScoreboard getScoreboard() {
		return (TntTagScoreboard)super.getScoreboard();
	}

	@Override
	public TntTagSettings createSettings() {
		return new TntTagSettings(this);
	}

	@Override
	public ArenaHeartbeat createHeartbeat() {
		return new TntTagHeartbeat(this);
	}

	@Override
	public ArenaScoreboard createScoreboard() {
		return new TntTagScoreboard(this);
	}

	@Override
	public void onStart() {
		super.onStart();

		List<Player> playersInGame = getPlayers(ArenaJoinMode.PLAYING);

		for(Player player : playersInGame){
			playersTag.put(player.getUniqueId(),false);
			player.getInventory().clear();
			player.setFoodLevel(20);
			player.setHealth(20);
		}

		//Using RandomNoRepeatPicker to teleport all the players at random entrance locations
		//Such that no two players are teleported at same spot
		final RandomNoRepeatPicker<Location> locationPicker = RandomNoRepeatPicker.newPicker(Location.class);
		locationPicker.setItems(getSettings().getEntrances().getLocations());

		if (getState() != ArenaState.EDITED)
			forEachInAllModes((player) -> {
				final Location location = locationPicker.pickRandom(player);

				teleport(player, location);
			});



		//Build Logic To Mark Tag Players
		//Randomly assigning taggers such that number of taggers < totalPlayers/2
		placeTntTags();
		for(Player players : playersInGame){
			players.sendMessage("Sound will be played now");
			players.playSound(players.getLocation(),"itemsadder:ambient.creepy" , 1, 1);
		}
		//
		explosionTimer();

	}

	public void placeTntTags(){

		int tagPlayer = 1;

		List<Player> playersActive = getPlayers(ArenaJoinMode.PLAYING);
		ItemStack headWear = ItemsAdder.getCustomItem("jpwilliamson:Tnthead");
		ItemStack tntEquip = ItemsAdder.getCustomItem("jpwilliamson:TntBaton");
		//Define half of the players as taggers on game start
		while(tagPlayer <= (playersTag.size()/2)){

			broadcast(ChatColor.DARK_RED + "Taggers are now been defined!" + ChatColor.RESET);
			broadcast(ChatColor.RED +""+ChatColor.BOLD+ "There are " + (playersTag.size()/2) + " to start with!" + ChatColor.RESET);

			Random random = new Random();
			int index = random.nextInt(playersTag.size());
			Player playerTagged = playersActive.get(index);
			playersTag.get(playersActive.get(index).getUniqueId());
			for(Map.Entry<UUID,Boolean> entry : playersTag.entrySet()){
				if(playerTagged.getUniqueId().equals(entry.getKey()) && !entry.getValue()) {
					//Mark the tagger with value true
					entry.setValue(true);
					//playerTagged.getInventory().setHelmet(new ItemStack(Material.TNT));
					//playerTagged.getInventory().setBoots(new ItemStack(Material.FEATHER));
					playerTagged.getInventory().setHelmet(headWear);
					playerTagged.updateInventory();
					playerTagged.getInventory().addItem(new ItemStack(Material.TNT, 500));
					playerTagged.updateInventory();

					//Give sword to tagger for pvp
				//	playerTagged.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
					//playerTagged.getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
					playerTagged.getInventory().setItemInMainHand(tntEquip);
					playerTagged.sendMessage(ChatColor.DARK_RED + "You've TNT attached! Tag someone else before the times run out");
				}
				}
			tagPlayer++;
			}

			if(playersTag.size()==1){
				broadcast(ChatColor.DARK_RED + "Taggers are now been defined!" + ChatColor.RESET);
				broadcast(ChatColor.RED +""+ChatColor.BOLD+ "There are " + (playersTag.size()) + " to start with!" + ChatColor.RESET);
				Player playerTagged = playersActive.get(0);
				for(Map.Entry<UUID,Boolean> entry : playersTag.entrySet()){
					if(playerTagged.getUniqueId().equals(entry.getKey()) && !entry.getValue()) {
						//Mark the tagger with value true
						entry.setValue(true);
						//playerTagged.getInventory().setHelmet(new ItemStack(Material.TNT));
						playerTagged.getInventory().setHelmet(headWear);
						playerTagged.updateInventory();
						playerTagged.getInventory().addItem(new ItemStack(Material.TNT, 500));
						playerTagged.updateInventory();

						//Give sword to tagger for pvp
						//playerTagged.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
						playerTagged.getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
						playerTagged.sendMessage(ChatColor.DARK_RED + "You've TNT attached! Tag someone else before the times run out");
					}
				}

				Boat boat = Objects.requireNonNull(playerTagged.getLocation().getWorld()).spawn(playerTagged.getLocation().clone().add(0, 0.5, 0),Boat.class);
				boat.setGravity(false);
//				Vector vector = new Vector(playerTagged.getLocation().getX()+1, playerTagged.getLocation().getY(), playerTagged.getLocation().getZ());
//				boat.setVelocity(vector);

			}
		}


	@Override
	public void onPvP(Player attacker, Player victim, EntityDamageByEntityEvent event) {
		//super.onPvP(attacker, victim, event);
		ItemStack headWear = ItemsAdder.getCustomItem("jpwilliamson:Tnthead");
		ItemStack tntEquip = ItemsAdder.getCustomItem("jpwilliamson:TntBaton");
		for(Map.Entry<UUID,Boolean> entry : playersTag.entrySet()){

			//If the victim also has TNT
			//Cancel the damage event
			if(victim.getUniqueId().equals(entry.getKey()) && entry.getValue()){
				event.setCancelled(true);
				attacker.sendMessage(ChatColor.GREEN + "The player you're damaging already has TNT!");
				return;
			}

			//Check If Victim is not a tagger
			//Make him a tagger
			if(victim.getUniqueId().equals(entry.getKey() )&& !entry.getValue()){
				entry.setValue(true);
			}
			//If the attacker is a tagger
			//Switch the TNT to victim
			//Remove helmet from the attacker
			if(attacker.getUniqueId().equals(entry.getKey()) && entry.getValue()){
				entry.setValue(false);
				attacker.getInventory().setHelmet(null);
				attacker.getInventory().setItemInMainHand(null);
				attacker.getInventory().clear();
				attacker.sendMessage(ChatColor.DARK_AQUA + "You've tagged" + ChatColor.DARK_RED + victim.getDisplayName());
				attacker.sendMessage(ChatColor.DARK_GREEN + "You're no longer a tagger! Run to save yourself, you've 10 seconds to move away!");
				attacker.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,10,0,false,false));


				victim.sendMessage(ChatColor.RED + "You've been tagged!");
				//victim.getInventory().setHelmet(new ItemStack(Material.TNT));
				victim.getInventory().setHelmet(headWear);
				victim.getInventory().addItem(new ItemStack(Material.TNT,500));
				victim.playSound(victim.getLocation(), Sound.ENTITY_TNT_PRIMED, 1, 1);
				victim.updateInventory();
				victim.getInventory().setItemInMainHand(tntEquip);
				//victim.getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
				//victim.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
				victim.sendMessage(ChatColor.DARK_PURPLE + "Wait for 5 seconds before you make a move!");
				victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5,10,false,false));
			}
		}
	}

	public void switchToSpectate(Player player){

		PlayerUtil.normalize(player, true);

		//Provide invisibility to players spectating
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,getHeartbeat().getTimeLeft(),0,false,false));

		player.setGameMode(GameMode.SPECTATOR);
		player.setAllowFlight(true);
		player.setFlying(true);

		final List<ArenaPlayer> alivePlayers = getArenaPlayers(ArenaJoinMode.PLAYING);
		Valid.checkBoolean(!alivePlayers.isEmpty(),"Cannot spectate when there are no alive players found! Found: " + alivePlayers);

		teleport(player, RandomUtil.nextItem(alivePlayers).getPlayer().getLocation().add(1, 0, 1));

	}
	//A method using runnable, checks for explosion time left in
	public void explosionTimer(){

		Common.runTimer(0, 20, new BukkitRunnable() {
			@Override
			public void run() {
				if(explosionTime>0){
					explosionTime--;
				}
				else{

				}
			}
		});
	}


	/**
	 * @see Arena#onLeave(org.bukkit.entity.Player, ArenaLeaveReason)
	 */
	@Override
	protected void onLeave(final Player player, final ArenaLeaveReason reason) {
		super.onLeave(player, reason);

		checkLastStanding();
	}

	/**
	 * Run the last standing check when a player enters spectate mode, stop
	 * arena if only 1 player is left playing
	 *
	 */
	@Override
	protected void onSpectateStart(final Player player, final ArenaLeaveReason reason) {
		super.onSpectateStart(player, reason);

		checkLastStanding();
	}


	/*
	 * Check if there is only one last player in the playing mode then stop the arena
	 * and announce winner
	 */
	private void checkLastStanding() {
		if (getPlayers(ArenaJoinMode.PLAYING).size() == 1 && !isStopping()) {
			final Player winner = getPlayers(ArenaJoinMode.PLAYING).get(0);

			leavePlayer(winner, ArenaLeaveReason.LAST_STANDING);
		}
	}


	/**
	 * @see Arena#canSpectateOnLeave(org.bukkit.entity.Player)
	 */
	@Override
	protected boolean canSpectateOnLeave(final Player player) {
		return getPlayers(ArenaJoinMode.PLAYING).size() > 1;
	}

	@Override
	protected boolean hasPvP() {
		return true;
	}

	@Override
	protected void onEntityClick(Player player, Entity clicked, PlayerInteractAtEntityEvent event) {
		//super.onEntityClick(player, clicked, event);

		if(clicked instanceof Boat){
			player.getInventory().addItem(new ItemStack(Material.EMERALD));
		}
	}

	public void updateEntityLocation(Player player){

	}
}
