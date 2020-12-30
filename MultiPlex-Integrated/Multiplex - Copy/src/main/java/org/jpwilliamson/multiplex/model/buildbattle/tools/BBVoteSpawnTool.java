package org.jpwilliamson.multiplex.model.buildbattle.tools;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jpwilliamson.multiplex.model.ArenaTeam;
import org.jpwilliamson.multiplex.model.buildbattle.BuildBattleArena;
import org.jpwilliamson.multiplex.model.buildbattle.BuildBattleSettings;
import org.jpwilliamson.multiplex.tool.VisualTool;
import org.jpwilliamson.multiplex.util.Constants;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.tool.Tool;
import org.mineacademy.fo.remain.CompColor;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;

import java.util.List;

public class BBVoteSpawnTool extends VisualTool<BuildBattleArena> {

	@Getter
	private static final Tool instance = new BBVoteSpawnTool();

	private BBVoteSpawnTool(){
		super(BuildBattleArena.class);
	}

	@Override
	protected String getBlockName(Block block, Player player, BuildBattleArena arena) {
		final ArenaTeam team = findTeam(block.getLocation(), arena);

		return "[" + team.getColor() + team.getName() + " Vote SpawnPoint&f]";
	}

	@Override
	protected CompMaterial getBlockMask(Block block, Player player, BuildBattleArena arena) {
		final ArenaTeam team = findTeam(block.getLocation(), arena);

		return CompColor.toWool(team.getColor());
	}

	@Override
	public ItemStack getItem() {
		return ItemCreator.of(CompMaterial.ANVIL,
				"&lVOTE TEAM SPAWN TOOL",
				"",
				"Click a block to set",
				"Click air to switch teams")
				.build().makeMenuTool();
	}

	@Override
	protected void handleBlockClick(Player player, BuildBattleArena arena, ClickType click, Block block) {
		final ArenaTeam team = findTeam(player);

		if(team == null){
			Messenger.error(player, "Could not find the team to set the voting point. Right/left click air to select one");
			return;
		}

		final BuildBattleSettings settings = arena.getSettings();
		final ArenaTeam oldteam = settings.findVotingTeam(block.getLocation());

		if(oldteam != null && !oldteam.equals(team)){
			Messenger.error(player, "This block is already set as spawnpoint for the " + oldteam.getName() + " team.");

			return;
		}

		settings.setVotingPoints(team, block.getLocation().subtract(arena.getReferenceLocation()));
		Messenger.success(player, "Placed a Vote spawnpoint for the " + team.getColor() + team.getName() + "&7team.");
	}

	@Override
	protected void handleAirClick(Player player, BuildBattleArena arena, ClickType click) {
		final List<ArenaTeam> teams = ArenaTeam.getTeams();

		if(teams.isEmpty()){
			Messenger.error(player, "There are no teams created yet. Use '/arena teams' to create some.");

			return;
		}

		final ArenaTeam team = findTeam(player);
		final ArenaTeam next = Common.getNext(team, teams, click == ClickType.RIGHT);

		CompMetadata.setTempMetadata(player, Constants.Tag.TEAM_TOOL, next.getName());
		Messenger.success(player, "&7" + (click == ClickType.RIGHT ? ">>" : "<<") + " Now placing spawnpoints for the " + next.getColor() + next.getName() + " &7team.");
	}

	private ArenaTeam findTeam(final Player player) {
		if (CompMetadata.hasTempMetadata(player, Constants.Tag.TEAM_TOOL)) {
			final String teamName = CompMetadata.getTempMetadata(player, Constants.Tag.TEAM_TOOL).asString();

			return ArenaTeam.findTeam(teamName);
		}

		return null;
	}

	private ArenaTeam findTeam(final Location location, final BuildBattleArena arena) {
		final ArenaTeam team = arena.getSettings().findVotingTeam(location);
		Valid.checkNotNull(team, "Spawnpoint at " + Common.shortLocation(location) + " refers to an unknown team!");

		return team;
	}

	@Override
	protected List<Location> getVisualizedPoints(final BuildBattleArena arena) {
//		return arena.getSettings().getVotingPoints();
		List<Location> updateLocationList = (arena.getSettings()).getVotingPoints();
		for(Location location : updateLocationList){
			location.add(arena.getReferenceLocation());
		}
		return updateLocationList;
	}

	@Override
	protected boolean autoCancel() {
		return true;
	}
}
