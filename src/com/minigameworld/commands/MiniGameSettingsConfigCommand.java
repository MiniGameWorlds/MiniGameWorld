package com.minigameworld.commands;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.minigameworld.managers.MiniGameManager;
import com.minigameworld.util.Setting;
import com.minigameworld.util.Utils;

public class MiniGameSettingsConfigCommand {
	private MiniGameManager minigameManager;
	// setting.yml
	private Map<String, Object> settings;

	public MiniGameSettingsConfigCommand(MiniGameManager minigameManager) {
		this.minigameManager = minigameManager;
		this.settings = this.minigameManager.getSettings();
	}

	public boolean settings(Player p, String[] args) throws Exception {
		if (!p.isOp()) {
			return true;
		}

		// /mg settings <key> <value>
		String key = args[1];
		switch (key) {
		case Setting.SETTINGS_LOBBY:
			this.lobby(p, args);
			break;
		case Setting.SETTINGS_MESSAGE_PREFIX:
			this.message_prefix(p, args);
			break;
		case Setting.SETTINGS_MINIGAME_SIGN:
			this.minigame_sign(p, args);
			break;
		case Setting.SETTINGS_MINIGAME_COMMAND:
			this.minigame_command(p, args);
			break;
		}

		// save config
		this.minigameManager.getYamlManager().save(this.minigameManager);

		// reload config
		this.minigameManager.reload();

		// refer settings again (reload make new config and retarget settings)
		this.settings = this.minigameManager.getSettings();

		return true;
	}

	private void setKeyValue(Player p, String key, Object value) {
		if (this.settings.containsKey(key)) {
			this.settings.put(key, value);
			Utils.sendMsg(p, key + " set to " + value);
		} else {
			Utils.sendMsg(p, "settings.yml doesn't have " + key + " key");
		}
	}

	private boolean lobby(Player p, String[] args) throws Exception {
		// /mg settings lobby [<x> <y> <z>]
		if (args.length == 2) {
			Location playerLoc = p.getLocation();
			this.settings.put(Setting.SETTINGS_LOBBY, playerLoc);

			// msg
			Utils.sendMsg(p, Setting.SETTINGS_LOBBY + " set to your location");
		} else if (args.length == 5) {
			double x = Integer.parseInt(args[2]);
			double y = Integer.parseInt(args[3]);
			double z = Integer.parseInt(args[4]);
			World w = p.getLocation().getWorld();
			Location loc = new Location(w, x, y, z);
			this.settings.put(Setting.SETTINGS_LOBBY, loc);

			// msg
			String locString = String.format("x: %f, y: %f, z: %f", x, y, z);
			Utils.sendMsg(p, Setting.SETTINGS_LOBBY + " set to (" + locString + ")");
		} else {
			return false;
		}
		return true;
	}

	private boolean message_prefix(Player p, String[] args) throws Exception {
		// /mg settings <key> <value>
		String value = args[2];

		this.setKeyValue(p, Setting.SETTINGS_MESSAGE_PREFIX, value);
		return true;
	}

	private boolean minigame_sign(Player p, String[] args) throws Exception {
		boolean value = Boolean.parseBoolean(args[2]);

		this.setKeyValue(p, Setting.SETTINGS_MINIGAME_SIGN, value);
		return true;
	}

	private boolean minigame_command(Player p, String[] args) throws Exception {
		boolean value = Boolean.parseBoolean(args[2]);

		this.setKeyValue(p, Setting.SETTINGS_MINIGAME_COMMAND, value);
		return true;
	}
}
