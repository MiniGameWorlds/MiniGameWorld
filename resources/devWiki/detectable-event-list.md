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
- ProjectileHitEvent (When hit entity is a player or projectile shooter is a player)
- ProjectileLaunchEvent (When projectile shooter is a player)

### - VehicleEvent
- VehicleDamageEvent
- VehicleDestroyEvent
- VehicleEnterEvent
- VehicleExitEvent

---

# Custom Detectable detector
- There are two ways to process undetectable events
- If you handle a undetectable event, you need to make sure the event is related with your minigame, because undetectable event will be passed the event to the minigame even if the event is not related with your minigame


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
	protected void processEvent(Event event) {
		if (event instanceof WeatherChangeEvent) {
			WeatherChangeEvent e = (WeatherChangeEvent) event;
			e.getWorld().setWeatherDuration(1);
		}
	}
}
```


## Second method
- If pass a event to the `minigame.passEvent()` from the event listener, you can process the event you want in the `processEvent()` of your minigame class
```java
public class CommonListener implements Listener {
	MiniGame minigame;

	public CommonListener() {
        // register listener to plugin manager
        Bukkit.getPluginManager().registerEvents(this, YourPluginMain);

		// register minigame
		MiniGameWorld mw = MiniGameWorld.create("x.x.x");
		minigame = new FitTool();
		mw.registerMiniGame(minigame);
	}

	@EventHandler
	public void onWeatherChanged(WeatherChangeEvent e) {
		// pass undetectable event: event will be passed to the minigame.processEvent()
		this.minigame.passEvent(e);
	}
}
```



## Third method
- Implements `Listener` and register to bukkit plugin manager and make event handler method in your minigame class, then pass event to `passEvent()`
- **NEVER process event directly in event handler method**
- **NEVER pass event directly to processEvent() in event handler method, but pass to only passEvent()**
```java
public class YourMiniGame extends SoloMiniGame implements Listener {

	public YourMiniGame() {
		super("YourMiniGame", 60, 10);

        // register listener to plugin manager
        Bukkit.getPluginManager().registerEvents(this, YourPluginMain);
	}

	@Override
	protected void processEvent(Event event) {
		if (event instanceof WeatherChangeEvent) {
			WeatherChangeEvent e = (WeatherChangeEvent) event;
			e.getWorld().setWeatherDuration(1);
		}
	}

	@EventHandler
	public void onWeatherChanged(WeatherChangeEvent e) {
        // NEVER process event here
		// pass the event to passEvent() (NEVER pass to the processEvent() directly)
		passEvent(e);
	}
}
```
<!-- If process in event handler method or pass to the processEvent(), some problems are occurrs
1. Can not preprocess in processEvent() of frame class (super class)
2. Can not check minigame has started
 -->

















