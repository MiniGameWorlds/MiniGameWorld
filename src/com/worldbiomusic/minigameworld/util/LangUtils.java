package com.worldbiomusic.minigameworld.util;

import java.io.File;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.wbm.plugin.util.ServerTool;
import com.worldbiomusic.minigameworld.MiniGameWorldMain;

import me.smessie.MultiLanguage.bukkit.AdvancedMultiLanguageAPI;

public class LangUtils {
	public static void sendMsg(Player p, String messageKey) {
		sendMsg(p, messageKey, true);
	}

	public static void sendMsg(Player p, String messageKey, boolean prefix) {
		sendMsg(p, messageKey, prefix, null);
	}

	public static void sendMsg(Player p, String messageKey, String[][] replaces) {
		sendMsg(p, messageKey, true, replaces);
	}

	public static void sendMsg(Player p, String messageKey, boolean prefix, String[][] replaces) {
		String message = getMsg(p, messageKey, prefix, replaces);
		p.sendMessage(message);
	}

	public static void sendMsgToEveryone(String msg) {
		sendMsgToEveryone(msg, true);
	}

	public static void sendMsgToEveryone(String msg, boolean prefix) {
		sendMsgToEveryone(msg, prefix, null);
	}

	public static void sendMsgToEveryone(String msg, String[][] replaces) {
		sendMsgToEveryone(msg, true, replaces);
	}

	public static void sendMsgToEveryone(String msg, boolean prefix, String[][] replaces) {
		Bukkit.getOnlinePlayers().forEach(p -> sendMsg(p, msg, prefix, replaces));
	}

	public static String getMsg(Player p, String messageKey) {
		return getMsg(p, messageKey, true, null);
	}

	public static String getMsg(Player p, String messageKey, String[][] replaces) {
		return getMsg(p, messageKey, true, replaces);
	}

	public static String getMsg(Player p, String messageKey, boolean prefix) {
		return getMsg(p, messageKey, prefix, null);
	}

	public static String getMsg(Player p, String messageKey, boolean prefix, String[][] replaces) {
		String message = null;

		if (p == null) {
			return message;
		}

		// default first key
		messageKey = "message." + messageKey;

		String language = null;
		// if AdvancedMultiLanguage plugin is not exist, use English
		if (ServerTool.isPluginEnabled("AdvancedMultiLanguage")) {
			String uuid = p.getUniqueId().toString();
			String pluginName = MiniGameWorldMain.getInstance().getName();
			message = AdvancedMultiLanguageAPI.getMessage(uuid, messageKey, pluginName);
			language = AdvancedMultiLanguageAPI.getLanguageOfUuid(uuid);
		} else {
			language = "EN";
			message = getLangYaml(language).getString(messageKey);
		}

		// check prefix
		if (prefix) {
			message = Utils.getMessagePrefix() + message;
		}

		// replace placeholders
		message = replace(message, replaces);

		// replace custom placeholders
		message = replaceCustomPlaceholders(language, message);

		return ChatColor.translateAlternateColorCodes('&', message);
	}

	private static YamlConfiguration getLangYaml(String language) {
		File file = new File(MiniGameWorldMain.getInstance().getDataFolder() + File.separator + "messages"
				+ File.separator + language + ".yml");
		return YamlConfiguration.loadConfiguration(file);
	}

	public static String replaceCustomPlaceholders(String language, String message) {
		YamlConfiguration yaml = getLangYaml(language);
		ConfigurationSection customSection = yaml.getConfigurationSection("message.custom");
		Set<String> keys = customSection.getKeys(true);
		for (String key : keys) {
			String value = customSection.getString(key);
			message = replace(message, key, value);
		}

		return message;
	}

	public static String replace(String message, String placeholder, String value) {
		if (message == null) {
			return message;
		}
		return message.replace("<" + placeholder + ">", value);
	}

	public static String replace(String message, String[][] replaces) {
		if (replaces == null) {
			return message;
		}

		for (int i = 0; i < replaces.length; i++) {
			String[] change = replaces[i];
			if (change.length < 2) {
				continue;
			}
			String placeholder = change[0];
			String value = change[1];
			message = replace(message, placeholder, value);
		}
		return message;
	}

	public static String path(Class<?> c) {
		return c.getSimpleName() + ".";
	}

	public static String path(Object o) {
		return path(o.getClass());
	}
}
