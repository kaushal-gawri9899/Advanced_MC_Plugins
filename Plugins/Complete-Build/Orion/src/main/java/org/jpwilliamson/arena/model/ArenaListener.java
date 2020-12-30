package org.jpwilliamson.arena.model;

import java.util.*;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.jpwilliamson.arena.menu.VoteMenu;
import org.jpwilliamson.arena.model.buildbattle.BuildBattleArena;
import org.jpwilliamson.arena.model.buildbattle.BuildBattleSettings;
import org.jpwilliamson.arena.mysql.ArenaDatabase;
import org.jpwilliamson.arena.util.ArenaUtil;
import org.jpwilliamson.arena.util.Constants;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.collection.StrictSet;
import org.mineacademy.fo.event.RocketExplosionEvent;
import org.mineacademy.fo.exception.EventHandledException;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.region.Region;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;
import org.mineacademy.fo.remain.Remain;
import org.mineacademy.fo.settings.SimpleSettings;

import javax.security.auth.callback.CallbackHandler;

/**
 * Listen for events during the game play
 */
public final class ArenaListener implements Listener {

	/**
	 * The entities we prevent clicking, placing or removing even if the arena is played
	 */
	private final static StrictSet<String> ENTITY_TYPE_MANIPULATION_BLACKLIST = new StrictSet<>("ITEM_FRAME", "PAINTING", "ARMOR_STAND", "LEASH_HITCH");

	public ArenaListener() {
		registerCompatibleEvent("org.bukkit.event.entity.EntityBreedEvent", new EntityBreedListener());
		registerCompatibleEvent("org.bukkit.event.entity.SpawnerSpawnEvent", new SpawnerSpawnListener());
		registerCompatibleEvent("org.bukkit.event.player.PlayerInteractAtEntityEvent", new InteractAtEntityListener());
		registerCompatibleEvent("org.bukkit.event.block.BlockExplodeEvent", new ExplodeAndEntitySpawnListener());
		registerCompatibleEvent("org.bukkit.event.player.PlayerItemConsumeEvent", new ConsumeItemListener());
	}

	/**
	 * Register a class containing events not available in older Minecraft versions
	 *
	 * @param classPath
	 * @param listener
	 */
	private void registerCompatibleEvent(final String classPath, final Listener listener) {
		try {
			Class.forName(classPath);

			Common.registerEvents(listener);
		} catch (final ClassNotFoundException ex) {
			// Do nothing event does not exist
		}
	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Handle player chat, join, leave and death-related events
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * Teleport players to spawn if they are found in an arena on join
	 *
	 * @param event
	 */
	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final Arena arena = ArenaManager.findArena(player.getLocation());

		// Load his database data
		Common.runLaterAsync(() -> ArenaDatabase.load(player));

		// Add an invisible tag containing the time now, used for teleport exemption
		CompMetadata.setTempMetadata(player, "JoinTime", System.currentTimeMillis());

		if (arena != null && !player.isOp()) {
			player.teleport(player.getWorld().getSpawnLocation()); // change this to wherever you want to move players into

			Messenger.warn(player, "You have been teleported from a stopped arena to the world spawn.");
		}
	}

	/**
	 * Automatically leave the arena if the player quits the server
	 *
	 * @param event
	 */
	@EventHandler
	public void onQuit(final PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		final Arena arena = ArenaManager.findArena(player);

		if (arena != null)
			arena.leavePlayer(player, ArenaLeaveReason.DISCONNECT);

		// Save his database data
		Common.runLaterAsync(() -> ArenaDatabase.save(player));
	}

	/**
	 * Respawn on death and limit death messages to arena players
	 *
	 * @param event
	 */
	@EventHandler
	public void onDeath(final PlayerDeathEvent event) {
		final Player player = event.getEntity();
		final ArenaPlayer cache = ArenaPlayer.getCache(player);
		final ArenaJoinMode mode = cache.getMode();

		if (mode == ArenaJoinMode.PLAYING || mode == ArenaJoinMode.SPECTATING) {
			Remain.respawn(player);

			try {
				cache.getArena().onPlayerDeath(player, event);

			} catch (final ArenaPipelineEndException ex) {
				// Handled
			}

			event.setDeathMessage(null);

			try {
				event.setKeepInventory(true);

			} catch (final NoSuchMethodError ex) {
				// Old MC lack the set inventory method
			}
		}
	}

	/**
	 * Pass the respawn event to arenas to enable custom handling of respawn per arena
	 *
	 * @param event
	 */
	@EventHandler
	public void onRespawn(final PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		final ArenaPlayer cache = ArenaPlayer.getCache(player);
		final ArenaJoinMode mode = cache.getMode();

		if (mode == ArenaJoinMode.PLAYING || mode == ArenaJoinMode.SPECTATING) {
			final Location respawnLocation = cache.getArena().getRespawnLocation(player);

			if (respawnLocation != null)
				event.setRespawnLocation(respawnLocation);

			Common.runLater(() -> {
				try {
					cache.getArena().onPlayerRespawn(player, cache);

				} catch (final ArenaPipelineEndException ex) {
					// Everything handled successfully
				}
			});
		}
	}

	/**
	 * Isolate game chat to arena recipients and prevent if spectating
	 *
	 * @param event
	 */
	@EventHandler
	public void onChat(final AsyncPlayerChatEvent event) {
		final Player player = event.getPlayer();
		final ArenaPlayer cache = ArenaPlayer.getCache(player);
		final ArenaJoinMode mode = cache.getMode();

		if (cache.hasArena() && mode != ArenaJoinMode.EDITING) {

			if (mode == ArenaJoinMode.SPECTATING) {
				Messenger.error(player, "You cannot chat while spectating an arena!");

				event.setCancelled(true);
			} else {
				final Arena arena = cache.getArena();

				// You can use this lambda expression instead of the for loop below
				//event.getRecipients().removeIf(recipient -> arena.findPlayer(recipient) == null);

				for (final Iterator<Player> it = event.getRecipients().iterator(); it.hasNext();) {
					final Player recipient = it.next();

					if (arena.findPlayer(recipient) == null)
						it.remove();
				}

				final ChatColor teamColor = arena.hasTeams() && arena.getState() == ArenaState.PLAYED ? cache.getArenaTeam().getColor() : ChatColor.WHITE;
				event.setFormat(Common.colorize("&8[&6Arena " + arena.getName() + "&8] " + teamColor + player.getName() + ": &7" + event.getMessage()));
			}
		}
	}

	/**
	 * Prevent command usage during arena play
	 *
	 * @param event
	 */
	@EventHandler
	public void onCommand(final PlayerCommandPreprocessEvent event) {
		final Player player = event.getPlayer();
		final ArenaPlayer cache = ArenaPlayer.getCache(player);
		final ArenaJoinMode mode = cache.getMode();

		final String[] args = event.getMessage().split(" ");
		final String firstArg = args.length > 0 ? args[0] : "";

		if (!firstArg.isEmpty() && cache.hasArena() && mode != ArenaJoinMode.EDITING)
			if (!Valid.isInList(firstArg, SimpleSettings.MAIN_COMMAND_ALIASES)) {
				Messenger.error(player, "You cannot execute this command in an arena.");

				event.setCancelled(true);
			}
	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Prevent arena interaction
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * Restrict clicking in arenas to play state only
	 *
	 * @param event
	 */
	@EventHandler
	public void onInteract(final PlayerInteractEvent event) {
		final Action action = event.getAction();
		final Player player = event.getPlayer();
		final Arena arena = ArenaManager.findArena(event.hasBlock() ? event.getClickedBlock().getLocation() : player.getLocation());

		if (arena != null) {

			if(arena.getType().equalsIgnoreCase("buildBattle")){
				BuildBattleArena arena1 = (BuildBattleArena)arena;
				BuildBattleSettings settings = arena1.getSettings();
				if(settings.isVotingTime()){
					if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_AIR)){
						if(event.hasItem())
						{
							if(Objects.requireNonNull(event.getItem()).getType().equals(Material.RED_GLAZED_TERRACOTTA)){
								arena1.setTeamScore(player,1);
							}

							else if(event.getItem().getType().equals(Material.BLUE_GLAZED_TERRACOTTA)){
								arena1.setTeamScore(player,2);
							}

							else if(event.getItem().getType().equals(Material.ORANGE_GLAZED_TERRACOTTA)){
								arena1.setTeamScore(player,3);
							}

							else if(event.getItem().getType().equals(Material.GREEN_GLAZED_TERRACOTTA)){
								arena1.setTeamScore(player,4);
							}
						}
					}
				}
				return;
			}

			final ArenaPlayer arenaPlayer = arena.findPlayer(player);

			if (arenaPlayer != null && (arena.isEdited() || arena.isPlayed()) && arenaPlayer.getMode() != ArenaJoinMode.SPECTATING) {

				final boolean isBoat = event.hasItem() && CompMaterial.isBoat(event.getItem().getType());
				final boolean isSoil = event.hasBlock() && event.getClickedBlock().getType() == CompMaterial.FARMLAND.getMaterial();

				if ((isBoat || isSoil) && arenaPlayer.getMode() != ArenaJoinMode.EDITING) {
					event.setCancelled(true);
					player.updateInventory();
				}

				try {
					arena.onClick(player, action, event);

				} catch (final EventHandledException ex) {
					// Handled
				}

				return;
			}

			if (arenaPlayer == null && !action.toString().contains("AIR") && action != Action.PHYSICAL)
				Messenger.warn(player, "Use /arena edit to make changes to this arena.");

			event.setCancelled(true);
			player.updateInventory();
		}
	}

	/**
	 * Prevent flight during arena play
	 *
	 * @param event
	 */
	@EventHandler
	public void onFlightToggle(final PlayerToggleFlightEvent event) {
		executeIfPlayingArena(event, (player, arenaPlayer) -> {
			if (arenaPlayer.getMode() == ArenaJoinMode.SPECTATING)
				return;

			if(arenaPlayer.hasArena())
			{
				if(arenaPlayer.getArena().getType().equalsIgnoreCase("BuildBattle")
						&& arenaPlayer.getMode().equals(ArenaJoinMode.PLAYING)){
					return;
				}
			}

			player.setAllowFlight(false);
			player.setFlying(false);

			Messenger.error(player, "You cannot fly playing an arena.");
		});
	}

	/**
	 * Prevent gamemode change during arena play
	 *
	 * @param event
	 */
	@EventHandler
	public void onGamemodeChange(final PlayerGameModeChangeEvent event) {
		executeIfPlayingArena(event, (player, arenaPlayer) -> {
			if(!arenaPlayer.getArena().getType().equalsIgnoreCase("buildbattle"))
			{event.setCancelled(true);

			Messenger.error(player, "You cannot change gamemode while playing an arena!");}
		});
	}

	/**
	 * Prevent clicking inventory in spectate mode
	 *
	 * @param event
	 */
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		final ArenaPlayer cache = ArenaPlayer.getCache(player);

		if (cache.hasArena() && cache.getMode() != ArenaJoinMode.EDITING) {
			if (cache.getMode() == ArenaJoinMode.SPECTATING && Menu.getMenu(player) == null)
				event.setCancelled(true);

			if (event.getSlot() == 39 && cache.getArenaTeam() != null)
				event.setCancelled(true);
		}

//		if(cache.getArena() instanceof BuildBattleArena && VoteMenu.getMenu(player)!=null){
//			event.setCancelled(true);
//		}
		ItemStack m = event.getCurrentItem();
		Map<String,Integer> themeMap = VoteMenu.getThemeMap();
		List value = new ArrayList(themeMap.keySet());
		if(m!=null) {

			if (m.getType().equals(Material.RED_STAINED_GLASS_PANE) || m.getType().equals(Material.ACACIA_SIGN) || m.getType().equals(Material.PAPER)) {
				if(m.getType().equals(Material.PAPER)){
					int slot = event.getSlot();
					Bukkit.broadcastMessage("Slot is " + slot);
					//Common.log("Slot is" + slot);
					VoteMenu.changeInventory(slot,player);
				}
				event.setCancelled(true);
			}

		}
	}

	/**
	 * Cancel experience changing if playing in arena because
	 * we use the exp bar for custom experience points
	 *
	 * @param event
	 */
	@EventHandler
	public void onExpChange(final PlayerExpChangeEvent event) {
		final Player player = event.getPlayer();
		final ArenaPlayer cache = ArenaPlayer.getCache(player);

		if (cache.hasArena())
			event.setAmount(0);
	}

	/**
	 * Prevent entering beds when not playing
	 *
	 * @param event
	 */
	@EventHandler
	public void onBedEnter(final PlayerBedEnterEvent event) {
		final Arena arena = ArenaManager.findArena(event.getBed().getLocation());

		if (arena != null) {
			final ArenaPlayer arenaPlayer = arena.findPlayer(event.getPlayer());

			if (arenaPlayer == null || arenaPlayer.getMode() == ArenaJoinMode.SPECTATING)
				event.setCancelled(true);
		}
	}

	/**
	 * Prevent creating vehicles such as boats when not playing
	 *
	 * @param event
	 */
	@EventHandler
	public void onVehicleCreate(final VehicleCreateEvent event) {
		if (event instanceof Cancellable) // Compatibility with older MC versions
			cancelIfInStoppedOrLobby(event.getVehicle(), event);
	}

	/**
	 * Prevent entering vehicles when not playing
	 *
	 * @param event
	 */
	@EventHandler
	public void onVehicleEnter(final VehicleEnterEvent event) {
		preventVehicleGrief(event.getEntered(), event);
	}

	/**
	 * Prevent damaging vehicles such as boats when not playing
	 *
	 * @param event
	 */
	@EventHandler
	public void onVehicleDamage(final VehicleDamageEvent event) {
		preventVehicleGrief(event.getAttacker(), event);
	}

	/**
	 * Prevent destroying vehicles such as boats when not playing
	 *
	 * @param event
	 */
	@EventHandler
	public void onVehicleDestroy(final VehicleDestroyEvent event) {
		preventVehicleGrief(event.getAttacker(), event);
	}

	/**
	 * Prevent pushing vehicles if not playing arena
	 *
	 * @param event
	 */
	@EventHandler
	public void onVehicleCollision(final VehicleEntityCollisionEvent event) {
		preventVehicleGrief(event.getEntity(), event);
	}

	/**
	 * Prevent manipulating vehicles when not playing
	 *
	 * @param involvedEntity
	 * @param event
	 * @param <T>
	 */
	private <T extends VehicleEvent & Cancellable> void preventVehicleGrief(final Entity involvedEntity, final T event) {
		final Arena arena = ArenaManager.findArena(event.getVehicle().getLocation());

		if (arena != null) {
			if (!arena.isPlayed() && !arena.isEdited()) {
				event.setCancelled(true);

				return;
			}

			if (involvedEntity instanceof Player) {
				final ArenaPlayer cache = ArenaPlayer.getCache((Player) involvedEntity);

				if (!cache.hasArena() || cache.getMode() == ArenaJoinMode.SPECTATING)
					event.setCancelled(true);

			} else {
				final Arena involvedArena = ArenaManager.findArena(involvedEntity.getLocation());

				if (involvedArena == null || !involvedArena.equals(arena))
					event.setCancelled(true);
			}
		}
	}

	/**
	 * Restrict killing or damaging entities during not playing/editing arenas
	 *
	 * @param event
	 */
	@EventHandler
	public void onEntityAttacked(final EntityDamageByEntityEvent event) {
		final Entity victim = event.getEntity();
		final Entity attacker = event.getDamager();
		final Arena victimArena = ArenaManager.findArena(victim.getLocation());

		if (victimArena == null)
			return;

		if (!victimArena.isPlayed() && !victimArena.isEdited()) {
			event.setCancelled(true);

			return;
		}

		final Arena attackerArena = ArenaManager.findArena(attacker.getLocation());

		if (attackerArena == null || !attackerArena.equals(victimArena)) {
			event.setCancelled(true);

			return;
		}

		if (attacker instanceof Player) {
			final ArenaPlayer attackerCache = victimArena.findPlayer((Player) attacker);

			if (attackerCache == null || !attackerCache.hasArena() || !attackerCache.getArena().equals(victimArena)) {
				event.setCancelled(true);

				return;
			}

			if (attackerCache != null) {
				if (attackerCache.getMode() == ArenaJoinMode.SPECTATING)
					event.setCancelled(true);

				else if (victim instanceof Player) {
					if (!victimArena.hasPvP()) {
						event.setCancelled(true);

						return;
					}

					try {
						victimArena.onPvP((Player) attacker, (Player) victim, event);
					} catch (final ArenaPipelineEndException ex) {
						// Handled
					}

				} else
					try {
						victimArena.onPlayerDamage((Player) attacker, victim, event);

					} catch (final ArenaPipelineEndException ex) {
						// Handled
					}
			}

			if (ENTITY_TYPE_MANIPULATION_BLACKLIST.contains(victim.getType().toString()) && !victimArena.isEdited())
				if (attackerCache == null || attackerCache.getMode() != ArenaJoinMode.EDITING)
					event.setCancelled(true);
		}

		else {
			if (victim instanceof Player) {
				final ArenaPlayer victimCache = ArenaPlayer.getCache((Player) victim);

				if (victimCache.getMode() != ArenaJoinMode.PLAYING) {
					event.setCancelled(true);

					return;
				}
			}

			try {
				victimArena.onDamage(attacker, victim, event);
			} catch (final ArenaPipelineEndException ex) {
				// Handled
			}
		}
	}

	/**
	 * Prevent sun burning entities such as zombies
	 *
	 * @param event
	 */
	@EventHandler
	public void onEntityCombust(final EntityCombustEvent event) {
		cancelIfInArena(event.getEntity().getLocation(), event);
	}

	/**
	 * Prevent generic entity interaction in stopped arenas
	 *
	 * @param event
	 */
	@EventHandler
	public void onEntityInteract(final EntityInteractEvent event) {
		cancelIfInStoppedOrLobby(event.getEntity(), event);
	}

	/**
	 * Prevent TNT explosion starting in stopped arenas
	 *
	 * @param event
	 */
	@EventHandler
	public void onExplosionPrime(final ExplosionPrimeEvent event) {
		cancelIfInStoppedOrLobby(event.getEntity(), event);

		if (!event.isCancelled() && event.getEntity() instanceof EnderCrystal) {
			final Arena arena = ArenaManager.findArena(event.getEntity().getLocation());

			if (arena != null)
				event.setCancelled(true);
		}
	}

	/**
	 * Stop destroying blocks if some of them are in arenas
	 *
	 * @param event
	 */
	@EventHandler
	public void onEntityExplode(final EntityExplodeEvent event) {
		preventBlockGrief(event.getLocation(), event.blockList(), event);

		if (event.getEntity() instanceof EnderCrystal) {
			final Arena arena = ArenaManager.findArena(event.getLocation());

			if (arena != null)
				event.setCancelled(true);
		}
	}

	/**
	 * Iterate through all blocks and stop the whole event if one or more are in the arena
	 *
	 * @param centerLocation
	 * @param blocks
	 * @param event
	 */
	private void preventBlockGrief(final Location centerLocation, final List<Block> blocks, final Cancellable event) {
		final Arena centerArena = ArenaManager.findArena(centerLocation);

		if (centerArena != null)
			try {
				centerArena.onExplosion(centerLocation, blocks, event);
			} catch (final ArenaPipelineEndException ex) {
				// Handled
			}

		for (final Iterator<Block> it = blocks.iterator(); it.hasNext();) {
			final Block block = it.next();
			final Arena arena = ArenaManager.findArena(block.getLocation());

			if (arena != null) {
				if (arena.getSettings().isDestructionEnabled()) {
					if (!arena.isPlayed()) {
						it.remove();

						continue;
					}
				} else {
					it.remove();

					continue;
				}
			}

			if (arena == null && centerArena != null)
				it.remove();
		}

		if (centerArena != null) {
			if (event instanceof EntityExplodeEvent)
				((EntityExplodeEvent) event).setYield(0F);
			try {
				if (event instanceof BlockExplodeEvent)
					((BlockExplodeEvent) event).setYield(0F);
			} catch (final Throwable t) {
				// Old MC
			}
		}
	}

	/**
	 * Prevent targeting entities inside the arena by entities who are outside
	 *
	 * @param event
	 */
	@EventHandler
	public void onEntityTarget(final EntityTargetEvent event) {
		final Entity from = event.getEntity();
		final Entity target = event.getTarget();

		final Arena targetArena = ArenaManager.findArena(from.getLocation());

		if (from instanceof ExperienceOrb && targetArena != null) {
			from.remove();

			return;
		}

		final Arena arena = target != null ? ArenaManager.findArena(target.getLocation()) : null;

		if (arena != null) {
			if (!arena.isPlayed() && !arena.isEdited())
				event.setCancelled(true);

			else if (targetArena == null || !targetArena.equals(arena))
				event.setCancelled(true);
		}

		if (target instanceof Player) {
			final ArenaPlayer cache = ArenaPlayer.getCache((Player) target);

			// Prevent players in editing or spectating mode from being targeted
			if (cache.hasArena() && cache.getMode() != ArenaJoinMode.PLAYING)
				event.setCancelled(true);
		}
	}

	/**
	 * Prevent any damage in stopped arenas
	 *
	 * @param event
	 */
	@EventHandler
	public void onEntityDamage(final EntityDamageEvent event) {
		final Entity victim = event.getEntity();
		final Arena arena = ArenaManager.findArena(victim.getLocation());

		if (arena != null) {
			if (!arena.isPlayed() && !arena.isEdited()) {
				event.setCancelled(true);

				return;
			}

			if (victim instanceof Player) {
				final Player player = (Player) victim;
				final ArenaPlayer cache = ArenaPlayer.getCache(player);

				if (cache.getMode() == ArenaJoinMode.SPECTATING) {
					event.setCancelled(true);

					player.setFireTicks(0);
					return;
				}
			}

			try {
				arena.onDamage(victim, event);

			} catch (final ArenaPipelineEndException ex) {
				// Handled
			}
		}
	}

	/**
	 * Clear up exp/drops on entity death that is in a stopped arena
	 *
	 * @param event
	 */
	@EventHandler
	public void onEntityDeath(final EntityDeathEvent event) {
		final LivingEntity victim = event.getEntity();
		final Arena arena = ArenaManager.findArena(victim.getLocation());

		if (arena != null) {
			if (!arena.isPlayed() && !arena.isEdited()) {
				event.setDroppedExp(0);
				event.getDrops().clear();

				return;
			}

			final Player killer = victim.getKiller();
			final ArenaPlayer killerArena = killer != null ? ArenaPlayer.getCache(killer) : null;

			// If the killer is a player who's playing in the same arena, call the method
			if (killerArena != null && killerArena.getMode() == ArenaJoinMode.PLAYING && killerArena.getArena().equals(arena)) {
				event.setDroppedExp(0);
				event.getDrops().clear();

				try {
					arena.onPlayerKill(killer, victim);

				} catch (final ArenaPipelineEndException exc) {
					// Handled
				}
			}
		}
	}

	/**
	 * Prevent monsters spawning in stopped arenas
	 *
	 * @param event
	 */
	@EventHandler
	public void onCreatureSpawn(final CreatureSpawnEvent event) {
		final Arena arena = ArenaManager.findArena(event.getLocation());

		if (arena != null) {
			if (!arena.isPlayed() && !arena.isEdited()) {
				event.setCancelled(true);

				return;
			}

			final Region region = arena.getSettings().getRegion();

			if (region != null && region.isWhole()) {
				int creatures = 0;

				for (final Entity entity : region.getEntities())
					if (entity instanceof Creature)
						creatures++;

				if (creatures >= arena.getSettings().getMaxCreatures())
					event.setCancelled(true);
			}
		}
	}

	/**
	 * Prevent item spawning in stopped arenas
	 *
	 * @param event
	 */
	@EventHandler
	public void onItemSpawn(final ItemSpawnEvent event) {
		cancelIfInStoppedOrLobby(event.getLocation(), event);
	}

	/**
	 * Prevent dispensors shooting blocks in stopped arenas
	 *
	 * @param event
	 */
	@EventHandler
	public void onBlockDispense(final BlockDispenseEvent event) {
		cancelIfInStoppedOrLobby(event.getBlock().getLocation(), event);
	}

	/**
	 * Prevent any form of bucket use in arenas that are not being edited
	 *
	 * @param event
	 */
	@EventHandler
	public void onBucketFill(final PlayerBucketFillEvent event) {
		ArenaPlayer cache = ArenaPlayer.getCache(event.getPlayer());
		if(cache.hasArena()){
			if(cache.getMode().equals(ArenaJoinMode.PLAYING) && cache.getArena().getType().equalsIgnoreCase("buildBattle")){
				BuildBattleSettings settings = (BuildBattleSettings) cache.getArena().getSettings();
				if(!settings.isVotingTime())
					return;
			}
		}
		preventBucketGrief(event.getBlockClicked().getLocation(), event);
	}

	/**
	 * Prevent any form of bucket use in arenas that are not being edited
	 *
	 * @param event
	 */
	@EventHandler
	public void onBucketEmpty(final PlayerBucketEmptyEvent event) {
		ArenaPlayer cache = ArenaPlayer.getCache(event.getPlayer());
		if(cache.hasArena()){
			if(cache.getMode().equals(ArenaJoinMode.PLAYING) && cache.getArena().getType().equalsIgnoreCase("buildBattle")){
				BuildBattleSettings settings = (BuildBattleSettings) cache.getArena().getSettings();
				if(!settings.isVotingTime())
					return;
			}
		}
		preventBucketGrief(event.getBlockClicked().getLocation(), event);
	}

	/**
	 * Check if the arena at location exists and if there is a player, cancel if he is not editing it
	 *
	 * @param location
	 * @param event
	 * @param <T>
	 */
	private <T extends PlayerEvent & Cancellable> void preventBucketGrief(final Location location, final T event) {
		final Arena arena = ArenaManager.findArena(location);

		if (arena != null) {
			final ArenaPlayer cache = arena.findPlayer(event.getPlayer());

			if (cache == null || cache.getMode() != ArenaJoinMode.EDITING)
				event.setCancelled(true);
		}
	}

	/**
	 * Prevent any block forming such as ice in arenas what-so-ever
	 *
	 * @param event
	 */
	@EventHandler
	public void onBlockForm(final BlockFormEvent event) {
		cancelIfInArena(event.getBlock().getLocation(), event);
	}

	/**
	 * Prevent any block spread such as fire burning in arenas what-so-ever
	 *
	 * @param event
	 */
	@EventHandler
	public void onBlockSpread(final BlockSpreadEvent event) {
		cancelIfInArena(event.getBlock().getLocation(), event);
	}

	/**
	 * Prevent zombies from breaking doors!
	 *
	 * @param event
	 */
	@EventHandler
	public void onDoorBreak(final EntityBreakDoorEvent event) {
		if (!(event.getEntity() instanceof Player))
			cancelIfInArena(event.getBlock().getLocation(), event);
	}

	/**
	 * Prevent projectile abuse in stopped arenas or by players from outside
	 *
	 * @param event
	 */
	@EventHandler
	public void onPotion(final PotionSplashEvent event) {
		preventProjectileGrief(event.getEntity());
	}

	/**
	 * Prevent projectile abuse in stopped arenas or by players from outside
	 *
	 * @param event
	 */
	@EventHandler
	public void onProjectileLaunch(final ProjectileLaunchEvent event) {
		preventProjectileGrief(event.getEntity());
	}

	/**
	 * Prevent projectile abuse in stopped arenas or by players from outside
	 *
	 * @param event
	 */
	@EventHandler
	public void onProjectileHit(final ProjectileHitEvent event) {
		preventProjectileGrief(event.getEntity());
	}

	/**
	 * Prevent projectile abuse in stopped arenas or by players from outside
	 *
	 * @param event
	 */
	@EventHandler
	public void onRocketExplosion(final RocketExplosionEvent event) {
		preventProjectileGrief(event.getProjectile());
	}

	/**
	 * Handle projectile abuse in stopped arenas or by players from outside
	 *
	 * @param projectile
	 */
	private void preventProjectileGrief(final Projectile projectile) {
		final Arena arena = ArenaManager.findArena(projectile.getLocation());

		if (arena != null) {

			if (!arena.isPlayed() && !arena.isEdited())
				projectile.remove();

			else if (projectile.getShooter() instanceof Player) {
				final ArenaPlayer arenaPlayer = arena.findPlayer((Player) projectile.getShooter());

				if (arenaPlayer == null || arenaPlayer.getMode() == ArenaJoinMode.SPECTATING) {
					projectile.remove();

					try {
						if (projectile instanceof Arrow)
							((Arrow) projectile).setDamage(0);
					} catch (final Throwable t) {
						// Old MC
					}
				}
			}
		}
	}

	/**
	 * Prevent any form of hanging manipulation (such as placing images) while not editing arenas
	 *
	 * @param event
	 */
	@EventHandler
	public void onHangingPlace(final HangingPlaceEvent event) {
		preventHangingGrief(event.getEntity(), event);
	}

	/**
	 * Prevent any form of hanging manipulation (such as placing images) while not editing arenas
	 *
	 * @param event
	 */
	@EventHandler
	public void onHangingBreak(final HangingBreakEvent event) {
		preventHangingGrief(event.getEntity(), event);
	}

	/**
	 * If the arena at location exists and is not edited we cancel the event
	 *
	 * @param hanging
	 * @param event
	 */
	private void preventHangingGrief(final Entity hanging, final Cancellable event) {
		final Arena arena = ArenaManager.findArena(hanging.getLocation());

		if (arena != null && !arena.isEdited())
			event.setCancelled(true);
	}

	/**
	 * Prevent piston abuse in arenas
	 *
	 * @param event
	 */
	@EventHandler
	public void onPistonExtend(final BlockPistonExtendEvent event) {
		preventPistonMovement(event, event.getBlocks());
	}

	/**
	 * Prevent piston abuse in arenas
	 *
	 * @param event
	 */
	@EventHandler
	public void onPistonRetract(final BlockPistonRetractEvent event) {
		try {
			preventPistonMovement(event, event.getBlocks());
		} catch (final NoSuchMethodError ex) {
			// Old MC lack the event.getBlocks method
		}
	}

	/**
	 * Calculate if the blocks being pushed/pulled by piston cross an arena border
	 * and cancel the event if they do
	 *
	 * @param event
	 * @param blocks
	 */
	private void preventPistonMovement(final BlockPistonEvent event, List<Block> blocks) {
		final BlockFace direction = event.getDirection();
		final Arena pistonArena = ArenaManager.findArena(event.getBlock().getLocation());

		// Clone the list otherwise it wont work
		blocks = new ArrayList<>(blocks);

		// Calculate blocks ONE step ahed in the push/pull direction
		for (int i = 0; i < blocks.size(); i++) {
			final Block block = blocks.get(i);

			blocks.set(i, block.getRelative(direction));
		}

		for (final Block block : blocks) {
			final Arena arena = ArenaManager.findArena(block.getLocation());

			if (arena != null && pistonArena == null || arena == null && pistonArena != null) {
				event.setCancelled(true);

				break;
			}
		}
	}

	/**
	 * Prevent building in stopped non edited arenas
	 *
	 * @param event
	 */
	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event) {
		if(ArenaPlayer.getCache(event.getPlayer()).hasArena()){

			if(ArenaPlayer.getCache(event.getPlayer()).getMode().equals(ArenaJoinMode.PLAYING) &&
					ArenaPlayer.getCache(event.getPlayer()).getArena().getType().equalsIgnoreCase("buildbattle")) {
				BuildBattleArena arena = (BuildBattleArena) ArenaPlayer.getCache(event.getPlayer()).getArena();
				if(arena.buildBlocks.contains(event.getBlock().getBlockData())){
					arena.buildBlocks.remove(event.getBlock().getBlockData());
					return;
				}
			}
		}
		preventBuild(event.getPlayer(), event, false);
	}

	/**
	 * Prevent building in stopped non edited arenas
	 *
	 * @param event
	 */
	@EventHandler
	public void onBlockPlace(final BlockPlaceEvent event) {
		if(ArenaPlayer.getCache(event.getPlayer()).hasArena()){

			if(ArenaPlayer.getCache(event.getPlayer()).getMode().equals(ArenaJoinMode.PLAYING) &&
					ArenaPlayer.getCache(event.getPlayer()).getArena().getType().equalsIgnoreCase("buildbattle")) {
				if(!((BuildBattleSettings)ArenaPlayer.getCache(event.getPlayer()).getArena().getSettings()).isVotingTime()){
					BuildBattleArena arena = (BuildBattleArena) ArenaPlayer.getCache(event.getPlayer()).getArena();
					if(!arena.getSettings().isVotingTime()){
						arena.buildBlocks.add(event.getBlock().getBlockData());
						return;
					}
				}
			}
		}
		preventBuild(event.getPlayer(), event, true);
	}

	/**
	 * Prevent griefing arenas
	 *
	 * @param player
	 * @param event
	 * @param <T>
	 */
	private <T extends BlockEvent & Cancellable> void preventBuild(final Player player, final T event, boolean place) {
		final Arena arena = ArenaManager.findArena(event.getBlock().getLocation());

		if (arena != null) {
			final ArenaPlayer arenaPlayer = arena.findPlayer(player);
			final Block block = event.getBlock();

			if (arenaPlayer != null && arenaPlayer.getMode() == ArenaJoinMode.EDITING)
				return;

			if (arenaPlayer == null)
				Messenger.warn(player, "You cannot build unless you do /arena edit first.");

			try {
				if (place)
					arena.onBlockPlace(player, block, (BlockPlaceEvent) event);
				else
					arena.onBlockBreak(player, block, (BlockBreakEvent) event);
			} catch (final ArenaPipelineEndException ex) {
				// Handled
			}
		}
	}

	/**
	 * Prevent egg being thrown in/to stopped arenas
	 *
	 * @param event
	 */
	@EventHandler
	public void onEggThrow(final PlayerEggThrowEvent event) {
		final Egg egg = event.getEgg();
		final Arena arena = ArenaManager.findArena(egg.getLocation());

		if (arena != null) {
			final ArenaPlayer arenaPlayer = egg.getShooter() instanceof Player ? arena.findPlayer((Player) egg.getShooter()) : null;

			if (arenaPlayer == null || arenaPlayer.getMode() == ArenaJoinMode.SPECTATING)
				event.setHatching(false);
		}
	}

	/**
	 * Prevent item pickup in stopped arenas or by non playing players
	 *
	 * @param event
	 */
	@EventHandler
	public void onItemPickup(final PlayerPickupItemEvent event) {
		preventItemGrief(event, event.getItem());
	}

	/**
	 * Prevent item dropping in stopped arenas or by non playing players
	 *
	 * @param event
	 */
	@EventHandler
	public void onItemDrop(final PlayerDropItemEvent event) {
		preventItemGrief(event, event.getItemDrop());
	}

	/**
	 * Handle item manipulation in stopped arenas or by non playing players
	 *
	 * @param event
	 * @param item
	 * @param <T>
	 */
	private <T extends PlayerEvent & Cancellable> void preventItemGrief(final T event, final Item item) {
		final Player player = event.getPlayer();
		final Arena arenaAtLocation = ArenaManager.findArena(item.getLocation());

		if (arenaAtLocation == null)
			return;

		if (!arenaAtLocation.isEdited() && !arenaAtLocation.isPlayed()) {
			event.setCancelled(true);

			return;
		}

		final ArenaPlayer arenaPlayer = arenaAtLocation.findPlayer(player);

		if (arenaPlayer == null || arenaPlayer.getMode() == ArenaJoinMode.SPECTATING)
			event.setCancelled(true);
	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Handle teleportation
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * Prevent liquids flowing in from/to arenas and prevent dragon egg being teleported
	 *
	 * @param event
	 */
	public void onBlockTeleport(BlockFromToEvent event) {
		final Block block = event.getBlock();

		event.setCancelled(true);

		final SerializedMap moveData = calculateMoveData(block.getLocation(), event.getToBlock().getLocation());
		final Arena fromArena = ArenaManager.findArena(moveData.getString("from"));

		if (moveData.getBoolean("leaving") || moveData.getBoolean("entering") || (fromArena != null && block.getType() == CompMaterial.DRAGON_EGG.getMaterial()))
			event.setCancelled(true);
	}

	/**
	 * Prevent entities such as endermen from moving/entering arenas
	 *
	 * @param event
	 */
	@EventHandler(ignoreCancelled = true)
	public void onTeleport(final EntityTeleportEvent event) {
		final Entity entity = event.getEntity();

		if (entity instanceof Player)
			return;

		final SerializedMap moveData = calculateMoveData(event.getFrom(), event.getFrom());

		if (moveData.getBoolean("leaving") || moveData.getBoolean("entering"))
			event.setCancelled(true);
	}

	/**
	 * Prevent players from escaping or entering arenas unnaturally by teleporting
	 *
	 * @param event
	 */
	@EventHandler(ignoreCancelled = true)
	public void onTeleport(final PlayerTeleportEvent event) {
		final Player player = event.getPlayer();

		if (ArenaWorldManager.isWorldBeingProcessed(event.getTo().getWorld())) {
			Messenger.error(player, "Cannot teleport to your destination as its world is being processed right now.");

			event.setCancelled(true);
			return;
		}

		if (CompMetadata.hasTempMetadata(player, Constants.Tag.TELEPORT_EXEMPTION))
			return;

		final SerializedMap moveData = calculateMoveData(event.getFrom(), event.getTo(), player);
		final ArenaPlayer cache = ArenaPlayer.getCache(player);
		String errorMessage = null;

		if ((cache.hasArena() && cache.getArena().isStopping()) || cache.isLeavingArena())
			return;

		if (!cache.hasArena() && moveData.getBoolean("toIsArena")) {
			// Must ignore teleports until 0.5 seconds have passed since the played joined due to Spigot/Bukkit behavior
			final MetadataValue joinTime = CompMetadata.getTempMetadata(player, "JoinTime");
			final long joinDuration = System.currentTimeMillis() - (joinTime != null ? joinTime.asLong() : 0);

			if (joinDuration > 1000 && player.isOnGround())
				errorMessage = "You cannot teleport into the arena unless you edit it with /arena edit";

		} else if (cache.hasArena() && cache.getMode() != ArenaJoinMode.EDITING) {
			if (moveData.getBoolean("leaving"))
				errorMessage = "You cannot teleport away from your arena unless you leave with /arena leave first.";

			else if (moveData.getBoolean("entering"))
				errorMessage = "Your destination is an arena. Edit it with /arena edit first.";
		}

		if (errorMessage != null) {
			Messenger.error(player, errorMessage);

			event.setCancelled(true);
		}
	}

	/**
	 * See {@link #calculateMoveData(Location, Location, Player)}
	 *
	 * @param from
	 * @param to
	 * @return
	 */
	private SerializedMap calculateMoveData(final Location from, final Location to) {
		return calculateMoveData(from, to, null);
	}

	/**
	 * Creates a map of data containing information if the player is entering an arena, leaving it,
	 * the arena names and if both of these cases exist
	 *
	 * @param from
	 * @param to
	 * @param fromPlayer
	 * @return
	 */
	private SerializedMap calculateMoveData(final Location from, final Location to, final Player fromPlayer) {
		final Arena arenaFrom = fromPlayer != null ? ArenaManager.findArena(fromPlayer) : ArenaManager.findArena(from);
		final Arena arenaTo = ArenaManager.findArena(to);

		final boolean isLeaving = arenaFrom != null && (arenaTo == null || !arenaFrom.equals(arenaTo));
		final boolean isEntering = arenaTo != null && (arenaFrom == null || !arenaTo.equals(arenaFrom));

		return SerializedMap.ofArray(
				"to", arenaTo != null ? arenaTo.getName() : "",
				"from", arenaFrom != null ? arenaFrom.getName() : "",
				"toIsArena", arenaTo != null,
				"fromIsArena", arenaFrom != null,
				"leaving", isLeaving,
				"entering", isEntering);

	}

	// ------–------–------–------–------–------–------–------–------–------–------–------–
	// Utilities
	// ------–------–------–------–------–------–------–------–------–------–------–------–

	/**
	 * Cancel the event if it is in an arena (what-so-ever)
	 *
	 * @param location
	 * @param event
	 */
	private void cancelIfInArena(final Location location, final Cancellable event) {
		final Arena arena = ArenaManager.findArena(location);

		if (arena != null)
			event.setCancelled(true);
	}

	/**
	 * Cancel the event if it the entity in a stopped non edited arena (or during lobby)
	 *
	 * @param entity
	 * @param event
	 */
	private void cancelIfInStoppedOrLobby(final Entity entity, final Cancellable event) {
		cancelIfInStoppedOrLobby(entity.getLocation(), event);
	}

	/**
	 * Cancel the event if the location is in a stopped non edited arena (or during lobby)
	 *
	 * @param location
	 * @param event
	 */
	private void cancelIfInStoppedOrLobby(final Location location, final Cancellable event) {
		final Arena arena = ArenaManager.findArena(location);

		if (arena != null && !arena.isPlayed() && !arena.isEdited())
			event.setCancelled(true);
	}

	/**
	 * Run the function if the player has arena he is spectating/playing
	 *
	 * @param event
	 * @param consumer
	 */
	private void executeIfPlayingArena(final PlayerEvent event, final BiConsumer<Player, ArenaPlayer> consumer) {
		final Player player = event.getPlayer();
		final ArenaPlayer cache = ArenaPlayer.getCache(player);

		if (cache.hasArena() && cache.getMode() != ArenaJoinMode.EDITING)
			consumer.accept(player, cache);
	}

	/**
	 * A separate listener for newer MC versions
	 */
	private final class EntityBreedListener implements Listener {

		/**
		 * Prevent entities breeding in stopped non edited arenas
		 *
		 * @param event
		 */
		@EventHandler
		public void onEntityBreed(final EntityBreedEvent event) {
			cancelIfInStoppedOrLobby(event.getEntity(), event);
		}
	}

	/**
	 * A separate listener for newer MC versions
	 */
	private final class SpawnerSpawnListener implements Listener {

		/**
		 * Prevent spawners from functioning in stopped non edited arenas
		 *
		 * @param event
		 */
		@EventHandler
		public void onSpawnerSpawn(final SpawnerSpawnEvent event) {
			cancelIfInStoppedOrLobby(event.getEntity(), event);
		}
	}

	/**
	 * A separate listener for newer MC versions
	 */
	private final class InteractAtEntityListener implements Listener {

		/**
		 * Prevent clicking on entities when not playing arenas
		 *
		 * @param event
		 */
		@EventHandler(ignoreCancelled = false)
		public void onInteractAtEntity(final PlayerInteractAtEntityEvent event) {
			final Player player = event.getPlayer();
			final Entity entity = event.getRightClicked();
			final Arena arena = ArenaManager.findArena(entity.getLocation());

			if (arena != null) {
				final ArenaPlayer arenaPlayer = arena.findPlayer(player);

				if (arenaPlayer == null || arenaPlayer.getMode() == ArenaJoinMode.SPECTATING) {
					event.setCancelled(true);

					return;
				}

				try {
					arena.onEntityClick(player, entity, event);

				} catch (final ArenaPipelineEndException ex) {
					// Handled
				}
			}
		}
	}

	/**
	 * A separate listener for newer MC versions
	 */
	private final class ExplodeAndEntitySpawnListener implements Listener {

		/**
		 * Stop destroying blocks if some of them are in arenas
		 *
		 * @param event
		 */
		@EventHandler
		public void onBlockExplode(final BlockExplodeEvent event) {
			preventBlockGrief(event.getBlock().getLocation(), event.blockList(), event);
		}

		/**
		 * Prevent any entity spawning in stopped arenas
		 *
		 * @param event
		 */
		@EventHandler
		public void onEntitySpawn(final EntitySpawnEvent event) {

			final Entity entity = event.getEntity();
			final Arena arena = ArenaManager.findArena(event.getLocation());

			if(arena != null){
				if(arena.getType().equalsIgnoreCase("buildBattle")){
					BuildBattleArena arena1 = (BuildBattleArena)arena;
					if(arena1.isPlayed()){
						return;
					}
				}
			}

			if (arena == null)
				return;

			if (!arena.isPlayed() && !arena.isEdited()) {
				event.setCancelled(true);

				return;
			}

			if (ENTITY_TYPE_MANIPULATION_BLACKLIST.contains(entity.getType().toString()) && !arena.isEdited())
				event.setCancelled(true);

			// Track flying blocks and remove them once arena border is reached
			if (entity instanceof FallingBlock)
				ArenaUtil.preventArenaLeave(arena, (FallingBlock) entity);
		}
	}

	/**
	 * A separate listener for newer MC versions
	 */
	private final class ConsumeItemListener implements Listener {

		/**
		 * Cancel food consumption in stopped arenas or during lobby
		 *
		 * @param event
		 */
		@EventHandler
		public void onConsumeFood(final PlayerItemConsumeEvent event) {
			cancelIfInStoppedOrLobby(event.getPlayer(), event);
		}
	}
}
