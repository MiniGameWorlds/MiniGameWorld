
[<img src="https://user-images.githubusercontent.com/61288262/183587918-374329ff-18d2-4c0b-84bd-11f7847c673d.png" width="20%"></img>](https://github.com/MiniGameWorlds)


# MiniGameWorld [![GitHub release (latest by date)](https://img.shields.io/github/v/release/MiniGameWorlds/MiniGameWorld?style=for-the-badge)](https://github.com/MiniGameWorlds/MiniGameWorld/releases)
Minecraft minigame framework  

**※ This plugin is under development**  
This is not stable and there must be many bugs and errors.


# Features
- Foolish and simple API
- Applicability and scalability regardless of the server genre
- Multi-game instances
- Multi-world instances (recommend [Paper](https://papermc.io/))
- Config control
- Language support
- Party
- View


# How to fast start
0.9.0 is not stable so below is how to setup minigame server fast with 0.8.2 and demo world and minigames.

1. Download your favorite bukkit ([paper](https://papermc.io/downloads/paper) recommended) and config your server directory.  
(server run.bat script for Windows)
```bat
@echo off
title Minecraft Paper Server

:loop
java -Xms1G -Xmx1G -Dfile.encoding=UTF-8 -jar paper-1.20.6.jar nogui

echo server stopped
echo press enter to restart the server
pause >nul

goto loop
```
2. Download the following plugins.
- [MiniGameWorld 0.8.2](https://github.com/MiniGameWorlds/MiniGameWorld/releases/download/0.8.2/MiniGameWorld-0.8.2.jar): minigame framework
- [WbmMC 0.3.11](https://github.com/etc-repo/wbmMC/releases/download/0.3.11/wbmMC-0.3.11.jar): common library
- [AllMiniGames 1.17](https://raw.githubusercontent.com/MiniGameWorlds/AllMiniGames/758c7d3ebaefb22a6d9b08c5b793ef85bb896b9b/download/AllMiniGames-1.17.jar): minigames
- [MiniGameWorld-Test.jar](https://raw.githubusercontent.com/MiniGameWorlds/MiniGameWorld-Test-Server/main/resources/MiniGameWorld-Test.jar): (Optional) prevent players from doing harmful things to your server like placing or breaking blocks.
3. Insert downloaded plugins into the plugins folder in your bukkit and start your bukkit. Then new worlds and MiniGameWorld(in plugins) folders will be created. And stop the server.
4. Download [MiniGameWorld-Test-Server zip file](https://github.com/MiniGameWorlds/MiniGameWorld-Test-Server/archive/refs/heads/main.zip) and unzip.
5. Remove and replace the world folder with "resources/world". (demo play world)
6. Remove and replace the plugins/MiniGameWorld folder with "resources/MiniGameWorld" (pre-setup configs)
7. Launch minecraft(tested in 1.20.6) and join your server. Then you should spawn in a concrete structure. Enjoy.
<img src="https://github.com/MiniGameWorlds/MiniGameWorld/assets/61288262/1b864452-d3d6-4652-b845-9bbb2c3f3835" width="49%"></img>


**[Tips]**  
- Type "/mw" to get menu opener (can join and leave games) or type "/mw menu" to open directly.
- Right-click signs to join a minigame.
- You can create your own minigame structure. Then, you have to change the location of the minigame config file (in plugins/MiniGameWorld/minigames/{minigame}.yml. You can tune up other game settings also. After edit any configs, type "/mw reload" to apply changes (OPs only)
- Deop players for normal play.
- When this plugin was developed, sign block right-click for editing didn't work. But now, it allows editing by it. So you can tell players to not change the sign content.
- See [youtube videos](https://www.youtube.com/@minigameworld2488/videos) how to play games.
- For more details, please refer to the [user wiki](https://github.com/MiniGameWorlds/MiniGameWorld/blob/main/resources/userWiki/Home.md) or discord(id: 4hhhh4)


# Wiki
If you want to add minigames to your server, see [User Wiki](resources/userWiki/Home.md)

If you want to develop a minigame, see [Dev Wiki](resources/devWiki/Home.md)
