# Config
- Minigame has each files in `plugins/MiniGameWorld/minigames` folder
- Deleted minigame file will be removed automatically when the server is stopped
- Minigame config created with default values by developer's settings
- Not neccessary or missed keys are managed automatically when the server starts

## `setting.yml`
Plugin settings in `MiniGameWorld/settings.yml`
```yml
settings:
  message-prefix: §lMiniGameWorld§r
  backup-delay: 60
  debug-mode: false
  isolated-chat: true
  isolated-join-quit-message: true
  join-sign-caption: '[MiniGame]'
  leave-sign-caption: '[Leave MiniGame]'
  scoreboard: true
  scoreboard-update-delay: 10
  remove-not-necessary-keys: false
  min-leave-time: 3
  start-sound: BLOCK_NOTE_BLOCK_CHIME
  finish-sound: BLOCK_NOTE_BLOCK_BELL
  check-update: true
  edit-messages: false
  ingame-leave: false
  template-worlds: []
  join-priority: MAX_PLAYERS
  party-invite-timeout: 60
  party-ask-timeout: 60

```
- `message-prefix`: System message prefix of `MiniGameWorld` plugin (must surround the content with `'`)
- `backup-delay`: Delay of saving backup data (per minute)
- `debug-mode`: Print debug logs (true/false)
- `isolated-chat`: Playing minigame players can only chat with each other (true/false)
- `isolated-join-quit-message`: Minigame join/quit message only notify in minigame (true/false)
- `join-sign-caption`: Caption of join sign block (must surround the content with `'`)
- `leave-sign-caption`: Caption of leave sign block (must surround the content with `'`)
- `scoreboard`: If true, use scoreboard system (true / false)
- `scoreboard-update-delay`: Scoreboard update delay (tick (`20`tick = `1`second))
- `remove-not-necessary-keys`: If true, not necessary config keys will be removed (Caution: Map data in `custom-data` of minigames could be initialized) (true / false)
- `min-leave-time`: Minimun time to leave the minigame (sec)
- `start-sound`: Sound when a minigame starts (connect words with `_` of [Sound](https://www.digminecraft.com/lists/sound_list_pc.php))
- `finish-sound`: Sound when a minigame finished (connect words with `_` of [Sound](https://www.digminecraft.com/lists/sound_list_pc.php))
- `check-update`: If true, check latest version update (true / false)
- `edit-messages`: If true, language message changes will be applied (saved) (true / false)
- `ingame-leave`: If true, players can leave game while playing (true / false)
- `template-worlds`: World list which will be used for instance world system
- `join-priority`: Minigame join priority (`MAX_PLAYERS`, `MIN_PLAYERS`, `RANDOM`)
- `party-invite-timeout`: Party invite timeout (sec)
- `party-ask-timeout`: Party ask timeout (sec)

## `minigames/<MiniGame>.yml`
Minigame settings in `MiniGameWorld/minigames/<MiniGame>.yml`
```yml
PVP:
  title: PVP
  min-players: 2
  max-players: 10
  waiting-time: 30
  play-time: 120
  active: true
  icon: STONE_SWORD
  view: true
  scoreboard: true
  instances: 1
  instance-world: false
  locations:
  - ==: org.bukkit.Location
    world: world
    x: 0.0
    y: 4.0
    z: 0.0
    pitch: 0.0
    yaw: 0.0
  tutorial:
  - 'kill: +1'
  custom-data:
    health: 30
    items:
    - ==: org.bukkit.inventory.ItemStack
      v: 3105
      type: STONE_SWORD
    - ==: org.bukkit.inventory.ItemStack
      v: 3105
      type: BOW
    - ==: org.bukkit.inventory.ItemStack
      v: 3105
      type: ARROW
      amount: 32
    chat: true
    score-notifying: true
    block-break: false
    block-place: false
    pvp: true
    pve: true
    inventory-save: true
    minigame-respawn: true
    live-gamemode: SURVIVAL
    dead-gamemode: SPECTATOR
    color: RED
    food-level-change: true
    player-hurt: true

```
- `title`: Minigame title (don't use color code, but `custom-data.color`)
- `min-players`: Minimun player count for start game (`0` <= `min-players`)
- `max-players`: Maximun player count for playing game (`0` <= `max-players`)
- `waiting-time`: Waiting time (sec) (`-1` for infinity)
- `play-time`: Minigame play time (sec) (`-1` for infinity)
- `active`: Whether this minigame is active in the server (true/false)
- `icon`: Material which show in menu (connect words with `_` of [Material](https://minecraftitemids.com/types/material))
- `view`: Whether a player can view a minigame (true/false)
- `scoreboard`: If false, minigame will not show scoreboard to players (true/false)
- `instances`: Max number of game instances (`-1` <= `instances`) (`-1` is for infinite)
- `instance-world`: If true, copied `locations` worlds will be used for game play and be deleted automatically (**Place worlds folder in the bukkit server folder and list them in `template-worlds` in `settings.yml`**). If false, random location in `locations` will be used (true/false)
- `locations`: Minigame location list which will be selected randomly (list)
- `tutorial`: Tutorials that will be shown when game starts (list)
- `custom-data`: Custom data created by developer or default custom options
#### **Default custom-data options**
- `chat`: Whether players can chat (true/false)
- `score-notifying`: Whether score change notifies to players (true/false)
- `block-break`: Whether players can break block (true/false)
- `block-place`: Whether players can place block (true/false)
- `pvp`: Whether players can damage to each other (true/false)
- `pve`: Whether players can damage to living entity (not player) (true/false)
- `inventory-save`: Whether player's inventory saving when death (true/false)
- `minigame-respawn`: Whether player respawn in `location` (true/false)
- `live-gamemode`: GameMode when a player join minigame (`CREATIVE`, `SURVIVAL`, `ADVENTURE`, `SPECTATOR`)
- `dead-gamemode`: GameMode when a player is dead as a minigame player (Not literally dead, just died in the game) (`CREATIVE`, `SURVIVAL`, `ADVENTURE`, `SPECTATOR`)
- `color`: Minigame color (`BLACK`, `DARK_BLUE`, `DARK_GREEN`, `DARK_AQUA`, `DARK_RED`, `DARK_PURPLE`, `GOLD`, `GRAY`, `DARK_GRAY`, `BLUE`, `GREEN`, `AQUA`, `RED`, `LIGHT_PURPLE`, `YELLOW`, `WHITE`, `MAGIC`, `BOLD`, `STRIKETHROUGH`, `UNDERLINE`, `ITALIC`, `RESET`)
- `food-level-change`: Whether a player's food level(hunger) changes (true/false)
- `player-hurt`: Whether a player damaged by something (true/false)

#### **TeamBattle game default custom-data options**
- `group-chat`: If false, teams can only chat with members (true/false)
- `team-pvp`: If true, team members can damage to each others (true/false)
- `team-size`: Max team member size of one team (`0` <= `team-size`)
> e.g. `max-players` is 12 and `team-size` is 4 then, there are can be up to 3 teams
- `team-register-mode`: Team member registration mode (`NONE`, `FAIR`, `FILL`, `FAIR_FILL`, `RANDOM`, `PARTY`)
> - e.g. playerCount: 13, teamMaxPlayerCount: 5, teamCount: 4
> - `NONE`: no divide (need custom distribution by the game)
> - `FAIR`: all teams have the same player count fairly(= maximun team count) (e.g. 4, 3, 3, 3)
> - `FILL`: fulfill teams as possible from first (= minimum team count) (e.g. 5, 5, 3, 0)
> - `FAIR_FILL`: FILL fairly (e.g. 5, 4, 4, 0)
> - `RANDOM`: random (e.g. ?, ?, ?, ?)
> - `PARTY`: create teams with only party members (only "`max-players` / `team-size`" party can join the game)