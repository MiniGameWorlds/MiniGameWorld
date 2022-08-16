# Before start
Check out **[API Introduction]** and **[Setup guide]**

---

# JumpMap
- We will make a simple JumpMap minigame (same with Parkour)
- And... game will be finished when a player breaks a glowstone  

- How does it become a JumpMap minigame with just breaking glowstone?
- After making a jump map and isn't it enough to just place a glowstone on top of it?


- Yes, actually, I chose this as a very simple minigame on purpose
- It should be simple at first, isn't it?  



- Before making, let's think about the rules of the minigame
1. 1 player game
2. Plus 1 score when a player break the glowstone, and then teleport to minigame spawn so that player can challenge it more
3. Prevent player hurt
4. Prevent player hunger change

- These are the only rules of the jumpmap minigame  



- Now, let's go into the codes

# 1. Create a class
- Create a `JumpMap` class in your package
```java
public class JumpMap {

}
```

- After then, the class has to extend `SoloMiniGame` class (~~Solo~~)
```java
import com.minigameworld.frames.SoloMiniGame;

public class JumpMap extends SoloMiniGame{

}
```


- Next, add the constructor and methods of the extended class
```java
import java.util.List;

import org.bukkit.event.Event;

import com.minigameworld.frames.SoloMiniGame;

public class JumpMap extends SoloMiniGame{

	public JumpMap(String title, int playTime, int waitingTime) {
		super(title, playTime, waitingTime);
	}

	@Override
	protected void initGame() {
	}

	@Override
	protected void onEvent(Event event) {
	}

	@Override
	protected List<String> tutorial() {
		return null;
	}

}
```

- Before next, we have something to do
- Remove auto generated constructor and create a new constructor with no arguments
- And pass minigame setting values to `super()`
```java
public JumpMap() {
	// title, play time (sec), waiting time (sec)
	super("JumpMap", 120, 10);
}

```

- Let's take a look at the methods that have been added one by one  

```java
public JumpMap() {
  super("JumpMap", 120, 10);
}
```
- This is just constructor. (just passes the minigame settings)




```java
@Override
protected void initGame() {
}
```
- Called before the players join



```java
@Override
protected void onEvent(Event event) {
}
```
- Event only related to this minigame will passed the here 
- You don't have to register event handlers



```java
@Override
protected List<String> tutorial() {
  return null;
}
```
- This returns your minigame tutorial. 
- No matter how simple it is, we need to know what kind of game it is, right?


# 2. Process events
- Now, we will implements the second rule `Plus 1 score when a player break the glowstone, and then teleport to minigame spawn so that player can challenge it more` (~~so long~~)
- This is related with **action**, so this has to be processed with event
- Therefore, we will detect event in `onEvent()` with `instanceof` to detect `BlockBreakEvent`
```java
@Override
protected void onEvent(Event event) {
  if (event instanceof BlockBreakEvent) {

  }
}
```


- Convert `Event` type to `BlockBreakEvent` and get a player and broken block from the event
```java
@Override
protected void onEvent(Event event) {
  if (event instanceof BlockBreakEvent) {
    // convert event type
    BlockBreakEvent e = (BlockBreakEvent) event;
    
    // get a player
    Player player = e.getPlayer();
    
    // get broken block
    Block block = e.getBlock();
  }
}
```

- Now, there is only one thing we need to check, what exactly?
- All we have to do is check whether the broken block is glowstone or not  
```java
@Override
protected void onEvent(Event event) {
  if (event instanceof BlockBreakEvent) {
    BlockBreakEvent e = (BlockBreakEvent) event;
    Player player = e.getPlayer();
    Block block = e.getBlock();

    // check broken block is glowstone
    if (block.getType() == Material.GLOWSTONE) {

    }
  }
}
```


- If the event enters the `if` statement, the player has successfully clear the JumpMap once!
- Then, according to the second rule, we have to teleport the player to the minigame spawn location and give 1 score
```java
@Override
protected void onEvent(Event event) {
  if (event instanceof BlockBreakEvent) {
    BlockBreakEvent e = (BlockBreakEvent) event;
    Player player = e.getPlayer();
    Block block = e.getBlock();

    if (block.getType() == Material.GLOWSTONE) {
      // plus 1 score
      plusScore(1);

      // teleport player to minigame spawn
      player.teleport(getLocation());

      // send message
      player.sendMessage("You clear the jumpmap!");
    }
  }
}
```




# 3. Options
- Now, before the end heh.... third(`Prevent player hurt`) and fourth(`Prevent player hunger change`) rules remain
- But these need only 3 lines!! how??

- Just put custom options to the constructor
- (But this API has feature that the server admin can edit minigame config so jumpmap game can be a another gamemode like PVP jumpmap...)
```java
public JumpMap() {
	super("JumpMap", 120, 10);

	// options
	getCustomOption().set(Option.PLAYER_HURT, false); // disable player hurt
	getCustomOption().set(Option.PVE, false); // disable mob attack
	getCustomOption().set(Option.FOOD_LEVEL_CHANGE, false); // prevent player hunger change
}
```

- And set the icon of the GUI menu to `ACACIA_STAIRS`
```java
public JumpMap() {
	super("JumpMap", 120, 10);

	getCustomOption().set(Option.PLAYER_HURT, false);
	getCustomOption().set(Option.PVE, false);
	getCustomOption().set(Option.FOOD_LEVEL_CHANGE, false);

	// setting
	getSetting().setIcon(Material.ACACIA_STAIRS); // menu icon
}
```



# 4. Tutorial
- It's already time to finish the minigame
- My guess world be about 70 lines of code, overall it's simple, isn't it?  

- Now all that's left is a tutorial that tells the player what the hexx this minigame is
```java
@Override
protected List<String> tutorial() {
	List<String> tutorial = new ArrayList<>();
	tutorial.add("Challenge the jump map and get 1 score");
	return tutorial;
}
```


- Congraturations, we made a simple minigame
- This is just a simple minigame, but beginning of anything is shabby

# 5. Full source code
- Check the full source code below and let's test the plugin together
```java
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import com.minigameworld.frames.SoloMiniGame;
import com.minigameworld.frames.helpers.MiniGameCustomOption.Option;

public class JumpMap extends SoloMiniGame {

	public JumpMap() {
		// title, play time (sec), waiting time (sec)
		super("JumpMap", 120, 10);

		// options
		getCustomOption().set(Option.PLAYER_HURT, false); // disable player hurt
		getCustomOption().set(Option.PVE, false); // disable mob attack
		getCustomOption().set(Option.FOOD_LEVEL_CHANGE, false); // prevent player hunger change

		// setting
		getSetting().setIcon(Material.ACACIA_STAIRS); // menu icon
	}

	// called when a game inits
	@Override
	protected void initGame() {
	}

	// events will be passed to here
	@Override
	protected void onEvent(Event event) {
		if (event instanceof BlockBreakEvent) {
			// convert event type
			BlockBreakEvent e = (BlockBreakEvent) event;
			// get a player
			Player player = e.getPlayer();
			// get broken block
			Block block = e.getBlock();

			// check broken block is glowstone
			if (block.getType() == Material.GLOWSTONE) {
				// plus 1 score
				plusScore(1);

				// teleport player to minigame spawn
				player.teleport(getLocation());

				// send message
				player.sendMessage("You clear the jumpmap!");
			}
		}
	}

	// tutorial of this minigame
	@Override
	protected List<String> tutorial() {
		List<String> tutorial = new ArrayList<>();
		tutorial.add("Challenge the jump map and get 1 score");
		return tutorial;
	}

}
```


# Play Video
- [Youtube](https://youtu.be/02q7DUxWjY4)

---

# After making
Check out **[Build guide]**












[API Introduction]: introduction.md
[Setup guide]: setup.md
[Build guide]: build.md