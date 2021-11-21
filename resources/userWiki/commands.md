# Commands
- aliases: `/mw`


## General
- `/minigame join <title>`: join `<title>` minigame
- `/minigame leave`: leave current playing minigame (when only waiting)
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
- `/minigame settings minigame-sign <true|false>`: set activation of minigame `sign block` join / leave
- `/minigame settings minigame-command <true|false>`: set activation of minigame command
- `/minigame settings message-prefix <value>`: set plugin message prefix

### minigames
- set minigame settings in `minigames/<minigame>.yml` config 
- `/minigame minigames <classname> title <value>`: set title
- `/minigame minigames <classname> location <<player> | <x> <y> <z>>`: set minigame spawn location (without [<x> <y> <z>]: set player's location)
- `/minigame minigames <classname> min-player-count <value>`: set min player count
- `/minigame minigames <classname> max-player-count <value>`: set max player count
- `/minigame minigames <classname> waiting-time <value>`: set waiting time (sec)
- `/minigame minigames <classname> time-limit <value>`: set playing time limit (sec)
- `/minigame minigames <classname> active <value>`: set activation of minigame
- `/minigame minigames <classname> tutorial <line> <tutorials>`: set tutorials at <line>
- `/minigame minigames <classname> icon <value>`: set icon (uppercase of item)
