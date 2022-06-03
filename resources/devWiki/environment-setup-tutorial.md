# 1. Downlaod files
- You can use MiniGameWorld in [Spigot] or [Paper] bukkit
- Download [MiniGameWorld] and [wbmMC]

# 2. Create project
- Open your IDE (e.g. Eclipse or IntelliJ or etc)
- After create a project, setup the minecraft plugin dev settings (i.e. plugin.yml, Bukkit([Spigot] or [Paper] or [Purpur]) dependency)
- You also have to add [MiniGameWorld] as a dependency to your plugin ([wbmMC] is optional)

# 3. plugin.yml
- You must add `MiniGameWorld` to `depend` of plugin.yml
```yaml
name: YourMiniGame
version: 1.0
main: your.packages.YourMiniGameMain
api-version: <MiniGameWorld support 1.14+>

# Add this!
depend: [MiniGameWorld]
```

[Spigot]: https://getbukkit.org/download/spigot
[Paper]: https://papermc.io/
[Purpur]: https://purpurmc.org/
[MiniGameWorld]: https://github.com/MiniGameWorlds/MiniGameWorld/releases
[wbmMC]: https://github.com/worldbiomusic/wbmMC/releases