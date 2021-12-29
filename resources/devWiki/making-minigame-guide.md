# Description
- Make a minigame with [API] docs



# Tutorial
<a href="https://www.youtube.com/watch?v=ibilvmzcdzs&list=PLOyhTkb3nnYbBtEdS38nkIpyU8RM-pEZd&index=1">
<img src="youtube-minigame-dev-tutorial-thumbnail.png" width="50%" ></img>
</a>


---

# Order
## 1. Setup dev environment
- [Link](https://github.com/MiniGameWorlds/MiniGameWorld/blob/main/resources/devWiki/making-minigame-home.md#how-to-set-dev-environment)


## 2. Create class
- Select a minigame frame

### `SoloMiniGame`
- _Solo play_
- [FitTool](https://github.com/MiniGameWorlds/AllMiniGames/blob/main/src/com/worldbiomusic/allgames/games/solo/FitTool.java)


### `SoloBattleMiniGame`
- _Individual battle play_
- [FallingBlock](https://github.com/MiniGameWorlds/AllMiniGames/blob/main/src/com/worldbiomusic/allgames/games/solobattle/FallingBlock.java)
- [PVP](https://github.com/MiniGameWorlds/AllMiniGames/blob/main/src/com/worldbiomusic/allgames/games/solobattle/PVP.java)
- [RandomScore](https://github.com/MiniGameWorlds/AllMiniGames/blob/main/src/com/worldbiomusic/allgames/games/solobattle/RandomScore.java)
- [RockScissorPaper](https://github.com/MiniGameWorlds/AllMiniGames/blob/main/src/com/worldbiomusic/allgames/games/solobattle/RockScissorPaper.java)
- [ScoreClimbing](https://github.com/MiniGameWorlds/AllMiniGames/blob/main/src/com/worldbiomusic/allgames/games/solobattle/ScoreClimbing.java)
- [SuperMob](https://github.com/MiniGameWorlds/AllMiniGames/blob/main/src/com/worldbiomusic/allgames/games/solobattle/SuperMob.java)

### `TeamMiniGame`
- _Cooperative play_
- [BreedMob](https://github.com/MiniGameWorlds/AllMiniGames/blob/main/src/com/worldbiomusic/allgames/games/team/BreedMob.java)
- [RemoveBlock](https://github.com/MiniGameWorlds/AllMiniGames/blob/main/src/com/worldbiomusic/allgames/games/team/RemoveBlock.java)

### `TeamBattleMiniGame`
- _Team battle play_
- [HiddenArcher](https://github.com/MiniGameWorlds/AllMiniGames/blob/main/src/com/worldbiomusic/allgames/games/teambattle/HiddenArcher.java)
- [MoreHit](https://github.com/MiniGameWorlds/AllMiniGames/blob/main/src/com/worldbiomusic/allgames/games/teambattle/MoreHit.java)
- [PassMob](https://github.com/MiniGameWorlds/AllMiniGames/blob/main/src/com/worldbiomusic/allgames/games/teambattle/PassMob.java)


## 3. Essential methods
- `initGameSetting()`: executed every time when minigame starts
- `processEvent()`: executed when event is passed to minigame
- `registerTutorial()`: tutorial string


## 4. Set [Options](#Options)
- Can set various options for minigame
- You must check [custom options](#--minigamecustomoption) for your minigame environment


## 5.Register MiniGame
- Register MiniGame with registerMiniGame() of MiniGameWorld.create()
```java
MiniGameWorld mw = MiniGameWorld.create("x.x.x");
mw.registerMiniGame(new FitTool());
```

---

# Options
## - MiniGameSetting
- Fundamental settings of minigame (See api doc for Init value)
- `settingFixed`: fix value: `minPlayerCount`, `maxPlayerCount`, `timeLimit`, `customData` (can't edit in config)
- `passUndetectableEvent`: pass all event to minigame (must check event in detail (e.g. check player from event is playing current minigame))
### How to use
```java
public PassMob() {
  super("PassMob", 2, 60 * 3, 10);
  // settings
  this.getSetting().setPassUndetectableEvent(true);
  this.getSetting().setIcon(Material.OAK_FENCE);
}
```

## - MiniGameCustomOption
- Below custom options are created in `custom-data` section by default (See api doc for Init value)
- `CHATTING`: whether chat event cancel
- `SCORE_NOTIFYING`: whether notify score change
- `BLOCK_BREAK`: whether player can break block
- `BLOCK_PLACE`: whether player can place block
- `PVP`: whether players can pvp
- `PVE`: whether players can damage entity (not player)
- `INVENTORY_SAVE`: whether inventory save
- `MINIGAME_RESPAWN`: whether player will be respawn in minigame location
- `LIVE_GAMEMODE`: gamemode when a player join a minigame
- `DEAD_GAMEMODE`: gamemode when a player's live changed to false in a minigame
- `COLOR`: minigame custom color
- `FOOD_LEVEL_CHANGE`: whether player's food level(hunger) changes
- `PLAYER_HURT`: whether a player damaged by something 
### How to use
```java
public PassMob() {
  super("PassMob", 2, 60 * 3, 10);
  // custom options
  this.getCustomOption().set(Option.MINIGAME_RESPAWN, false);
}
```

## - Task Management
- Can manage task easily
- `Register`: `getTaskManager().registerTask("name", new Runnable() { // code });` in anywhere
- `Run`: `getTaskManager().runTask("name");` in anywhere after register
- Do not register/run system task(`_waitingTimer`, `_finishTimer`)
- Do not register task with `BukkitRunnable`
### How to register
```java
@Override
protected void registerTasks() {
  // register task
  this.getTaskManager().registerTask("task1", new Runnable() {

    @Override
    public void run() {
      // code
    }
  });
}
```
### How to use
```java
@Override
protected void processEvent(Event event) {
  // code
  this.getTaskManager().runTask("task1");
}
```



## - Exception handling
- GameException: `PLAYER_QUIT_SERVER`, `CUSTOM`
- Use `handleGameException()` with overriding
- Related player must leave the minigame

### How to handle exception
```java
@Override
protected void handleGameException(Player p, Exception exception) {
	super.handleGameException(p, exception);

	if (exception == Exception.PLAYER_QUIT_SERVER) {
		// handle exception
	} else if (exception == Exception.CUSTOM) {
		String reason = exception.getDetailedReason();
		Object obj = exception.getDetailedObj();

		// handle exception

	}
}
```

### How to create exception
```java
public void createServerEvent(Player p) {
	MiniGame.Exception ex = MiniGame.Exception.CUSTOM;
	ex.setDetailedReason("SERVER_EVENT_TIME");
	ex.setDetailedObj(something);
 	MiniGameWorld mw = MiniGameWorld.create("x.x.x");
  	mw.createException(p, ex);
 }
```

## - Custom Data
- Minigame Developer can add custom data
- Minigame User can play and edit custom data
### How to register
- Override `registerCustomData()` and add data
```java
@Override
protected void registerCustomData() {
  Map<String, Object> customData = this.getCustomData();
  customData.put("health", 30);
  List<ItemStack> items = new ArrayList<>();
  items.add(new ItemStack(Material.STONE_SWORD));
  customData.put("items", items);
}
```
### How to load/use
- Override `loadCustomData()` and load data
```java
@Override
protected void loadCustomData() {
  // set health scale
  this.health = (int) this.getCustomData().get("health");
  // give kit tool
  List<ItemStack> items = (List<ItemStack>) this.getCustomData().get("items");
  items.forEach(item -> p.getInventory().addItem(item));
}
```

## - Task Reservation
- `runTaskAfterStart()`: executed after minigame started
- `runTaskBeforeFinish()`: executed before minigame finishes
- `runTaskAfterFinish()`: executed after minigame finished
### Example
```java
@Override
protected void runTaskAfterStart() {
  super.runTaskAfterStart();
  // give kits
  for (Player p : this.getPlayers()) {
    InventoryTool.addItemToPlayer(p, new ItemStack(Material.IRON_SWORD));
    InventoryTool.addItemToPlayer(p, new ItemStack(Material.BOW));
    InventoryTool.addItemToPlayer(p, new ItemStack(Material.ARROW, 64));
  }
}
```

## - Players
- `containsPlayer()`: check player is contained
- `getPlayers()`: get minigame participants List
- `getPlayerCount()`: get participants count
- `getLivePlayers()`: return live players
- `randomPlayer()`: return random players among participants
- `sendMessage()`: send message to player
- `sendTitle()`: send title to player


## - PlayerData
- manage `score`, `live`
- `getPlayerData()`: get player data of minigame (`score`, `live`)
- `plusScore()`: plus player score
- `minusScore()`: minus player score
- `setLive()`: set player live
- `isLive()`: check player is live


## - etc
- `finishGame()`: finish minigame
- `getLeftWaitingTime()`: get left waiting time (sec)
- `getLeftFinishTime()`: get left time to finish (sec)


---

# Caution
## Player state management
- MiniGameWorld manages and restores player's states when join / leave
> `Inventory`, `Health`, `Food level`, `Exp`, `Potion Effects`, `Glowing`, `Hiding` and `Game Mode`
- If the minigame has changed any of the unmanageable state in the list above, must to restore all the changed state at the game finished using `runTaskBeforeFinish()`

## Detectable Events
- MiniGameWorld only passes detectable events that can extract players from a event
- Detectable events are only passed to player's playing minigame
- Can use all sub-events of detectable events (i.e.  `PlayerDeathEvent`, `PlayerJoinEvent`, `PlayerJumpEvent`...etc of `PlayerEvent`)
- If **needs not related with player event**, set `passUndetectableEvent` setting to true of `MiniGameSetting`
- [Detectable Event List](detectable-event-list.md)

## Override
- Almost overrided method shold have `super.method()`

## Player Death
- Player can't return to joined location from minigame location, if player is dead state when minigame finished (don't let the player die with using any other ways when minigame finished)

## Flow
<img src="flow.png" width=50%></img>

[API]: https://worldbiomusic.github.io/MiniGameWorld/
