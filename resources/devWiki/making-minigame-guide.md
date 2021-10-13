# Description
- Make minigame with [API] document



# Tutorial
<a href="https://youtu.be/">
<img src="youtube-minigame-dev-tutorial-thumbnail.png" width="50%" ></img>
</a>
- In making...

---

# Order
## 1. Setup dev environment
- [Link](https://github.com/worldbiomusic/MiniGameWorld/blob/main/resources/userWiki/making-minigame-wiki.md#how-to-set-dev-environment)


## 2. Create class
### Essential overriding methods
- `initGameSetting()`: executed every time when minigame starts
- `processEvent()`: executed when event is passed to minigame
- `registerTutorial()`: tutorial string

### Frame class
- Select with minigame features
#### `SoloMiniGame`
- Solo play
##### Examples
- [FitTool](https://github.com/worldbiomusic/MiniGameWorld/blob/main/src/com/worldbiomusic/minigameworld/minigameframes/games/FitTool.java)

#### `SoloBattleMiniGame`
- Individual battle play
##### Examples
- [FallingBlock](https://github.com/worldbiomusic/MiniGameWorld/blob/main/src/com/worldbiomusic/minigameworld/minigameframes/games/FallingBlock.java)
- [PVP](https://github.com/worldbiomusic/MiniGameWorld/blob/main/src/com/worldbiomusic/minigameworld/minigameframes/games/PVP.java)
- [RandomScore](https://github.com/worldbiomusic/MiniGameWorld/blob/main/src/com/worldbiomusic/minigameworld/minigameframes/games/RandomScore.java)
- [RockScissorPaper](https://github.com/worldbiomusic/MiniGameWorld/blob/main/src/com/worldbiomusic/minigameworld/minigameframes/games/RockScissorPaper.java)
- [ScoreClimbing](https://github.com/worldbiomusic/MiniGameWorld/blob/main/src/com/worldbiomusic/minigameworld/minigameframes/games/ScoreClimbing.java)
- [SuperMob](https://github.com/worldbiomusic/MiniGameWorld/blob/main/src/com/worldbiomusic/minigameworld/minigameframes/games/SuperMob.java)

#### `TeamMiniGame`
- Cooperative play
##### Examples
- [BreedMob](https://github.com/worldbiomusic/MiniGameWorld/blob/main/src/com/worldbiomusic/minigameworld/minigameframes/games/BreedMob.java)
- [RemoveBlock](https://github.com/worldbiomusic/MiniGameWorld/blob/main/src/com/worldbiomusic/minigameworld/minigameframes/games/RemoveBlock.java)

#### `TeamBattleMiniGame`
- Team battle play
##### Examples
- [HiddenArcher](https://github.com/worldbiomusic/MiniGameWorld/blob/main/src/com/worldbiomusic/minigameworld/minigameframes/games/HiddenArcher.java)
- [MoreHit](https://github.com/worldbiomusic/MiniGameWorld/blob/main/src/com/worldbiomusic/minigameworld/minigameframes/games/MoreHit.java)
- [PassMob](https://github.com/worldbiomusic/MiniGameWorld/blob/main/src/com/worldbiomusic/minigameworld/minigameframes/games/PassMob.java)


## 3. Set [Options](#Options)
- Can set various options for minigame
- You must check [custom options](#--minigamecustomoption)

## 4.Register MiniGame
- Register MiniGame with registerMiniGame() of MiniGameWorld.create()
```java
MiniGameWorld mw = MiniGameWorld.create();
mw.registerMiniGame(new FitTool());
```

---

# Options
## - MiniGameSetting
- Fundamental settings of minigame
- `settingFixed`: fix value: `minPlayerCount`, `maxPlayerCount`, `timeLimit`, `customData` (can't edit in config)
- `passUndetectableEvent`: pass all event to minigame (must check event in detail (e.g. check player from event is playing current minigame))
- `rankOrder`: rank order method by score
### How to use
```java
public PassMob() {
  super("PassMob", 2, 60 * 3, 10);
  // settings
  this.getSetting().setPassUndetectableEvent(true);
  this.getSetting().setRankOrder(RankOrder.ASCENDING);
  this.getSetting().setIcon(Material.OAK_FENCE);
}
```

## - MiniGameCustomOption
- All custom options are in `custom-data` section
- `CHATTING`: whether chat event cancel (default: `true`)
- `SCORE_NOTIFYING`: whether notify score change (default: `true`)
- `BLOCK_BREAK`: whether player can break block (default: `false`)
- `BLOCK_PLACE`: whether player can place block (default: `false`)
- `PVP`: whether players can pvp (default: `false`)
- `PVE`: whether players can damage entity (not player) (default: `true`)
- `INVENTORY_SAVE`: whether inventory save (default: `true`)
- `MINIGAME_RESPAWN`: whether player will be respawn in minigame location (default: `true`)
### How to use
```java
public PassMob() {
  super("PassMob", 2, 60 * 3, 10);
  // options
  this.getCustomOption().set(Option.MINIGAME_RESPAWN, false);
}
```

## - Task Management
- Can manage task easily
- `Register`: `getTaskManager().registerTask("name", new Runnable() { // code });` in `registerTasks()`
- `Run`: `getTaskManager().runTask("name");` in anywhere
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
- Use `handleException()` with overriding
- Related player must leave the minigame



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

## - Reservation Task
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
