package com.minigameworld.minigameframes.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.Inventory;

public class MiniGameEventDetector {
	// detectable events
	private Set<Class<? extends Event>> detectableEventList;

	public MiniGameEventDetector() {
		// register detectable events
		this.registerDetectableEvent();
	}

	private void registerDetectableEvent() {
		// init
		this.detectableEventList = new HashSet<>();

		// events only related with Player
		this.detectableEventList.add(BlockBreakEvent.class);
		this.detectableEventList.add(BlockPlaceEvent.class);
		this.detectableEventList.add(PlayerEvent.class);
		this.detectableEventList.add(EntityEvent.class);
		this.detectableEventList.add(HangingEvent.class);
		this.detectableEventList.add(InventoryEvent.class);
		this.detectableEventList.add(InventoryMoveItemEvent.class);
		this.detectableEventList.add(PlayerLeashEntityEvent.class);
	}

	// check detectable event (using isAssignableFrom())
	public boolean isDetectableEvent(Event event) {
		for (Class<? extends Event> c : this.detectableEventList) {
			if (c.isAssignableFrom(event.getClass())) {
				return true;
			}
		}
		return false;
	}

	// check detectable event (using isAssignableFrom())
	public boolean isDetectableEvent(Class<? extends Event> event) {
		for (Class<? extends Event> c : this.detectableEventList) {
			if (c.isAssignableFrom(event)) {
				return true;
			}
		}
		return false;
	}

	public List<Player> getPlayersFromEvent(Event e) {
		List<Player> eventPlayers = new ArrayList<>();

		// check detailed events
		if (this.getPlayersFromDetailedEvent(e, eventPlayers)) {
			return eventPlayers;
		}

		// get players from each Event
		if (e instanceof BlockBreakEvent) {
			eventPlayers.add(((BlockBreakEvent) e).getPlayer());
		} else if (e instanceof BlockPlaceEvent) {
			eventPlayers.add(((BlockPlaceEvent) e).getPlayer());
		} else if (e instanceof PlayerEvent) {
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
		} else if (e instanceof EntityDamageByEntityEvent) {
			Entity damager = ((EntityDamageByEntityEvent) e).getDamager();
			if (damager instanceof Player) {
				eventPlayers.add((Player) damager);
			}
		}

		return eventPlayers;
	}

	private boolean getPlayersFromDetailedEvent(Event event, List<Player> eventPlayers) {
		// several case
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
		}

		return !eventPlayers.isEmpty();
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
