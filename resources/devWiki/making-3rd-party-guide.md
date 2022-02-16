# Description
- Third party plugin can access/change below things
- [Join / Leave / View](#Minigame-Join-/-Leave-/-View)
- [Minigame Exception](#Minigame-Exception)
- [Menu](#Menu)
- [Party](#Party)
- [Observer](#Observer-System)
- [Custom Events](#Custom-Events)


# Tutorial
<a href="https://youtu.be/">
<img src="youtube-thirdparty-dev-tutorial-thumbnail.png" width="50%" ></img>
</a>
- In making...

---

# Minigame Join / Leave / View
- Can change `Join / Leave / View` way 
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
- Can add custom slot to `menu`
- Beware of already exist slot in `menu` inventory
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
- Can use different way to access party
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
	PartyManager partyManager = mw.getPartyManager();

	// show ask clickable chat to asked player
	partyManager.ask(asker, asked);
}
```

---

# Observer System
- Observer can reserve tasks with timing of MiniGame
- Implements `update()` of `MiniGameObserver` interface to use

## Timing
- `REGISTRATION`: When minigame is registered to MiniGameWorld plugin
- `UNREGISTRATION`: When minigame is unregistered from MiniGameWorld plugin

## Examples
- Used in [MiniGameWorld-Rank]
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
			if (r.getMinigame().equals(minigame)) {
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
- There are some minigame timing events
- Use `Listener` for handling events

## MiniGameEvent
- All minigame events extends `MiniGameEvent`
- `MiniGameStartEvent`: Called when a minigame starts 
- `MiniGameEventPassEvent`: Called when a event passed to a started minigame
- `MiniGameFinishEvent`: Called when a minigame finished
- `MiniGameExceptionEvent`: Called when a exception related with minigame has occurred
- `MiniGameServerExceptionEvent`: Called when a exception related with server has occurred
- `MiniGamePlayerExceptionEvent`: Called when a exception related with a playing player has occurred
- `MiniGameScoreboardUpdateEvent`: Called when a scoreboard of minigame is updated 
- `MiniGamePlayerJoinEvent`: Called when a player try to join a minigame
- `MiniGamePlayerLeaveEvent`: Called when a player try to leave a minigame
- `MiniGamePlayerViewEvent`: Called when a player try to view a minigame
- `MiniGamePlayerUnviewEvent`: Called when a player try to unview a minigame

## Examples
### Reward System
- Used in [MiniGameWorld-Reward]
- Give reward when minigame finished
- Can distinguish with `class name` or `title` of minigame
```java
class RewardManager implements Listener {

	@EventHandler
	public void onMiniGameFinish(MiniGameFinishEvent e) {
		MiniGameAccessor minigame = e.getMiniGame();
		// give rewards when finish
		List<? extends MiniGameRankResult> rankList = minigame.getRank();

		for (int i = 0; i < 3; i++) {
			MiniGameRankResult rankResult = rankList.get(i);
			for (Player p : rankResult.getPlayers()) {
				// give rewards
				p.getInventory().addItem(new ItemStack(Material.OAK_WOOD, 10 - i));
			}
		}
	}
}
```
### Save Rank Data
- Used in [MiniGameWorld-Rank]
- Save rank data to config
```java
class RankManager implements Listener {

	JavaPlugin plugin;
	public RankManager(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onMiniGameFinish(MiniGameFinishEvent e) {
		MiniGameAccessor minigame = e.getMiniGame();
		List<? extends MiniGameRankResult> rankResult = minigame.getRank();

		// check rank is empty
		if (rankResult.isEmpty()) {
			return;
		}

		// get minigame data section
		String minigameClassName = minigame.getClassName();
		ConfigurationSection section = plugin.getConfig().getConfigurationSection(minigameClassName);

		for (MiniGameRankResult result : rankResult) {
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
### Fee for Join
- Use cancellable `MiniGamePlayerJoinEvent`
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
- There are 3 exception types
- `MiniGamePlayerException`: leave the player from the minigame
```java
// minigame player exception: playing player will leave from the minigame
if (MiniGameWorldUtils.checkPlayerIsPlayingMiniGame(p)) {
	Bukkit.getServer().getPluginManager()
			.callEvent(new MiniGamePlayerExceptionEvent("reason", p));
}
```
- `MiniGameException`: finish the minigame
```java
// minigame exception: speific minigame will finish
MiniGame minigame = this.minigameManager.getMiniGameList().get(0);
Bukkit.getServer().getPluginManager()
		.callEvent(new MiniGameExceptionEvent(minigame, "reason"));
```
- `MiniGameServerException`: finish all minigames
```java
// server exception: all minigames will finish
Bukkit.getServer().getPluginManager().callEvent(new MiniGameServerExceptionEvent("reason"));
```

---


# Custom event detector
- `Event` passed to minigame is filtered by player's minigame in the default MiniGameEventDetector.
- However, custom detector allows you to pass event you want to all minigames

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
		mw.registerMiniGameEventExternalDetector(new MyCustomEventDetector());
	}
}
```



[MiniGameWorld-Reward]: https://github.com/MiniGameWorlds/MiniGameWorld-Reward
[MiniGameWorld-Rank]: https://github.com/MiniGameWorlds/MiniGameWorld-Rank