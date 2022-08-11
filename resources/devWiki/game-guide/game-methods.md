# Game methods
There are methods that must be overriden in the frame minigame class.


## `Constructor`
**Construtor shoud have `public` modifier and pass the basic setting values through the `super()`.** Because Each frame class has different constructor arguments, check out the [API](https://minigameworlds.github.io/MiniGameWorld/).
```java
public class GameA extends SoloMiniGame {
	public GameA() {
        // super(title, playTime, waitingTime);
		super("GameA", 30, 10);
	}
}

```
```java
public class GameB extends SoloBattleMiniGame {
	public GameB() {
        // super(title, minPlayers, maxPlayers, playTime, waitingTime);
		super("GameB", 2, 5, 30, 5);
	}
}
```

---

## `initGame()`
Called when a minigame instance is created and before the first player joins.
```java
public class Spleef extends SoloBattleMiniGame {
	public Spleef() {
		super("Spleef", 2, 10, 300, 20);
	}

    // setup floor before the game starts
	@Override
	protected void initGame() {
		craeteFloor();
	}

    private void craeteFloor() {
        // create snow floor
    }
}

```

---

## `tutorial()`
![tutorial](https://user-images.githubusercontent.com/61288262/183815110-6f441a15-d650-4b19-a56c-c4d47931b295.PNG)

You can add your minigame tutorial that will be shown up to playing players with play time.
```java
@Override
protected List<String> tutorial() {
    List<String> tutorial = new ArrayList<>();
    tutorial.add("Break block: " + ChatColor.GREEN + "+1");
    tutorial.add("Fallen: " + ChatColor.RED + "die");
    return tutorial;
}
```

Or you can simply use `List.of()`.
```java
@Override
protected List<String> tutorial() {
    return List.of("Break block: " + ChatColor.GREEN + "+1", "Fallen: " + ChatColor.RED + "die");
}
```

And you can use a trick to use placeholder in the tutorial like below.
```java
class Game extends SoloMiniGame {
    int a;
    // ...
    @Override
    protected List<String> tutorial() {
        return List.of("a: {a}".replace("{a}", this.a));
    }
}
```
If you share your minigame, you have to notify your users how to use placeholder.



