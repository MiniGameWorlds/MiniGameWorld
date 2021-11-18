# Description
- This document describe how to use MiniGameWorld pluin in your server
- Plugin works with `spigot`, `paper` bukkit
- MiniGame types: `Solo`, `SoloBattle`, `Team`, `TeamBattle`



# How to apply
1. Download [MiniGameWorld] and [wbmMC]
2. Download [MiniGames] that you want to add to your server
3. Put `MiniGameWorld`, `wbmMC` and `minigame plugins` in server `plugins` folder
4. Make minigame place for minigame  
5-1. Update minigame location in `plugins/MiniGameWorld/minigames/<class-name>.yml` file and run command `/minigame reload`  
5-2. Update minigame location using `/minigame minigames <class-name> location <<player> | <x> <y> <z>>`  



# Link
- [Join / Leave]
- [Commands]
- [Config]
- [Permissions]



# Party System
- Party **doesn't** have `leader`
- All members can `join` / `leave` minigames
- All members can `invite` / `allow` players
- All members can `kickvote` player 
- Party members can join minigame `separately`



# Download
- [MiniGameWorld]: MiniGameWorld Framework
- [wbmMC]: Essential library
- [MiniGames]: MiniGames made by some makers
- [Third-Parties]: You can `give reward with rank `, `save rank data` and etc with third party plugins



# Tutorial
<a href="https://www.youtube.com/watch?v=ibilvmzcdzs&list=PLOyhTkb3nnYbBtEdS38nkIpyU8RM-pEZd">
<img src="youtube-user-tutorial-thumbnail.png" width="50%" ></img>
</a>



# Backup
- Config automatically saved when server stopped
- Directory: `MiniGameWorld_backup`
- Format: `2021-10-03+19;07;58` means saved at `2021year 10month 3day PM 7hour 7minute 58second`



# Caution
- Not recommend to run `/reload` in the server console
- A player's `Inventory`, `Health`, `Food level`, `Exp`, `Potion Effects`, `Glowing`, `Hiding` and `Game Mode` are saved at the game start and restored when the game finished







[MiniGameWorld]: https://github.com/MiniGameWorlds/MiniGameWorld/releases
[wbmMC]: https://github.com/worldbiomusic/wbmMC/releases
[MiniGames]: https://github.com/MiniGameWorlds/MiniGameWorld/discussions/categories/minigames
[Commands]: commands.md
[Third-Parties]: https://github.com/MiniGameWorlds/MiniGameWorld/discussions/categories/third-parties
[Join / Leave]: how-to-join-leave.md
[Youtube: User Tutorial]: https://youtu.be/sE0vaj0xM8Q
[Config]: config.md
[Permissions]: permissions.md
