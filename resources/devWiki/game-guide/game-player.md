# Game player
There is `MiniGamePlayer` class that has a player's `score`, `live` and `states`. You get `GamePlayer` by `getGamePlayer(Player)` method.


## Score
Player's score is set to 0 by default and you can plus or minus using `plusScore(Player, int)` and `minusScore(Player, int)` method of MiniGame class. And the rankings are sorted in descending order of the players' scores at the game finish.


## Live
`Live` is used in minigame only, not means real player's dead. Player's gamemode will be changed to `dead-gamemode` of [custom option](custom-options.md).

And players' live count of game is related with finish condition. See [settings](settings.md#setting-details)


## States
When a player joined a game, all states like game mode and inventory will be saved at the game start and restored at the game finish. You can find the managed state list searching `MiniGamePlayerState` in the [API](https://minigameworlds.github.io/MiniGameWorld/)



## ETC
- `containsPlayer(Player)`: check the player is playing this minigame
- `getViewManager().isViewing(Player)`: check the player is viewing this minigame
- `getPlayers()`: get all players
- `randomPlayer()`: get random player between the playing players
- `topPlayer()`: get top score player