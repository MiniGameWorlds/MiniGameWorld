package com.worldbiomusic.minigameworld.commands;

import java.util.Map;

import org.bukkit.command.CommandSender;

import com.worldbiomusic.minigameworld.managers.DataManager;
import com.worldbiomusic.minigameworld.managers.MiniGameManager;
import com.worldbiomusic.minigameworld.util.Setting;
import com.worldbiomusic.minigameworld.util.Utils;

public class MiniGameSettingsConfigCommand {

	private MiniGameManager minigameManager;
	private DataManager dataManager;
	// setting.yml
	private Map<String, Object> settings;

	public MiniGameSettingsConfigCommand(MiniGameManager minigameManager, DataManager dataManager) {
		this.minigameManager = minigameManager;
		this.dataManager = dataManager;
		this.settings = this.minigameManager.getSettings();
	}

	public boolean settings(CommandSender sender, String[] args) throws Exception {
		// check permission
		if (!Utils.checkPerm(sender, "config.settings")) {
			return true;
		}

		// print just value of key
		if (args.length == 2) {
			this.printValue(sender, args);
		} else {
			// /mg settings <key> <value>
			String key = args[1];
			switch (key) {
			case Setting.SETTINGS_MESSAGE_PREFIX:
				this.message_prefix(sender, args);
				break;
			case Setting.SETTINGS_DEBUG_MODE:
				this.debug_mode(sender, args);
				break;
			case Setting.SETTINGS_BACKUP_DATA_SAVE_DELAY:
				this.backup_data_save_delay(sender, args);
				break;
			case Setting.SETTINGS_ISOLATED_CHAT:
				this.isolated_chat(sender, args);
				break;
			case Setting.SETTINGS_ISOLATED_JOIN_QUIT_MESSAGE:
				this.isolated_join_quit_message(sender, args);
				break;
			}

			// save config
			this.minigameManager.getYamlManager().save(this.minigameManager);

			// reload config
			this.dataManager.reload(this.minigameManager);

			// refer settings again (reload make new config and retarget settings)
			this.settings = this.minigameManager.getSettings();
		}

		return true;
	}

	private void printValue(CommandSender sender, String[] args) throws Exception {
		// /mg settings <key>
		String key = args[1];
		if (this.settings.containsKey(key)) {
			Object value = this.settings.get(key);
			Utils.sendMsg(sender, key + ": " + value);
		} else {
			Utils.sendMsg(sender, "settings.yml doesn't have " + key + " key");
		}
	}

	private void setKeyValue(CommandSender sender, String key, Object value) throws Exception {
		if (this.settings.containsKey(key)) {
			this.settings.put(key, value);
			Utils.sendMsg(sender, key + " set to " + value);
		} else {
			Utils.sendMsg(sender, "settings.yml doesn't have " + key + " key");
		}
	}

	private boolean message_prefix(CommandSender sender, String[] args) throws Exception {
		// /mg settings <key> <value>
		String value = args[2];

		this.setKeyValue(sender, Setting.SETTINGS_MESSAGE_PREFIX, value);
		return true;

	}

	private boolean debug_mode(CommandSender sender, String[] args) throws Exception {
		boolean value = Boolean.parseBoolean(args[2]);

		this.setKeyValue(sender, Setting.SETTINGS_DEBUG_MODE, value);
		return true;
	}

	private boolean backup_data_save_delay(CommandSender sender, String[] args) throws Exception {
		int value = Integer.parseInt(args[2]);

		this.setKeyValue(sender, Setting.SETTINGS_BACKUP_DATA_SAVE_DELAY, value);
		return true;
	}

	private boolean isolated_chat(CommandSender sender, String[] args) throws Exception {
		boolean value = Boolean.parseBoolean(args[2]);

		this.setKeyValue(sender, Setting.SETTINGS_ISOLATED_CHAT, value);
		return true;
	}

	private boolean isolated_join_quit_message(CommandSender sender, String[] args) throws Exception {
		boolean value = Boolean.parseBoolean(args[2]);

		this.setKeyValue(sender, Setting.SETTINGS_ISOLATED_JOIN_QUIT_MESSAGE, value);
		return true;
	}

}
