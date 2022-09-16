# Description
This wiki describes how to use the MiniGameWorld plugin on your server. And this Plugin works on `spigot`, `paper`, `purpur` bukkit with `1.14+` versions. MiniGame has `Solo`, `SoloBattle`, `Team`, `TeamBattle`, `Fake` and `custom` types for playing

Minigames will work independently regardless of the server gamemodes(Economy, Survival, RPG, etc) with wide applicability and scalability. And one minigame can be played in many different ways with custom config settings and custom maps. Also player's all states(`Inventory`, `Health`, `Exp`, `Game Mode`, `etc`) are saved at the game start and restored when the game is finished.

---

# Features

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
- 3rd-parties ([MiniGameWorld-Reward], [MiniGameWorld-Rank], [MiniGameWorld-Controller])
- [Fake Minigame]



# How to apply
1. Download [MiniGameWorld], [wbmMC] and [Multiverse-Core].
2. Download [Demo MiniGames] that you want. Game configs will be saved in the `plugins/MiniGameWorld/minigames` folder.
3. Put `MiniGameWorld`, `wbmMC`, `Multiverse-Core` and `minigame` plugins in your `plugins` folder.
4. Make a place for minigame yourself or you can download template game maps in [here](https://github.com/MiniGameWorlds/MiniGameWorld-Test-Server).
5. Setup game location in `plugins/MiniGameWorld/minigames/<game>.yml` with `x, y, z` and run command `/mw reload` to adjust (OP required).  
**`---or---`**  
use command: `/mw games <game> locations <<player> | <x> <y> <z>>` (OP required).
> E.g. `/mw games GameA locations Steve`: set location to Steve's location  
> E.g. `/mw games GameA locations 0 4 0`: set location to 0(x) 4(y) 0(z)



# Download
### Required
- [wbmMC]: Essential library
- [MiniGameWorld]: Minigame Framework
- [Multiverse-Core]: Multi world plugin
### Optional
- [Demo MiniGames]: Demo games
- [MiniGameWorld-Reward]: Reward items and xp with a rank
- [MiniGameWorld-Rank]: Save the ranks
- [MiniGameWorld-Controller]: Control minigames with players
- [AdvancedMultiLanguamSupport multi languages


---

# Trouble shootings
## Server
- If you `stop` or `reload` your server without command (i.e. `/stop` or `/reload` or `/reload confirm` or `/restart`), must make sure that all players are not joining any minigames
- If you have some problems with **bungeecord** server, use [BungeeGuard] plugin

## Plugin
- If plugin is not loaded, try to change the `check-update` option in `settings.yml` config to **false** (update checker will not work if the plugin is loaded too often (for GitHub API rate limit))

## Language Messages
- If `edit-messages` option in `settings.yml` config is **true**, message configs in `MiniGameWorld/messages` change will be applied (default: `false`)

## If a game cannot be created
- **All worlds are being used**: If the `instance-world` option is **false**, the location being used can not be used at the same time. Even if another game is using the location.

- **Reached max instance count**: All games have the maximum instance count(`instances` option) so the game may have reached its maximum instance count.

- **Party is too big**: Can not join if the player's party is larger than the maximum player of minigame.


[MiniGameWorld]: https://github.com/MiniGameWorlds/MiniGameWorld/releases
[wbmMC]: https://github.com/etc-repo/wbmMC/releases
[Multiverse-Core]: https://www.spigotmc.org/resources/multiverse-core.390/
[MiniGameWorld-Reward]: https://github.com/MiniGameWorlds/MiniGameWorld-Reward/releases
[MiniGameWorld-Rank]: https://github.com/MiniGameWorlds/MiniGameWorld-Rank/releases
[MiniGameWorld-Controller]: https://github.com/MiniGameWorlds/MiniGameWorld-Controller/releases
[Demo MiniGames]: https://github.com/MiniGameWorlds/AllMiniGames
[Commands]: commands.md
[Third-Parties]: https://github.com/MiniGameWorlds
[Join / View / Leave]: how-to-join-leave.md
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