package com.worldbiomusic.minigameworld.util;

import org.bukkit.Sound;

import com.worldbiomusic.minigameworld.MiniGameWorldMain;

import net.md_5.bungee.api.ChatColor;

public class Setting {
	public static final String API_VERSION = apiVersion();
	
	public static String MESSAGE_PREFIX = ChatColor.BOLD + "MiniGameWorld" + ChatColor.RESET;

	public static boolean DEBUG_MODE = false;

	public static final String MENU_INV_TITLE = "MiniGameWorld";

	public static final int BSTATS_PLUGIN_ID = 14291;

	/**
	 * Per minutes
	 */
	public static int BACKUP_DATA_SAVE_DELAY = 60;

	// party
	public static final int PARTY_INVITE_TIMEOUT = 60;
	public static final int PARTY_ASK_TIMEOUT = 60;

	// Minigame
	public static boolean ISOLATED_CHAT = true;
	public static boolean ISOLATED_JOIN_QUIT_MESSAGE = true;
	public static String JOIN_SIGN_CAPTION = "[MiniGame]";
	public static String LEAVE_SIGN_CAPTION = "[Leave MiniGame]";
	public static boolean SCOREBOARD = true;
	public static int SCOREBOARD_UPDATE_DELAY = 10;
	public static int MIN_LEAVE_TIME = 3;
	public static Sound START_SOUND = Sound.BLOCK_END_PORTAL_SPAWN;
	public static Sound FINISH_SOUND = Sound.ENTITY_ENDER_DRAGON_DEATH;

	public static boolean REMOVE_NOT_NECESSARY_KEYS = false;

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
	public static final String SETTINGS_REMOVE_NOT_NECESSARY_KEYS = "remove-not-necessary-keys";
	public static final String SETTINGS_MIN_LEAVE_TIME = "min-leave-time";
	public static final String SETTINGS_START_SOUND = "start-sound";
	public static final String SETTINGS_FINISH_SOUND = "finish-sound";

	// minigames
	public static final String MINIGAMES_TITLE = "title";
	public static final String MINIGAMES_LOCATION = "location";
	public static final String MINIGAMES_MIN_PLAYER_COUNT = "min-player-count";
	public static final String MINIGAMES_MAX_PLAYER_COUNT = "max-player-count";
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
