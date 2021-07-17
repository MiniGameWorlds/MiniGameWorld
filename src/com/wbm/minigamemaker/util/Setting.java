package com.wbm.minigamemaker.util;

import java.util.logging.Logger;

import org.bukkit.entity.Player;

import com.wbm.minigamemaker.Main;

public class Setting {
	static Main main = Main.getInstance();
	static Logger logger = main.getLogger();

	public static void sendMsg(Player p, String msg) {
		p.sendMessage("[MiniGameMaker] " + msg);
	}

	public static void log(String msg) {
		logger.info(msg);
	}
	
	public static void warning(String msg) {
		logger.warning(msg);
	}
}
