# Config
- Minigame has each files in `MiniGameWorld/minigames` folder
- Deleted minigame file will be removed automatically when the server is stopped
- Minigame config created with default values by developer's settings
- Missed keys will be restored automatically when the server starts

## `setting.yml`
- Plugin settings
```yml
settings:
  message-prefix: §lMiniGameWorld§r
  backup-data-save-delay: 60
  minigame-sign: true
  debug-mode: false
  isolated-chat: true
  isolated-join-quit-message: true
```
- `message-prefix`: System message of `MiniGameWorld` plugin
- `backup-data-save-delay`: Delay of saving backup data
- `minigame-sign`: Whether you can join or leave 
minigame with right-click sign (true / false)
- `debug-mode`: print debug logs
- `isolated-chat`: Playing minigame players can only chat with each other
- `isolated-join-quit-message`: Minigame join/quit message only notify in minigame

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
  location:
    ==: org.bukkit.Location
    world: world
    x: -24.495412277481673
    y: 160.0
    z: 12.42264804369194
    pitch: 7.8000712
    yaw: 88.20009
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
    color: RED
    food-level-change: true
    player-hurt: true
```
- `title`: Minigame title (can be different with Class Name)
- `min-player-count`: Minimun player count for start game
- `max-player-count`: Maximun player count for playing game
- `waiting-time`: Waiting time (sec)
- `time-limit`: Minigame playing time limit (sec)
- `active`: Whether this minigame is active in the server(true/false)
- `icon`: Material which show in menu (Material)
- `location`: Minigame join location
- `tutorial`: Tutorials
- `custom-data`: Custom data created by developer or default custom options
#### **Default custom-data options**
- `chatting`: Whether players can chat (true/false)
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
