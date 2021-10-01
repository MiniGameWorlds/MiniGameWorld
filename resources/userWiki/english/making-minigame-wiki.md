# Description
- This doucment describe how to make minigame with `MiniGameWorld`
- About MiniGameWorld plugin's structure: [Dev Wiki]



# API
<!-- <img src="api-design.png" width="49.5%"></img> -->
![](api-design.png)
- `Custom minigame` and `third-party` uses `MiniGameWorld` API
## API class
- `MiniGameWorld`: can access data of MiniGameWorld plugin, can register minigame to MiniGameWorld plugin
- `MiniGameAccessor`: can access data of registered minigame in MiniGameWorld plugin



# How to set Dev Environment
- [Paper]: Minecraft bukkit
- [MiniGameWorld]: MiniGame Framework
- [wbmMC]: Minecraft util library (not essential) 
- Add build path libs
- `plugin.yml`: add `[MiniGameWorld]` in `depend` section



# How to make
## MiniGame
- [making-minigame-guide](making-minigame-guide.md)

## Thirdy party plugin
- [making-3rd-party-guide](making-3rd-party-guide.md)



# Tutorial
- [Tutorial]()



# Contact 
- [Discord]



[Dev Wiki]: ../devWiki/home.md
[Paper]: https://papermc.io/
[MiniGameWorld]: https://github.com/worldbiomusic/MiniGameWorld/releases
[wbmMC]: https://github.com/worldbiomusic/wbmMC
[Discord]: https://discord.com/invite/fJbxSy2EjA
[Paper API]: https://papermc.io/javadocs/paper/1.16/index.html?overview-summary.html
