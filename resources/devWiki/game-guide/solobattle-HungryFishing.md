# Before start
Check out **[API Introduction]** and **[Setup guide]**

---



# Hungry Fishing
**At least 0.8.1 API is required**

Hello everyone, I'm back here with new solo battle minigame tutorial. 

The minigame we're going to make this time is **Hungry Fishing**. Rule is very simple. Player has to fish and catch the food to fill the player's hunger(food level)

In additional, we will learn about how to add **custom-data** to the minigame in this tutorial.



# 1. Rules
1. If a player fill own hunger, get `left play time` scores and change gamemode to spectator
2. When a item caught by fishing, change to the item among those in the custom data with `chance` percent
3. If a player failed to catch item by fishing, decrease hunger

# 2. Create a class
Create a `HungryFishing` class in your package and extend `SoloBattleMiniGame` class with override methods. (Make the constructor to pass the values directly)
```java
public class HungryFishing extends SoloBattleMiniGame {

	public HungryFishing() {
		super("HungryFishing", 2, 10, 60 * 5, 20);
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

Set icon and add options to the constructor for game play.
```java
public HungryFishing() {
  super("HungryFishing", 2, 10, 60 * 5, 20);

  // settings
  setting().setIcon(Material.FISHING_ROD);

  // options
  customOption().set(Option.COLOR, ChatColor.AQUA);
  customOption().set(Option.PLAYER_HURT, false);
  customOption().set(Option.PVP, false);
  customOption().set(Option.PVE, false);
}
```




# 3. Setup custom data

We will add custom data for more configurable gameplay. First, add variables like below.
```java
public class HungryFishing extends SoloBattleMiniGame {

	private Map<Material, Integer> catchItems;
	private int hunger;
	private int failHunger;
	...
}
```

Override `initCustomData()` and `loadCustomData()` methods.
```java
@Override
protected void initCustomData() {
	super.initCustomData();
}

@Override
public void loadCustomData() {
	super.loadCustomData();
}
```

In `initCustomData()`, we have to initialize(serialize) the custom data variables.
- `cath-items`: item list with change percent when a player catch a fish
- `hunger`: player's starting hunger
- `fail-hunger`: amount when a player failed to catch a fish
```java
@Override
protected void initCustomData() {
	super.initCustomData();

	Map<String, Object> data = customData();

	// catch-items
	Map<String, Integer> itemList = new HashMap<>();
	itemList.put(Material.COOKIE.name(), 40);
	itemList.put(Material.MELON_SLICE.name(), 30);
	itemList.put(Material.CARROT.name(), 20);
	itemList.put(Material.COOKED_PORKCHOP.name(), 10);
	data.put("catch-items", itemList);

	// hunger
	data.put("hunger", 1);

	// fail-hunger
	data.put("fail-hunger", 2);
}
```

In `loadCustomData()`, we have to load(deserialize) the custom data variables.
```java
@Override
public void loadCustomData() {
	super.loadCustomData();

	Map<String, Object> data = customData();

	// catch-items
	this.catchItems = new HashMap<>();
	Map<String, Integer> itemList = (Map<String, Integer>) data.get("catch-items");
	itemList.forEach((k, v) -> catchItems.put(Material.valueOf(k), v));

	// hunger
	this.hunger = (int) data.get("hunger");

	// fail-hunger
	this.failHunger = (int) data.get("fail-hunger");
}
```

And, make `getRandomItem()` method for our future.
```java
// get random item in the catch-items list (custom data)
Material getRandomItem() {
	int random = new Random().nextInt(100);

	int range = 0;
	for (Entry<Material, Integer> entry : this.catchItems.entrySet()) {
		Material item = entry.getKey();
		int percent = entry.getValue();

		range += percent;

		if (random < range) {
			return item;
		}
	}

	// for safe
	return Material.COOKIE;
}
```



# 4. Process events
We have to process two events: `PlayerFishEvent` to detect fishing and `FoodLevelChangeEvent` for checking player hunger is full or not.

## 4.1 PlayerFishEvent
On this event, we will process `catch` and `fail`.

Create `onFishEvent()`.
```java
@Override
protected void onEvent(Event event) {
	if (event instanceof PlayerFishEvent) {
		onFishEvent((PlayerFishEvent) event);
	}
}

void onFishEvent(PlayerFishEvent event) {
}
```

Create `onCatch()` and `onFail()` for clean code and invoke them in the `onFishEvent()`.
```java
void onFishEvent(PlayerFishEvent event) {
	State state = event.getState();

	if (state == State.CAUGHT_FISH) {
		onCatch(event);
	} else if (state == State.FAILED_ATTEMPT) {
		onFail(event);
	}
}

void onCatch(PlayerFishEvent event) {
}

void onFail(PlayerFishEvent event) {
}
```

In `onCatch()` method, we have to replace the caught item to random catch-items(custom data) with message, title and sound.
```java
void onCatch(PlayerFishEvent event) {
	Player p = event.getPlayer();

	Entity entity = event.getCaught();
	ItemStack item = ((Item) entity).getItemStack();

	// replace with random item
	item.setType(getRandomItem());

	// send msg and title
	sendMessages(p.getName() + ChatColor.GREEN + " caught a item");
	sendTitle(p, ChatColor.GREEN + "Catch!", "");

	// sound
	p.playSound(p.getLocation(), Sound.BLOCK_BELL_USE, 10.0F, 1.0F);
}
```

In `onFail()` method, we have to decrease player's hunger(custom data) with message, title and sound.
```java
void onFail(PlayerFishEvent event) {
	Player p = event.getPlayer();

	// decrease player hunger
	int playerHunger = p.getFoodLevel() - this.failHunger;
	if (playerHunger < 1) {
		playerHunger = 1;
	}
	p.setFoodLevel(playerHunger);

	// send msg and title
	sendMessages(p.getName() + ChatColor.RED + " failed to catch item");
	sendMessage(p, "You lost " + this.failHunger + " hunger");
	sendTitle(p, ChatColor.RED + "Failed", "");

	// sound
	p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 10.0F, 1.0F);
}
```

## 4.2 FoodLevelChangeEvent
When a player eats food, we can detect player's hunger.

Make `onFoodLevelChange()` method and call from the `onEvent()`.
```java
@Override
protected void onEvent(Event event) {
	if (event instanceof PlayerFishEvent) {
		onFishEvent((PlayerFishEvent) event);
	} else if (event instanceof FoodLevelChangeEvent) {
		onFoodLevelChange((FoodLevelChangeEvent) event);
	}
}

void onFoodLevelChange(FoodLevelChangeEvent event) {
}
```


In `onFoodLevelChange()` method, we have to check two.
1. Player's hunger is fulfilled or not
2. All players' hunger is fulfilled or not

```java
void onFoodLevelChange(FoodLevelChangeEvent event) {
	Player p = (Player) event.getEntity();
	int foodLevel = event.getFoodLevel();

	// check food level is max
	if (foodLevel >= 20) {
		// give score
		plusScore(p, leftPlayTime());

		// msg, sound
		sendMessages(ChatColor.GREEN + p.getName() + " filled all hunger!");
		p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 10.0F, 1.0F);

		// change player to spectator
		p.setGameMode(GameMode.SPECTATOR);

		// check game finish condition
		if (checkFinish()) {
			finishGame();
		}
	}
}

// check all players hunger is max or not
boolean checkFinish() {
	for (Player p : players()) {
		if (p.getGameMode() != GameMode.SPECTATOR) {
			return false;
		}
	}
	return true;
}
```



# 5. ETC
We almost implemented minigame rules. But there still remains something to do.

When the game starts, players need fishing rod to fish and hunger must be set to the starting hunger(custom data).
```java
@Override
protected void onStart() {
	super.onStart();

	players().forEach(p -> {
		// set start hungry level
		p.setFoodLevel(this.hunger);

		// give fishing rod
		ItemStack fishingRod = new ItemStack(Material.FISHING_ROD);
		ItemMeta meta = fishingRod.getItemMeta();
		meta.setUnbreakable(true);
		fishingRod.setItemMeta(meta);
		p.getInventory().addItem(fishingRod);
	});
}
```

Finally, register for the tutorial.
```java
@Override
protected List<String> tutorial() {
	return List.of("Fish and fill your hunger with items!", "If you fail to catch, you will be hungry");
}
```



# 6. Full source code
```java
public class HungryFishing extends SoloBattleMiniGame {

	private Map<Material, Integer> catchItems;
	private int hunger;
	private int failHunger;

	public HungryFishing() {
		super("HungryFishing", 2, 10, 60 * 5, 20);

		// settings
		setting().setIcon(Material.FISHING_ROD);

		// options
		customOption().set(Option.COLOR, ChatColor.AQUA);
		customOption().set(Option.PLAYER_HURT, false);
		customOption().set(Option.PVP, false);
		customOption().set(Option.PVE, false);
	}

	@Override
	protected void initCustomData() {
		super.initCustomData();

		Map<String, Object> data = customData();

		// catch-items
		Map<String, Integer> itemList = new HashMap<>();
		itemList.put(Material.COOKIE.name(), 40);
		itemList.put(Material.MELON_SLICE.name(), 30);
		itemList.put(Material.CARROT.name(), 20);
		itemList.put(Material.COOKED_PORKCHOP.name(), 10);
		data.put("catch-items", itemList);

		// hunger
		data.put("hunger", 1);

		// fail-hunger
		data.put("fail-hunger", 2);
	}

	@Override
	public void loadCustomData() {
		super.loadCustomData();

		Map<String, Object> data = customData();

		// catch-items
		this.catchItems = new HashMap<>();
		Map<String, Integer> itemList = (Map<String, Integer>) data.get("catch-items");
		itemList.forEach((k, v) -> catchItems.put(Material.valueOf(k), v));

		// hunger
		this.hunger = (int) data.get("hunger");

		// fail-hunger
		this.failHunger = (int) data.get("fail-hunger");
	}

	@Override
	protected void initGame() {
	}

	@Override
	protected void onEvent(Event event) {
		if (event instanceof PlayerFishEvent) {
			onFishEvent((PlayerFishEvent) event);
		} else if (event instanceof FoodLevelChangeEvent) {
			onFoodLevelChange((FoodLevelChangeEvent) event);
		}
	}

	void onFishEvent(PlayerFishEvent event) {
		State state = event.getState();

		if (state == State.CAUGHT_FISH) {
			onCatch(event);
		} else if (state == State.FAILED_ATTEMPT) {
			onFail(event);
		}
	}

	void onCatch(PlayerFishEvent event) {
		Player p = event.getPlayer();

		Entity entity = event.getCaught();
		ItemStack item = ((Item) entity).getItemStack();

		// replace with random item
		item.setType(getRandomItem());

		// send msg and title
		sendMessages(p.getName() + ChatColor.GREEN + " caught a item");
		sendTitle(p, ChatColor.GREEN + "Catch!", "");

		// sound
		p.playSound(p.getLocation(), Sound.BLOCK_BELL_USE, 10.0F, 1.0F);
	}

	void onFail(PlayerFishEvent event) {
		Player p = event.getPlayer();

		// decrease player hunger
		int playerHunger = p.getFoodLevel() - this.failHunger;
		if (playerHunger < 1) {
			playerHunger = 1;
		}
		p.setFoodLevel(playerHunger);

		// send msg and title
		sendMessages(p.getName() + ChatColor.RED + " failed to catch item");
		sendMessage(p, "You lost " + this.failHunger + " hunger");
		sendTitle(p, ChatColor.RED + "Failed", "");

		// sound
		p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 10.0F, 1.0F);
	}

	Material getRandomItem() {
		int random = new Random().nextInt(100);

		int range = 0;
		for (Entry<Material, Integer> entry : this.catchItems.entrySet()) {
			Material item = entry.getKey();
			int percent = entry.getValue();

			range += percent;

			if (random < range) {
				return item;
			}
		}

		// for safe
		return Material.COOKIE;
	}

	void onFoodLevelChange(FoodLevelChangeEvent event) {
		Player p = (Player) event.getEntity();
		int foodLevel = event.getFoodLevel();

		// check food level is max
		if (foodLevel >= 20) {
			// give score
			plusScore(p, leftPlayTime());

			// msg, sound
			sendMessages(ChatColor.GREEN + p.getName() + " filled all hunger!");
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 10.0F, 1.0F);

			// change player to spectator
			p.setGameMode(GameMode.SPECTATOR);

			// check game finish condition
			if (checkFinish()) {
				finishGame();
			}
		}
	}

	// check all players hunger is max or not
	boolean checkFinish() {
		for (Player p : players()) {
			if (p.getGameMode() != GameMode.SPECTATOR) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();

		players().forEach(p -> {
			// set start hungry level
			p.setFoodLevel(this.hunger);

			// give fishing rod
			ItemStack fishingRod = new ItemStack(Material.FISHING_ROD);
			ItemMeta meta = fishingRod.getItemMeta();
			meta.setUnbreakable(true);
			fishingRod.setItemMeta(meta);
			p.getInventory().addItem(fishingRod);
		});
	}

	@Override
	protected List<String> tutorial() {
		return List.of("Fish and fill your hunger with items!", "If you fail to catch, you will be hungry");
	}

}
```

# 7. Play video
[Youtube](https://youtu.be/ZKx_IU-eEcE)


---

# After making
Check out **[Build guide]**













[API Introduction]: introduction.md
[Setup guide]: setup.md
[Build guide]: build.md
