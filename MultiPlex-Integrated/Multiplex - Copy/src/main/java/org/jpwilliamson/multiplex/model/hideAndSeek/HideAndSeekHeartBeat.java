package org.jpwilliamson.multiplex.model.hideAndSeek;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jpwilliamson.multiplex.model.ArenaHeartbeat;


public class HideAndSeekHeartBeat extends ArenaHeartbeat {
	/**
	 * Create a new countdown
	 *
	 * @param arena
	 */
	private HideAndSeekArena hidearena;
	final double shrinkingFactor;
	final int shrinkingDuration;
	@Setter
	private int shrinkTime;

	public HideAndSeekHeartBeat(final HideAndSeekArena arena) {
		super(arena);
		this.hidearena=arena;
		shrinkingFactor=0.80;
		shrinkingDuration=10;
	}

	@Override
	protected void onTick() {
		final int elapsedSeconds = getCountdownSeconds() - getTimeLeft();

		if(elapsedSeconds%60==0){
			((HideAndSeekArena)getArena()).getSettings().setIsShrinking(true);
			getArena().broadcastInfo(ChatColor.WHITE + "" + ChatColor.ITALIC + "The Arena Border will shrink to" + (int)(this.shrinkingFactor*100) + "% in" + this.shrinkingDuration + " seconds!");
			shrinkArenaBorder();
		}

		if (elapsedSeconds <= 60) {
			setShrinkTime(60 - elapsedSeconds);
		} else if (elapsedSeconds <= 120 && elapsedSeconds > 60) {
			setShrinkTime(120 - elapsedSeconds);
		} else if (elapsedSeconds <= 180 && elapsedSeconds > 120) {
			setShrinkTime(180 - elapsedSeconds);
		} else if (elapsedSeconds <= 240 && elapsedSeconds > 180) {
			setShrinkTime(240 - elapsedSeconds);
		} else if (elapsedSeconds <= 300 && elapsedSeconds > 240) {
			setShrinkTime(300 - elapsedSeconds);
		} else {
			setShrinkTime(360 - elapsedSeconds);
		}
	}

	public int getShrinkTime(){
		return this.shrinkTime;

	}
	public void shrinkArenaBorder(){
	    for(Player player : hidearena.getPlayersInGame()){
		player.getWorld().getWorldBorder().setSize(player.getWorld().getWorldBorder().getSize()*this.shrinkingFactor,shrinkingDuration);
		}
	}
}
