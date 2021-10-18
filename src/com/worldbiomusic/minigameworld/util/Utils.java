package com.worldbiomusic.minigameworld.util;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.worldbiomusic.minigameworld.MiniGameWorldMain;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameDataManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Utils {
	public static String messagePrefix = "MiniGameWorld";
	static MiniGameWorldMain main = MiniGameWorldMain.getInstance();
	static Logger logger = main.getLogger();

	private static String getMessagePrefixString() {
		return "[" + messagePrefix + "] ";
	}

	public static void sendMsg(Player p, String msg) {
		p.sendMessage(getMessagePrefixString() + msg);
	}

	public static void sendMsg(Player p, BaseComponent compo) {
		TextComponent msg = new TextComponent(getMessagePrefixString());
		msg.addExtra(compo);
		p.spigot().sendMessage(msg);
	}

	public static void info(String msg) {
		logger.info(msg);
	}

	public static void warning(String msg) {
		logger.warning(msg);
	}

	public static void debug(String msg) {
		info(ChatColor.RED + "[DEBUG] " + msg);
	}

	public static void broadcast(String msg) {
		Bukkit.broadcastMessage(getMessagePrefixString() + msg);
	}

	public static File getMiniGamesFolder() {
		return new File(MiniGameWorldMain.getInstance().getDataFolder(), MiniGameDataManager.FOLDER_NAME);
	}

	public static boolean checkPerm(Player p, String permission) {
		return p.hasPermission("minigameworld." + permission);
	}

	public static File getServerFile(String file) {
		return new File(main.getServer().getWorldContainer(), file);
	}
}
