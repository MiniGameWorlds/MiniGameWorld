# World instance system
**"Auto world creation and deletion"**

MiniGameWorld has **instance world system** that copies template world and uses for creating minigames. 
And after game finished, used world will be deleted automatically. Follow the tutorial below to use this.

Also support non world instance mode.


# Tutorial
### 1. Multiverse-Core
Download [Multiverse-Core](https://www.spigotmc.org/resources/multiverse-core.390/) plugin and apply to your server

### 2. Template world
To create copied world, you must place template world folder in your bukkit folder. (world container)
> E.g. Suppose you want to use world1 and world2 as tempalte worlds
```
bukkit folder
 ├─world1
 └─world2
```

### 3. Settings Configuration
Add your template world names to `template-worlds` in `settings.yml` config.
> E.g. `template-worlds: [world1, world2]`

### 4. Game Configuration
In your `MiniGameWorld/minigames` folder, open up the game config you want and set `instance-world` option to **true** (default is **false**). After then add your template world location to `locations` list. (location will be selected by random)
> E.g. in game config...
```yaml
...
  instance-world: true
  locations:
  - ==: org.bukkit.Location
    world: world1
    x: 50.0
    y: 100.0
    z: 50.0
    pitch: 0.0
    yaw: 0.0
  - ==: org.bukkit.Location
    world: world2
    x: 150.0
    y: 200.0
    z: 150.0
    pitch: 0.0
    yaw: 0.0
...
```



# Game will not be created
If the `instance-world` option is false **or** all worlds are being used. Because location being used can not be used at the same time. Even if another games is using the location.



# Warning
Too frequent world creation and deletion can slow your server performance.

