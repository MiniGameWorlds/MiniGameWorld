package com.minigameworld.api;

import java.io.File;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minigameworld.frames.MiniGame;
import com.minigameworld.managers.MiniGameManager;
import com.minigameworld.util.Utils;

/**
 * MiniGameWorld Utility API<br>
 */
public class MwUtil {
	private static MiniGameManager minigameManager;

	/**
	 * Do not use
	 */
	public static void setMiniGameManager(MiniGameManager minigameManager) {
		if (MwUtil.minigameManager == null) {
			MwUtil.minigameManager = minigameManager;
		}
	}

	/**
	 * Checks a player is playing any minigames
	 * 
	 * @param p Player to check
	 * @return True if player is playing any minigames
	 */
	public static boolean isPlayingGame(Player p) {
		return minigameManager.isPlayingGame(p);
	}

	/**
	 * Check a player is viewing any minigames
	 * 
	 * @param p Player to check
	 * @return True if a player is viewing any minigames
	 */
	public static boolean isViewingGame(Player p) {
		return minigameManager.isViewingGame(p);
	}

	/**
	 * Check a player is playing or viewing any minigame
	 * 
	 * @param p Player to check
	 * @return True if the player is playing or viewing any minigame
	 */
	public static boolean isInGame(Player p) {
		return minigameManager.isInGame(p);
	}

	/**
	 * Gets player's playing minigame
	 * 
	 * @param p Player to check
	 * @return Null if a player is not playing any minigames
	 */
	public static MiniGameAccessor getPlayingGame(Player p) {
		MiniGame minigame = minigameManager.getPlayingGame(p);
		return new MiniGameAccessor(minigame);
	}

	/**
	 * Get player's viewing minigame
	 * 
	 * @param p Player to check
	 * @return Null if a player is not viewing any minigames
	 */
	public static MiniGameAccessor getViewingGame(Player p) {
		MiniGame minigame = minigameManager.getViewingGame(p);
		return new MiniGameAccessor(minigame);
	}

	/**
	 * Get minigame player is in(playing or viewing) any minigame
	 * 
	 * @param p Player to check
	 * @return Null if a player is not in(playing or viewing) any minigames
	 */
	public static MiniGameAccessor getInGame(Player p) {
		MiniGame minigame = minigameManager.getInGame(p);
		return new MiniGameAccessor(minigame);
	}

	/**
	 * Get players who are playing a minigame
	 * 
	 * @param players Players to check
	 * @return Players who are playing a minigame
	 */
	public static List<Player> getPlayingGamePlayers(List<Player> players) {
		return getPlayingGamePlayers(players, false);
	}

	/**
	 * Get players who are playing a minigame (reverse option)
	 * 
	 * @param players Players to check
	 * @param reverse If true, get not playing game players
	 * @return Players who are playing a minigame
	 */
	public static List<Player> getPlayingGamePlayers(List<Player> players, boolean reverse) {
		return players.stream()
				.filter(p -> reverse ? !minigameManager.isPlayingGame(p) : minigameManager.isPlayingGame(p)).toList();

	}

	/**
	 * Get players who are viewing a minigame
	 * 
	 * @param players Players to check
	 * @return Players who are not viewing a minigame
	 */
	public static List<Player> getViewingGamePlayers(List<Player> players) {
		return getViewingGamePlayers(players, false);
	}

	/**
	 * Get players who are viewing a minigame (reverse option)
	 * 
	 * @param players Players to check
	 * @param reverse If true, get not viewing game players
	 * @return Players who are not viewing a minigame
	 */
	public static List<Player> getViewingGamePlayers(List<Player> players, boolean reverse) {
		return players.stream()
				.filter(p -> reverse ? !minigameManager.isViewingGame(p) : minigameManager.isViewingGame(p)).toList();
	}

	/**
	 * Get players who are in(playing or viewing) a minigame
	 * 
	 * @param players Players to check
	 * @return Players who are in(playing or viewing) a minigame
	 */
	public static List<Player> getInGamePlayers(List<Player> players) {
		return getInGamePlayers(players, false);
	}

	/**
	 * Get players who are in(playing or viewing) a minigame (reverse option)
	 * 
	 * @param players Players to check
	 * @param reverse If true, get not viewing game players
	 * @return Players who are NOT in(playing or viewing) a minigame
	 */
	public static List<Player> getInGamePlayers(List<Player> players, boolean reverse) {
		return players.stream().filter(p -> reverse ? !minigameManager.isInGame(p) : minigameManager.isInGame(p))
				.toList();
	}

	/**
	 * Get MiniGameAccessor with title
	 * 
	 * @param title Minigame title
	 * @return Null if title minigmae is not exist
	 */
	public static MiniGameAccessor getTemplateGame(String title) {
		MiniGame minigame = minigameManager.getTemplateGame(title);
		return minigame == null ? null : new MiniGameAccessor(minigame);
	}

	/**
	 * Gets MiniGameAccessor with class name
	 * 
	 * @param c Minigame class name
	 * @return Null if class name minigame not exist
	 */
	public static MiniGameAccessor getTemplateGame(Class<?> c) {
		MiniGame minigame = minigameManager.getTemplateGame(c);
		return minigame == null ? null : new MiniGameAccessor(minigame);
	}

	/**
	 * Get instance game with title and minigame id
	 * 
	 * @param title Minigame title
	 * @param id    Minigame instance id
	 * @return Null if there is no minigame matched
	 */
	public static MiniGameAccessor getInstanceGame(String title, String id) {
		MiniGame minigame = minigameManager.getInstanceGame(title, id);
		return minigame == null ? null : new MiniGameAccessor(minigame);
	}

	/**
	 * Get instance game with class simple name and minigame id
	 * 
	 * @param c Minigame class name
	 * @param id        Minigame instance id
	 * @return Null if there is no minigame matched
	 */
	public static MiniGameAccessor getInstanceGame(Class<?> c, String id) {
		MiniGame minigame = minigameManager.getInstanceGame(c, id);
		return minigame == null ? null : new MiniGameAccessor(minigame);
	}

	/**
	 * Get "plugins/MiniGameWorld/minigames" directory
	 * 
	 * @return Directory which has minigame configs
	 */
	public static File getMiniGamesDir() {
		return Utils.getMiniGamesDir();
	}

	/**
	 * Get "plugins/MiniGameWorld/messages" directory
	 * 
	 * @return Directory which has language messages
	 */
	public static File getMessagesDir() {
		return Utils.getMessagesDir();
	}

	/**
	 * Check MiniGameWorld permission without "minigameworld."<br>
	 * <b>Example</b><br>
	 * If want to check a player has "minigameworld.play.join" permission or not<br>
	 * <code>
		if (!MwUtil.checkPermission(p, "play.join")) {
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
