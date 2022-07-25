package com.minigameworld.util;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

/**
 * [IMPORTANT] When "prefix == null" and "usePrefix == true", {@link Utils}
 * prefix will be added
 */
public class Messenger {
	private String prefix;
	private boolean usePrefix;
	private String defaultMessageKey;
	private int[] titleTimes;

	public Messenger() {
		this("");
	}

	public Messenger(String defaultMessageKey) {
		this(null, false, defaultMessageKey);
	}

	public Messenger(String prefix, boolean usePrefix, String defaultMessageKey) {
		this.prefix = prefix;
		this.usePrefix = usePrefix;
		this.defaultMessageKey = defaultMessageKey;
		this.titleTimes = new int[] { 10, 40, 10 };
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public boolean isUsePrefix() {
		return usePrefix;
	}

	public void setUsePrefix(boolean usePrefix) {
		this.usePrefix = usePrefix;
	}

	public String getDefaultMessageKey() {
		return defaultMessageKey;
	}

	public void setDefaultMessageKey(String defaultMessageKey) {
		this.defaultMessageKey = defaultMessageKey;
	}

	public int[] getTitleTimes() {
		return titleTimes;
	}

	public void setTitleTimes(int fadeIn, int stay, int fadeOut) {
		this.titleTimes = new int[] { fadeIn, stay, fadeOut };
	}

	public void sendMsg(Player p, String msgKey) {
		sendMsg(p, msgKey, null);
	}

	public void sendMsg(Player p, String msgKey, String[][] placeholders) {
		p.sendMessage(getMsg(p, msgKey, placeholders));
	}

	public void sendMsg(List<Player> players, String msgKey) {
		sendMsg(players, msgKey, null);
	}

	public void sendMsg(List<Player> players, String msgKey, String[][] placeholders) {
		players.forEach(p -> sendMsg(p, msgKey, placeholders));
	}

	public void sendTitle(Player p, String msgKey) {
		sendTitle(p, msgKey, "");
	}

	public void sendTitle(Player p, String msgKey, String subMsgKey) {
		sendTitle(p, msgKey, null, subMsgKey, null);
	}

	public void sendTitle(Player p, String msgKey, String[][] placeholders) {
		sendTitle(p, msgKey, placeholders, "", null);
	}

	public void sendTitle(Player p, String msgKey, String[][] placeholders, String subMsgKey,
			String[][] subPlaceholders) {
		int fadeIn = this.titleTimes[0];
		int stay = this.titleTimes[1];
		int fadeOut = this.titleTimes[2];
		String title = getMsg(p, msgKey, placeholders);
		String subTitle = getMsg(p, subMsgKey, subPlaceholders);
		p.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
	}

	public void sendTitle(List<Player> players, String msgKey) {
		sendTitle(players, msgKey, "");
	}

	public void sendTitle(List<Player> players, String msgKey, String subMsgKey) {
		sendTitle(players, msgKey, null, subMsgKey, null);
	}

	public void sendTitle(List<Player> players, String msgKey, String[][] placeholders) {
		sendTitle(players, msgKey, placeholders, "", null);
	}

	public void sendTitle(List<Player> players, String msgKey, String[][] placeholders, String subMsgKey,
			String[][] subPlaceholders) {
		players.forEach(p -> sendTitle(p, msgKey, placeholders, subMsgKey, subPlaceholders));
	}

	public String getMsg(Player p, String msgKey) {
		return getMsg(p, msgKey, null);
	}

	public String getMsg(Player p, String msgKey, String[][] placeholders) {
		String message = "";

		// use when "prefix == null" and "usePrefix == true"
		boolean useUtilsPrefix = false;

		if (isUsePrefix()) {
			if (this.prefix == null) {
				useUtilsPrefix = true;
			} else {
				message = this.prefix + message;
			}
		}

		message += LangUtils.getMsg(p, this.defaultMessageKey + msgKey, useUtilsPrefix, placeholders);
		return message;
	}

	@Override
	public String toString() {
		return "Messenger [prefix=" + prefix + ", usePrefix=" + usePrefix + ", defaultMessageKey=" + defaultMessageKey
				+ ", titleTimes=" + Arrays.toString(titleTimes) + "]";
	}

}
