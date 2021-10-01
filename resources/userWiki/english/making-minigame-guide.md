# Description
## []()

---

# Order
## 1. Setup dev environment
- [Link](https://github.com/worldbiomusic/MiniGameWorld/blob/main/resources/userWiki/english/making-minigame-wiki.md#how-to-set-dev-environment)


## 2. Make class
### Essential overriding methods
- `initGameSetting()`: fired every time when minigame starts
- `processEvent()`: fired when event pass to minigame
- `registerTutorial()`: tutorial string

### Frame class
- Select according to minigame features
#### `SoloMiniGame`
- Solo play
- [Example Minigame]()


#### `SoloBattleMiniGame`
- Individual battle
- [Example Minigame]()


#### `TeamMiniGame`
- 1 Team play
- [Example Minigame]()


#### `TeamBattleMiniGame`
- Team battle play
- [Example Minigame]()



## 3.Register MiniGame
- Register MiniGame with registerMiniGame() of MiniGameWorld.create()
```java
MiniGameWorld mw = MiniGameWorld.create();
mw.registerMiniGame(new FitTool());
```

---

# Options
## - MiniGameSetting
- Fundamental settings of minigame
- `settingFixed`: fix value: `minPlayerCount`, `maxPlayerCount`, `timeLimit`, `customData` (can't edit in config) > false
- `passUndetectableEvents`: pass all event to minigame (must check event in detail (e.g. check event player is playing current minigame))
- `rankOrder`: rank order method by score

## - MiniGameCustomOption
- All custom options are in `custom-data` section
- `CHATTING`: whether chat event cancel
- `SCORE_NOTIFYING`: whether notify score change
- `BLOCK_BREAK`: whether player can break block
- `BLOCK_PLACE`: whether player can place block
- `PVP`: whether players can pvp
- `INVENTORY_SAVE`: whether inventory save
- `MINIGAME_RESPAWN`: whether player will be respawn in minigame location

## - Task Management
- Can manage task easily
- `Register`: `getTaskManager().registerTask("name", new Runnable() { // code });`
- `Run`: `getTaskManager().runTask("name");`
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
- GameException: `PLAYER_QUIT_SERVER`, `SERVER_STOP`
- Use `handleException()` with overriding
- Related player must leave the minigame



## - Custom Data
- Minigame Developer can add custom data
- Minigame User can play with edit custom data
### How to set
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
### How to use
- Can use wherever with `getCustomData()`
```java
@Override
protected void runTaskAfterStart() {
  // set health scale
  this.health = (int) this.getCustomData().get("health");
  // give kit tool
  List<ItemStack> items = (List<ItemStack>) this.getCustomData().get("items");
  items.forEach(item -> p.getInventory().addItem(item));
}
```

## - Reservation Task
- `runTaskAfterStart()`: run after minigame started
- `runTaskBeforeFinish()`: run before minigame finishes
- `runTaskAfterFinish()`: run after minigame finished


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
- 

## - etc
- `endGame()`: end minigame
- `getLeftWaitingTime()`: get left waiting time (sec)
- `getLeftFinishTime()`: get left time to finish (sec)


---

# Caution
## Player state management
- MiniGameWorld manages player's default states when join / leave
> `inventory`, `health`, `food level`, `exp`, `potion effects`, `glowing`, `hiding`, `game mode`  
- If minigame changed while playing minigame, have to restore all changed things using `runTaskBeforeFinish()`

## Detectable Events
- MiniGameWorld only detect event that can get player from event
- Detectable events only processed when player from event is playing minigame
- Can use sub-event (e.g. EntityDamageEvent(o), EntityDamageByEntityEvent(o), EntityDamageByBlockEvent(o))
```yaml
- PlayerEvent
- EntityEvent
- HangingEvent
- InventoryEvent
- InventoryMoveItemEvent
- PlayerLeashEntityEvent
```

## Override
- Almost overrided method shold have `super.method()`
