package org.jpwilliamson.arena.model.buildbattle;

import org.bukkit.entity.Player;
import org.jpwilliamson.arena.menu.VoteMenu;
import org.jpwilliamson.arena.model.team.TeamArenaScoreboard;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.Replacer;

public class BuildBattleScoreBoard extends TeamArenaScoreboard {

	public BuildBattleScoreBoard(final BuildBattleArena arena){
		super(arena);
	}

	@Override
	protected String replaceVariablesLate(Player player, String message) {
		message = super.replaceVariablesLate(player, message);
		message = Replacer.of(message).replaceAll("remaining_build_time"
				, getArena().getHeartbeat().getTimeLeft() <= 59 ?
						Common.plural(0,"second"):
						Common.plural(getArena().getHeartbeat().getTimeLeft()-60, "second"));
		return message;
	}

	@Override
	public void onStart() {
		super.onStart();

		String highestVoted = VoteMenu.getHighestVotedTheme();
		removeRow("Time left");
		addRows("Build Time left:{remaining_build_time}");
		addRows("Highest Voted Theme: " + highestVoted);
	}

	@Override
	public BuildBattleArena getArena() {
		return (BuildBattleArena) super.getArena();
	}
}
