package com.worldbiomusic.minigameworld.util;

import com.worldbiomusic.minigameworld.MiniGameWorldMain;

public class Setting {
	public static final String API_VERSION = apiVersion();

	public static boolean DEBUG_MODE = false;

	public static final String MENU_INV_TITLE = "MiniGameWorld";

	/**
	 * Per minutes
	 */
	public static int BACKUP_DATA_SAVE_DELAY = 60;

	// party
	public static final int PARTY_INVITE_TIMEOUT = 60;
	public static final int PARTY_ASK_TIMEOUT = 60;

	// Minigame
	public static final int MINIGAME_MIN_LEAVE_TIME = 3;
	public static boolean ISOLATED_CHAT = true;
	public static boolean ISOLATED_JOIN_QUIT_MESSAGE = true;
	public static String JOIN_SIGN_CAPTION = "[MiniGame]";
	public static String LEAVE_SIGN_CAPTION = "[Leave MiniGame]";
	public static boolean SCOREBOARD = true;
	public static int SCOREBOARD_UPDATE_DELAY = 4;

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

	// minigames
	public static final String MINIGAMES_TITLE = "title";
	public static final String MINIGAMES_LOCATION = "location";
	public static final String MINIGAMES_MIN_PLAYER_COUNT = "min-player-count";
	public static final String MINIGAMES_MAX_PLAYER_COUNT = "max-player-count";
	public static final String MINIGAMES_WAITING_TIME = "waiting-time";
	public static final String MINIGAMES_TIME_LIMIT = "time-limit";
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
