package com.worldbiomusic.minigameworld.commands;

import java.util.Map;

import org.bukkit.entity.Player;

import com.worldbiomusic.minigameworld.managers.MiniGameManager;
import com.worldbiomusic.minigameworld.util.Setting;
import com.worldbiomusic.minigameworld.util.Utils;

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

		// print just value of key
		if (args.length == 2) {
			this.printValue(p, args);
		} else {

			// /mg settings <key> <value>
			String key = args[1];
			switch (key) {
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
		}
		
		return true;
	}

	private void printValue(Player p, String[] args) throws Exception {
		// /mg settings <key>
		String key = args[1];
		if (this.settings.containsKey(key)) {
			Object value = this.settings.get(key);
			Utils.sendMsg(p, key + ": " + value);
		} else {
			Utils.sendMsg(p, "settings.yml doesn't have " + key + " key");
		}
	}

	private void setKeyValue(Player p, String key, Object value) throws Exception {
		if (this.settings.containsKey(key)) {
			this.settings.put(key, value);
			Utils.sendMsg(p, key + " set to " + value);
		} else {
			Utils.sendMsg(p, "settings.yml doesn't have " + key + " key");
		}
	}

	private boolean message_prefix(Player p, String[] args) throws Exception {
		// /mg settings <key> <value1> <value2> <value3> ...
		String value = "";
		for (int i = 2; i < args.length; i++) {
			value += args[i];
		}

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
