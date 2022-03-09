package com.worldbiomusic.minigameworld.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MiniGameHelpCommand {
	public boolean printHelp(CommandSender sender, String[] args) {
		if (!sender.isOp()) {
			return true;
		}

		sender.sendMessage(ChatColor.BOLD + "USAGE");

		try {
			// /mg <wrong>
			if (args.length == 0) {
				this.printUsage(sender);
			} else {
				String menu = args[0];
				switch (menu) {
				case "join":
					this.printJoinUsage(sender);
					break;
				case "party":
					this.printPartyUsage(sender);
					break;
				case "settings":
					this.printSettingsUsage(sender);
					break;
				case "minigames":
					this.printMinigamesUsage(sender);
					break;
				default:
					this.printUsage(sender);
				}
			}

		} catch (Exception e) {
		}

		return true;
	}

	private void printUsage(CommandSender sender) {
		sender.sendMessage("/minigame join <title>");
		sender.sendMessage("/minigame leave");
		sender.sendMessage("/minigame list");
		sender.sendMessage("/minigame menu");
		sender.sendMessage("/minigame party");
		sender.sendMessage("/minigame reload");
		sender.sendMessage("/minigame settings");
		sender.sendMessage("/minigame minigames");
	}

	private void printJoinUsage(CommandSender sender) {
		sender.sendMessage("/minigame join <title>: join <title> minigame");
	}

	private void printPartyUsage(CommandSender sender) {
		sender.sendMessage("/minigame party invite <player>: invite <player> to your party");
		sender.sendMessage("/minigame party accept <player>: accept <player>'s invitation");
		sender.sendMessage("/minigame party ask <player>: ask to <player> if you can join");
		sender.sendMessage("/minigame party allow <player>: allow <player>'s ask");
		sender.sendMessage("/minigame party leave: leave party");
		sender.sendMessage("/minigame party kickvote <player>: vote <player> that you want to kick from your party");
		sender.sendMessage("/minigame party msg <player>: send message to player");
		sender.sendMessage("/minigame party list: show party member list");
	}

	private void printSettingsUsage(CommandSender sender) {
		sender.sendMessage(
				"/minigame settings minigame-sign <true|false>: set activation of minigame sign block join / leave");
		sender.sendMessage("/minigame settings message-prefix <value>: set plugin message prefix");
	}

	private void printMinigamesUsage(CommandSender sender) {
		sender.sendMessage("/minigame minigames <classname> title <value>: set title");
		sender.sendMessage(
				"/minigame minigames <classname> location [<x> <y> <z>]: set minigame spawn location (without [ ]: set player's location)");
		sender.sendMessage("/minigame minigames <classname> min-player-count <value>: set min player count");
		sender.sendMessage("/minigame minigames <classname> max-player-count <value>: set max player count");
		sender.sendMessage("/minigame minigames <classname> waiting-time <value>: set waiting time (sec)");
		sender.sendMessage("/minigame minigames <classname> play-time <value>: set play time (sec)");
		sender.sendMessage("/minigame minigames <classname> active <value>: set activation of minigame");
		sender.sendMessage("/minigame minigames <classname> tutorial <line> <tutorials>: set tutorials at");
		sender.sendMessage("/minigame minigames <classname> icon <value>: set icon (uppercase of item)");
	}

}
