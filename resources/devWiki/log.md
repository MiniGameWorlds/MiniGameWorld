# TODO
- Make Youtube tutorial (in new brand channel)
- Make Github issue template
- Add various minigames (block hopscotch, block color, jump map, player boss raid, mob raid, defense, skill battle)
- Add `party` option to `Menu`
- Add `Scoreboard` to minigame (also managed in PlayerState)
##### Event Detection
```
- Try to use other library, not `classgraph` (e.g. [reflections]())
- Add detailed event while searching
```

---

# 2020-10 ~ 2021-03
- See `Relay Escape Plugin` log (separate minigame module from `Relay Escape` server)

# 2021-03 ~ 2021-05-09
- developed without log

# 2021-05-10
- Change score print mode to descending
- Create other MiniGame constructor (Set Location("world", 0, 4, 0) to default)
- Design MiniGame class that only 1 class can create 1 minigame in server
- Delete not exist minigame from minigames.json automatically
- Add `activated` variable
- Add `settingFixed` option (fix: maxPlayerCount, timeLimit, waitingTime)
- Add minigame frames: SoloMiniGame, CooperativeMiniGame, TeamBattleMiniGame

# 2021-05-15
- Add `scoreNotifying` option to MiniGame
- Fix MiniGame frame
- Enable to set settings of MiniGame with method

# 2021-05-16
- Make `handleException()` more generic, add Exception enum
- Add MiniGame attributes Checking method(checkAttributes())
- Change minigame frames name: SoloMiniGame, TeamMiniGame, SoloBattleMiniGame, TeamBattleMiniGame
- Finish game if only 1 player remain in SoloBattleMiniGame, TeamBattleMiniGame(cuz of battle)
- Add runTaskBeforeStart(), runTaskBeforeEnd()
- Check player is playing another game when trying to join a game
- Check minigame frames player count
- Process minigame exception in frame classes(e.g. player count)

# 2021-05-22
- Create `Setting` class that manage settings in `MiniGame` class

# 2021-05-23
- Add `Counter` for `waitingCounter`, `finishCounter` to get left time
- Add util methods to minigame frames(score, message)
- Each minigame frames override `printScore()`
- Add color to title, sounds
- Add `ScoreClimbing` minigame

# 2021-05-30
- Design `BukkitTaskManager`

# 2021-06-05
- Adjust `BukkitTaskManager` to MiniGame
- Notify everyone when a player joins or leaves a minigame
- Add processing exception method via `MiniGameMaker`
- Add minigame leaving option before start
- Add command for join/leave a minigam (add `minigameCommand` option to setting.json)
- Make player can leave with touch `SIGN block` (add `minigameSign` option to setting.json)
- Add some commands

# 2021-06-06
- Change dev wiki to document for development (write minigame to userWiki)
- Add `registerTasks()` for task register
- Add `forcePlayerCount` setting
- Add `RockScissorPaper` minigame (using forcePlayerCount)

# 2021-06-12
- Register only using event handlers (total: 321, using: 177)
- Combine sign block click to `processEvent()`

# 2021-06-13
- Add `customData` option that minigame dev can register custom data to minigames.json
- Change to register all event handler
- Check minigame with class name
- Add `PVP` minigame

# 2021-06-27
- Can't resolved to distinguish between `double` and `long` type in json format, just load to `double`
- Change `settingFixed` option to not managed in `minigames.json` (minigame dev should inform this option to users)

# 2021-07-03
- Add `YamlManager` to `wbmMC` library (https://github.com/worldbiomusic/Blog/blob/main/Minecraft/plugin/making/YAML.md)

# 2021-07-04
- Change config format (json > yaml)
 
# 2021-07-08
- Move `RankManager` to WbmMC
- Move `BukkitTaskManager` to WbmMC
- After runTask() of BukkitTaskManagger, remove registered runnable (because BukkitRunnable can not be reused)
- Write wiki

# 2021-07-10
- Create `MiniGameMaker` wrapper class
- Change `actived` to `active` in MiniGameSetting

# 2021-07-11
- Create `MiniGameAcessor` wrapper class
- Add `MiniGame Observer` pattern (make Third-party plugin can various do with observer event)
- Add sendMessage() to Minigame with title prefix
- Test `MiniGameMaker`, `MiniGameObserver` with external plugin
- Write wiki of wrapper class
- Write wiki of observer system

# 2021-07-17
- Capsulate data save/load from `MiniGameDataManger` to `MiniGameSetting`
- Write MiniGame class wiki, refactoring
- Add `RemoveBlock` minigame
- Add yaml config (using reload() of `YamlMember`), reload command(`/minigame reload`)
- Delete log tool of `wbmMC` (log have to be used in `getLogger()` of each plugin)
- Add `sendMsg()` to Setting with prefix
- Add `logger` to Setting()(log(), warning())
- Add `jardescription.jardesc`

# 2021-07-25
- Change paper api to `17` from `16`
- Remove `InventoryPickupItemEvent` from MiniGameManager (not related with player)

# 2021-09-08
- Resolved `classgraph` error (problem with `JDK 16`) (solution found in classgraph discussions)

# 2021-09-09
- Move tutorial to `MiniGameSetting` from `MiniGame` (MiniGame data have to be managed in MiniGameSetting)

# 2021-09-10
- Move CustomData to `MiniGameSetting` from `MiniGame`
- Make `settingFixed` fix `customData`
- Rename Main class name of plugin (`Main` > `MiniGameMakerMain`)
- Make MiniGame manage player's `inventory` (PlayerInvManager)
- Rename `spawnLocation` to `lobby` of settings.yml
- Declare some methods to final in `MiniGame` class that must not be edit by overriding

# 2021-09-12
- Add `minigame list` command
- `MiniGameSetting` manage `lobby`
- Add message prefix `messagePrefix` option to `setting.yml`
- Update `MiniGameAccessor`
- Write wiki

# 2021-09-13
- Change plugin name to `MiniGameWorld` (`MiniGameMaker` is already exist)
```yaml
# Word
- maker > world
# List
- [x] wikis
- [x] images
- [x] ppt
- [x] source(project , class, package, etc)
- [x] maven 
- [x] plugin.yml
- [x] jardesc.jardesc
```

# 2021-09-14
- Change `lobby` location to `MiniGameManager` from `MiniGameSetting` for api danger
- Separate `MiniGamePlayerData` from `MiniGame` for manage player data(`xp`, `health`, etc)

# 2021-09-15
- Design GUI(`/mg gui`)
- Add minigame `icon` used in GUI
- Update `MiniGameWorld` (GUI)

# 2021-09-16
- Command only can be used when `minigameCommand` is true in setting.yml
- Update `MiniGameWorld` (icon)
- Make `scoreNotifying` fix `forceFullPlayer`
- Make `scoreNotifying` not fix `waitingTime` 
- If `forceFullPlayer` is true, restart `waitingTime` when minigame players who wait for starting are not fulled

# 2021-09-17
- Add `Exception` event to `MiniGameObserver` in `handleException()`
- Change annotations to english (some of all)

# 2021-09-18
- Change annotations to english (some of all)
- Add player header to GUI
- Create command tab completer

# 2021-09-19
- Design and make frame `party` system

# 2021-09-20
- Make `party` system (need test)
- Process Things to be done when `join`/`quit` a server in `MiniGameManager`

# 2021-09-21
- Make `party` system
- Add `clickable chat` option to `party` system

# 2021-09-22
- Change annotations to english (complete)
- Add party member to GUI
- Add `minPlayerCount` to MiniGameSetting
- Remove `forcedFullPlayer` (`forcedFullPlayer` equals "`minPlayerCount` = `maxPlayerCount`")
- Combine `party` system with `minigame` system


# 2021-09-23
- Fix GUI click bug
- Improve `TaskManager` of `wbmMC` (Can reuse runnable instance) (must use `Runnable`, not `BukkitRunnable`)

# 2021-09-24
- Subdivide `Team` in `TeamBattleMiniGame`
- Make `TeamBattleMiniGame` manage `groupChat` in `customData`
- Make `TeamBattleMiniGame` manage `TeamRegisterMethod` in `customData`

# 2021-09-25
- Delete `checkAttribute()` in MiniGame
- Move `scoreNotifying` option to `customData` section
- Make chat send to only same minigame players
- Add `processChat()` to MiniGame (can be overrided)
- Add `GameMode` to `MiniGamePlayerData`
- Add `live` option to player of TeamBattleMiniGame
- Add `BreedMob` minigame

# 2021-09-26
- Add `passUndetectableEvents` option to `MiniGameSetting` (pass undetectable event to minigame) (warning: minigame has to check event is related)

# 2021-09-27
- Separate `MiniGameEventDetector` from `MiniGameManager`
- Add `getPlayersFromDetailedEvent()` to `MiniGameEventDetector` that detect detailed events that eventDetector can not detect
- Change MiniGame event handler priority: `NORMAL` > `HIGHEST`
- Add `blockBreak`, `blockPlace` options to customData
- Separate `MiniGameRankManager` from `MiniGame`
- Separate `MiniGamePlayerData` from `MiniGame`
- Add `live` option to `MiniGamePlayerData` (easy to check game finish timing)
- Separate `MiniGameTaskManager` from `MiniGame`
- Change config file structure to have 1 config per minigame

# 2021-09-28
- Separate `MiniGameCustomOption` from `MiniGame` (e.g. chatting, damage)
- Add `pvp` option to `MiniGameCustomOption` (add `teamPvp` to `TeamBattleMiniGame`)
- Add `SuperMob` minigame
- Fix `undetectableEvent` logic (if `passUndetectableEvent` is true, find first in general `detectableEvent` and can't find, then find in detailed)

# 2021-09-29
- Remove check `isMinPlayersLive()` in `setLive()` for low sequence sensitivity
``` 
- minigame dont' have to use `live` always (can finish game with another condition(e.g. wait for time limit))
- When player leave a minigame: `isMinPlayersLive()` is checked automatically
// e.g.
if(!this.isMinPlayersLive()) {
	this.endGame();
}
```
- Edit detectable event list
- Add detailed event list (BlockEvent)
- Add `PassMob` minigame
- Add `FallingBlock` minigame
- Add `RankOrder` to `MiniGameSetting`
- Add `INVENTORY_SAVE`, `MINIGAME_RESPAWN` to `MiniGameCustomOption`
- Change yaml config key format (e.g. setPvp > set-pvp)

# 2021-09-30
- Add detailed event list (VehicleEvent, UnknownCommandEvent, EntityEvent)
- Removed deprecated api
- Create config(settings.yml, minigames/<game>.yml) command

# 2021-10-01
- Write user wiki
- Update `MiniGameAccessor` (remove MiniGameSetting getters, but add `getSetting()` to access with `Map`)
- Rename: `MiniGameGUI` > `MiniGameMenu`
- Create VersionChecker (MiniGame or Third party can be checked with version string)
- Delete korean wiki

# 2021-10-02
- Add `loadCustomData()` to MiniGame
- Create Github discussion template
- Change yaml config key can be ordered (`HashMap` > `LinkedHashMap`)

# 2021-10-03
- Add backup module (BackupDataManager)
- Refactor minigames (e.g. adjust `loadCustomData()`)
- Make `MiniGamePlayerState` manage `player health scale`

# 2021-10-04
- Write `MiniGame` javadoc
- Finish all minigames when server stopped (if send minigame exception, got different result according to player list)
- Add `CUSTOM` to MiniGame.Exception and `detailedReason`, `detailedObj`
- Add `GameFinishCondition` to `MiniGameSetting`
- edit wiki

# 2021-10-05
- Change `log.md` to english
- Write javadoc: `MiniGameWorld`, `MiniGameAccessor`, `MiniGameEventNotifier`, `MiniGameObserver`, `SoloMiniGame`, `SoloBattleMiniGame`, `TeamMiniGame`, `TeamBattleMiniGame`, `PartyManager`, `Party`, `PartyMember`
- Edit wiki
- Add description `party` system to user wiki
- Create minigame `custom order` category and template

# 2021-10-06
- Write javadoc: `MiniGameEventDetector`
- Write third-party-guide wiki
- Test with `black0712_`
- Fix bugs
- Add `checkPluginStartToBeDisabled()` to MiniGameManager for finish game when server doen(Because minigames disabled earlier than MiniGameWorld(Then can not find minigame class in JVM))
- Make help usage of all commands

# 2021-10-07
- Plan to test plugin with black0712's server
- Invite black0712 to github repo
- Fix using MiniGameEvent.FINISH observer can get players from minigame
- Create third-party: MiniGameWorld-Reward
- Remove `lobby` option (player will tp to joined before location after game finished)

# 2021-10-08
- Insert `worldbiomusic` in package name (for distinguish with other forks)
- Make logo image`
- Add flow image to wiki

# 2021-10-10
- CustomOption: PVE (entity(not player) damage by player)
- Print value of key for `settings`, `minigames` commands
- Make config data save in directly when plugin first load
- Make Setting.API_VERSION managed by version of `plugin.yml` automatically
- Upload plugin to Spigot forum
- Put javadoc in `docs` dir

# 2021-10-11
- Release MobMiniGames.jar
- Make user tutorial video


















