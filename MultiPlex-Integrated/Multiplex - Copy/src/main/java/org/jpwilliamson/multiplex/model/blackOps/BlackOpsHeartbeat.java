package org.jpwilliamson.multiplex.model.blackOps;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.jpwilliamson.multiplex.model.ArenaHeartbeat;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.List;

public class BlackOpsHeartbeat extends ArenaHeartbeat {

	@Setter
	@Getter
	private Integer currentRound = 0;

	@Getter
	private Integer numberOfRounds;

	@Getter
	private RoundHeartbeat heartbeat;

	private MonsterRound currentMonsterRound ;

	public BlackOpsHeartbeat(final BlackOpsArena arena){
		super(arena);

		numberOfRounds = MonsterRound.getRounds().size();
	}

	@Override
	protected void onTickError(Throwable t) {
		super.onTickError(t);
	}

	@Override
	protected void onTick() {
		if(!heartbeat.isRunning() && heartbeat.numberOfMonsters() == 0){
			giveRareItems();
			currentRound += 1;
			currentMonsterRound = MonsterRound.findRound(currentRound);

			assert currentMonsterRound != null;
			SimpleTime time = MonsterRound.getTotalTime(currentMonsterRound);
			heartbeat = new RoundHeartbeat(time,getArena(), currentMonsterRound);
			heartbeat.launch();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		spawnChest();

		currentRound += 1;
		currentMonsterRound = MonsterRound.findRound(currentRound);

		if(currentMonsterRound == null)
			Common.log("Monster Round is null");

		SimpleTime time = MonsterRound.getTotalTime(currentMonsterRound);
		heartbeat = new RoundHeartbeat(time,getArena(), currentMonsterRound);
		heartbeat.launch();
	}

	public void spawnChest(){
		List<Location> locations = getArena().getSettings().getChestLocations().getLocations();

		for(Location location: locations){
			location.clone().add(0,1,0).getBlock().setType(CompMaterial.CHEST.getMaterial());

			Chest chest = (Chest) location.clone().add(0,1,0).getBlock().getState();
			getArena().getChests().add(chest);
		}
	}

	public void giveRareItems(){
		for(Chest chest: getArena().getChests()){
			chest.getBlockInventory().clear();
			chest.getBlockInventory().addItem(RandomUtil.nextItem(getArena().getSettings().getRareItems()));
			chest.getBlockInventory().addItem(RandomUtil.nextItem(getArena().getSettings().getRareItems()));
		}
	}

	@Override
	protected void onEnd() {
	}

	@Override
	public BlackOpsArena getArena() {
		return (BlackOpsArena) super.getArena();
	}
}
