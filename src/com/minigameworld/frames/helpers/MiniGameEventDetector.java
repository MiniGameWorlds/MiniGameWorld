package com.minigameworld.frames.helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
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
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.Inventory;

import com.minigameworld.api.MiniGameExternalEventDetector;
import com.minigameworld.managers.MiniGameManager;

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
 * - Some of VehicleEvent<br>
 */
public class MiniGameEventDetector {
	/**
	 * Default detectable event list
	 */
	private Set<Class<? extends Event>> detectableEventList;
	private List<MiniGameExternalEventDetector> externalDetectors;
	private MiniGameManager minigameManager;

	public MiniGameEventDetector(MiniGameManager minigameManager) {
		this.minigameManager = minigameManager;
		this.externalDetectors = new ArrayList<>();

		// register detectable events
		this.registerDetectableEvent();
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
		return !this.detectPlayers(event).isEmpty();
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
	public Set<Player> detectPlayers(Event e) {
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
	 * Gets players from Vehicle event
	 * 
	 * @param event        Event to get player
	 * @param eventPlayers Players from event
	 */
	private void getPlayersFromVehicleEvent(VehicleEvent event, Set<Player> eventPlayers) {
		if (event instanceof VehicleEnterEvent) {
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

	public void registerExternalDetector(MiniGameExternalEventDetector detector) {
		if (!this.externalDetectors.contains(detector)) {
			this.externalDetectors.add(detector);
		}
	}

	public void unregisterExternalDetector(MiniGameExternalEventDetector detector) {
		this.externalDetectors.remove(detector);
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
