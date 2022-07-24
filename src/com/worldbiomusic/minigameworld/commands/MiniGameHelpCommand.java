package com.worldbiomusic.minigameworld.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.worldbiomusic.minigameworld.util.Setting;

public class MiniGameHelpCommand {
	public boolean printHelp(CommandSender sender, String[] args) {
		sender.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "[USAGE]");

		try {
			if (args.length == 0) {
				this.printUsage(sender);
			} else {
				String menu = args[0];
				switch (menu) {
				case "join":
					this.printJoinUsage(sender);
					break;
				case "view":
					this.printViewUsage(sender);
					break;
				case "reload":
					this.printReloadUsage(sender);
					break;
				case "backup":
					this.printBackupUsage(sender);
					break;
				case "party":
					this.printPartyUsage(sender);
					break;
				case "settings":
					this.printSettingsUsage(sender);
					break;
				case "games":
					this.printGamesUsage(sender);
					break;
				default:
					this.printUsage(sender);
				}
			}

			// print wiki url
			printWikiUrl(sender);
		} catch (Exception e) {
		}

		return true;
	}

	public void printWikiUrl(CommandSender sender) {
		String url = "" + ChatColor.GREEN + ChatColor.UNDERLINE + Setting.URL_WIKI_COMMAND + ChatColor.RESET;
		sender.sendMessage("" + ChatColor.AQUA + ChatColor.BOLD + "[Wiki] " + ChatColor.RESET + url);
	}

	public void printUsage(CommandSender sender) {
		sender.sendMessage("/minigame join <title>");
		sender.sendMessage("/minigame leave");
		sender.sendMessage("/minigame list");
		sender.sendMessage("/minigame menu");
		sender.sendMessage("/minigame party");
		sender.sendMessage("/minigame reload [<backup-folder>]");
		sender.sendMessage("/minigame backup [<backup-folder>]");
		sender.sendMessage("/minigame settings");
		sender.sendMessage("/minigame games");
	}

	public void printJoinUsage(CommandSender sender) {
		sender.sendMessage("/minigame join <title>: join <title> minigame");
	}

	public void printViewUsage(CommandSender sender) {
		sender.sendMessage("/minigame view <title>: view <title> minigame");
	}

	public void printReloadUsage(CommandSender sender) {
		sender.sendMessage("/minigame reload [<backup-folder>]: reload all data");
	}

	public void printBackupUsage(CommandSender sender) {
		sender.sendMessage("/minigame backup [<backup-folder>]: backup all data");
	}

	public void printPartyUsage(CommandSender sender) {
		sender.sendMessage("/minigame party invite <player>: invite <player> to your party");
		sender.sendMessage("/minigame party accept <player>: accept <player>'s invitation");
		sender.sendMessage("/minigame party ask <player>: ask to <player> if you can join");
		sender.sendMessage("/minigame party allow <player>: allow <player>'s ask");
		sender.sendMessage("/minigame party leave: leave party");
		sender.sendMessage("/minigame party kickvote <player>: vote <player> that you want to kick from your party");
		sender.sendMessage("/minigame party msg <player>: send message to player");
		sender.sendMessage("/minigame party list: show party member list");
	}

	public void printSettingsUsage(CommandSender sender) {
		sender.sendMessage(
				"/minigame settings message-prefix [<value>]: set plugin message prefix (can contain spaces)");
		sender.sendMessage("/minigame settings backup-delay [<value>]: set backup data save delay (min)");
		sender.sendMessage(
				"/minigame settings debug-mode [<value>]: if true, console will print debug logs (true / false)");
		sender.sendMessage(
				"/minigame settings isolated-chat [<value>]: Playing minigame players can only chat with each other (true / false)");
		sender.sendMessage(
				"/minigame settings isolated-join-quit-message [<value>]: Minigame join/quit message only notify in minigame (true / false)");
		sender.sendMessage(
				"/minigame settings join-sign-caption [<value>]: Caption of join sign block (can contain spaces)");
		sender.sendMessage(
				"/minigame settings leave-sign-caption [<value>]: Caption of leave sign block (can contain spaces)");
		sender.sendMessage("/minigame settings scoreboard [<value>]: If true, use scoreboard system (true / false)");
		sender.sendMessage(
				"/minigame settings scoreboard-update-delay [<value>]: Scoreboard update delay per tick (20tick = 1second)");
		sender.sendMessage(
				"/minigame settings remove-not-necessary-keys [<value>]: Set remove-not-necessary-keys (true / false)");
		sender.sendMessage("/minigame settings min-leave-time [<value>]: Set min-leave-time (sec)");
		sender.sendMessage("/minigame settings start-sound [<value>]: Set start-sound (Sound)");
		sender.sendMessage("/minigame settings finish-sound [<value>]: Set finish-sound (Sound)");
		sender.sendMessage(
				"/minigame settings check-update [<value>]: If true, check update when a plugin is loaded (true / false)");
		sender.sendMessage(
				"/minigame settings edit-messages [<value>]: If true, language message changes will be applied(saved) (true / false)");
		sender.sendMessage(
				"/minigame settings ingame-leave [<value>]: If true, players can leave while playing (true / false)");
		sender.sendMessage(
				"/minigame settings template-worlds [<value>]: Set world list which will be used for instance world system");
		sender.sendMessage("/minigame settings join-priority [<value>]: Set minigame join priority ");
		sender.sendMessage("/minigame settings party-invite-timeout [<value>]: Set party invite timeout (sec)");
		sender.sendMessage("/minigame settings party-ask-timeout [<value>]: Set party ask timeout (sec)");
	}

	public void printGamesUsage(CommandSender sender) {
		sender.sendMessage("/minigame games <classname> title [<value>]: set title (can contain spaces)");
		sender.sendMessage("/minigame games <classname> min-players [<value>]: set min player count (number)");
		sender.sendMessage("/minigame games <classname> max-players [<value>]: set max player count (number)");
		sender.sendMessage("/minigame games <classname> waiting-time [<value>]: set waiting time (sec)");
		sender.sendMessage("/minigame games <classname> play-time [<value>]: set play time (sec)");
		sender.sendMessage("/minigame games <classname> active [<value>]: set activation of minigame (true / false)");
		sender.sendMessage("/minigame games <classname> icon [<value>]: set icon (Item])");
		sender.sendMessage("/minigame games <classname> view [<value>]: set view allow (true / false)");
		sender.sendMessage(
				"/minigame games <classname> scoreboard [<value>]: use scoreboard system in minigame (true / false)");
		sender.sendMessage(
				"/minigame games <classname> instances [<value>]: Set max number of game instances (`-1` <= `instances`) (`-1` is for infinite)");
		sender.sendMessage(
				"`/minigame games <classname> instance-world [<value>]: If true, copied `locations` worlds will be used for game play and be deleted automatically (**Place worlds folder in the bukkit server folder and list them in `template-worlds` in `settings.yml`**). If false, random location in `locations` will be used (true/false)");
		sender.sendMessage(
				"/minigame games <classname> locations <+ <<player> | <x> <y> <z>> | - <index>>: add or remove minigame spawn location (`+`: add, `-`: remove)");
		sender.sendMessage(
				"/minigame games <classname> tutorial <line> [<value>]: set tutorials at line (set [<value>] with -` to remove line) (can contain spaces) (line: 1 ~ )");
		sender.sendMessage("/minigame games <classname> custom-data: only print is available");

	}

}
