package org.jpwilliamson.sumoarena.model.tnt;


import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jpwilliamson.sumoarena.model.Arena;
import org.jpwilliamson.sumoarena.model.ArenaHeartbeat;
import org.jpwilliamson.sumoarena.model.ArenaJoinMode;
import org.jpwilliamson.sumoarena.model.ArenaStopReason;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class TntTagHeartbeat extends ArenaHeartbeat {


	private int activeTaggers;

	private int explosionTime;
	/**
	 * Create a new countdown
	 *
	 * @param arena
	 */
	public TntTagHeartbeat(final TntTagArena arena) {
		super(arena);
	}

	@Override
	protected void onTick() {
		super.onTick();


		final int elapsedSeconds = getCountdownSeconds() - getTimeLeft();

		List<Player> inGamePlayers = getArena().getPlayers(ArenaJoinMode.PLAYING);
		//Explode the TNT after each minute passed in game

		Random random = new Random();
		int time = random.nextInt(60);
		if(elapsedSeconds<=60){
			setExplosionTime(60-elapsedSeconds);
		}
		else if(elapsedSeconds<=120 && elapsedSeconds>60){
			setExplosionTime(120-elapsedSeconds);
		}
		else if(elapsedSeconds<=180 && elapsedSeconds>120){
			setExplosionTime(180-elapsedSeconds);
		}
		else if(elapsedSeconds<=240 && elapsedSeconds>180){
			setExplosionTime(240-elapsedSeconds);
		}
		else if(elapsedSeconds<=300 && elapsedSeconds>240){
			setExplosionTime(300-elapsedSeconds);
		}
		else
		{
			setExplosionTime(360-elapsedSeconds);
		}
		if (elapsedSeconds % 60 == 0) {
			for (Map.Entry<UUID, Boolean> entry : getArena().playersTag.entrySet()) {
				if(entry.getValue()){
					entry.setValue(false);

					//It will take polynomial time
					//Trying to derive a solution that is faster
					for(Player player : inGamePlayers){
						if(player.getUniqueId().equals(entry.getKey())){
							Location explodeLocation = player.getLocation();
							explodeLocation.getWorld().createExplosion(explodeLocation.getX(), explodeLocation.getY(), explodeLocation.getZ(), 1,false,false);
							player.getInventory().setHelmet(null);
							//player.setGameMode(GameMode.SPECTATOR);
							getArena().switchToSpectate(player);
							getArena().playersTag.remove(player.getUniqueId());
						}

						player.sendMessage("BOOM! Players with TNT have been exploded!");
						player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE,1,1);
					}
				}
			}
		}

		//After ten seconds have been passed after the explosion
		//We again add the TNT taggers to the players left in Game if more than 1 player present
		if(elapsedSeconds%60==10 && elapsedSeconds!=10 && getArena().playersTag.size()>1){
			getArena().placeTntTags();
		}

		//Stop the Arena if only one player left
		//Though already handled in TntArena for
				// when player is added to spectate mode
				// or when only last player left

//		if(getArena().playersTag.size()==1) {
//			getArena().stopArena(ArenaStopReason.LAST_PLAYER_LEFT);
//		}

		int count =0;
		for(Map.Entry<UUID,Boolean> entry : getArena().playersTag.entrySet()){
			if(entry.getValue()){
				count++;
			}
		}

		setActiveTaggers(count);
		for(Map.Entry<UUID,Boolean> entry : getArena().playersTag.entrySet()){
			if(entry.getValue()){
				for(Player player : inGamePlayers){
					if(entry.getKey().equals(player.getUniqueId())){
						getArena().updateEntityLocation(player);
					}
				}
			}
		}


	}

	@Override
	public TntTagArena getArena() {
		return (TntTagArena) super.getArena();
	}

	public void setActiveTaggers(int count){
		this.activeTaggers=count;

	}

	public int getActiveTaggers(){
		return this.activeTaggers;
	}

	public void setExplosionTime(int time){
		this.explosionTime=time;
	}

	public int getExplosionTime(){
		return this.explosionTime;
	}
}

