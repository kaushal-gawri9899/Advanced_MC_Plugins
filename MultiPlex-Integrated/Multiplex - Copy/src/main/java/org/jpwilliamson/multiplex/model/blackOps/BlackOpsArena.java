package org.jpwilliamson.multiplex.model.blackOps;

import io.github.bananapuncher714.cartographer.core.Cartographer;

import io.github.bananapuncher714.cartographer.core.api.WorldCursor;
import io.github.bananapuncher714.cartographer.core.api.map.WorldCursorProvider;
import io.github.bananapuncher714.cartographer.core.map.Minimap;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCursor;
import org.jpwilliamson.multiplex.model.*;
import org.mineacademy.fo.*;
import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.remain.Remain;

import java.util.*;

public class BlackOpsArena extends Arena {

	/*
	* Game Rules And Structure
	* 1. A Number of Players will be teleported to a schematic region
	* 2. In this Game they will have a map with them
	* 3. they can see the other players as Green cursors in the map
	* 4. zombies will appear as red cursors
	* 5. There will be a set of rounds
	* 6. After every round a random items will appear in the loot chest -> loot chest will be marked with red cross in the map
	* 7. The motive is to clear all the zombie rounds and win the boss stage
	* 8. if a player dies he will be sent into the spectate mode
	* 9. In the arena food and energy items will spawn at random locations during the fight
	* 10. As the rounds progresses the number of zombies will also increases
	* 11. Players are not allowed to run away from the arena/ shoot other players/ break the blocks
	* 12. Different Guns will be used in the Game
	* 13. In the loot players will get potions of different types
	* 14. There will be rest time in between the rounds
	* 15. on Killing a Zombie player earn points also there will be some drop from the dead*/

	public static final String TYPE = "black_Ops";

	private Minimap minimap = null;

	@Getter
	private List<Chest> chests = new ArrayList<>(); // Loot chests

	@Getter
	private Map<Player, Integer> playerKills = new HashMap<>(); // Number of Zombies killed by the player

	private BlackOpsScoreboard scoreboard; // FeatherBoard supported scoreboard

	private List<WorldCursorProvider> providers = new ArrayList<>(); // map providers

	//----------------------------------------------------------------------------------------------------

	public BlackOpsArena(final String name){
		super(TYPE, name);

		this.scoreboard = GameScoreboard();

		getSettings().setLives(1);
		getSettings().setGameDuration(SimpleTime.from("30 minutes"));
	}

	public BlackOpsSettings createSettings(){
		return new BlackOpsSettings(this);
	}

	public BlackOpsHeartbeat createHeartbeat(){
		return new BlackOpsHeartbeat(this);
	}

	public BlackOpsScoreboard GameScoreboard(){
		return new BlackOpsScoreboard(this);
	}

	//-----------------------------------------------------------------------------------------------------

	@Override
	protected void onStart() {
		super.onStart();

		this.minimap = Cartographer.getInstance().getMapManager().constructNewMinimap("BlackOps");

		WorldCursorProvider provider = (player, minimap, playerSetting) -> {
			Set<WorldCursor> cursors = new HashSet<>();

			// Marking Player Locations
			for(Player player1: getPlayers(ArenaJoinMode.PLAYING)){
				WorldCursor cursor = new WorldCursor(player1.getLocation());

				cursor.setType(MapCursor.Type.GREEN_POINTER);

				cursors.add(cursor);
			}

			// Marking Chest Locations
			for(Location location: getSettings().getChestLocations()){
				WorldCursor cursor = new WorldCursor(location);

				cursor.setType(MapCursor.Type.RED_X);
				cursors.add(cursor);
			}

			return cursors;
		};

		minimap.register(provider);

		providers.add(provider);

		scoreboard.onStart();

		for(Player player: getPlayers(ArenaJoinMode.PLAYING)){
			scoreboard.showGameBoard(player);

			teleport(player,getSettings().getEntranceLocation());
			giveInitialTools(player);
			giveMap(player);
		}
	}

	@Override
	protected void onPlayerKill(final Player killer, final LivingEntity victim) {
		super.onPlayerKill(killer, victim);

		if (victim instanceof Monster) {
			final ArenaPlayer cache = ArenaPlayer.getCache(killer);
			final double points = MathUtil.formatTwoDigitsD(RandomUtil.nextBetween(10, 20) + Math.random());

			playerKills.replace(killer,playerKills.get(killer) + 1);

			cache.giveArenaPoints(killer, points);
			Messenger.warn(killer, "You received " + points + " points for killing " + Common.article(ItemUtil.bountifyCapitalized(victim.getType()))
					+ " and now have " + cache.getArenaPoints() + " points!");
		}
	}

	@Override
	protected Location getRespawnLocation(final Player player) {
		return getSettings().getEntranceLocation();
	}

	@Override
	protected void onLeave(final Player player, final ArenaLeaveReason reason) {
		super.onLeave(player, reason);

		scoreboard.stopShowing(player);

		playerKills.remove(player);

		checkLastStanding();
	}

	@Override
	protected void onSpectateStart(final Player player, final ArenaLeaveReason reason) {
		super.onSpectateStart(player, reason);

		player.getInventory().remove(Cartographer.getInstance().getMapManager().getItemFor(minimap));

		checkLastStanding();
	}

	private void checkLastStanding() {
		if (getPlayers(ArenaJoinMode.PLAYING).size() == 1 && !isStopping()) {
			final Player player = getPlayers(ArenaJoinMode.PLAYING).get(0);

			Remain.sendTitle(player,"&4ONE MAN ARMY","&2Kill them all");
		}
		else if(getPlayers(ArenaJoinMode.PLAYING).size() == 0 && !isStopping()) {
			stopArena(ArenaStopReason.ZOMBIES_WON);
		}
	}

	@Override
	protected void onJoin(Player player, ArenaJoinMode joinMode) {
		super.onJoin(player, joinMode);

		if(playerKills.size() == 0) {
			MonsterRound.loadRounds();
			player.getWorld().setTime(14000);
		}

		player.getInventory().setHelmet(new ItemStack(Material.LANTERN));

		getScoreboard().onPlayerLeave(player);
		scoreboard.onLobby();
		scoreboard.showLobbyBoard(player);

		playerKills.put(player,0);
	}

	@Override
	protected void onStop() {
		for(Player player: getPlayersInAllModes()) {
			scoreboard.stopShowing(player);
			player.getInventory().remove(Cartographer.getInstance().getMapManager().getItemFor(minimap));
		}

		super.onStop();

		clearChest();

		handleProviders();

		providers.clear();
		Cartographer.getInstance().getMapManager().remove(minimap);
		chests.clear();
		playerKills.clear();
		getHeartbeat().setCurrentRound(0);
	}

	//-----------------------------------------------------------------------------------------------

	@Override
	protected boolean hasLives() {
		return true;
	}

	@Override
	protected boolean hasClasses() {
		return false;
	}

	@Override
	protected boolean hasPvP() {
		return false;
	}

	//-----------------------------------------------------------------------------------------------

	// give basic initial tools to the player
	public void giveInitialTools(Player player){
		for(ItemStack itemStack: getSettings().getInitialItems()){
			if(itemStack != null)
				player.getInventory().addItem(itemStack);
		}
	}

	public void handleProviders(){
		for(WorldCursorProvider provider: providers)
			minimap.unregister(provider);
	}

	public void clearChest(){
		for(Chest chest: chests){
			chest.getBlockInventory().clear();
			chest.setType(Material.AIR);
		}
	}

	public void giveMap(Player player){
		ItemStack map = Cartographer.getInstance().getMapManager().getItemFor(minimap);

		player.getInventory().addItem(map);
	}

	//-------------------------------------------------------------------------------------------------

	@Override
	public BlackOpsSettings getSettings() {
		return (BlackOpsSettings) super.getSettings();
	}

	@Override
	public BlackOpsHeartbeat getHeartbeat() {
		return (BlackOpsHeartbeat) super.getHeartbeat();
	}
}
