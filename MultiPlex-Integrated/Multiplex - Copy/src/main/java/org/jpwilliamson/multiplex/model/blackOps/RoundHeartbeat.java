package org.jpwilliamson.multiplex.model.blackOps;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.jpwilliamson.multiplex.model.ArenaJoinMode;
import org.jpwilliamson.multiplex.model.ArenaStopReason;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.TimeUtil;
import org.mineacademy.fo.model.Countdown;
import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.remain.Remain;

import java.util.List;
import java.util.Objects;

public class RoundHeartbeat extends Countdown {

	@Getter
	private BlackOpsArena arena;

	@Getter
	private Integer wave;

	@Getter
	private final MonsterRound monsterRound;

	protected RoundHeartbeat(SimpleTime time, BlackOpsArena arena, MonsterRound monsterRound) {
		super(time);

		this.arena = arena;
		this.monsterRound = monsterRound;
		wave = 0;
	}

	@Override
	protected void onStart() {
		for(Player player: arena.getPlayers(ArenaJoinMode.PLAYING)){
			Remain.sendTitle(player,"&3Round " + monsterRound.getName(), "&5Starts in " + TimeUtil.formatTimeGeneric(monsterRound.getTime().getTimeSeconds()));
		}
	}

	@Override
	protected void onTick() {

		int elapsedTime = getSecondsSinceStart();

		if(elapsedTime >= monsterRound.getTime().getTimeSeconds() && (elapsedTime % monsterRound.getSpawnTime().getTimeSeconds() == 0)){
			spawnWave();
			wave += 1;
		}

		if(numberOfMonsters() == 0){
			if(monsterRound.equals(MonsterRound.getLast())){
				cancel();
				arena.stopArena(ArenaStopReason.LAST_TEAM_STANDING);
			}
			else {
				cancel();
				wave = 0;
			}
		}
	}

	public void spawnWave(){

		List<Location> locations = arena.getSettings().getMonsterSpawnLocation().getLocations();

		for(Location location: locations){

			Location point = location.clone().add(0.5 * Math.random(), 1, 0.5 * Math.random());

			EntityType monster = RandomUtil.nextItem(monsterRound.getMonsters());

			Objects.requireNonNull(location.getWorld()).spawnEntity(point, monster);
		}
	}

	public int numberOfMonsters(){
		List<Entity> entities = arena.getSettings().getRegion().getEntities();
		int count = 0;

		for(Entity entity: entities){
			if(entity instanceof LivingEntity){
				if(entity instanceof Monster){
					count++;
				}
			}
		}
		return count;
	}

	@Override
	protected void onTickError(Throwable t) {
		arena.stopArena(ArenaStopReason.ERROR);
	}

	@Override
	protected void onEnd(){
	}
}
