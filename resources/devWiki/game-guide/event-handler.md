# Event handler
To handle minigame events normally, you have to check somethings like the event is related with this minigame or minigame is started or not. So with this API, new event handler is supported for more easy use. 

# 1. @GameEvent
Using `@GameEvent` annotation, the event will be called if below conditions are matched.
1. Method modifier is `public` or `protected`
2. If game has started
3. Detected player of event is playing this game

(player is only detectable by...)
- **getPlayer()** of `PlayerEvent`
- **getEntity()** of `EntityEvent`
- **getEntity()** of `HangingEvent`
- **getView().getPlayer()** of `InventoryEvent`
- **getInitiator().getViewers()** of `InventoryMoveItemEvent`
- **getEntity()** of `PlayerLeashEntityEvent`
- **getSender()** of `TabCompleteEvent`
- **related with some events** of `BlockEvent` and `VehicleEvent` (see [Details](detectable-event-list.md))


So, if you want to handle `PlayerBedEnterEvent` add handler method with `@GameEvent` annotation.
```java
@GameEvent
public void onEnterBed(PlayerBedEnterEvent e) {
    e.getPlayer().sendMessage("you entered a bed");
}
```

Generally, event will be passed game is only playing, not waiting. But using state option of `@GameEvent` annotation, event can be passed when the game is waiting.
```java
@GameEvent(state = State.WAIT)
public void onEnterBed(PlayerBedEnterEvent e) {
    e.getPlayer().sendMessage("you entered a bed");
}
```
Also `State.ALL` (WAIT + PLAY) is possible.
```java
@GameEvent(state = State.ALL)
public void onEnterBed(PlayerBedEnterEvent e) {
    e.getPlayer().sendMessage("you entered a bed");
}
```


But if you want to handle a event that is not possible to detect player, you can use **forced** option of `@GameEvent` annotation. Then event will always be called, so you have to always check the event is related with this game.
```java
public class GameA extends SoloBattleMiniGame {
	Entity mob;

	public GameA() {
		super("GameA", 2, 5, 30, 5);
	}

	@GameEvent(forced = true) // always be called 
	public void onMobHurt(EntityDamageEvent e) {
		// check damaged entity equals with this game mob
		if (e.getEntity().equals(this.mob)) {
			sendMessages("mob got damaged!");
		}
	}
}
```


※ If a event is called frequently like `PlayerMoveEvent`, implement it with `@EventHandler` and register it to the bukkit plugin manager for better performance.

※ If there are methods that have the same `decalred class name`, `method name`, `return type` and `parameter types`, the only one method will be handled.

---

# 2. @EventHandler
You can use normal event handler of bukkit. But there are some check list for your game.
1. Add `@EventHandler` annotation to the method and reigster listener to bukkit plugin manager.
2. `isStarted()` for checking the game has staretd or not.
3. Check event is related with your game.

```java
public class GameA extends SoloBattleMiniGame implements Listener {
	public GameA() {
		super("GameA", 2, 5, 30, 5);

        // register listener
		Bukkit.getPluginManager().registerEvents(this, MiniGameWorldMain.getInstance());
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
        // check game has started (#2)
		if (isStarted()) {
			Player p = e.getPlayer();
            // check the event player is playing this game (#3)
			if (containsPlayer(p)) {
				sendMessage(p, "You broke a block!");
			}
		}
	}
}
```

But be careful to register too many handlers using this way because minigame instance will be created and removed automatically,  listeners will stacks up in the plugin manager. (It could cause memory leak)



