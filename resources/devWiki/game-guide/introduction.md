# About MiniGameWorld
MiniGameWorld is minigame play place for users and minigame creating framework for developers.



# Features
The main feature is that preserve the state of the server and player, and works independently of other plugins, so it is **applicable regardless of the genre of the server**.

## 1. Minigame frame classes
Minigame frame class manages settings, start, finish, player's scores, lives, events etc.

There are 4 frame classes: `Solo`(1 player), `Solo battle`(FFA), `Team`(1 team), `Team battle`(TDM)


## 2. Scoreboard
Info player's score, live and left time  
![image](https://user-images.githubusercontent.com/61288262/169811178-3ec609cd-a7b0-4bbe-95bd-2c58550a4ec3.png)


## 3. GUI menu
Support inventory GUI for easy control
![image](https://user-images.githubusercontent.com/61288262/169811272-ddd8f2d4-fec4-4557-b2be-d6844ccc7138.png)


## 4. Config control
- Support config control with Map<String, Object> for developers (`custom-data`)
- Support config control by editing files for users
```yaml
Center:
  title: Center
  min-players: 2
  max-players: 5
  waiting-time: 20
  play-time: 120
  active: true
  icon: END_ROD
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
  - §cNever Sneak!
  - §cNever Fall!
  custom-data:
    chat: true
    score-notifying: false
    block-break: false
    block-place: false
    pvp: false
    pve: true
    inventory-save: true
    minigame-respawn: true
    live-gamemode: SURVIVAL
    dead-gamemode: SPECTATOR
    color: BLUE
    food-level-change: true
    player-hurt: true
```



## 5. API
- Support player control: join/leave, view/unview
- Support minigame control: register/unregister, start/finish, exception, settings
- Support utils control: party, GUI menu



## 6. Custom events
There are custom events for 3rd party plugins ([details](3rd-party-guide/Home.md)) (e.g. `MiniGameStartEvent`, `MiniGamePlayerJoinEvent`, `MenuClickEvent`)



## 7. Game instances
Support multi game instance system that means one game can has multiple instances for many players. (maximum instance count can be controlled in game config)



## 8. World instances
Support world instance system that. If there is one template world, that will be copied for multiple instances. (world instance can be controlled in game config)



## 9. More
Util tools: party, backup, language, player state and APIs...

You can find more ([User Wiki], [Dev Wiki] and [API docs]) and [demo minigame source code](https://github.com/MiniGameWorlds/AllMiniGames) in these links.












[User Wiki]: https://github.com/MiniGameWorlds/MiniGameWorld/blob/main/resources/userWiki/Home.md
[Dev Wiki]: https://github.com/MiniGameWorlds/MiniGameWorld/blob/main/resources/devWiki/Home.md
[API docs]: https://minigameworlds.github.io/MiniGameWorld/
