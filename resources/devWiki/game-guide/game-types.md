# Game types
There are 5 pre-made game types: `SoloMiniGame`, `SoloBattleMiniGame`, `TeamMiniGame`, `TeamBattleMiniGame`, `Fake`. Extending these classes, you can use util features and can save your time.


# `SoloMiniGame`
Only 1 player can play the game. Game will be finished if player left the game or died by `setLive(p, false)` method.
## Utils
- `getSoloPlayer()`: Get joined player
- `getScore()`: get solo player's score
- `plusScore(int)`: plus `int` score
- `minusScore(int)`: minus `int` score

---

# `SoloBattleMiniGame`
Each player battles each other like **FFA**. By default, Game will be finished when there are less than `2` players.
## Utils
Nothing.

---

# `TeamMiniGame`
All players are the same team and also have a same score. Game will be finished when all players left or died by `setLive(p, false)` method.
## Utils
- `getTeamScore()`: Get team score
- `plusTeamScore(int)`: Plus `int` score to the team
- `minusTeamScore(int)`: Minus `int` score to the team

---

# `TeamBattleMiniGame`
Each team battles each other. By default, game will be finished when there are less than `2` teams.

## Utils
- `getTeams()`: Get team list
- `getLiveTeams()`: Get live team list
- `getRandomTeam()`: Get random team
- `getTeam(int or String or Player)`: Get team with `team index` or `team name` or a `player`
- `getTeamScore(int or String or Player)`: Get team score with `team index` or `team name` or a `player`
- `plusTeamScore(int or String or Player, int)`: Plus `int` team score with `team index` or `team name` or a `player`
- `minusTeamScore(int or String or Player, int)`: Minus `int` team score with `team index` or `team name` or a `player`
- `Team#getMembers()`: Get team members
- `Team#getLiveMembers()`: Get live team members
- `Team#getName()`: Get team name
- `Team#getScore()`: Get team score
- `Team#plusTeamScore(int)`: Plus `int` team score
- `Team#minusTeamScore(int)`: Minus `int` team score

## Team register mode
Players are distributed to each teams by several modes. Also you can set mode with `setTeamRegisterMode(mode)`.

E.g. playerCount: 13, teamMaxPlayerCount: 5, teamCount: 4
- `NONE`: no distribution (override `TeamBattleMiniGame.registerPlayersToTeam()` and register new teams)
- `FAIR`: all teams have the same player count fairly (= maximun team count) (4, 3, 3, 3)
- `FILL`: fulfill teams as possible from first (= minimum team count) (5, 5, 3, 0)
- `FAIR_FILL`: fill fairly (5, 4, 4, 0) (**default**)
- `RANDOM`: random (?, ?, ?, ?)
- `PARTY`: party members have the same team (only "`max-players` / `team-size`" party can join the game)


## Custom options
Below options will be added to `custom-data` section of the minigame config by default. That means users also can edit these settings.
- `group-chat`: If true, send message only to team members. (default: `true`)
- `team-pvp`: If true, team members can not attack each other. (pvp option have to be set to true) (default: `false`)
- `team-size`: Related with MiniGameSetting.maxPlayers(), if "max-players" is `12` and "team-size" is `4` then `3` teams will be created. (default: `2`)



---

# `Fake`
Players can NOT join and play this game. Game can detect and handle player's join try only. Generally, used for a special purpose like teleporting to somewhere or sending messages when a player tries to join.

## Utils
- `onFakeJoin(Player)`: Called when a player tries to join the fake minigame but players can not join. (join event will be canelled automatically)

---

# Custom
If you want, you can create your custom minigame frame class extending `MiniGame` class. Override `frameType()`, `printScores()`, `rank()` and `onException(exception)` if you need and several util methods.