package org.jpwilliamson.multiplex.model.skyWars;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

import org.jpwilliamson.multiplex.model.Arena;
import org.jpwilliamson.multiplex.model.ArenaHeartbeat;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.YamlConfig;

public class SkyWarsHeartbeat extends ArenaHeartbeat {

	/**
	 * Create a new heart beat
	 *
	 * @param arena
	 */
	protected SkyWarsHeartbeat(final Arena arena) {
		super(arena);
	}

	/*
	 * @see ArenaHeartbeat#onTick()
	 * Drop Irons on every 4 seconds, Drop Golds on every 8 Seconds, Drop Diamonds on every 12 seconds
	 * On every 30th second, drop an ender chest used for purchase menu in game
	 */
	@Override
	protected void onTick() {
		super.onTick();

		final SkyWarsSettings settings = getArena().getSettings();
		final int elapsedSeconds = getCountdownSeconds() - getTimeLeft();

		// Drop iron
		if (elapsedSeconds % 4 == 0)
			dropItems(settings.getIron(), ItemCreator.of(
					CompMaterial.IRON_INGOT,
					"Iron Ingot",
					"",
					"Use this to buy items!"));

		// Drop gold
		if (elapsedSeconds % 8 == 0)
			dropItems(settings.getGold(), ItemCreator.of(
					CompMaterial.GOLD_INGOT,
					"Gold Ingot",
					"",
					"Use this to buy better items!"));

		// Drop diamonds
		if (elapsedSeconds % 12 == 0)
			dropItems(settings.getDiamonds(), ItemCreator.of(
					CompMaterial.DIAMOND,
					"Diamond",
					"",
					"Use this to buy the best items!"));

		if (elapsedSeconds%30 == 0)
			dropItems(settings.getChests(), ItemCreator.of(CompMaterial.ENDER_CHEST, "Reward Chest", "Click it to get Items"));
	}

//	/*
//	 * Drop items at the given locations 1 block above
//	 */
	private void dropItems(YamlConfig.LocationList items, ItemCreator.ItemCreatorBuilder item) {
		for (final Location point : items) {
			final Item droppedItem = point.getWorld().dropItem(point.clone().add(0.5, 1, 0.5), item.build().make());

			droppedItem.setVelocity(new Vector(0, 0, 0));
		}
	}

	/**
	 * @see ArenaHeartbeat#getArena()
	 */
	@Override
	public SkyWarsArena getArena() {
		return (SkyWarsArena) super.getArena();
	}
}
