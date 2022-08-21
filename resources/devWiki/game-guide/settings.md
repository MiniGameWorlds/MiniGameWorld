# Setting
Fundamental settings of minigame like title, minimum players, locations and etc. Almost settings can be edit in game config by users but special settings are only controlled by the developers.
- Search `MiniGameSetting` in [API] doc to check default value
- Details: [Settings](../../userWiki/config.md)  

# How to use
Game settings must be setup in the constructor with `setting()`. But primary settings are passed by `super(...)`.
```java
class GameA extends SoloBattleMiniGame {
    public PassMob() {
        super("PassMob", 2, 60 * 3, 10);

        // settings
        setting().setIcon(Material.OAK_FENCE);
        setting().set
    }
}
```

# Setting details
## `waiting-time` and `play-time`
If `waiting-time`/`play-time` is `-1`, timer is not counted so that game should have start/finish condition to be processed.

## `location` and `locations`
`location` is selected and current location of the game. And `locations` is location list that is setup in config.  
When a game instance is created, `location` will be selected between `locations` by random.

## `settingFixed`
If `settingFixed` is true, `minPlayers`, `maxPlayers`, `playTime` and `customData` can not be edit by users.

## `gameFinishCondition` and `gameFinishConditionPlayerCount`
There are 4 game finish conditions.
- `NONE`: Check nothing. (You have to set game finish condition)
- `LESS_THAN_PLAYERS_LIVE`: finish when `live players count` < `gameFinishConditionPlayerCount` (default)
- `MORE_THAN_PLAYERS_LIVE`: finish when `live players count` > `gameFinishConditionPlayerCount`
- `LESS_THAN_PLAYERS_LEFT`: finish when `live or daed players count` < `gameFinishConditionPlayerCount`








[API]: https://minigameworlds.github.io/MiniGameWorld/