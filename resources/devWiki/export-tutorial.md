# 1. Register minigame with API
```java
public class YourMiniGamePluginMain extends JavaPlugin {
	@Override
	public void onEnable() {
		super.onEnable();

		// register minigame
		MiniGameWorld mw = MiniGameWorld.create("x.x.x"); // API version
		mw.registerMiniGame(new YourMiniGame());
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}
}
```

# 2. Export jar file
- Export your plugin to jar file

# 3. Test
- Locate your minigame plugin to `.../plugins` directory in your bukkit directory
- `.../plugins` directory has to also contain [MiniGameWorld] and [wbmMC]
- Run your server and check log in console
- If your minigame registered,  
you **have to** setup minigame location in `plugins/MiniGameWorld/minigames/<class-name>.yml` file and run `/minigame reload` command (need OP)  
**`or`**  
Update minigame location using command `/minigame minigames <class-name> location <<player> | <x> <y> <z>>` (need OP)  
- Now, players can enjoy your minigame! (try `/mw menu` command for GUI menu)

# 4. Share your minigame
- Others can enjoy your minigame with your minigame plugin
- However, others can play your minigame better if you inform about your minigame like below

```yaml
# <MiniGame Name>
- <description>
- Bukkit: `Spigot` <!--  Write bukkit, If event of minigame is only available in specific bukkit-->
- Type: <`Solo` | `SoloBattle` | `Team` | `TeamBattle` | `Custom`>
- API Version: `<version>`
- Minecraft Version: `<version>`

# How to play
- <way to play>

# Play Video
- <video url>

# Config
<```yaml
Config
```>
- <!-- (Config-guide) If need description for special custom data in config>

# Warning
<!-- 
<e.g. - (Map guide) If required a special map>
<e.g. - Avoid building with Brick_Block>

<e.g. - (Other required Rules)>
<e.g. - Make sure PVP on>
-->
```






[MiniGameWorld]: https://github.com/MiniGameWorlds/MiniGameWorld/releases
[wbmMC]: https://github.com/worldbiomusic/wbmMC/releases