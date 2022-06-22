# Description
- This document describes how to use MiniGameWorld pluin in your server
- Plugin works on `spigot`, `paper`, `purpur` bukkit with `1.14+` versions
- MiniGame types: `Solo`, `SoloBattle`, `Team`, `TeamBattle` and `custom`

---

# Features
- **Works independently of other plugins** (Available for all types of servers: Economy, Survival, RPG, etc)
- Each minigames is isolated from each other (doesn't affect each other)
- Same minigame can be played in many different ways with custom config settings and custom maps 
- A player's all state(`Inventory`, `Health`, `Food level`, `Exp`, `Potion Effects`, `Glowing`, `Hiding`, `Game Mode and etc`) are saved at the game start and restored when the game finished
- [Join / View / Leave]
- [Menu] system
- [Party] system
- [View] system
- [Scoreboard] system
- [Permissions]
- [Commands]
- [Config] control
- [Backup] system
- Update checker
- [Language Support]
- [Function Item]
- 3rd-parties (e.g. [MiniGameWorld-Reward], [MiniGameWorld-Rank], [MiniGameWorld-Controller])
- [Fake Minigame] system

# How to apply
1. Download [MiniGameWorld] and [wbmMC]
2. Download [MiniGames] that you want to add to your server
3. Put `MiniGameWorld`, `wbmMC` and `minigame plugins` in server `plugins` folder
4. Make a place for minigame yourself
5. Setup minigame location in `plugins/MiniGameWorld/minigames/<class-name>.yml` file with `x, y, z` and run command `/minigame reload` (OP required)  
**`---or---`**  
Setup minigame location using command `/minigame games <class-name> location <<player> | <x> <y> <z>>` (OP required)  



# Download
- [MiniGameWorld]: MiniGameWorld Framework
- [wbmMC]: Essential library
- [MiniGames]: MiniGames made by some makers (optional)
- [Third-Parties]: You can `give reward with rank `, `save rank data` and etc with third party plugins (optional)
- [AdvancedMultiLanguage]: Support mulit languages (optional)



# Youtube Tutorial
<a href="https://www.youtube.com/watch?v=sE0vaj0xM8Q">
<img src="youtube-user-tutorial-thumbnail.png" width="50%" ></img>
</a>

---

# Trouble shootings
## Server
- If you `stop` or `reload` your server without command (i.e. `/stop` or `/reload` or `/reload confirm` or `/restart`), must make sure that all players are not joining any minigames
- If you have some problems with **bungeecord** server, use [BungeeGuard] plugin

## Plugin
- If plugin is not loaded, try to change the `check-update` option in `settings.yml` config to **false** (update checker will not work if plugin is loaded too often (for github api rate limit))

## Language Messages
- If `edit-messages` option in `settings.yml` config is **false**, message changes will not be saved



[MiniGameWorld]: https://github.com/MiniGameWorlds/MiniGameWorld/releases
[wbmMC]: https://github.com/worldbiomusic/wbmMC/releases
[MiniGames]: https://github.com/MiniGameWorlds/AllMiniGames
[Commands]: commands.md
[Third-Parties]: https://github.com/MiniGameWorlds
[Join / View / Leave]: how-to-join-leave.md
[Youtube: User Tutorial]: https://youtu.be/sE0vaj0xM8Q
[Config]: config.md
[Permissions]: permissions.md
[Party]: party.md
[Backup]: backup.md
[View]: view.md
[Menu]: menu.md
[Scoreboard]: scoreboard.md
[Language Support]: language-support.md
[Function Item]: function-item.md
[Fake Minigame]: fake-minigame.md
[MiniGameWorld-Reward]: https://github.com/MiniGameWorlds/MiniGameWorld-Reward
[MiniGameWorld-Rank]: https://github.com/MiniGameWorlds/MiniGameWorld-Rank
[MiniGameWorld-Controller]: https://github.com/MiniGameWorlds/MiniGameWorld-Controller
[AdvancedMultiLanguage]: https://www.spigotmc.org/resources/advanced-multi-language.21338/
[BungeeGuard]: https://www.spigotmc.org/resources/bungeeguard.79601/