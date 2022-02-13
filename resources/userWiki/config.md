# Config
- Minigame has each files in `plugins/MiniGameWorld/minigames` folder
- Deleted minigame file will be removed automatically when the server is stopped
- Minigame config created with default values by developer's settings
- Not neccessary or missed keys are managed automatically when the server starts

## `setting.yml`
- Plugin settings
```yml
settings:
  message-prefix: §lMiniGameWorld§r
  backup-data-save-delay: 60
  debug-mode: false
  isolated-chat: true
  isolated-join-quit-message: true
  join-sign-caption: '[MiniGame]'
  leave-sign-caption: '[Leave MiniGame]'
  scoreboard: true
  scoreboard-update-delay: 10
  remove-not-necessary-keys: false
```
- `message-prefix`: System message prefix of `MiniGameWorld` plugin (must surround the content with `'`)
- `backup-data-save-delay`: Delay of saving backup data (per minute)
- `debug-mode`: Print debug logs (true/false)
- `isolated-chat`: Playing minigame players can only chat with each other (true/false)
- `isolated-join-quit-message`: Minigame join/quit message only notify in minigame (true/false)
- `join-sign-caption`: Caption of join sign block (must surround the content with `'`)
- `leave-sign-caption`: Caption of leave sign block (must surround the content with `'`)
- `scoreboard`: If true, use scoreboard system (true / false)
- `scoreboard-update-delay`: Scoreboard update delay (tick (`20`tick = `1`second))
- `remove-not-necessary-keys`: If true, not necessary config keys will be removed (Caution: Map data in `custom-data` of minigames could be initialized) (true / false)

## `minigames/<MiniGame>.yml`
- Minigame settings
```yml
PVP:
  title: PVP
  min-player-count: 2
  max-player-count: 5
  waiting-time: 30
  time-limit: 120
  active: true
  icon: STONE_SWORD
  view: true
  location:
    ==: org.bukkit.Location
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
      v: 2865
      type: STONE_SWORD
    - ==: org.bukkit.inventory.ItemStack
      v: 2865
      type: BOW
    - ==: org.bukkit.inventory.ItemStack
      v: 2865
      type: ARROW
      amount: 32
    - ==: org.bukkit.inventory.ItemStack
      v: 2865
      type: COOKED_PORKCHOP
      amount: 10
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
- `min-player-count`: Minimun player count for start game
- `max-player-count`: Maximun player count for playing game
- `waiting-time`: Waiting time (sec)
- `time-limit`: Minigame playing time limit (sec)
- `active`: Whether this minigame is active in the server(true/false)
- `icon`: Material which show in menu (Material)
- `view`: Whether a player can view a minigame
- `location`: Minigame join location
- `tutorial`: Tutorials
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
- `dead-gamemode`: GameMode when a player is dead as a minigame player (Not literally dead) (`CREATIVE`, `SURVIVAL`, `ADVENTURE`, `SPECTATOR`)
- `color`: Minigame color (`BLACK`, `DARK_BLUE`, `DARK_GREEN`, `DARK_AQUA`, `DARK_RED`, `DARK_PURPLE`, `GOLD`, `GRAY`, `DARK_GRAY`, `BLUE`, `GREEN`, `AQUA`, `RED`, `LIGHT_PURPLE`, `YELLOW`, `WHITE`, `MAGIC`, `BOLD`, `STRIKETHROUGH`, `UNDERLINE`, `ITALIC`, `RESET`)
- `food-level-change`: Whether a player's food level(hunger) changes (true/false)
- `player-hurt`: Whether a player damaged by something (true/false)

#### **TeamBattleMiniGame default custom-data options**
- `group-chat`: If false, teams can only chat with members
- `team-pvp`: If true, team members can damage to each others
- `team-size`: Max team member size of one team
> e.g. `max-player-count` is 12 and `team-size` is 4 then, there are can be up to 3 teams
- `team-register-mode`: Team member registration mode (`NONE`, `FAIR`, `FILL`, `FAIR_FILL`, `RANDOM`, `PARTY`)
> - e.g. playerCount: 13, teamMaxPlayerCount: 5, teamCount: 4
> - `NONE`: no divide (use registerPlayersToTeam())
> - `FAIR`: all teams have the same player count fairly(= maximun team count) (e.g. 4, 3, 3, 3)
> - `FILL`: fulfill teams as possible from first (= minimum team count) (e.g. 5, 5, 3, 0)
> - `FAIR_FILL`: FILL fairly (e.g. 5, 4, 4, 0)
> - `RANDOM`: random (e.g. ?, ?, ?, ?)
> - `PARTY`: create teams with only party members (only "`max-player-count` / `team-size`" party can join the game)