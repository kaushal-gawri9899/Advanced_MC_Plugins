package org.jpwilliamson.multiplex.model.blackOps;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.settings.YamlConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class MonsterRound extends YamlConfig {

	private static volatile List<MonsterRound> loadedRounds = new ArrayList<>();

	@Getter
	private String name;

	private ItemStack icon;

	@Getter
	private List<EntityType> monsters;

	@Getter
	private SimpleTime time;

	@Getter
	private Integer waves;

	@Getter
	private SimpleTime spawnTime;

	@Getter
	private Integer position;

	private MonsterRound(final String name) {
		this.name = name;

		loadConfiguration(NO_DEFAULT, "rounds/" + name + ".yml");
	}

	@Override
	protected void onLoadFinish() {
		this.icon = get("Icon", ItemStack.class);
		this.time = getTime("Time","10 seconds");
		this.waves = getInteger("Waves",0);
		this.monsters = getList("Monsters",EntityType.class);
		this.name = getString("Name");
		this.spawnTime = getTime("Spawn_Time","10 seconds");
		this.position = getInteger("Position",0);
	}

	public void setIcon(final ItemStack icon) {
		this.icon = icon;

		save();
	}

	public void setPosition(int position){
		this.position = position;

		save();
	}

	public void setWaves(final int waves){
		this.waves = waves;

		save();
	}

	public void setTime(String time){
		this.time = SimpleTime.from(time);

		save();
	}

	public void setName(String name){
		this.name = name;

		save();
	}

	public void addMonster(EntityType type){
		monsters.add(type);

		save();
	}

	public void setSpawnTime(String time){
		spawnTime = SimpleTime.from(time);

		save();
	}

	public ItemStack getIcon() {
		return icon != null && icon.getType() != Material.AIR ? icon: new ItemStack(Material.CARVED_PUMPKIN);
	}

	@Override
	public SerializedMap serialize() {

		return SerializedMap.ofArray("Icon",icon,
				"Waves", waves,
				"Monsters", monsters,
				"Time", time,
				"Name", name,
				"Spawn_Time", spawnTime,
				"Position", position);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MonsterRound that = (MonsterRound) o;
		return Objects.equals(name, that.name) &&
				Objects.equals(icon, that.icon) &&
				Objects.equals(monsters, that.monsters) &&
				Objects.equals(time, that.time) &&
				Objects.equals(waves, that.waves) &&
				Objects.equals(spawnTime,that.spawnTime) &&
				Objects.equals(position,that.position);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, icon, monsters, time, waves, spawnTime, position);
	}

	public static void loadRounds() {
		loadedRounds.clear();

		final File[] roundFiles = FileUtil.getFiles("rounds","yml");

		for(final File roundFile: roundFiles) {
			final String name = FileUtil.getFileName(roundFile);

			loadOrCreateRound(name);
		}
	}

	public static MonsterRound findRound(int position){
		for(MonsterRound monsterRound: getRounds()){
			if(monsterRound.getPosition() == position)
				return monsterRound;
		}
		return null;
	}

	public static MonsterRound getLast(){
		return getRounds().get(getRounds().size()-1);
	}

	public static MonsterRound loadOrCreateRound(final String name){
		Valid.checkBoolean(!isRoundLoaded(name), "Class " + name + " is already loaded: " + getRoundNames());

		try {
			final MonsterRound monsterRound = new MonsterRound(name);
			loadedRounds.add(monsterRound);

			Common.log("[+] Loaded rounds " + monsterRound.getName());
			return monsterRound;

		} catch (final Throwable t) {
			Common.throwError(t, "Failed to load round " + name);
		}

		return null;
	}

	public static void removeRound(@NonNull final MonsterRound monsterRound) {
		Valid.checkBoolean(isRoundLoaded(monsterRound.getName()), "Round " + monsterRound.getName() + " not loaded. Available: " + getRoundNames());

		loadedRounds.remove(monsterRound);
		monsterRound.delete();
	}

	public static boolean isRoundLoaded(final String name){
		return findRound(name) != null;
	}

	public static MonsterRound findRound(@NonNull final String name){
		for(final MonsterRound monsterRound: loadedRounds){
			if(monsterRound.getName().equalsIgnoreCase(name))
				return monsterRound;
		}

		return null;
	}

	public static SimpleTime getTotalTime(MonsterRound round){
		int time = round.getWaves() * round.getSpawnTime().getTimeSeconds();
		return SimpleTime.fromSeconds(time);
	}

	public static List<MonsterRound> getRounds() {
		return Collections.unmodifiableList(loadedRounds);
	}

	public static List<String> getRoundNames() {
		return Common.convert(loadedRounds, MonsterRound::getName);
	}
}
