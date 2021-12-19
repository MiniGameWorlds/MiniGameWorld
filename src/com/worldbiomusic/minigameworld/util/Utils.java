package com.worldbiomusic.minigameworld.util;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.worldbiomusic.minigameworld.MiniGameWorldMain;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameDataManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Utils {
	public static String messagePrefix = ChatColor.BOLD + "MiniGameWorld" + ChatColor.RESET;
	static MiniGameWorldMain main = MiniGameWorldMain.getInstance();
//	static Logger logger = main.getLogger();
	static ConsoleCommandSender sender = main.getServer().getConsoleSender();

	private static String getMessagePrefixString() {
		return "[" + messagePrefix + "] ";
	}

	public static void sendMsg(CommandSender sender, String msg) {
		sender.sendMessage(getMessagePrefixString() + msg);
	}

	public static void sendMsg(Player p, BaseComponent compo) {
		TextComponent msg = new TextComponent(getMessagePrefixString());
		msg.addExtra(compo);
		p.spigot().sendMessage(msg);
	}

	public static void info(String msg) {
		sender.sendMessage(getMessagePrefixString() + msg);
	}

	public static void warning(String msg) {
		sender.sendMessage(ChatColor.YELLOW + getMessagePrefixString() + msg);
	}

	public static void debug(String msg) {
		if (Setting.DEBUG_MODE) {
			info(ChatColor.RED + "[DEBUG] " + msg);
		}
	}

	public static void broadcast(String msg) {
		Bukkit.broadcastMessage(getMessagePrefixString() + msg);
	}

	public static File getMiniGamesFolder() {
		return new File(MiniGameWorldMain.getInstance().getDataFolder(), MiniGameDataManager.FOLDER_NAME);
	}

	public static boolean checkPerm(CommandSender sender, String permission) {
		return sender.hasPermission("minigameworld." + permission);
	}

	public static File getServerFile(String file) {
		return new File(main.getServer().getWorldContainer(), file);
	}
}
