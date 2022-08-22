# Description
Third party plugin can access/change below things
- [Join / Leave / View](#Minigame-Access)
- [Minigame Exception](#Exception)
- [Menu](#Menu)
- [Party](#Party)
- [Observer](#Observer-System)
- [Custom Events](#Custom-Events)

---

# Setup
1. [API Introduction]
2. [Setup guide]

---

# Minigame Access
`Join / Leave / View` ways can be changed.
## Example
- join with chat
```java
@EventHandler
public void onPlayerChat(AsyncPlayerChatEvent e) {
	Player p = e.getPlayer();
	String msg = e.getMessage();

	String[] tokens = msg.split(" ");
	if (tokens.length != 2) {
		return;
	}

	MiniGameWorld mw = MiniGameWorld.create("x.x.x");
	
	String minigameTitle = tokens[1];
	if (tokens[0].equals("join")) {
		// join game
		mw.joinGame(p, minigameTitle);
	}
}
```
- leave minigame with right-click block
```java
@EventHandler
public void onPlayerClickLeaveBlock(PlayerInteractEvent e) {
	Player p = e.getPlayer();
	if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
		Block b = e.getClickedBlock();
		if (b.getType() == Material.BEDROCK) {
			MiniGameWorld mw = MiniGameWorld.create("x.x.x");
			// leave minigame
			mw.leaveGame(p);
		}
	}
}
```
- view/unview with chat
```java
@EventHandler
public void onPlayerChat(AsyncPlayerChatEvent e) {
	Player p = e.getPlayer();
	String msg = e.getMessage();

	String[] tokens = msg.split(" ");
	if (tokens.length != 2) {
		return;
	}

	MiniGameWorld mw = MiniGameWorld.create("x.x.x");
	
	String minigameTitle = tokens[1];
	if (tokens[0].equals("view")) {
		// view game
		mw.viewGame(p, minigameTitle);
	} else if (tokens[0].equals("unview")) {
		// unview(leave) game 
		mw.unviewGame(p);
	}
}
```

---

# Menu
Can add custom icon to [menu](../../userWiki/menu.md). But beware of already exist icons in `menu` inventory.
## Example
- Add custom slot to menu
```java
@Override
public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	// only player
	if (!(sender instanceof Player)) {
		return true;
	}

	Player p = (Player) sender;

	if (args[0].equalsIgnoreCase("m")) {
		MiniGameWorld mw = MiniGameWorld.create("x.x.x");

		// get opened menu inventory
		Inventory inv = mw.openMenu(p);

		// custom slot
		ItemStack customSlot = new ItemStack(Material.REDSTONE);
		ItemMeta meta = customSlot.getItemMeta();
		meta.setDisplayName("custom slot");
		customSlot.setItemMeta(meta);

		// set slot to inv
		inv.setItem(7, customSlot);
	}

	return true;
}
```
---

# Party
Can use different ways to control the parties
```java
/*
 * Ask a player to join party by right-click	
 */
@EventHandler
public void onPlayerAskPartyJoin(PlayerInteractAtEntityEvent e) {
	// check clicked entity is a player
	if (!(e.getRightClicked() instanceof Player)) {
		return;
	}

	Player asker = e.getPlayer();
	Player asked = (Player) e.getRightClicked();

	// get party manager
	MiniGameWorld mw = MiniGameWorld.create("x.x.x");
	PartyManager partyManager = mw.partyManager();

	// show ask clickable chat to asked player
	partyManager.ask(asker, asked);
}
```

---

# Observer System
Observer can reserve tasks with timing of MiniGame. Implements `update()` of `MiniGameObserver` interface to use

## Timing
- `REGISTRATION`: Called when a minigame is registered to MiniGameWorld
- `UNREGISTRATION`: Called when a minigame is unregistered from MiniGameWorld

## Examples
Used in [MiniGameWorld-Rank]
```java
@Override
public void update(MiniGameAccessor minigame, Timing timing) {
	// load exist minigame rank data (if not exist, create new config)
	if (timing == Timing.REGISTRATION) {
		MiniGameRank rank = new MiniGameRank(minigame);

		this.rankList.add(rank);
		this.yamlManager.registerMember(rank);

		// sort rank orders
		rank.sortRankOrders();
	} else if (timing == Timing.UNREGISTRATION) {
		MiniGameRank rank = null;
		for (MiniGameRank r : this.rankList) {
			if (r.getMinigame().isSameTemplate(minigame)) {
				rank = r;
			}
		}

		// save data and unregister
		if (rank != null) {
			this.yamlManager.save(rank);
			this.yamlManager.unregisterMember(rank);
			this.rankList.remove(rank);
		}
	}
}
```

---

# Custom Events
There are some minigame timing events. Register `Listener` to handle events. (To see all events, see [java doc](https://minigameworlds.github.io/MiniGameWorld/))

## MiniGameEvent
All minigame events extend `MiniGameEvent`
- `MiniGameStartEvent`: Called when a minigame starts 
- `MiniGameFinishEvent`: Called when a minigame finished
- `MiniGameExceptionEvent`: Called when a exception related with minigame has occurred
- `MiniGameServerExceptionEvent`: Called when a exception related with server has occurred
- `MiniGamePlayerExceptionEvent`: Called when a exception related with a playing player has occurred
- `MiniGameScoreboardUpdateEvent`: Called when a scoreboard of minigame is updated 
- `MiniGamePlayerJoinEvent`: Called when a player try to join a minigame
- `MiniGamePlayerLeaveEvent`: Called when a player try to leave a minigame
- `MiniGamePlayerViewEvent`: Called when a player try to view a minigame
- `MiniGamePlayerUnviewEvent`: Called when a player try to unview a minigame
## MenuEvent
All menu events extend `MenuEvent`
- `MenuOpenEvent`: Called when a player opens menu
- `MenuCloseEvent`: Called when a player closes menu
- `MenuClickEvent`: Called when menu is clicked


## Examples
### Reward System
Can give reward when a minigame has finished. (Used in [MiniGameWorld-Reward])
```java
class RewardManager implements Listener {

	@EventHandler
	public void onMiniGameFinish(MiniGameFinishEvent e) {
		MiniGameAccessor minigame = e.getMiniGame();
		// give rewards when finish
		List<? extends MiniGameRank> rankList = minigame.rank();

		for (int i = 0; i < 3; i++) {
			MiniGameRank rankResult = rankList.get(i);
			for (Player p : rankResult.getPlayers()) {
				// give rewards
				p.getInventory().addItem(new ItemStack(Material.OAK_WOOD, 10 - i));
			}
		}
	}
}
```
### Save Rank Data
Can save rank data with finished players data(score, live, team members, etc.) (Used in [MiniGameWorld-Rank])
```java
class RankManager implements Listener {
	JavaPlugin plugin;
	public RankManager(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onMiniGameFinish(MiniGameFinishEvent e) {
		MiniGameAccessor minigame = e.getMiniGame();
		List<? extends MiniGameRank> rankResult = minigame.rank();

		// check rank is empty
		if (rankResult.isEmpty()) {
			return;
		}

		// get minigame data section
		String minigameClassName = minigame.className();
		ConfigurationSection section = plugin.getConfig().getConfigurationSection(minigameClassName);

		for (MiniGameRank result : rankResult) {
			int score = result.getScore();
			for (Player p : result.getPlayers()) {
				// get player's name, score
				String playerName = p.getName();

				// set data
				section.set(playerName, score);
			}
		}

		// save config
		plugin.saveConfig();
	}
}
```
### Join fee
Also you can cancel the event if you want to add specific condition.
```java
@EventHandler
public void onPlayerJoinMiniGame(MiniGamePlayerJoinEvent e) {
	Player p = e.getPlayer();
	Inventory inv = p.getInventory();
	
	// check player has diamonds
	if (!inv.contains(Material.DIAMOND)) {
		p.sendMessage("You need diamond to join the minigame!");
		// cancel join event
		e.setCancelled(true);
		return;
	}

	// remove 1 diamond
	for (ItemStack item : inv.getContents()) {
		if (item != null && item.getType() == Material.DIAMOND) {
			item.setAmount(item.getAmount() - 1);
			break;
		}
	}
	
	p.sendMessage("You pay 1 diamond to join this minigame!");
}
```

### Exception
There are 3 exception types

- `MiniGamePlayerException`: leave the player from the minigame by throwing a exception
```java
// minigame player exception: playing player will leave from the minigame
if (MwUtil.isPlayingGame(p)) {
	Bukkit.getServer().getPluginManager()
			.callEvent(new MiniGamePlayerExceptionEvent("reason", p));
}
```

- `MiniGameException`: finish the minigame
```java
// minigame exception: speific minigame will finish
MiniGame minigame = this.minigameManager.getInstanceGames().get(0);
Bukkit.getServer().getPluginManager()
		.callEvent(new MiniGameExceptionEvent(minigame, "reason"));
```

- `MiniGameServerException`: finish all minigames
```java
// server exception: all minigames will finish
Bukkit.getServer().getPluginManager().callEvent(new MiniGameServerExceptionEvent("reason"));
```

### Menu customizing
Example plugin: [MiniGameWorld-Controller]
```java
@EventHandler
public void onMenuOpenEvent(MenuOpenEvent e) {
	// get menu inventory
	Inventory menu = e.getMenu();

	// custom icon
	ItemStack customIcon = new ItemStack(Material.RED_BED);
	ItemMeta meta = customIcon.getItemMeta();
	meta.setDisplayName("Custom Icon");
	customIcon.setItemMeta(meta);

	// insert custom icon to menu
	menu.setItem(8, customIcon);
}

@EventHandler
public void onMenuClickEvent(MenuClickEvent e) {
	// cancel event
	e.setCancelled(true);

	// get clicked icon
	ItemStack icon = e.getIcon();
	if (icon == null) {
		return;
	}

	// just print icon display name
	Player p = e.getPlayer();
	p.sendMessage("Clicked icon name: " + icon.getItemMeta().getDisplayName());
}
```

---


# Custom event detector
`Event` passed to minigame is filtered by player's minigame in the default MiniGameEventDetector. However, custom detector allows you to pass event you want to all minigames

## Example
1. Create custom detector
```java
class MyCustomEventDetector implements MiniGameEventExternalDetector {

	@Override
	public Set<Player> getPlayersFromEvent(Event event) {
		Set<Player> players = new HashSet<>();

		// Make ProjectileLaunchEvent event pass to all minigames if shooter is a player
		if (event instanceof ProjectileLaunchEvent) {
			ProjectileLaunchEvent e = (ProjectileLaunchEvent) event;
			Projectile proj = e.getEntity();
			if (proj.getShooter() instanceof Player) {
				Player shooter = (Player) proj.getShooter();
				players.add(shooter);
			}
		}
		return players;
	}
}
```

2. Register detector to `MiniGameWorld`
```java
public class MyPluginMain extends JavaPlugin {
	@Override
	public void onEnable() {
		MiniGameWorld mw = MiniGameWorld.create("x.x.x");
		
		// register custom detector
		mw.registerExternalEventDetector(new MyCustomEventDetector());
	}
}
```



[MiniGameWorld-Reward]: https://github.com/MiniGameWorlds/MiniGameWorld-Reward
[MiniGameWorld-Rank]: https://github.com/MiniGameWorlds/MiniGameWorld-Rank
[MiniGameWorld-Controller]: https://github.com/MiniGameWorlds/MiniGameWorld-Controller



[API Introduction]: ../game-guide/introduction.md
[Setup guide]: ../game-guide/setup.md
[Build guide]: ../game-guide/build.md