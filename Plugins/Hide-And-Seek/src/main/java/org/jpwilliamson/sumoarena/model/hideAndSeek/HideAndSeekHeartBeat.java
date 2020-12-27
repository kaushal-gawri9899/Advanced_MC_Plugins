package org.jpwilliamson.sumoarena.model.hideAndSeek;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jpwilliamson.sumoarena.model.Arena;
import org.jpwilliamson.sumoarena.model.ArenaHeartbeat;

public class HideAndSeekHeartBeat extends ArenaHeartbeat {
	/**
	 * Create a new countdown
	 *
	 * @param arena
	 */
	private HideAndSeekArena hidearena;
	final double shrinkingFactor;
	final int shrinkingDuration;

	public HideAndSeekHeartBeat(final HideAndSeekArena arena) {
		super(arena);
		this.hidearena=arena;
		shrinkingFactor=0.80;
		shrinkingDuration=10;
	}

	@Override
	protected void onTick() {
		final int elapsedSeconds = getCountdownSeconds() - getTimeLeft();

		if(elapsedSeconds%30==0){
			((HideAndSeekArena)getArena()).getSettings().setIsShrinking(true);
			getArena().broadcastInfo(ChatColor.WHITE + "" + ChatColor.ITALIC + "The Arena Border will shrink to" + (int)(this.shrinkingFactor*100) + "% in" + this.shrinkingDuration + " seconds!");
			shrinkArenaBorder();
		}
	}

	public void shrinkArenaBorder(){
	    for(Player player : hidearena.getPlayersInGame()){
		player.getWorld().getWorldBorder().setSize(player.getWorld().getWorldBorder().getSize()*this.shrinkingFactor,shrinkingDuration);
		}
	}
}
