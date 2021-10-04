# Description

---

# Order
## 1. Setup dev environment
- [Link](https://github.com/worldbiomusic/MiniGameWorld/blob/main/resources/userWiki/making-minigame-wiki.md#how-to-set-dev-environment)


## 2. Craete class
### Essential overriding methods
- `initGameSetting()`: executed every time when minigame starts
- `processEvent()`: executed when event is passed to minigame
- `registerTutorial()`: tutorial string

### Frame class
- Select according to minigame features
#### `SoloMiniGame`
- Solo play
- [Example Minigame]()


#### `SoloBattleMiniGame`
- Individual battle play
- [Example Minigame]()


#### `TeamMiniGame`
- Cooperative play
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
- `CHATTING`: whether chat event cancel
- `SCORE_NOTIFYING`: whether notify score change
- `BLOCK_BREAK`: whether player can break block
- `BLOCK_PLACE`: whether player can place block
- `PVP`: whether players can pvp
- `INVENTORY_SAVE`: whether inventory save
- `MINIGAME_RESPAWN`: whether player will be respawn in minigame location
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
- MiniGameWorld manages player's default states when join / leave
> `inventory`, `health`, `food level`, `exp`, `potion effects`, `glowing`, `hiding`, `game mode`  
- If minigame changed things that MiniGameWorld can not manage, have to restore all changed things (with `runTaskBeforeFinish()`)

## Detectable Events
- MiniGameWorld only passes detectable events that can get player from event
- Detectable events only pass to minigame that player from event is playing minigame
- Can use sub-event (e.g. EntityDamageEvent(o), EntityDamageByEntityEvent(o), EntityDamageByBlockEvent(o))
- If **needs not related with player event**, set `passUndetectableEvent` setting to true of `MiniGameSetting`
- [Detectable Event List](detectable-event-list.md)

## Override
- Almost overrided method shold have `super.method()`
