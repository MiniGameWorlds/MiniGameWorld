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
				message_prefix(sender, args);
				break;
			case Setting.SETTINGS_DEBUG_MODE:
				debug_mode(sender, args);
				break;
			case Setting.SETTINGS_BACKUP_DATA_SAVE_DELAY:
				backup_data_save_delay(sender, args);
				break;
			case Setting.SETTINGS_ISOLATED_CHAT:
				isolated_chat(sender, args);
				break;
			case Setting.SETTINGS_ISOLATED_JOIN_QUIT_MESSAGE:
				isolated_join_quit_message(sender, args);
				break;
			case Setting.SETTINGS_JOIN_SIGN_CAPTION:
				join_sign_caption(sender, args);
				break;
			case Setting.SETTINGS_LEAVE_SIGN_CAPTION:
				leave_sign_caption(sender, args);
				break;
			case Setting.SETTINGS_SCOREBOARD:
				scoreboard(sender, args);
				break;
			case Setting.SETTINGS_SCOREBOARD_UPDATE_DELAY:
				scoreboard_update_delay(sender, args);
				break;
			case Setting.SETTINGS_REMOVE_NOT_NECESSARY_KEYS:
				remove_not_necessary_keys(sender, args);
				break;
			case Setting.SETTINGS_MIN_LEAVE_TIME:
				min_leave_time(sender, args);
				break;
			case Setting.SETTINGS_START_SOUND:
				start_sound(sender, args);
				break;
			case Setting.SETTINGS_FINISH_SOUND:
				finish_sound(sender, args);
				break;
			case Setting.SETTINGS_CHECK_UPDATE:
				check_update(sender, args);
				break;
			case Setting.SETTINGS_EDIT_MESSAGES:
				edit_messages(sender, args);
				break;
			case Setting.SETTINGS_INGAME_LEAVE:
				ingame_leave(sender, args);
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
		String value = "";
		for (int i = 2; i < args.length; i++) {
			value += args[i];
			if (i < args.length - 1) {
				value += " ";
			}
		}

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

	private boolean join_sign_caption(CommandSender sender, String[] args) throws Exception {
		String value = "";
		for (int i = 2; i < args.length; i++) {
			value += args[i];
			if (i < args.length - 1) {
				value += " ";
			}
		}

		setKeyValue(sender, Setting.SETTINGS_JOIN_SIGN_CAPTION, value);
		return true;
	}

	private boolean leave_sign_caption(CommandSender sender, String[] args) throws Exception {
		String value = "";
		for (int i = 2; i < args.length; i++) {
			value += args[i];
			if (i < args.length - 1) {
				value += " ";
			}
		}

		setKeyValue(sender, Setting.SETTINGS_LEAVE_SIGN_CAPTION, value);
		return true;
	}

	private boolean scoreboard(CommandSender sender, String[] args) throws Exception {
		boolean value = Boolean.parseBoolean(args[2]);

		this.setKeyValue(sender, Setting.SETTINGS_SCOREBOARD, value);
		return true;
	}

	private boolean scoreboard_update_delay(CommandSender sender, String[] args) throws Exception {
		int value = Integer.parseInt(args[2]);

		this.setKeyValue(sender, Setting.SETTINGS_SCOREBOARD_UPDATE_DELAY, value);
		return true;
	}

	private boolean remove_not_necessary_keys(CommandSender sender, String[] args) throws Exception {
		boolean value = Boolean.parseBoolean(args[2]);

		this.setKeyValue(sender, Setting.SETTINGS_REMOVE_NOT_NECESSARY_KEYS, value);
		return true;
	}

	private boolean min_leave_time(CommandSender sender, String[] args) throws Exception {
		int value = Integer.parseInt(args[2]);

		this.setKeyValue(sender, Setting.SETTINGS_MIN_LEAVE_TIME, value);
		return true;
	}

	private boolean start_sound(CommandSender sender, String[] args) throws Exception {
		String value = args[2].toUpperCase();

		this.setKeyValue(sender, Setting.SETTINGS_START_SOUND, value);
		return true;
	}

	private boolean finish_sound(CommandSender sender, String[] args) throws Exception {
		String value = args[2].toUpperCase();

		this.setKeyValue(sender, Setting.SETTINGS_FINISH_SOUND, value);
		return true;
	}

	private boolean check_update(CommandSender sender, String[] args) throws Exception {
		boolean value = Boolean.parseBoolean(args[2]);

		this.setKeyValue(sender, Setting.SETTINGS_CHECK_UPDATE, value);
		return true;
	}

	private boolean edit_messages(CommandSender sender, String[] args) throws Exception {
		boolean value = Boolean.parseBoolean(args[2]);

		this.setKeyValue(sender, Setting.SETTINGS_EDIT_MESSAGES, value);
		return true;
	}

	private boolean ingame_leave(CommandSender sender, String[] args) throws Exception {
		boolean value = Boolean.parseBoolean(args[2]);

		this.setKeyValue(sender, Setting.SETTINGS_INGAME_LEAVE, value);
		return true;
	}
}
