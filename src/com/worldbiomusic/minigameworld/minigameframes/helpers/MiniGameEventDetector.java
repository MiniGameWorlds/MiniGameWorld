package com.worldbiomusic.minigameworld.minigameframes.helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockShearEntityEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.projectiles.ProjectileSource;

import com.worldbiomusic.minigameworld.api.MiniGameEventExternalDetector;
import com.worldbiomusic.minigameworld.managers.MiniGameManager;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

/**
 * Event detector to send Minigames<br>
 * <br>
 * <b>[IMPORTANT]</b><br>
 * Detectable event means can get player from "Default event list" or "Detailed
 * event list"<br>
 * <br>
 * 
 * [Default Event List]<br>
 * - PlayerEvent<br>
 * - EntityEvent<br>
 * - HangingEvent<br>
 * - InventoryEvent<br>
 * - InventoryMoveItemEvent<br>
 * - PlayerLeashEntityEvent<br>
 * - TabCompleteEvent<br>
 * <br>
 * 
 * [Detailed Event list]<br>
 * - Some of BlockEvent<br>
 * - Some of EntityEvent<br>
 * - Some of VehicleEvent<br>
 */
public class MiniGameEventDetector {
	/**
	 * Default detectable event list
	 */
	private Set<Class<? extends Event>> detectableEventList;
	private List<MiniGameEventExternalDetector> externalDetectors;
	private MiniGameManager minigameManager;

	public MiniGameEventDetector(MiniGameManager minigameManager) {
		this.minigameManager = minigameManager;

		// register detectable events
		this.registerDetectableEvent();

		this.externalDetectors = new ArrayList<>();
	}

	/**
	 * Registers default detectable events
	 */
	private void registerDetectableEvent() {
		// init
		this.detectableEventList = new HashSet<>();

		// events only related with Player
		this.detectableEventList.add(PlayerEvent.class);
		this.detectableEventList.add(EntityEvent.class);
		this.detectableEventList.add(HangingEvent.class);
		this.detectableEventList.add(InventoryEvent.class);
		this.detectableEventList.add(InventoryMoveItemEvent.class);
		this.detectableEventList.add(PlayerLeashEntityEvent.class);
		this.detectableEventList.add(TabCompleteEvent.class);
	}

	/**
	 * Checks event is detectable
	 * 
	 * @param event Event to check
	 * @return True if detectable event
	 */
	public boolean isDetectableEvent(Event event) {
		return !this.getPlayersFromEvent(event).isEmpty();
	}

	/**
	 * Checks event is detectable
	 * 
	 * @param event Event to check
	 * @return True if detectable event
	 */
	public boolean isDetectableEvent(Class<? extends Event> event) {
		return this.isDetectableEvent(event);
	}

	/**
	 * Gets players from default events + detailed events
	 * 
	 * @param e Event to get players
	 * @return Players from event
	 */
	public Set<Player> getPlayersFromEvent(Event e) {
		Set<Player> eventPlayers = new HashSet<>();

		// check basic events
		getPlayersFromBasicEvent(e, eventPlayers);

		// check detailed events
		if (isPlayersEmpty(eventPlayers)) {
			getPlayersFromDetailedEvent(e, eventPlayers);
		}

		// check external detectors
		if (isPlayersEmpty(eventPlayers)) {
			getPlayersFromExternalDetectors(e, eventPlayers);
		}

		return eventPlayers;
	}

	private boolean isPlayersEmpty(Set<Player> eventPlayers) {
		return eventPlayers.isEmpty();

	}

	private void getPlayersFromBasicEvent(Event e, Set<Player> eventPlayers) {
		if (e instanceof PlayerEvent) {
			eventPlayers.add(((PlayerEvent) e).getPlayer());
		} else if (e instanceof EntityEvent) {
			Entity entity = ((EntityEvent) e).getEntity();
			if (entity instanceof Player) {
				eventPlayers.add((Player) entity);
			}
		} else if (e instanceof HangingEvent) {
			Entity entity = ((HangingEvent) e).getEntity();
			if (entity instanceof Player) {
				eventPlayers.add((Player) entity);
			}
		} else if (e instanceof InventoryEvent) {
			HumanEntity entity = (((InventoryEvent) e).getView()).getPlayer();
			if (entity instanceof Player) {
				eventPlayers.add((Player) entity);
			}
		} else if (e instanceof InventoryMoveItemEvent) {
			Inventory inv = ((InventoryMoveItemEvent) e).getInitiator();
			List<HumanEntity> viewers = inv.getViewers();
			for (HumanEntity humanEntity : viewers) {
				if (humanEntity instanceof Player) {
					eventPlayers.add((Player) humanEntity);
				}
			}
		} else if (e instanceof PlayerLeashEntityEvent) {
			eventPlayers.add(((PlayerLeashEntityEvent) e).getPlayer());
		} else if (e instanceof TabCompleteEvent) {
			if (((TabCompleteEvent) e).getSender() instanceof Player) {
				eventPlayers.add((Player) ((TabCompleteEvent) e).getSender());
			}
		}
	}

	/**
	 * Gets players from detailed events
	 * 
	 * @param event        Event to get players
	 * @param eventPlayers Players from event
	 * @return False if no players from event
	 */
	private void getPlayersFromDetailedEvent(Event event, Set<Player> eventPlayers) {
		// several case
		if (event instanceof BlockEvent) {
			this.getPlayersFromBlockEvent((BlockEvent) event, eventPlayers);
		} else if (event instanceof EntityEvent) {
			this.getPlayersFromEntityEvent((EntityEvent) event, eventPlayers);
		} else if (event instanceof VehicleEvent) {
			this.getPlayersFromVehicleEvent((VehicleEvent) event, eventPlayers);
		}
	}

	/**
	 * Gets players from Block event
	 * 
	 * @param event        Event to get player
	 * @param eventPlayers Players from event
	 */
	private void getPlayersFromBlockEvent(BlockEvent event, Set<Player> eventPlayers) {
		if (event instanceof BlockBreakEvent) {
			eventPlayers.add(((BlockBreakEvent) event).getPlayer());
		} else if (event instanceof BlockDamageEvent) {
			eventPlayers.add(((BlockDamageEvent) event).getPlayer());
		} else if (event instanceof BlockDropItemEvent) {
			eventPlayers.add(((BlockDropItemEvent) event).getPlayer());
		} else if (event instanceof BlockFertilizeEvent) {
			eventPlayers.add(((BlockFertilizeEvent) event).getPlayer());
		} else if (event instanceof BlockIgniteEvent) {
			eventPlayers.add(((BlockIgniteEvent) event).getPlayer());
		} else if (event instanceof BlockPlaceEvent) {
			eventPlayers.add(((BlockPlaceEvent) event).getPlayer());
		} else if (event instanceof BlockShearEntityEvent) {
			BlockShearEntityEvent e = (BlockShearEntityEvent) event;
			if (e.getEntity() instanceof Player) {
				eventPlayers.add((Player) e.getEntity());
			}
		} else if (event instanceof CauldronLevelChangeEvent) {
			CauldronLevelChangeEvent e = (CauldronLevelChangeEvent) event;
			if (e.getEntity() instanceof Player) {
				eventPlayers.add((Player) e.getEntity());
			}
		} else if (event instanceof SignChangeEvent) {
			eventPlayers.add(((SignChangeEvent) event).getPlayer());
		}
	}

	/**
	 * Gets players from Entity event
	 * 
	 * @param event        Event to get player
	 * @param eventPlayers Players from event
	 */
	private void getPlayersFromEntityEvent(EntityEvent event, Set<Player> eventPlayers) {
		if (event instanceof EntityDeathEvent) {
			Player killer = ((EntityDeathEvent) event).getEntity().getKiller();
			if (killer != null) {
				eventPlayers.add(killer);
			}
		} else if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if (e.getDamager() instanceof Player) {
				eventPlayers.add((Player) e.getDamager());
			}
			// check projectile
			else if (e.getDamager() instanceof Projectile) {
				Projectile proj = (Projectile) e.getDamager();
				if (proj.getShooter() instanceof Player) {
					eventPlayers.add((Player) proj.getShooter());
				}
			}
		} else if (event instanceof EntityTargetEvent) {
			EntityTargetEvent e = (EntityTargetEvent) event;
			if (e.getTarget() instanceof Player) {
				eventPlayers.add((Player) e.getTarget());
			}
		} else if (event instanceof ProjectileHitEvent) {
			ProjectileHitEvent e = (ProjectileHitEvent) event;
			Entity hitEntity = e.getHitEntity();
			ProjectileSource shooter = e.getEntity().getShooter();
			if (hitEntity != null && hitEntity instanceof Player) {
				eventPlayers.add((Player) hitEntity);
			}
			if (shooter != null && shooter instanceof Player) {
				eventPlayers.add((Player) shooter);
			}

		} else if (event instanceof ProjectileLaunchEvent) {
			ProjectileLaunchEvent e = (ProjectileLaunchEvent) event;
			Projectile proj = e.getEntity();
			ProjectileSource shooter = proj.getShooter();
			if (shooter != null && shooter instanceof Player) {
				eventPlayers.add((Player) shooter);
			}
		}

	}

	/**
	 * Gets players from Vehicle event
	 * 
	 * @param event        Event to get player
	 * @param eventPlayers Players from event
	 */
	private void getPlayersFromVehicleEvent(VehicleEvent event, Set<Player> eventPlayers) {
		if (event instanceof VehicleDamageEvent) {
			VehicleDamageEvent e = (VehicleDamageEvent) event;
			if (e.getAttacker() instanceof Player) {
				eventPlayers.add(((Player) e.getAttacker()));
			}
		} else if (event instanceof VehicleDestroyEvent) {
			VehicleDestroyEvent e = (VehicleDestroyEvent) event;
			if (e.getAttacker() instanceof Player) {
				eventPlayers.add(((Player) e.getAttacker()));
			}
		} else if (event instanceof VehicleEnterEvent) {
			VehicleEnterEvent e = (VehicleEnterEvent) event;
			if (e.getEntered() instanceof Player) {
				eventPlayers.add(((Player) e.getEntered()));
			}
		} else if (event instanceof VehicleExitEvent) {
			VehicleExitEvent e = (VehicleExitEvent) event;
			if (e.getExited() instanceof Player) {
				eventPlayers.add(((Player) e.getExited()));
			}
		}
	}

	private void getPlayersFromExternalDetectors(Event event, Set<Player> eventPlayers) {
		this.externalDetectors.forEach(d -> eventPlayers.addAll(d.getPlayersFromEvent(event)));
	}

	public void registerExternalDetector(MiniGameEventExternalDetector detector) {
		if (!this.externalDetectors.contains(detector)) {
			this.externalDetectors.add(detector);
		}
	}

	public void unregisterExternalDetector(MiniGameEventExternalDetector detector) {
		this.externalDetectors.remove(detector);
	}

	/**
	 * Leave only one player in the same minigame event
	 * 
	 * @param eventPlayers Players related with event
	 */
	private void leavePlayerPlayingTheSameMiniGame(Set<Player> eventPlayers) {
		if (eventPlayers.isEmpty()) {
			return;
		}

		Set<Player> removingPlayers = new HashSet<>();

		// Pick standard minigame player
		Iterator<Player> it = eventPlayers.iterator();
		Player firstP = null;

		while (it.hasNext()) {
			Player tmpP = it.next();
			if (this.minigameManager.isPlayingMiniGame(tmpP)) {
				firstP = tmpP;
			}
		}

		if (firstP == null) {
			return;
		}
		MiniGame firstPMiniGame = this.minigameManager.getPlayingMiniGame(firstP);

		// remove duplicated player playing the same minigame
		for (Player p : eventPlayers) {
			if (firstP.equals(p)) {
				continue;
			}

			if (this.minigameManager.isPlayingMiniGame(p)) {
				MiniGame pMiniGame = this.minigameManager.getPlayingMiniGame(p);
				if (firstPMiniGame.equals(pMiniGame)) {
					removingPlayers.add(p);
				}
			}

		}
		eventPlayers.removeAll(removingPlayers);
	}

}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
