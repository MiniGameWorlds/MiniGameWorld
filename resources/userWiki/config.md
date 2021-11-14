# Config
- Minigame has each files in `MiniGameWorld/minigames` folder
- Deleted minigame file will be removed automatically when the server is stopped
- Minigame config created with default values by developer's settings
- Missed keys will be restored automatically when the server starts

## `setting.yml`
- Plugin settings
```yml
settings:
  message-prefix: MiniGameWorld
  minigame-sign: true
```
- `message-prefix`: System message of `MiniGameWorld` plugin
- `minigame-sign`: Whether you can join or leave minigame with right-click sign (true / false)


## `minigames/<MiniGame>.yml`
- Minigame settings
```yml
PVP:
  title: BattleRoyale
  min-player-count: 2
  max-player-count: 5
  waiting-time: 10
  time-limit: 180
  active: true
  icon: STONE_SWORD
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
  - 'death: respawn'
  custom-data:
    health: 30
    items:
    - ==: org.bukkit.inventory.ItemStack
      v: 2730
      type: STONE_SWORD
    - ==: org.bukkit.inventory.ItemStack
      v: 2730
      type: BOW
    - ==: org.bukkit.inventory.ItemStack
      v: 2730
      type: ARROW
      amount: 32
    chatting: true
    score-notifying: true
    block-break: false
    block-place: false
    pvp: true
    pve: true
    inventory-save: true
    minigame-respawn: true
    live-gamemode: SURVIVAL
    dead-gamemode: SPECTATOR
    color: GOLD    
```
- `title`: Minigame title (can be different with Class Name)
- `min-player-count`: Minimun player for playing game
- `max-player-count`: Maximun player for playing game
- `waiting-time`: Waiting time (sec)
- `time-limit`: Minigame playing time limit (sec)
- `active`: Whether this minigame is active in the server(true/false)
- `icon`: Material which show in menu (Material)
- `location`: Minigame join location
- `tutorial`: Tutorials
- `custom-data`: Custom data added by developer
- `custom-data.chatting`: Whether players can chat (true/false)
- `custom-data.score-notifying`: Whether score change notifies to players (true/false)
- `custom-data.block-break`: Whether players can break block (true/false)
- `custom-data.block-place`: Whether players can place block (true/false)
- `custom-data.pvp`: Whether players can damage to each other (true/false)
- `custom-data.pve`: Whether players can damage to living entity (not player) (true/false)
- `custom-data.inventory-save`: Whether player's inventory saving when death (true/false)
- `custom-data.minigame-respawn`: Whether player respawn in `location` (true/false)
- `custom-data.live-gamemode`: GameMode when a player join minigame (`CREATIVE`, `SURVIVAL`, `ADVENTURE`, `SPECTATOR`)
- `custom-data.dead-gamemode`: GameMode when a player is dead as a minigame player (Not literally dead) (`CREATIVE`, `SURVIVAL`, `ADVENTURE`, `SPECTATOR`)
- `custom-data.color`: Minigame color (`BLACK`, `DARK_BLUE`, `DARK_GREEN`, `DARK_AQUA`, `DARK_RED`, `DARK_PURPLE`, `GOLD`, `GRAY`, `DARK_GRAY`, `BLUE`, `GREEN`, `AQUA`, `RED`, `LIGHT_PURPLE`, `YELLOW`, `WHITE`, `MAGIC`, `BOLD`, `STRIKETHROUGH`, `UNDERLINE`, `ITALIC`, `RESET`)