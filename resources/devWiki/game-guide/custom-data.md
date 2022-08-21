# Custom data
Custom data is data that user can change game play with various options. But developer has to register initial custom data first. These data will be saved in `custom-data` section of game config in `plugin/MiniGameWorld/minigames` folder.

※ Avoid using already exist [custom options keys](../../userWiki/config.md).

# How to use
## 1. Initialize custom data
First, add custom data as a class member data. And override the `initCustomData()` and put the custom data.
```java
public class GameA extends SoloBattleMiniGame {
	int data;
	Location loc;
	List<String> list;

	public GameA() {
		super("GameA", 2, 5, 30, 5);
	}

	@Override
	protected void initCustomData() {
		super.initCustomData();

		Map<String, Object> customData = customData();
		customData.put("data", 1);
		customData.put("location", new Location(Bukkit.getWorld("world"), 0, 4, 0));
		customData.put("list", List.of("a", "b", "c"));
	}
}
```

## 2. Load custom data
After initialization, override `loadCustomData()` and assign data to the field variables.
```java
public class GameA extends SoloBattleMiniGame {
	// ...

	@Override
	public void loadCustomData() {
		super.loadCustomData();

		Map<String, Object> customData = customData();
		this.data = (int) customData.get("data");
		this.loc = (Location) customData.get("location");
		this.list = (List<String>) customData.get("list");
	}

	@Override
	protected void onStart() {
		super.onStart();
        // check custom data
		sendMessages("data: " + this.data + ", loc: " + this.loc + ", list: " + this.list);
	}
}
```




