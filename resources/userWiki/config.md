# Config
- Minigame has each files in `MiniGameWorld/minigames` folder
- Deleted minigame file will be removed automatically
- Minigame config created with default values by developer's settings
## `setting.yml`
- Plugin settings
```yml
settings:
  message-prefix: MiniGameWorld
  minigame-sign: true
  minigame-command: true

```
- `message-prefix`: system message of `MiniGameWorld` plugin
- `minigame-sign`: whether you can join or leave minigame with right-click sign (true / false)
- `minigame-command`: whether you can use plugin command (true / false)


## `minigames/<MiniGame>.yml`
- Minigame settings
```yml
PVP:
  title: PVP
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
    inventory-save: true
    minigame-respawn: true

```
- `title`: minigame title (can be different with Class Name)
- `min-player-count`: minimun player for playing game
- `max-player-count`: maximun player for playing game
- `waiting-time`: waiting time (sec)
- `time-limit`: minigame playing time limit (sec)
- `active`: whether this minigame is active in the server(true/false)
- `icon`: Material which show in menu (Material)
- `location`: minigame join location
- `tutorial`: tutorials
- `custom-data`: custom data added by developer
