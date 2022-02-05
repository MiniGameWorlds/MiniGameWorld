# 1. Downlaod files
- You can use MiniGameWorld in [Spigot] or [Paper] bukkit
- Download [MiniGameWorld] and [wbmMC]

# 2. Create project
- You can use Eclipse or IntelliJ
- After create a project, setup the minecraft plugin dev settings (i.e. plugin.yml, Bukkit dependency)
- You also have to add [MiniGameWorld] dependency ([wbmMC] is optional)

# 3. plugin.yml
- You must add `MiniGameWorld` dependency in plugin.yml
```yaml
name: YourMiniGame
version: 1.0
main: your.packages.YourMiniGameMain
api-version: 1.x

depend: [MiniGameWorld]
```

[Spigot]: https://getbukkit.org/download/spigot
[Paper]: https://papermc.io/
[MiniGameWorld]: https://github.com/MiniGameWorlds/MiniGameWorld/releases
[wbmMC]: https://github.com/worldbiomusic/wbmMC/releases