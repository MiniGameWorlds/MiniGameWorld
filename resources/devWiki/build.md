# 1. Register your minigame
Register your minigame instance with `MiniGameWorld` API
```java
public class YourMiniGamePluginMain extends JavaPlugin {
	@Override
	public void onEnable() {
		super.onEnable();

		// load API
		MiniGameWorld mw = MiniGameWorld.create("x.x.x"); // API version (Latest: MiniGameWorld.API_VERSION)

    // register minigame
		mw.registerMiniGame(new YourMiniGame());
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}
}
```
API works if the second version(`minor version`) is the same with (e.g. `0.7.5` works with any `0.7.x`)

`MiniGameWorld.API_VERSION` is always latest version of the API


# 2. Build
Export(build) the plugin to `jar` file using your IDE




# 3. Test
## 3.1 Ready
Put [MiniGameWorld] and [wbmMC] and your minigame in the `plugins` folder

After running the server, you will see the registered minigame in green letters on the console, if the minigame is registered normally
![image](https://user-images.githubusercontent.com/61288262/170834753-bb32f19d-972f-4069-9ae2-0c3e5aa26a31.png)



## 3.2 Setup minigame spawn location
Minigame spawn location must be setup before playing so select one of the below methods (OP required)
> - method1: `/mw games <minigame> locations <player>`: add `<player>`'s location as a minigame spawn location 
> - method2: `/mw games <minigame> locations <x> <y> <z>`: add `<x>` `<y>` `<z>` as a minigame spawn location
> - method3: `/mw reload` command after editing the minigame spawn `location` in the config file (`plugins/MiniGameWorld/minigames/<minigame>.yml`)



## 3.3 Join the minigame
Run `/mw menu` command and click the minigame icon of the upper GUI menu 
![image](https://user-images.githubusercontent.com/61288262/170742634-2988cfe1-a6a7-4c46-9b0e-dc51ce67e9c6.png)


## 3.4 Test
Play and check out the rules of your minigame



# 4. Share your minigame
Others can play our minigame wherever they have [MiniGameWorld] and [wbmMC] plugins, but it would be better if you give the information of your mingame

And it is also a good way to summarize the information of the minigame in this form below (of course, it's good to tell them the minigame spawn setup method)

> [Example Minigame Information](https://github.com/MiniGameWorlds/AllMiniGames/blob/main/wiki/LavaUp.md)
```yaml
# <MiniGame Name>
- <description>
- Bukkit: `Spigot`
- Type: <`Solo` | `SoloBattle` | `Team` | `TeamBattle` | `Custom`>
- API Version: `<version>`
- Minecraft Version: `1.14+`


# How to play
- <way to play>


# Play Video
- [Youtube]()


# Config
# You can find the minigame config file in `.../plugins/MiniGameWorld/minigames/<minigame>.yml`
<-- ```yaml
Config
``` -->
- <Config descriptions>


# Warning
- <e.g. Do not build with blocks that can burn >
```




# Finish
- If you have some questions, follow this [link](https://discord.gg/fJbxSy2EjA)
- If you have some interest in this API, check out this [user wiki](https://github.com/MiniGameWorlds/MiniGameWorld/blob/main/resources/userWiki/Home.md) and [dev wiki](https://github.com/MiniGameWorlds/MiniGameWorld/blob/main/resources/devWiki/Home.md)
