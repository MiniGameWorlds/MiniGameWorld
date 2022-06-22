# Description
- This doucment describes how to make a `Minigame`, `Third-Party` plugin
- MiniGameWorld plugin's structure: [MiniGameWorld]



# Features
- Simple API
- Can Use all Events
- Can add any type of custom data with `Map<String, Object>` which user can edit
- Each minigames is isolated from each other (doesn't affect each other)
- Minigame util tools (soreboard, live, task ...etc)
- Also can make 3rd-party with API
- Custom Events
- Update checker
- Language support system


# Tutorial
- ## [how to create minigame](making-minigame-guide.md)

- ## [how to create 3rd party](making-3rd-party-guide.md)



# API design
<!-- <img src="api-design.png" width="49.5%"></img> -->
![](api-design.png)
- `Custom minigame` and `third-party` uses `MiniGameWorld` API
## Document
- [MiniGameWorld API](https://minigameworlds.github.io/MiniGameWorld/) ([list](https://github.com/MiniGameWorlds/MiniGameWorld/blob/main/docs/README.md))



# How to setup Dev Environment
- [Spigot] or [Paper]: Minecraft bukkit
- [MiniGameWorld]: MiniGame Framework
- [wbmMC]: Minecraft util library (optional)
- Add build path jars
- Add `[MiniGameWorld]` to `depend` section in `plugin.yml`



# ETC
- [Patch Note](https://github.com/MiniGameWorlds/MiniGameWorld/blob/main/resources/devWiki/log.md)
- This project uses [classgraph](https://github.com/classgraph/classgraph) library licensed from MIT to register all events in  Minecraft bukkit
- [Scoreboard Conflict]



# [How to develop MiniGameWorld plugin](dev-plugin-home.md)
- Description for development about `MiniGameWorld` plugin


[Spigot]: https://getbukkit.org/download/spigot
[Paper]: https://papermc.io/
[MiniGameWorld]: plugin-design.md
[wbmMC]: https://github.com/worldbiomusic/wbmMC/releases
[Paper API]: https://papermc.io/javadocs/paper/1.16/index.html?overview-summary.html
[Scoreboard Conflict]: https://github.com/MiniGameWorlds/MiniGameWorld/blob/main/resources/userWiki/scoreboard.md

