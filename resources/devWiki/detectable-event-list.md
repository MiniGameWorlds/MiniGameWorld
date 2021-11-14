# Base Events
- Detect event when a player is only subject of the event

## List
- PlayerEvent
- EntityEvent
- HangingEvent
- InventoryEvent
- InventoryMoveItemEvent
- PlayerLeashEntityEvent
- TabCompleteEvent

---

# Detailed Events
- Detect event which `Base Event` can't detect
- Detect event when a player is not a origin of the event, but a related entity

## List

### - BlockEvent
- BlockBreakEvent
- BlockDamageEvent
- BlockDropItemEvent
- BlockFertilizeEvent
- BlockIgniteEvent
- BlockPlaceEvent
- BlockShearEntityEvent
- CauldronLevelChangeEvent
- SignChangeEvent

### - EntityEvent
- EntityDeathEvent (When killer is a player)
- EntityDamageByEntityEvent (When `damager` or `projectile shooter` is a player))

### - VehicleEvent
- VehicleDamageEvent
- VehicleDestroyEvent
- VehicleEnterEvent
- VehicleExitEvent

---

# Other Events
- You can process **all events** about your bukkit (`Spigot` or `Paper`) in `processEvent()` method in `MiniGame` class, if follow below rules
- Must set `passUndetectableEvent` to true of `MiniGameSetting` to make minigame can process events other than `Detailed Events`
- If you try to handle a player, you need to make sure that player is playing your minigame, because undetectable event will pass the event to the minigame without checking if the player is playing the minigmae



















