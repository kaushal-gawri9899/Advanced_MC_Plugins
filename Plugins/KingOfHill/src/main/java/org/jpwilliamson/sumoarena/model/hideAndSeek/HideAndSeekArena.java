package org.jpwilliamson.sumoarena.model.hideAndSeek;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jpwilliamson.sumoarena.model.*;
import org.mineacademy.fo.model.RandomNoRepeatPicker;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompSound;
import org.mineacademy.fo.settings.YamlConfig;
//import sun.plugin.javascript.navig4.Layer;

import java.util.List;
import java.util.Random;

public class HideAndSeekArena extends Arena {

	public static final String TYPE = "hideAndSeek";

	@Getter
	private List<Player> playersInGame;
	@Getter
	public Player seeker;

	//	/**
//	 * Create a new arena. If the arena settings do not yet exist,
//	 * they are created automatically.
//	 *
//	 * @param type
//	 * @param name
//	 */
	public HideAndSeekArena(String name) {
		super(TYPE, name);
	}

	@Override
	protected void onStart() {
		super.onStart();
		//playersInGame = this.getPlayersInAllModes();
		playersInGame = this.getPlayers(ArenaJoinMode.PLAYING);
		//This will ensure no two players are spawned at same spot
		final RandomNoRepeatPicker<Location> locationPicker = RandomNoRepeatPicker.newPicker(Location.class);
		locationPicker.setItems(getSettings().getEntrances().getLocations());

		if(getState()!=ArenaState.EDITED){
			for(Player players : playersInGame){
				final Location location = locationPicker.pickRandom(players);
				teleport(players, location);
			}
		}
		this.seeker = this.playersInGame.get(new Random().nextInt(this.playersInGame.size()));
		this.seeker.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "You're the Seeker");
		for(Player player : playersInGame){
			if(!player.getUniqueId().equals(this.seeker.getUniqueId())){
				player.sendMessage(ChatColor.DARK_BLUE + "" + ChatColor.ITALIC  + this.seeker.getDisplayName() + " is the seeker, you've 30 seconds to hide");
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, getSettings().getHideTime(), 0, false,false));
				player.getInventory().clear();
			}
			else{
				player.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.WHITE + "Wait for " + getSettings().getHideTime() + " seconds before starting your search");
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,getSettings().getHideTime(),10,false,false));

				//Add items in seeker inventory to help him kill
				ItemStack item = new ItemStack(Material.GOLDEN_SWORD);
				item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 15);
				player.getInventory().setItem(0, item);
			}
		}
	}

	@Override
	protected void onPlayerKill(final Player killer, final LivingEntity victim) {
		//super.onPlayerKill(killer, victim);
		boolean isPresent = false;
		boolean isHider = false;

		if(victim instanceof Player) {
			if (playersInGame != null && getSettings().isGameRunning()) {
				for (Player player : playersInGame) {
					if (player.getUniqueId().equals(victim.getUniqueId())) {
						isPresent = true;
					}
				}
				if (isPresent) {
					if (!victim.getUniqueId().equals(seeker.getUniqueId())) {
						victim.sendMessage(ChatColor.RED + "You were found by the seeker");
						CompSound.ITEM_BREAK.play((Player) victim);
						((Player) victim).setGameMode(GameMode.SPECTATOR);
						victim.teleport(seeker.getLocation());
						for (Player player : playersInGame) {
							player.sendMessage(ChatColor.WHITE + " " + ChatColor.ITALIC + ((Player) victim).getDisplayName() + ChatColor.RESET + ChatColor.WHITE + "was found by the seeker");
						}
					}
					else{
						victim.teleport(getSettings().getStadiumLocation());
						for (Player player : playersInGame) {
							player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "The Seeker Got Killed");
							player.sendMessage("Seekers Won This Round");
							player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT,1));
							player.getInventory().addItem(new ItemStack(Material.DIAMOND,1));
							player.teleport(getSettings().getStadiumLocation());
						}

					}

					for(Player players : playersInGame){
						if(players.getGameMode()==GameMode.SURVIVAL && !players.getUniqueId().equals(seeker.getUniqueId())){
							isHider=true;
						}
					}

					if(!(isHider)){
						seeker.getInventory().addItem(new ItemStack(Material.DIAMOND,3));
						seeker.getInventory().addItem(new ItemStack(Material.TIPPED_ARROW,2));
						for(Player player : playersInGame){
							player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Seeker Found Everyone! " + seeker.getDisplayName() + " Won!");
							player.getInventory().clear();
						}
					}
				}
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		playersInGame.clear();
	}

	@Override
	public HideAndSeekSettings getSettings() {
		return (HideAndSeekSettings) super.getSettings();
	}

	@Override
	public HideAndSeekHeartBeat getHeartbeat() {
		return (HideAndSeekHeartBeat)super.getHeartbeat();
	}

	@Override
	public HideAndSeekScoreboard getScoreboard() {
		return (HideAndSeekScoreboard)super.getScoreboard();
	}

	@Override
	protected HideAndSeekSettings createSettings() {
		return new HideAndSeekSettings(this);
	}

	@Override
	protected HideAndSeekHeartBeat createHeartbeat() {
		return new HideAndSeekHeartBeat(this);
	}

	@Override
	protected ArenaScoreboard createScoreboard() {
		return new HideAndSeekScoreboard(this);
	}




}
