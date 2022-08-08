# Game instance system
This system allows minigames to have multi instances. And you can also set the maximum instance count of each minigames.



## How to set maximum instance count
To set maximum instance count, go into your `MiniGameWorld/minigames` and open up the game config you want then you can find `instances` key. (default is `1`)
- `-1`: infinite
- `0`: no instance will be created
- `1 ~ ∞`: maximum instance count



## Timing of instance creation
Instance creation is always invoked when a player try to join.
- If there is no instance to play.
- If player's party is too big to join the exist instance.



## Game will not be created
If reached maximum instance count or player's party is larger than the maximum player of minigame.



## Join priority
You can set the game instance joining priority of players for your server politics in `join-priority` of `MiniGameWorld/settings.yml` config.
```yaml
join-priority: MAX_PLAYERS
```

### 1. Max players (default)
The player will join the instance which has the most players among the game instances at first.
- value: `MAX_PLAYERS`

### 2. Min players
The player will join the instance which has the least players among the game instances at first.
- value: `MIN_PLAYERS`

### 3. Random
The player will join the random instance among the game instances.
- value: `RANDOM`


