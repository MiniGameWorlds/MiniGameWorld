# Description
- This document describe how to use MiniGameWorld pluin in your server



# How to apply
1. Download [MiniGameWorld]
2. Download [MiniGames] that you want to add to your server
3. Put `MiniGameWorld` and `minigame plugins` in server `plugins` folder
4. Make minigame place for minigame
5-1. Update minigame location in `plugins/MiniGameWorld/minigames/<title>.yml` file and run command `/minigame reload`
5-2. Update minigame location using `/minigame minigames <title> location [<x> <y> <z>]`


# How to join / leave
- [Join / Leave]



# Types
- Solo
- SoloBattle
- Team
- Team Battle
- Custom



# Commands
- [Commands]



# Party System
- Party **doesn't** have `leader`
- All members can `join` / `leave` minigames
- All members can `invite` / `allow` players
- All members can `kickvote` player 
- Party members can join minigame `separately`


# Plugins
- [MiniGames]: MiniGames made by some makers
- [Third-Parties]: You can `give reward with rank `, `save rank data` and etc with third party plugins




# Tutorial
- [Youtube]()



# Backup Data
- Config automatically saved when server stopped
- Directory: `MiniGameWorld_backup`
- Format: `2021-10-03+19;07;58` means saved at `2021year 10month 3day PM 7hour 7minute 58second`



# Caution
- Not recommend to run `/reload` in the server console
- Player's `inventory`, `health`, `food level`, `exp`, `potion effects`, `glowing`, `hiding`, `game mode` will be stored at game starting and be restored at the game end



# Config
- [Config](config.md)



[MiniGameWorld]: https://github.com/worldbiomusic/MiniGameWorld/releases
[MiniGames]: https://github.com/worldbiomusic/MiniGameWorld/discussions/categories/minigames
[Commands]: commands.md
[Third-Parties]: https://github.com/worldbiomusic/MiniGameWorld/discussions/categories/third-parties
[Join / Leave]: how-to-join-leave.md
