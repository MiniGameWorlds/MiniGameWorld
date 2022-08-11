# Custom options
All minigames generally have custom options to support various game plays by users. But developer also can setup the initial values in the constructor. And they are saved in the `custom-data` section of the minigame config.

- Search `MiniGameCustomOption.Option` in [API] doc to check default value
- Details: [Custom options](../../userWiki/config.md)  



# How to use
Custom option must be initialized in the constructor by `getCustomOption().set()`.
```java
// constructor
public GameA() {
    super("GameA", 2, 60 * 3, 10);

    // set players can not chat
    getCustomOption().set(Option.CHAT, false);

    // set players can break the blocks
    getCustomOption().set(Option.BLOCK_BREAK, true);

    // set game color
    getCustomOption().set(Option.COLOR, ChatColor.BLUE);

    // set pvp off
    getCustomOption().set(Option.PVP, false);
}
```

If you want custom data to be fixed, you can setup `getSetting().setSettingFixed(true)` in th constructor.



[API]: https://minigameworlds.github.io/MiniGameWorld/