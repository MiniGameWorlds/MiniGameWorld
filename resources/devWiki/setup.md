# Setup
Need a [Spigot] or forked bukkit(paper, purpur, etc) bukkit server to test MiniGameWorld plugin


# 1. Download files
Download [MiniGameWorld] and [wbmMC]



# 2. Create project
After creating a project, setup the minecraft plugin dev settings (plugin.yml, Spigot dependency) in your IDE

You also have to add [MiniGameWorld] as a dependency ([wbmMC] is optional)
> ## How to add API dependency in IDEs
> - In eclipse: `project right-click` -> `Build Path` -> `Configure Build Path` -> click `Libraries` tab -> click `Add External JARs` -> add `MiniGameWorld.jar` plguin
> - In IntelliJ: `File` -> `Project Structure` -> `Modules` -> `Dependencies` -> `+ button` -> `JARs or Directories` -> add `MiniGameWorld.jar` plugin


# 3. plugin.yml
**Add `MiniGameWorld` to `depend` in plugin.yml**
```yaml
name: YourMiniGame
version: 1.0
main: your.packages.YourMiniGameMain
api-version: 1.x

# Add MiniGameWorld
depend: [MiniGameWorld]
```







[Spigot]: https://getbukkit.org/download/spigot
[Paper]: https://papermc.io/
[Purpur]: https://purpurmc.org/
[MiniGameWorld]: https://github.com/MiniGameWorlds/MiniGameWorld/releases
[wbmMC]: https://github.com/worldbiomusic/wbmMC/releases
