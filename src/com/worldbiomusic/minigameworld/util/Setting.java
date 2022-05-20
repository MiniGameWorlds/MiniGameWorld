package com.worldbiomusic.minigameworld.util;

import org.bukkit.ChatColor;
import org.bukkit.Sound;

import com.worldbiomusic.minigameworld.MiniGameWorldMain;

public class Setting {
	public static final String API_VERSION = apiVersion();

	public static String MESSAGE_PREFIX = ChatColor.BOLD + "MiniGameWorld" + ChatColor.RESET;

	public static boolean DEBUG_MODE = false;

	public static final String MENU_INV_TITLE = "MiniGameWorld";

	public static final int BSTATS_PLUGIN_ID = 14291;

	// url
	public static final String URL_WIKI_README = "https://github.com/MiniGameWorlds/MiniGameWorld/blob/main/README.md";
	public static final String URL_WIKI_COMMAND = "https://github.com/MiniGameWorlds/MiniGameWorld/blob/main/resources/userWiki/commands.md";

	/**
	 * Per minutes
	 */
	public static int BACKUP_DATA_SAVE_DELAY = 60;

	// party
	public static final int PARTY_INVITE_TIMEOUT = 60;
	public static final int PARTY_ASK_TIMEOUT = 60;

	// Minigame (default values)
	public static boolean ISOLATED_CHAT = true;
	public static boolean ISOLATED_JOIN_QUIT_MESSAGE = true;
	public static String JOIN_SIGN_CAPTION = "[MiniGame]";
	public static String LEAVE_SIGN_CAPTION = "[Leave MiniGame]";
	public static boolean SCOREBOARD = true;
	public static int SCOREBOARD_UPDATE_DELAY = 10;
	public static int MIN_LEAVE_TIME = 3;
	public static Sound START_SOUND = Sound.BLOCK_NOTE_BLOCK_CHIME;
	public static Sound FINISH_SOUND = Sound.BLOCK_NOTE_BLOCK_BELL;
	public static boolean REMOVE_NOT_NECESSARY_KEYS = false;
	public static boolean CHECK_UPDATE = true;
	public static boolean EDIT_MESSAGES = false;
	public static boolean INGAME_LEAVE = false;

	// settings.yml
	public static final String SETTINGS_MESSAGE_PREFIX = "message-prefix";
	public static final String SETTINGS_DEBUG_MODE = "debug-mode";
	public static final String SETTINGS_BACKUP_DATA_SAVE_DELAY = "backup-data-save-delay";
	public static final String SETTINGS_ISOLATED_CHAT = "isolated-chat";
	public static final String SETTINGS_ISOLATED_JOIN_QUIT_MESSAGE = "isolated-join-quit-message";
	public static final String SETTINGS_JOIN_SIGN_CAPTION = "join-sign-caption";
	public static final String SETTINGS_LEAVE_SIGN_CAPTION = "leave-sign-caption";
	public static final String SETTINGS_SCOREBOARD = "scoreboard";
	public static final String SETTINGS_SCOREBOARD_UPDATE_DELAY = "scoreboard-update-delay";
	public static final String SETTINGS_MIN_LEAVE_TIME = "min-leave-time";
	public static final String SETTINGS_START_SOUND = "start-sound";
	public static final String SETTINGS_FINISH_SOUND = "finish-sound";
	public static final String SETTINGS_REMOVE_NOT_NECESSARY_KEYS = "remove-not-necessary-keys";
	public static final String SETTINGS_CHECK_UPDATE = "check-update";
	public static final String SETTINGS_EDIT_MESSAGES = "edit-messages";
	public static final String SETTINGS_INGAME_LEAVE = "ingame-leave";

	// minigames
	public static final String MINIGAMES_TITLE = "title";
	public static final String MINIGAMES_LOCATION = "location";
	public static final String MINIGAMES_MIN_PLAYER_COUNT = "min-players";
	public static final String MINIGAMES_MAX_PLAYER_COUNT = "max-players";
	public static final String MINIGAMES_WAITING_TIME = "waiting-time";
	public static final String MINIGAMES_PLAY_TIME = "play-time";
	public static final String MINIGAMES_ACTIVE = "active";
	public static final String MINIGAMES_TUTORIAL = "tutorial";
	public static final String MINIGAMES_CUSTOM_DATA = "custom-data";
	public static final String MINIGAMES_ICON = "icon";
	public static final String MINIGAMES_VIEW = "view";
	public static final String MINIGAMES_SCOREBOARD = "scoreboard";

	private static String apiVersion() {
		return MiniGameWorldMain.getInstance().getDescription().getVersion();
	}

}
