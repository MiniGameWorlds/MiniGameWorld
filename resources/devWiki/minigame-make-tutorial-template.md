# Before start
- **[What is MiniGameWorld API](api-intro.md)**
- Also check out **[Environment Setup Tutorial](environment-setup-tutorial.md)**

---

# <game-type> type
- 

# 1. Rules
- 

# 2. Create a class
- Create a `<minigame>` minigame class in your package 
```java
public class <minigame> {
	
}
```

- After create, extends `<game-type>`
```java
public class <minigame> extends <game-type> {
	
}
```

- Then, we need to add constructor and some overrided methods
```java
public class <minigame> extends <game-type> {

	// constructor: setup minigame info
	public <minigame>(String title, int minPlayers, int maxPlayers, int playTime, int waitingTime) {
		super(title, minPlayers, maxPlayers, playTime, waitingTime);
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
- Remove auto-generated constructor and create new constructor which has no argument
- Pass constants to `super()` 
- Unit of `playTime` and `waitingTime` is second

```java
public class <minigame> extends <game-type> {

	public <minigame>() {
		// title, min player, max player, play time, waiting time
		super("<minigame>", 2, 10, 60, 10);
	}

	...
}
```

- Setup minigame settings and options
```java
public class <minigame> extends <game-type> {

	public <minigame>() {
		super("<minigame>", 2, 10, 60, 10);
		
        // GUI menu icon
		setting().setIcon(Material.GRASS);
		
		// custom option
		customOption().set(Option., );
	}

	...
}
```

- Register tutorial
```java
public class <minigame> extends <game-type> {
	...

	@Override
	protected List<String> tutorial() {
		List<String> tutorial = new ArrayList<>();
		tutorial.add("");
		
		return tutorial;
	}
	
}
```

# 4. Process events
- All events related with a player playing your minigame will be passed to `onEvent()` (Details: [Detectable Event List](detectable-event-list.md))

```java
@Override
protected void onEvent(Event event) {
    // check event type
    if (event instanceof ) {
        
    }
}
```


# 5. Full source code
```java

```

# Play Video
- [Youtube]()

---

# After finish
- Check out **[How to export minigame](export-tutorial.md)**