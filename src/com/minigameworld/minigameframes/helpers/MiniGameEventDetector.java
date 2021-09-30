package com.minigameworld.minigameframes.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
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
import org.bukkit.event.block.BlockReceiveGameEvent;
import org.bukkit.event.block.BlockShearEntityEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.command.UnknownCommandEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.Inventory;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.destroystokyo.paper.event.entity.EnderDragonFireballHitEvent;
import com.destroystokyo.paper.event.entity.EndermanAttackPlayerEvent;

import io.papermc.paper.event.block.BellRingEvent;
import io.papermc.paper.event.entity.ElderGuardianAppearanceEvent;

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
		this.detectableEventList.add(PlayerEvent.class);
		this.detectableEventList.add(EntityEvent.class);
		this.detectableEventList.add(HangingEvent.class);
		this.detectableEventList.add(InventoryEvent.class);
		this.detectableEventList.add(InventoryMoveItemEvent.class);
		this.detectableEventList.add(PlayerLeashEntityEvent.class);
	}

	// check detectable event

	// [IMPORTANT] never check in detectableEventList (because, detectableEventList
	// can't not get player in every event)
	public boolean isDetectableEvent(Event event) {
		return !this.getPlayersFromEvent(event).isEmpty();
	}

	public boolean isDetectableEvent(Class<? extends Event> event) {
		return this.isDetectableEvent(event);
	}

	public List<Player> getPlayersFromEvent(Event e) {
		List<Player> eventPlayers = new ArrayList<>();

		// check detailed events
		if (this.getPlayersFromDetailedEvent(e, eventPlayers)) {
			return eventPlayers;
		}

		// get players from each Event
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
		}

		return eventPlayers;
	}

	private boolean getPlayersFromDetailedEvent(Event event, List<Player> eventPlayers) {
		// several case
		if (event instanceof BlockEvent) {
			this.getPlayersFromBlockEvent((BlockEvent) event, eventPlayers);
		} else if (event instanceof EntityEvent) {
			this.getPlayersFromEntityEvent((EntityEvent) event, eventPlayers);
		} else if (event instanceof VehicleEvent) {
			this.getPlayersFromVehicleEvent((VehicleEvent) event, eventPlayers);
		} else if (event instanceof UnknownCommandEvent) {
			this.getPlayersFromUnknownCommandEvent((UnknownCommandEvent) event, eventPlayers);
		}

		return !eventPlayers.isEmpty();
	}

	private void getPlayersFromBlockEvent(BlockEvent event, List<Player> eventPlayers) {
		if (event instanceof BellRingEvent) {
			BellRingEvent e = (BellRingEvent) event;
			if (e.getEntity() instanceof Player) {
				eventPlayers.add((Player) e.getEntity());
			}
		} else if (event instanceof BlockBreakEvent) {
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
		} else if (event instanceof BlockReceiveGameEvent) {
			BlockReceiveGameEvent e = (BlockReceiveGameEvent) event;
			if (e.getEntity() instanceof Player) {
				eventPlayers.add((Player) e.getEntity());
			}
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
		} else if (event instanceof TNTPrimeEvent) {
			TNTPrimeEvent e = (TNTPrimeEvent) event;
			if (e.getPrimerEntity() instanceof Player) {
				eventPlayers.add((Player) e.getPrimerEntity());
			}
		}
	}

	private void getPlayersFromEntityEvent(EntityEvent event, List<Player> eventPlayers) {
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
		} else if (event instanceof ElderGuardianAppearanceEvent) {
			ElderGuardianAppearanceEvent e = (ElderGuardianAppearanceEvent) event;
			eventPlayers.add(e.getAffectedPlayer());
		} else if (event instanceof EnderDragonFireballHitEvent) {
			EnderDragonFireballHitEvent e = (EnderDragonFireballHitEvent) event;
			for (LivingEntity entity : e.getTargets()) {
				if (entity instanceof Player) {
					eventPlayers.add((Player) entity);
				}
			}
		} else if (event instanceof EndermanAttackPlayerEvent) {
			eventPlayers.add(((EndermanAttackPlayerEvent) event).getPlayer());
		}

	}

	private void getPlayersFromVehicleEvent(VehicleEvent event, List<Player> eventPlayers) {
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

	private void getPlayersFromUnknownCommandEvent(UnknownCommandEvent event, List<Player> eventPlayers) {
		if (event.getSender() instanceof Player) {
			eventPlayers.add((Player) event.getSender());
		}
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
