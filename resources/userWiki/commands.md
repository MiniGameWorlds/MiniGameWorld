# Commands
- aliases: `/mw` (`/minigame` = `/mw`)
- `<>`: data (required)
- `[]`: optional

# General
- `/minigame`: get `Menu Opener`
- `/minigame help`: print usage
- `/minigame join <title>`: join `<title>` minigame
- `/minigame view <title>`: view `<title>` minigame
- `/minigame leave`: leave current playing/viewing minigame
- `/minigame list`: print minigame list
- `/minigame menu`: open menu
- `/minigame reload [<backup-folder>]`: reload all data
- `/minigame backup [<backup-folder>]`: backup all data


# Party
- `/minigame party invite <player>`: invite `<player>` to your party
- `/minigame party accept <player>`: accept `<player>`'s invitation
- `/minigame party ask <player>`: ask to `<player>` if you can join
- `/minigame party allow <player>`: allow `<player>`'s ask
- `/minigame party leave`: leave party
- `/minigame party kickvote <player>`: vote `<player>` that you want to kick from your party
- `/minigame party msg`: send message to party members
- `/minigame party list`: show party member list


# Config
- **Without `<value>`, print current value**
## settings
- set plugin settings in `settings.yml` config
- `/minigame settings message-prefix [<value>]`: set plugin message prefix (can contain spaces)
- `/minigame settings backup-delay [<value>]`: set backup data save delay (min)
- `/minigame settings debug-mode [<value>]`: if true, console will print debug logs (true / false)
- `/minigame settings isolated-chat [<value>]`: Playing minigame players can only chat with each other (true / false)
- `/minigame settings isolated-join-quit-message [<value>]`: Minigame join/quit message only notify in minigame (true / false)
- `/minigame settings join-sign-caption [<value>]`: Caption of join sign block (can contain spaces)
- `/minigame settings leave-sign-caption [<value>]`: Caption of leave sign block (can contain spaces)
- `/minigame settings scoreboard [<value>]`: If true, use scoreboard system (true / false)
- `/minigame settings scoreboard-update-delay [<value>]`: Scoreboard update delay per tick (`20`tick = `1`second)
- `/minigame settings remove-not-necessary-keys [<value>]`: Set remove-not-necessary-keys (true / false)
- `/minigame settings min-leave-time [<value>]`: Set min-leave-time (sec)
- `/minigame settings start-sound [<value>]`: Set start-sound ([Sound])
```yaml
/minigame settings start-sound ENTITY_SLIME_ATTACK
```
- `/minigame settings finish-sound [<value>]`: Set finish-sound ([Sound])
- `/minigame settings check-update [<value>]`: If true, check update when a plugin is loaded (true / false)
- `/minigame settings edit-messages [<value>]`: If true, language message changes will be applied(saved) (true / false)
- `/minigame settings ingame-leave [<value>]`: If true, players can leave while playing (true / false)
- `/minigame settings template-worlds [<value>]`: Set world list which will be used for instance world system
- `/minigame settings join-priority [<value>]`: Set minigame join priority 
- `/minigame settings party-invite-timeout [<value>]`: Set party invite timeout (sec)
- `/minigame settings party-ask-timeout [<value>]`: Set party ask timeout (sec)

## minigames
- set minigame settings in `minigames/<minigame>.yml` config 
- `/minigame games <classname> title [<value>]`: set title (can contain spaces)
- `/minigame games <classname> min-players [<value>]`: set min player count (number)
- `/minigame games <classname> max-players [<value>]`: set max player count (number)
- `/minigame games <classname> waiting-time [<value>]`: set waiting time (sec)
- `/minigame games <classname> play-time [<value>]`: set play time (sec)
- `/minigame games <classname> finish-delay [<value>]`: set finish delay (sec)
- `/minigame games <classname> active [<value>]`: set activation of minigame (true / false)
- `/minigame games <classname> icon [<value>]`: set icon ([Item]])
```yaml
/minigame games GameA icon OAK_PLANKS
```
- `/minigame games <classname> view [<value>]`: set view allow (true / false)
- `/minigame games <classname> scoreboard [<value>]`: use scoreboard system in minigame (true / false)
- `/minigame games <classname> instances [<value>]`: Set max number of game instances (`-1` <= `instances`) (`-1` is for infinite)
- `/minigame games <classname> instance-world [<value>]`: If true, copied `locations` worlds will be used for game play and be deleted automatically (**Place worlds folder in the bukkit server folder and list them in `template-worlds` in `settings.yml`**). If false, random location in `locations` will be used (true/false)
- `/minigame games <classname> locations <[+] <<player> | <x> <y> <z>> | - <index>>`: set or add or remove minigame spawn location (`+`: add, `-`: remove)
```yaml
/minigame games GameA locations wbm: Reset and add wbm's location
/minigame games GameA locations + wbm: add wbm's location
/minigame games GameA locations + 100 50 100: add x(100), y(50), z(100) location as a GameA location
/minigame games GameA locations - 2: remove second location (1 ~)
```
- `/minigame games <classname> tutorial <line> [<value>]`: set tutorials at line (set `[<value>] with`-` to remove line) (can contain spaces) (line: 1 ~ )
```yaml
/minigame games GameA tutorial 1 This is tutorial: Add "This is tutorial" to tutorial first line  
/minigame games GameA tutorial 3 -: Remove tutorial third line
```
- `/minigame games <classname> custom-data`: only print is available




[Sound]: https://www.digminecraft.com/lists/sound_list_pc.php
[Item]: https://minecraft.fandom.com/wiki/Materials