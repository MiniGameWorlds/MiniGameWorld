package com.worldbiomusic.minigameworld.util;

import java.io.File;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.wbm.plugin.util.CollectionTool;
import com.worldbiomusic.minigameworld.MiniGameWorldMain;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Utils {
	static MiniGameWorldMain main = MiniGameWorldMain.getInstance();
	static ConsoleCommandSender sender = main.getServer().getConsoleSender();

	private static String getMessagePrefixString() {
		return "[" + Setting.MESSAGE_PREFIX + "] ";
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

	public static boolean checkPerm(CommandSender sender, String permission) {
		return sender.hasPermission("minigameworld." + permission);
	}

	public static File getServerFile(String file) {
		return new File(main.getServer().getWorldContainer(), file);
	}

	public static void syncMapKeys(Map<String, Object> configMap, Map<String, Object> pureMap) {
		// remove not necessary keys to avoid error (i.e. keys for some updates)
		if (Setting.REMOVE_NOT_NECESSARY_KEYS) {
			CollectionTool.removeNotNecessaryKeys(configMap, pureMap);
		}

		// Restore before apply to avoid error (i.e. keys for some updates)
		CollectionTool.restoreMissedKeys(configMap, pureMap);

		// sync map keys
		CollectionTool.syncKeyOrder(configMap, pureMap);
	}
}

//
//
//
//
//
//
//
//
//
//
//
//
