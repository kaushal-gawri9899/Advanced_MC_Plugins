package org.jpwilliamson.arena.model.buildbattle;

import lombok.Getter;
import org.bukkit.Location;
import org.jpwilliamson.arena.model.ArenaTeam;
import org.jpwilliamson.arena.model.ArenaTeamPoints;
import org.jpwilliamson.arena.model.team.TeamArenaSettings;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.SimpleTime;

import java.util.List;

public class BuildBattleSettings extends TeamArenaSettings {

	@Getter
	private SimpleTime votingDuration;

	private ArenaTeamPoints votingPoints;

	@Getter
	private SimpleTime buildDuration;

	@Getter
	private SimpleTime practiceDuration;

	@Getter
	private SimpleTime practiceGameDuration;

	@Getter
	private SimpleTime votingPracticeDuration;

	@Getter
	private SimpleTime teamDuration;

	@Getter
	private SimpleTime teamGameDuration;

	@Getter
	private SimpleTime teamVotingDuration;

	public BuildBattleSettings(final BuildBattleArena arena){
		super(arena);
	}

	@Override
	protected void onLoadFinish() {
		super.onLoadFinish();


		setGameDuration(SimpleTime.fromSeconds(360));
		this.votingPoints = new ArenaTeamPoints(this, getMap("Voting_Points"));
		this.votingDuration = getTime("Voting_Duration", "60 seconds");
		this.buildDuration = getTime("Build_Duration", "5 minutes");
		this.practiceDuration = getTime("Practice_Duration", "2 minutes");
		this.practiceGameDuration = getTime("Practice_GameDuration", "2 minutes");
		this.votingPracticeDuration = getTime("Practice_VoteDuration", "0 minutes");
		this.teamDuration = getTime("Team_Duration", "5 minutes");
		this.teamGameDuration = getTime("Team_GameDuration", "6 minutes");
		this.teamVotingDuration = getTime("Team_VotingDuration", "60 seconds");
		Valid.checkBoolean(getVotingDuration().getTimeSeconds() + getBuildDuration().getTimeSeconds() == getGameDuration().getTimeSeconds()
				,"Game Duration must be equal to Voting Duration + Build Duration");
	}

	public void setVotingPoints(ArenaTeam team, Location location) {
		votingPoints.setPoint(team, location);
	}

	public void addVotinPoints(ArenaTeam team, Location location) {
		votingPoints.addPoint(team, location);
	}

	public void removeVotingPoints(ArenaTeam team, Location location){
		votingPoints.removePoint(team,location);
	}

	public boolean hasVotingPoints(Location location){
		return votingPoints.hasPoint(location);
	}

	public ArenaTeam findVotingTeam(Location location){
		return votingPoints.findTeam(location);
	}

	public Location findVotingPoint(ArenaTeam team){
		return votingPoints.findPoint(team);
	}

	public void setVotingDuration(SimpleTime votingDuration) {
		this.votingDuration = votingDuration;
		save();
	}

	public List<Location> getVotingPoints() {
		return votingPoints.getLocations();
	}

	public void setBuildDuration(SimpleTime buildDuration) {
		this.buildDuration = buildDuration;
		save();
	}

	@Override
	public SerializedMap serialize() {
		SerializedMap map = super.serialize();

		map.putArray("Voting_Duration", votingDuration,
				"Build_Duration", buildDuration,
				"Voting_Points", votingPoints,
				"Practice_Duration", practiceDuration,
				"Practice_GameDuration", practiceGameDuration,
				"Team_Duration", teamDuration,
				"Team_GameDuration", teamGameDuration,
				"Team_VotingDuration", teamVotingDuration);

		return map;
	}

	public boolean isVotingTime(){
		return getArena().getHeartbeat().getTimeLeft() < 60;
	}
}
