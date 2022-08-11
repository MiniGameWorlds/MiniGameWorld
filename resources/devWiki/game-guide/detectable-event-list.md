# Description
- Each event detectors have different priorities
- After higher priority detector detected event, the lower priority detectors will not detect any event
```yaml
1. Basic
2. Detailed
3. External (exist in MiniGameWorld API)
4. Custom Detectable
```

# Basic detector
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

# Detailed detector
- Detect event which `Basic detector` can't detect
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
- EntityDamageByEntityEvent (When damager or `projectile shooter` is a player))
- EntityTargetEvent (When taget entity is a player)
- ProjectileHitEvent (When hit entity is a player)
- ProjectileLaunchEvent (When projectile shooter is a player)

### - VehicleEvent
- VehicleDamageEvent
- VehicleDestroyEvent
- VehicleEnterEvent
- VehicleExitEvent

---
<!-- 
# Custom Detectable detector
- There are two ways to process undetectable events
- If you handle a undetectable event, you need to make sure the event is related with your minigame, because undetectable event will be passed the event to the minigame even if the event is not related with your minigame
- If need event already detectable by `API event detector` for some reason(e.g. performance(e.g. PlayerMoveEvent)), make sure that `useEventDetector` setting to `false` (ref: `PlayerMoveEvent` of [Parkour](https://github.com/MiniGameWorlds/AllMiniGames/blob/main/src/com/worldbiomusic/allgames/games/solobattle/parkour/Parkour.java))


## First method
- Add `getSetting().addCustomDetectableEvent(EventYouWant.class);` in constructor
```java
public class YourMiniGame extends SoloMiniGame implements Listener {
	public YourMiniGame() {
		super("YourMiniGame", 60, 10);

		// add custom detectable event
		getSetting().addCustomDetectableEvent(WeatherChangeEvent.class);
	}

	@Override
	protected void onEvent(Event event) {
		if (event instanceof WeatherChangeEvent) {
			WeatherChangeEvent e = (WeatherChangeEvent) event;
			e.getWorld().setWeatherDuration(1);
		}
	}
}
```


## Second method
- If a event passed to the `minigame.passEvent()` from the event listener, you can process the event you want in the `onEvent()` of your minigame class
- **If event could contain any player, check the player is playing your minigame before pass event with `passEvent()`**
```java
public class CommonListener implements Listener {
	MiniGame minigame;

	public CommonListener() {
        // register listener to plugin manager
        Bukkit.getPluginManager().registerEvents(this, YourPluginMain);

		// register minigame
		MiniGameWorld mw = MiniGameWorld.create("x.x.x");
		minigame = new FitTool();
		mw.registerGame(minigame);
	}

	@EventHandler
	public void onWeatherChanged(WeatherChangeEvent e) {
		// pass undetectable event: event will be passed to the minigame.onEvent()
		this.minigame.passEvent(e);
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		// pass undetectable event: event will be passed to the minigame.onEvent()
		this.minigame.passEvent(e);
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		// check player if event could contains players 
		if (e.getEntity().getShooter() instanceof Player) {
			Player shooter = (Player) e.getEntity().getShooter();
			
			// check player is playing your minigame
			if (minigame.containsPlayer(shooter)) {
				this.minigame.passEvent(e);
			}
		}
	}
}
```



## Third method
- Implements `Listener` and register to bukkit plugin manager and make event handler method in your minigame class, then pass event to `passEvent()`
- **If event could contain any player, check the player is playing your minigame before pass event with `passEvent()`**
- **NEVER process event directly in event handler method**
- **NEVER pass event directly to onEvent() in event handler method, but pass to only passEvent()**
```java
public class YourMiniGame extends SoloMiniGame implements Listener {

	public YourMiniGame() {
		super("YourMiniGame", 60, 10);

        // register listener to plugin manager
        Bukkit.getPluginManager().registerEvents(this, YourPluginMain);
	}

	@Override
	protected void onEvent(Event event) {
		if (event instanceof WeatherChangeEvent) {
			WeatherChangeEvent e = (WeatherChangeEvent) event;
			e.getWorld().setWeatherDuration(1);
		} else if (event instanceof ProjectileHitEvent) {
			ProjectileHitEvent e = (ProjectileHitEvent) event;
			
			// no need to check entity type, because entity checked before in "onProjectileHit()"
			Player shooter = (Player) e.getEntity().getShooter();
			shooter.sendMessage("You shoot something!");
		}
	}

	@EventHandler
	public void onWeatherChanged(WeatherChangeEvent e) {
        // NEVER process event here
		// pass the event to passEvent() (NEVER pass to the onEvent() directly)
		passEvent(e);
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		// check player if event could contains players 
		if (e.getEntity().getShooter() instanceof Player) {
			Player shooter = (Player) e.getEntity().getShooter();
			
			// check player is playing your minigame
			if (minigame.containsPlayer(shooter)) {
				this.minigame.passEvent(e);
			}
		}
	}
}
```
 If process in event handler method or pass to the onEvent(), some problems are occurrs
1. Can not preprocess in onEvent() of frame class (super class)
2. Can not check minigame has started


---

# For performance
- `MiniGameWorld` default detector is a little slow because, detects almost events to pass to the minigames
- For better performance, you have to follow below method

## Example
1. Set `useEventDetector` setting to **false** in the constructor
2. Implements **Listener** and register
```java
public class Parkour extends SoloBattleMiniGame implements Listener {

	public Parkour() {
		super("Parkour", 2, 10, 60 * 5, 15);

		// set "useEventDetector" setting to false
		getSetting().setUseEventDetector(false);
		
		// register this listener to plugin manager
		Bukkit.getServer().getPluginManager().registerEvents(this, AllMiniGamesMain.getInstance());
	}
}
```

3. Make event handler method for specific event
(if player related with event need to process, check the player is playing your minigame)
4. After then, **must** pass the event using `passEvent()` method
```java
@EventHandler
public void onPlayerMove(PlayerMoveEvent event) {
	PlayerMoveEvent e = (PlayerMoveEvent) event;
	
	// check player is playing this minigame
	if (containsPlayer(e.getPlayer())) {
		passEvent(event);
	}
}
```

5. Process event in `onEvent()` method with better performance
```java
@Override
protected void onEvent(Event event) {
	if (event instanceof PlayerMoveEvent) {
		// process event with better performance
	}
}
```









 -->
