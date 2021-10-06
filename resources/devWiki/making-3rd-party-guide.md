# Description
- Third party plugin can access/change below things
```yaml
- Join/Leave 
- Minigame Exception
- Menu
- Party
- Observer
```

---

# Minigame Join/Leave
- Can change `Join/Leave` way 
## Example
```java
// join minigame with portal
@EventHandler
public void onPlayerEnterPortal(EntityPortalEnterEvent e) {
  if (e.getEntity() instanceof Player) {
    Player p = (Player) e.getEntity();
    MiniGameWorld mw = MiniGameWorld.create("x.x.x");
    // join minigame
    mw.joinGame(p, "Minigame-Title");
  }
}

// leave minigame with right-click block
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
---

# Minigame Exception
- Can process various exception
## Example
- Send custom exception to minigames
```java
public void processServerEvent(Player p) {
	MiniGame.Exception ex = MiniGame.Exception.CUSTOM;
	ex.setDetailedReason("SERVER_EVENT_TIME");
	MiniGameWorld mw = MiniGameWorld.create("x.x.x");
	mw.handleException(p, ex, null);
 }
```

- Handle exception in minigame
```java
@Override
 protected void handleGameException(Player p, Exception exception, Object arg) {
	super.handleGameException(p, exception);
	if (exception == MiniGame.Exception.CUSTOM) {
		String detailedReason = exception.getDetailedReason();
		if (detailedReason.equals("SERVER_EVENT_TIME")) {
			// process somethings
		}
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
		Inventory inv = mw.openMiniGameMenu(p);

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
- Observer can reserve tasks with event of MiniGame
## MiniGameEvent
- `START`: fired when minigame started
- `FINISH`: fired when minigame finished
- `EXCEPTION`: fired when exception created

## Examples
### 1. Reward System
- Give reward when minigame finished
- Can distinguish with `class name` or `title` of minigame
```java
class RewardManager implements MiniGameObserver {

	public RewardManager(MiniGameWorld mw) {
		mw.registerMiniGameObserver(this);
	}

	@Override
	public void update(MiniGameEvent event, MiniGameAccessor minigame) {
		if (event == MiniGameEvent.FINISH) {
			List<Entry<Player, Integer>> rankList = minigame.getScoreRank();
			if (rankList.isEmpty()) {
				return;
			}

			for (int rank = 1; rank <= 3; rank++) {
				Player p = rankList.get(rank - 1).getKey();

				// give reward: diamonds
				int diamondAmount = 4 - rank;
				ItemStack diamond = new ItemStack(Material.DIAMOND, diamondAmount);
				p.getInventory().addItem(diamond);

				// msg
				p.sendMessage("You got " + diamondAmount + " diamonds for " + rank + " rank");
			}
		}
	}
}
```
### 2. Save Rank Data
- Save rank data to config
```java
class RankDataManager implements MiniGameObserver {

	private JavaPlugin plugin;

	public RankDataManager(MiniGameWorld mw, JavaPlugin plugin) {
		mw.registerMiniGameObserver(this);
		this.plugin = plugin;
	}

	@Override
	public void update(MiniGameEvent event, MiniGameAccessor minigame) {
		if (event == MiniGameEvent.FINISH) {
			List<Entry<Player, Integer>> rankList = minigame.getScoreRank();

			// check rank is empty
			if (rankList.isEmpty()) {
				return;
			}

			// get minigame data section
			String minigameClassName = minigame.getClassName();
			ConfigurationSection section = plugin.getConfig().getConfigurationSection(minigameClassName);
			
			for (Entry<Player, Integer> e : rankList) {
				// get player's name, score
				String playerName = e.getKey().getName();
				int score = e.getValue();

				// set data
				section.set(playerName, score);
			}

			// save config
			plugin.saveConfig();
		}
	}
}
```
