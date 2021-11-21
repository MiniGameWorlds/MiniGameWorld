package com.worldbiomusic.minigameworld.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MiniGameHelpCommand {
	public boolean printHelp(Player p, String[] args) {
		if (!p.isOp()) {
			return true;
		}

		p.sendMessage(ChatColor.BOLD + "USAGE");

		try {
			// /mg <wrong>
			if (args.length == 0) {
				this.printUsage(p);
			} else {
				String menu = args[0];
				switch (menu) {
				case "join":
					this.printJoinUsage(p);
					break;
				case "party":
					this.printPartyUsage(p);
					break;
				case "settings":
					this.printSettingsUsage(p);
					break;
				case "minigames":
					this.printMinigamesUsage(p);
					break;
				default:
					this.printUsage(p);
				}
			}

		} catch (Exception e) {
		}

		return true;
	}

	private void printUsage(Player p) {
		p.sendMessage("/minigame join <title>");
		p.sendMessage("/minigame leave");
		p.sendMessage("/minigame list");
		p.sendMessage("/minigame menu");
		p.sendMessage("/minigame party");
		p.sendMessage("/minigame reload");
		p.sendMessage("/minigame settings");
		p.sendMessage("/minigame minigames");
	}

	private void printJoinUsage(Player p) {
		p.sendMessage("/minigame join <title>: join <title> minigame");
	}

	private void printPartyUsage(Player p) {
		p.sendMessage("/minigame party invite <player>: invite <player> to your party");
		p.sendMessage("/minigame party accept <player>: accept <player>'s invitation");
		p.sendMessage("/minigame party ask <player>: ask to <player> if you can join");
		p.sendMessage("/minigame party allow <player>: allow <player>'s ask");
		p.sendMessage("/minigame party leave: leave party");
		p.sendMessage("/minigame party kickvote <player>: vote <player> that you want to kick from your party");
		p.sendMessage("/minigame party msg <player>: send message to player");
		p.sendMessage("/minigame party list: show party member list");
	}

	private void printSettingsUsage(Player p) {
		p.sendMessage(
				"/minigame settings minigame-sign <true|false>: set activation of minigame sign block join / leave");
		p.sendMessage("/minigame settings message-prefix <value>: set plugin message prefix");
	}

	private void printMinigamesUsage(Player p) {
		p.sendMessage("/minigame minigames <classname> title <value>: set title");
		p.sendMessage(
				"/minigame minigames <classname> location [<x> <y> <z>]: set minigame spawn location (without [ ]: set player's location)");
		p.sendMessage("/minigame minigames <classname> min-player-count <value>: set min player count");
		p.sendMessage("/minigame minigames <classname> max-player-count <value>: set max player count");
		p.sendMessage("/minigame minigames <classname> waiting-time <value>: set waiting time (sec)");
		p.sendMessage("/minigame minigames <classname> time-limit <value>: set playing time limit (sec)");
		p.sendMessage("/minigame minigames <classname> active <value>: set activation of minigame");
		p.sendMessage("/minigame minigames <classname> tutorial <line> <tutorials>: set tutorials at");
		p.sendMessage("/minigame minigames <classname> icon <value>: set icon (uppercase of item)");
	}

}
