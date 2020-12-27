package org.jpwilliamson.sumoarena.model.sumo;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.util.Vector;
import org.jpwilliamson.sumoarena.menu.ShopKeeperMenu;
import org.jpwilliamson.sumoarena.model.Arena;
import org.jpwilliamson.sumoarena.model.ArenaJoinMode;
import org.mineacademy.fo.collection.StrictMap;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompProperty;

import java.util.*;

public class SumoArena extends Arena {

	// type of arena
	public static final String TYPE = "sumo";

	public Map<UUID, Integer> playerRoundInfo = new HashMap<>();

	public boolean isFighting = false;

	public List<Player> fightingPlayers = new ArrayList<>();

	// constructor to initialize the arena
	public SumoArena(final String name){
		super(TYPE, name);
	}

	// create settings for the sumo arena
	@Override
	protected SumoSettings createSettings() {
		return new SumoSettings(this);
	}

	// sumoArena HeartBeat
	@Override
	protected SumoHeartBeat createHeartbeat() {
		return new SumoHeartBeat(this);
	}

	// SumoArena Scoreboard
	@Override
	protected SumoScoreBoard createScoreboard() {
		return new SumoScoreBoard(this);
	}

	@Override
	protected void onStart() {
		super.onStart();

		if(isEdited())
			return;

		List<Player> players = this.getPlayers(ArenaJoinMode.PLAYING);
		players.get(0).getWorld().setStorm(false);
		players.get(0).getWorld().setTime(300);

		for(Player player: players) {
			playerRoundInfo.put(player.getUniqueId(), -1);
			player.teleport(getSettings().getSumoStadium());
			player.getInventory().addItem(CompMaterial.IRON_INGOT.toItem());
		}

		for (final Location villagerLocation : getSettings().getShopKeepers()) {
			final Villager villager = Objects.requireNonNull(villagerLocation.getWorld()).spawn(villagerLocation.clone(), Villager.class);

			CompProperty.INVULNERABLE.apply(villager, true);
			villager.setVelocity(new Vector(0,0,0));
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		playerRoundInfo.clear();
	}

	@Override
	public SumoSettings getSettings() {
		return (SumoSettings) super.getSettings();
	}

	@Override
	public SumoScoreBoard getScoreboard() {
		return (SumoScoreBoard) super.getScoreboard();
	}

	@Override
	public SumoHeartBeat getHeartbeat() {
		return (SumoHeartBeat)super.getHeartbeat();
	}

	@Override
	protected void onEntityClick(Player player, Entity clicked, PlayerInteractAtEntityEvent event) {
		super.onEntityClick(player, clicked, event);

		if (clicked instanceof Villager)
			ShopKeeperMenu.openPurchaseMenu(this, player);
	}

	@Override
	protected void onPlayerDamage(Player attacker, Entity victim, EntityDamageByEntityEvent event) {
		super.onPlayerDamage(attacker, victim, event);

		if(victim instanceof Player){
			attacker.sendMessage(ChatColor.DARK_RED + "Can't Damage each other in the arena");
			event.setCancelled(true);
		}
	}
}
