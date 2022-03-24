# Commands
- aliases: `/mw`


## General
- `/minigame join <title>`: join `<title>` minigame
- `/minigame view <title>`: view `<title>` minigame
- `/minigame leave`: leave current playing/viewing minigame
- `/minigame list`: print minigame list
- `/minigame menu`: open menu
- `/minigame reload`: reload all configs

## Party
- `/minigame party invite <player>`: invite `<player>` to your party
- `/minigame party accept <player>`: accept `<player>`'s invitation
- `/minigame party ask <player>`: ask to `<player>` if you can join
- `/minigame party allow <player>`: allow `<player>`'s ask
- `/minigame party leave`: leave party
- `/minigame party kickvote <player>`: vote `<player>` that you want to kick from your party
- `/minigame party msg <player>`: send message to player
- `/minigame party list`: show party member list


## Config
### settings
- set plugin settings in `settings.yml` config
- `/minigame settings message-prefix <value1> [<value2> [<value3> [...]]]`: set plugin message prefix
- `/minigame settings backup-data-save-delay <value>`: set backup data save delay (min)
- `/minigame settings debug-mode <value>`: if true, console will print debug logs (true / false)
- `/minigame settings isolated-chat <value>`: Playing minigame players can only chat with each other (true / false)
- `/minigame settings isolated-join-quit-message <value>`: Minigame join/quit message only notify in minigame (true / false)
- `/minigame settings join-sign-caption <value1> [<value2> [<value3> [...]]]`: Caption of join sign block
- `/minigame settings leave-sign-caption <value1> [<value2> [<value3> [...]]]`: Caption of leave sign block
- `/minigame settings scoreboard <value>`: If true, use scoreboard system (true / false)
- `/minigame settings scoreboard-update-delay <value>`: Scoreboard update delay (tick (`20`tick = `1`second))
- `/minigame settings remove-not-necessary-keys <value>`: Set remove-not-necessary-keys (true / false)
- `/minigame settings min-leave-time <value>`: Set min-leave-time (sec)
- `/minigame settings start-sound <value>`: Set start-sound (Sound)
- `/minigame settings finish-sound <value>`: Set finish-sound (Sound)
- `/minigame settings check-update <value>`: If true, check update when a plugin is loaded (true / false)
- `/minigame settings edit-messages <value>`: If true, language message changes will be applied(saved) (true / false)

### minigames
- set minigame settings in `minigames/<minigame>.yml` config 
- `/minigame minigames <classname> title <value1> [<value2> [<value3> [...]]]`: set title (title)
- `/minigame minigames <classname> location <<player> | <x> <y> <z>>`: set minigame spawn location (without [<x> <y> <z>]: set player's location)
- `/minigame minigames <classname> min-player-count <value>`: set min player count (number)
- `/minigame minigames <classname> max-player-count <value>`: set max player count (number)
- `/minigame minigames <classname> waiting-time <value>`: set waiting time (sec)
- `/minigame minigames <classname> play-time <value>`: set play time (sec)
- `/minigame minigames <classname> active <value>`: set activation of minigame (true / false)
- `/minigame minigames <classname> tutorial <line> <value1> [<value2> [<value3> [...]]]`: set tutorials at <line>
- `/minigame minigames <classname> icon <value>`: set icon (uppercase of item)
- `/minigame minigames <classname> view <value>`: set view allow (true / false)
- `/minigame minigames <classname> scoreboard <value>`: use scoreboard system in minigame (true / false)