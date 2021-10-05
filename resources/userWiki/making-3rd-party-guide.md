# Description
- Third party plugin can access/change below things
```yaml
- Minigame data
- Minigame Join/Leave 
- Minigame Exception
- GUI
- Party 
- Observer System
```

---

# Minigame Data

---

# Minigame Join/Leave
- Can change `Join/Leave` way 
- Example
```java
// join minigame with portal
@EventHandler
public void onPlayerEnterPortal(EntityPortalEnterEvent e) {
  if (e.getEntity() instanceof Player) {
    Player p = (Player) e.getEntity();
    MiniGameWorld mw = MiniGameWorld.create();
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
      MiniGameWorld mw = MiniGameWorld.create();
      // leave minigame
      mw.leaveGame(p);
    }
  }
}
```
---

# Minigame Exception

---

# Menu

---

# Party

---

# Observer System
- Observer can reserve tasks with event of MiniGame
## MiniGameEvent
- `START`: fired when minigame started
- `FINISH`: fired when minigame finished
- `EXCEPTION`: fired when exception created

## Examples
### Reward System
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


