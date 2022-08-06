# Description
This wiki describes how to use MiniGameWorld pluin in your server. And this Plugin works on `spigot`, `paper`, `purpur` bukkit with `1.14+` versions. MiniGame has `Solo`, `SoloBattle`, `Team`, `TeamBattle`, `Fake` and `custom` types for playing

---

# Features
- **Works independently with other plugins** (Available for all types of servers: Economy, Survival, RPG, etc)
- Each minigames are isolated from each other (doesn't affect each other)
- Same minigame can be played in many different ways with custom config settings and custom maps 
- A player's all state(`Inventory`, `Health`, `Food level`, `Exp`, `Potion Effects`, `Glowing`, `Hiding`, `Game Mode` , `etc`) are saved at the game start and restored when the game finished
- [Join / View / Leave]
- [Menu]
- [Party]
- [View]
- [Scoreboard]
- [Permissions]
- [Commands]
- [Config]
- [Backup]
- [Chat]
- [World instance]
- [Game instance]
- [Update checker]
- [Language Support]
- [Function Item]
- 3rd-parties (e.g. [MiniGameWorld-Reward], [MiniGameWorld-Rank], [MiniGameWorld-Controller])
- [Fake Minigame]



# How to apply
1. Download [MiniGameWorld] and [wbmMC]
2. Download [MiniGames] that you want to add to your server
3. Put `MiniGameWorld`, `wbmMC` and `minigame plugins` in server `plugins` folder
4. Make a place for minigame yourself
5. Setup minigame location in `plugins/MiniGameWorld/minigames/<game>.yml` file with `x, y, z` and run command `/minigame reload` (OP required)  
**`---or---`**  
Setup minigame location using command `/minigame games <game> locations <<player> | <x> <y> <z>>` (OP required)



# Download
### Required
- [wbmMC]: Essential library
- [MiniGameWorld]: Minigame Framework
- [Multiverse-Core]: Multi world plugin
### Optional
- [MiniGames]: MiniGames made by some makers
- [MiniGameWorld-Reward]: Reward items and xp with rank
- [MiniGameWorld-Rank]: Save the ranks
- [MiniGameWorld-Controller]: Control minigame players
- [AdvancedMultiLanguage]: Support mulit languages



# Tutorial


---

# Trouble shootings
## Server
- If you `stop` or `reload` your server without command (i.e. `/stop` or `/reload` or `/reload confirm` or `/restart`), must make sure that all players are not joining any minigames
- If you have some problems with **bungeecord** server, use [BungeeGuard] plugin

## Plugin
- If plugin is not loaded, try to change the `check-update` option in `settings.yml` config to **false** (update checker will not work if plugin is loaded too often (for github api rate limit))

## Language Messages
- If `edit-messages` option in `settings.yml` config is **false**, message changes will not be saved

## If game cannot be created
- **All worlds are being used**: If the `instance-world` option is **false**, location being used can not be used at the same time. Even if another games is using the location.

- **Reached max instance count**: All games have the maximum instance count so the game may have reached its maximum instance count.

- **Party is too big**: Player's party is larger than the maximum player of minigame.


[MiniGameWorld]: https://github.com/MiniGameWorlds/MiniGameWorld/releases
[wbmMC]: https://github.com/etc-repo/wbmMC/releases
[Multiverse-Core]: https://www.spigotmc.org/resources/multiverse-core.390/
[MiniGameWorld-Reward]: https://github.com/MiniGameWorlds/MiniGameWorld-Reward/releases
[MiniGameWorld-Rank]: https://github.com/MiniGameWorlds/MiniGameWorld-Rank/releases
[MiniGameWorld-Controller]: https://github.com/MiniGameWorlds/MiniGameWorld-Controller/releases
[MiniGames]: https://github.com/MiniGameWorlds/AllMiniGames
[Commands]: commands.md
[Third-Parties]: https://github.com/MiniGameWorlds
[Join / View / Leave]: how-to-join-leave.md
[Youtube: User Tutorial]: https://youtu.be/sE0vaj0xM8Q
[Config]: config.md
[Permissions]: permissions.md
[Party]: party.md
[Backup]: backup.md
[Chat]: chat.md
[Game instance]: game-instance.md
[World instance]: world-instance.md
[Update checker]: update-checker.md
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