package com.worldbiomusic.minigameworld.api;

import java.io.File;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.worldbiomusic.minigameworld.MiniGameWorldMain;
import com.worldbiomusic.minigameworld.managers.MiniGameManager;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameDataManager;
import com.worldbiomusic.minigameworld.util.Utils;

/**
 * MiniGameWorld Utility API<br>
 */
public class MiniGameWorldUtils {
	private static MiniGameManager minigameManager;

	/**
	 * Do not use
	 */
	public static void setMiniGameManager(MiniGameManager minigameManager) {
		if (minigameManager == null) {
			MiniGameWorldUtils.minigameManager = minigameManager;
		}
	}

	/**
	 * Checks a player is playing any minigames
	 * 
	 * @param p Player to check
	 * @return True if player is playing any minigames
	 */
	public static boolean checkPlayerIsPlayingMiniGame(Player p) {
		return minigameManager.isPlayingMiniGame(p);
	}

	/**
	 * Check a player is viewing any minigames
	 * 
	 * @param p Player to check
	 * @return True if a player is viewing any minigames
	 */
	public static boolean checkPlayerIsViewingMiniGame(Player p) {
		return minigameManager.isViewingMiniGame(p);
	}

	/**
	 * Check a player is playing or viewing any minigame
	 * 
	 * @param p Player to check
	 * @return True if the player is playing or viewing any minigame
	 */
	public static boolean checkPlayerIsInMiniGame(Player p) {
		return minigameManager.isInMiniGame(p);
	}

	/**
	 * Gets player's playing minigame
	 * 
	 * @param p Player to check
	 * @return Null if a player is not playing any minigames
	 */
	public static MiniGameAccessor getPlayingMiniGame(Player p) {
		MiniGame minigame = minigameManager.getPlayingMiniGame(p);
		return new MiniGameAccessor(minigame);
	}

	/**
	 * Get player's viewing minigame
	 * 
	 * @param p Player to check
	 * @return Null if a player is not viewing any minigames
	 */
	public static MiniGameAccessor getViewingMiniGame(Player p) {
		MiniGame minigame = minigameManager.getViewingMiniGame(p);
		return new MiniGameAccessor(minigame);
	}

	/**
	 * Get minigame player is in(playing or viewing) any minigame
	 * 
	 * @param p Player to check
	 * @return Null if a player is not in(playing or viewing) any minigames
	 */
	public static MiniGameAccessor getInMiniGame(Player p) {
		MiniGame minigame = minigameManager.getInMiniGame(p);
		return new MiniGameAccessor(minigame);
	}

	/**
	 * Get players who are playing a minigame
	 * 
	 * @param players Players to check
	 * @return Players who are playing a minigame
	 */
	public static List<Player> getPlayingMiniGamePlayers(List<Player> players) {
		return players.stream().filter(p -> minigameManager.isPlayingMiniGame(p)).toList();
	}

	/**
	 * Get players who are not playing a minigame
	 * 
	 * @param players Players to check
	 * @return Players who are not playing a minigame
	 */
	public static List<Player> getNotPlayingMiniGamePlayers(List<Player> players) {
		return players.stream().filter(p -> !minigameManager.isPlayingMiniGame(p)).toList();
	}

	/**
	 * Get players who are not viewing a minigame
	 * 
	 * @param players Players to check
	 * @return Players who are not viewing a minigame
	 */
	public static List<Player> getViewingMiniGamePlayers(List<Player> players) {
		return players.stream().filter(p -> minigameManager.isViewingMiniGame(p)).toList();
	}

	/**
	 * Get players who are not viewing a minigame
	 * 
	 * @param players Players to check
	 * @return Players who are not viewing a minigame
	 */
	public static List<Player> getNotViewingMiniGamePlayers(List<Player> players) {
		return players.stream().filter(p -> !minigameManager.isViewingMiniGame(p)).toList();
	}

	/**
	 * Get players who are in(playing or viewing) a minigame
	 * 
	 * @param players Players to check
	 * @return Players who are in(playing or viewing) a minigame
	 */
	public static List<Player> getInMiniGamePlayers(List<Player> players) {
		return players.stream().filter(p -> minigameManager.isInMiniGame(p)).toList();
	}

	/**
	 * Get players who are NOT in(playing or viewing) a minigame
	 * 
	 * @param players Players to check
	 * @return Players who are NOT in(playing or viewing) a minigame
	 */
	public static List<Player> getNotInMiniGamePlayers(List<Player> players) {
		return players.stream().filter(p -> !minigameManager.isInMiniGame(p)).toList();
	}

	/**
	 * Gets MiniGameAccessor with class name
	 * 
	 * @param className Minigame class name
	 * @return Null if class name minigame not exist
	 */
	public static MiniGameAccessor getMiniGameWithClassName(String className) {
		MiniGame minigame = minigameManager.getMiniGameWithClassName(className);
		return new MiniGameAccessor(minigame);
	}

	/**
	 * Get "plugins/MiniGameWorld/minigames" directory
	 * 
	 * @return Directory which has minigame configs
	 */
	public static File getMiniGamesDirectory() {
		return new File(MiniGameWorldMain.getInstance().getDataFolder(), MiniGameDataManager.FOLDER_NAME);
	}

	/**
	 * Check MiniGameWorld permission without "minigameworld."<br>
	 * <b>Example</b><br>
	 * If want to check a player has "minigameworld.play.join" permission or not<br>
	 * <code>
		if (!MiniGameWorldUtils.checkPermission(p, "play.join")) {
			return;
		}
	 * </code>
	 * 
	 * @param sender     CommandSender to check
	 * @param permission Permission to check
	 * @return True if sender has the permission
	 */
	public static boolean checkPermission(CommandSender sender, String permission) {
		return Utils.checkPerm(sender, permission);
	}
}
