package org.jpwilliamson.multiplex.model.buildbattle;

import lombok.Getter;
import org.bukkit.Location;
import org.jpwilliamson.multiplex.model.ArenaTeam;
import org.jpwilliamson.multiplex.model.ArenaTeamPoints;
import org.jpwilliamson.multiplex.model.team.TeamArenaSettings;
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

	public BuildBattleSettings(final BuildBattleArena arena){
		super(arena);
	}

	@Override
	protected void onLoadFinish() {
		super.onLoadFinish();

		this.votingPoints = new ArenaTeamPoints(this, getMap("Voting_Points"));
		this.votingDuration = getTime("Voting_Duration", "60 seconds");
		this.buildDuration = getTime("Build_Duration", "5 minutes");

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
				"Voting_Points", votingPoints);

		return map;
	}

	public boolean isVotingTime(){
		return getArena().getHeartbeat().getTimeLeft() < 60;
	}
}
