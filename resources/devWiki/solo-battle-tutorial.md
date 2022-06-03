# Before start
- Check out **[Environment Setup Tutorial](environment-setup-tutorial.md)**

---

# Solo Battle MiniGame
- Solo players compete against each other

# 1. Sketch idea
- Simple one punch minigame
- Players can kill other players with just hit
- Plus 1 score to killer
- Set player live to false when hit by others

# 2. Create a class
- Create a `OnePunch` minigame class in your package
```java
public class OnePunch {
	
}
```

- After create, extends `SoloBattleMiniGame`
```java
public class OnePunch extends SoloBattleMiniGame {
	
}
```

- Then, we need to add constructor and some overrided methods
```java
public class OnePunch extends SoloBattleMiniGame {

	// constructor: setup minigame info
	public OnePunch(String title, int minPlayers, int maxPlayers, int playingTime, int waitingTime) {
		super(title, minPlayers, maxPlayers, playingTime, waitingTime);
	}

	// Initialize settings(e.g. prepare floor in TNT-Run) in every minigame starts
	@Override
	protected void initGame() {
	}

	// Process event (events will be passed to here without listener)
	@Override
	protected void onEvent(Event paramEvent) {
	}

	// Register your minigame tutorial (Editable in config)
	@Override
	protected List<String> tutorial() {
		return null;
	}
	
}
```

# 3. Setup info
- Pass constants to `super()` after remove arguments in constructor(`OnePunch(arguments...)`)
```java
public class OnePunch extends SoloBattleMiniGame {

	public OnePunch() {
		// title, min player, max player, play time, waiting time
		super("OnePunch", 2, 10, 60, 10);
	}

	...
}
```

- Setup minigame settings and options
```java
public class OnePunch extends SoloBattleMiniGame {

	public OnePunch() {
		super("OnePunch", 2, 10, 60, 10);
		
        // GUI menu icon
		getSetting().setIcon(Material.GRASS);
		
		// enable PVP
		getCustomOption().set(Option.PVP, true);
	}

	...
}
```

- Register tutorial
```java
public class OnePunch extends SoloBattleMiniGame {
	...

	@Override
	protected List<String> tutorial() {
		List<String> tutorial = new ArrayList<>();
		tutorial.add("Damager player: +1");
		tutorial.add("Victim player: death");
		
		return tutorial;
	}
	
}
```

# 4. Process events
- All events related with a player playing your minigame will be passed to `onEvent()` (Details: [Detectable Event List](detectable-event-list.md))
- We will use `EntityDamageByEntityEvent` to detect if a player has been hit by another player
```java
@Override
protected void onEvent(Event event) {
    // check event type
    if (event instanceof EntityDamageByEntityEvent) {
        // cast Event to EntityDamageByEntityEvent
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
    }
}
```

- MiniGameWorld API checks if the entity in EntityDamageByEntityEvent is the playing minigame player, so we have to check if  `damager` is the player and playing this minigame
```java
@Override
protected void onEvent(Event event) {
    if (event instanceof EntityDamageByEntityEvent) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        
        // damager entity
        Entity damagerEntity = e.getDamager();

		// check damager entity is a player
        if (!(damagerEntity instanceof Player)) {
            return;
        }

		// cast victim and damager to Player
        Player victim = (Player) e.getEntity();
        Player damager = (Player) damagerEntity;

		// check damager player is playing this minigame
        if (!containsPlayer(damager)) {
            return;
        }
    }
}
```

- Give 1 score to damager
- Set victim's live to false
```java
@Override
protected void onEvent(Event event) {
    if (event instanceof EntityDamageByEntityEvent) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
        
        // damager entity
        Entity damagerEntity = e.getDamager();

		// check damager entity is a player
        if (!(damagerEntity instanceof Player)) {
            return;
        }

		// cast victim and damager to Player
        Player victim = (Player) e.getEntity();
        Player damager = (Player) damagerEntity;

		// check damager player is playing this minigame
        if (!containsPlayer(damager)) {
            return;
        }

        // Give 1 score to damager
        plusScore(damager, 1);

        // Set victim's live to false
        setLive(victim, false);
    }
}
```

# 5. Full source code
```java
public class OnePunch extends SoloBattleMiniGame {

	public OnePunch() {
		super("OnePunch", 2, 10, 60, 10);

		getSetting().setIcon(Material.GRASS);

		getCustomOption().set(Option.PVP, true);
	}

	@Override
	protected void initGame() {
	}

	@Override
	protected void onEvent(Event event) {
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			
			Entity damagerEntity = e.getDamager();
			if (!(damagerEntity instanceof Player)) {
				return;
			}

			Player victim = (Player) e.getEntity();
			Player damager = (Player) damagerEntity;

			if (!containsPlayer(damager)) {
				return;
			}
			
			plusScore(damager, 1);
			
			setLive(victim, false);
		}
	}

	@Override
	protected List<String> tutorial() {
		List<String> tutorial = new ArrayList<>();
		tutorial.add("Damager player: +1");
		tutorial.add("Victim player: death");

		return tutorial;
	}

}
```

# Play Video
- [Youtube](https://www.youtube.com/watch?v=iKNFxQwEWAw)

---

# After finish
- Check out **[How to export minigame](export-tutorial.md)**