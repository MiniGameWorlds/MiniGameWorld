# TODO
- Customize minigame start, finish sound in settings.yml 
- Customize content of sign block (`[MiniGame]`, `[Leave MiniGame]`) in settings.yml 
- Change events of `MiniGameEventNotifier.MiniGameEvent` to **Custom Event** except for `REGISTRATION` and `UNREGISTRATION` (extends MiniGameEvent)
- update all plugins
- update 3rd-parties (change observer pattern to custom event)
- Add `party` icon to Menu
- Register to maven, gradle
- Set team glowing color (glowing is only visibled to team members)
- Make MiniGame template pluing and upload to github organization
- Make Version Update Checker
- Add `ScoreBoard` and `BossBar` elements to MiniGame (prevent elements changing by event.setCancelled())
- Change to use custom event instead of `MiniGameObserver` (about to START, BEFORE_FINISH, FINISH)
- Make Youtube tutorial (in new brand channel) (minigame-dev-tutorial for each types (Solo, SoloBattle, Team, TeamBattle)
- Make Github issue template
- Add various minigames (block hopscotch, block color, jump map, player boss raid, mob raid, defense, skill battle)
- Try to use other library, not `classgraph` (e.g. [reflections]())
- Support Script Addon

---

# Version Changes

## 0.0.1
### API Changes
- First Beta
### Build Changes

## 0.1.0
### API Changes
- Change `MiniGameWorld.VERSION` > `MiniGameWorld.API_VERSION`
### Build Changes
- Make API_VERSION managed by `version` of `plugin.yml` automatically
- Make config data save in immediately when plugin is first loaded
- Add permission nodes

## 0.1.1
### Build Changes
- **Change all things to fit with `spigot` bukkit**

## 0.2.0
### API Changes
- Change method name to `createException()` from `handleException()` in `MiniGameManager`, `MiniGameWorld`
### Build Changes
- Fix console color bugs
- Notify total score when score is changed

## 0.2.1
### Build Changes
- Test minecraft versions: `1.14`, `1.15`, `1.16`, `1.17` (not compatable with `1.12`, `1.13` for resource(`Material`, `Sound`))
- Minimize Detailed MiniGame Event Detecting range
- Fix sign block join bug
- Change all sign blocks can be used when join a minigame
- Update `wbmMC` library


## 0.2.2
### Build Changes
- Manage `visual fire`, `fire tick`, `freeze tick`, `invulnerable`, `silent`, `gravity`  in MiniGamePlayerStateManager
- Add `live GameMode`, `dead GameMode`, `color` to MiniGameCustomOption
- Restore missed keys of minigame data(yml) automatically

## 0.3.0
### API Changes
- Add `EVENT_PASSED`, `REGISTRATION`, `UNREGISTRATION`, `BEFORE_FINISH` to `MiniGameEventNotifier`
- Now can get minigame result with `MiniGameAccessor.getRank()` (Use `MiniGameRankResult` interface)
- Rank will be sorted only in descending order (remove RankOrder setting)

### Build Changes
- Add rank color
- Change backup file name format
- Fix bugs(minigame logic, observer)
- Manage more player's state during minigame playing
- Add `debug-mode` to `settings.yml` (for debugging when sending errors)
- Keys in all config will be managed automatically (auto-add, auto-remove, auto-sort in any situation)
- Now will be saved backup data every 60 minutes
- Works fine in `1.18`

## 0.3.1
### Build Changes
- Now commands can be used in console
- Update for using correct class loader of various Bukkit types and versions (classgraph)
- Add `FOOD_LEVEL_CHANGE`, `PLAYER_HURT` option to MiniGameCustomOption
- Notify player count when join a minigame
- Add `ISOLATED_CHAT` and `ISOLATED_JOIN_QUIT_MESSAGE` to settings.yml

## 0.3.2
### Build Changes
- Add `MiniGameEventExternalDetector` to detect other events by default
- Change observer directory

## 0.3.3
### Build Changes
- Prevent player food level(hunger) change while waiting minigame starts
- Improve GameFinishCondition features in MiniGameSetting

## 0.3.4
### Build Changes
- Add more detectable events
- Fix team registration bug in TeamBattleMiniGame
- Manage player's `allowFlight` state

## 0.3.5
### Build Changes
- Fix exception throwing when plugin is disabled with no minigames
- Change `PVE` meaning

---



# 2020-10 ~ 2021-03
- See `Relay Escape Plugin` log (separated minigame module from `Relay Escape` server)

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
- Release MobMiniGames.jar with fix
- Make user tutorial video

# 2021-10-15
- Add `UpDown` minigame
- Make a `MiniGame Dev Tutorial` video

# 2021-10-16
- Let Minigame maker should notify required bukkit when sharing a minigame(i.e. paper event cannot be used in spigot)

# 2021-10-17
- Add permission node
- Make All menu icon must be executed with a command
- Improve EventDetector: add basic event(AsyncTabCompleteEvent, TabCompleteEvent), detailed event(WorldEvent (GenericGameEvent, LootGenerateEvent, PortalCreateEvent), EntityEvent)
- remove `minigame-command` in `settings.yml`

# 2021-10-18
- Release `0.1.0`
- Remove `UpDown` minigame (require paper bukkit)
- **Change all things to fit with `spigot` bukkit**
- Change dev bukkit to `spigot` from `paper` (+ fixed `classgraph`)
- Release `0.1.1`

# 2021-10-26
- Make new dev tutorial video
- Devide to not add `bossbar`, `Scoreboard` to minigame, because almost servers are using own `bossbar`, `Scoreboard`, so may conflict with them
- Fix console color bugs
- Change method name to `createException()` from `handleException()` in `MiniGameManager`, `MiniGameWorld` (API change)
- Notify total score when score is changed
- Release `0.2.0`

# 2021-10-31
- Remove inner minigames
- Test minecraft versions: `1.14`, `1.15`, `1.16`, `1.17` (not compatable with `1.12`, `1.13` for resource(`Material`, `Sound`))
- Change `api-version` of plugin.yml to `1.14`
- Minimize MiniGame Event Detecting range
- Fix sign block join bug
- Change all sign blocks can be used when join a minigame
- Release `0.2.1`
- Update `wbmMC`

# 2021-11-01
- Create github organization
- Edit wiki (minigame examples in making a minigame wiki)

# 2021-11-07
- Edit wiki (event detecting list)
- Add `Available Minecraft Versions` to MiniGame distribute template in Github discussion


# 2021-11-15
- Manage `fire tick`,  `invulnerable`, `silent`, `gravity`, ~~`visual fire`~~(After 1.17), ~~`freeze tick`~~(After 1.17) in MiniGamePlayerStateManager
- Add `LIVE_GAMEMODE`, `DEAD_GAMEMODE`, `COLOR` to MiniGameCustomOption
- Restore missed keys of minigame data(yml) automatically
- Release `0.2.2`

# 2021-11-17
- Fix MiniGame logic bugs

# 2021-11-18
- Edit wiki

# 2021-11-20
- Move github repository (from own to organization)
- Release `FallingBlock`
- Upload `AllMiniGames` project
- Edit wiki

# 2021-11-21
- Fix MiniGame logic
- Upload `MiniGameWorld-Reward` to `MiniGameWorlds` organization
- Fix command help message bug
- Add rank color
- Change backup file name format to `yyyy-MM-dd'T'H;mm;ss` from `yyyy-MM-dd+H;mm;ss` (in wbmMC)

# 2021-11-25
- Add `EVENT_PASSED` to `MiniGameEventNotifier`
- Fix `MiniGameObserver` registration logic (Consider to change to use custom event)
- Make use `MiniGameRankComparable` which used for comparing rank in minigame
- Change API
- Manage `isFlying`, `velocity`, `walkSpeed`, `flySpeed` in `MiniGamePlayerState`
- Set cancelled to false in `onEvent()`, because minigame event must be processed independently
- Add `debug-mode` to `settings.yml` (Setting.DEBUG_MODE)

# 2021-11-26
- Add `REGISTRATION`, `UNREGISTRATION` events to `MiniGameNotifier`
- Fix observer interface

# 2021-11-27
- Remove `RankOrder` from `MiniGameSetting` (Fixed to `Descending` order)
- Remove not necessary keys in config when plugin starts (wbmMC)
- Sync(align) config keys order when plugin stars (wbmMC)

# 2021-11-28
- Save backup data every few minutes
- Remove cancelled to false in `onEvent()` (some problems occur)

# 2021-12-11
- Change shallow-copy to deep-copy of API (Cloneable) (Not perfect)

# 2021-12-12
- Add `MiniGameEvent.BEFORE_FINISH`
- Change "/" to `File.separator`
- Test with 1.18
- Release `0.3.0`

# 2021-12-19
- Now commands can be used in console
- Check the permissions at the end of the process, not before the command with

# 2021-12-28
- Update for using correct class loader of various Bukkit types and versions (classgraph)
- Change TeamBattleMiniGame team registration: Invoke CreateTeams() in initGameSettings()
- Add `FOOD_LEVEL_CHANGE`, `PLAYER_HURT` option to MiniGameCustomOption
- Notify player count when join a minigame
- Add detectable events
- Add `ISOLATED_CHAT` and `ISOLATED_JOIN_QUIT_MESSAGE` to settings.yml
- Prevent player hurt while waiting minigame starts
- Release `0.3.1`

# 2021-12-29
- Add `MiniGameEventExternalDetector` to detect other events by default
- Change observer directory
- Edit wiki
- Release `0.3.2`


# 2022-01-01
- Prevent player food level(hunger) change while waiting minigame starts

# 2022-01-04
- Improve GameFinishCondition features in MiniGameSetting
- Release `0.3.3`

# 2022-01-05
- Add more detectable events
- Fix team registration bug in TeamBattleMiniGame

# 2022-01-08
- Manage player's `allowFlight` state
- Release `0.3.4`

# 2022-01-13
- Fix exception throwing when plugin is disabled with no minigames
- Change `PVE` meaning
- Release `0.3.5`


# 2022-01-14
- Manage `MiniGamePlayerStateManager` in `MiniGamePlayerData`
- Minigame `processEvent()` Refactoring 

# 2022-01-15
- Add `view` feature
