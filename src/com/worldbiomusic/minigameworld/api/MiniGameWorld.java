package com.worldbiomusic.minigameworld.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.worldbiomusic.minigameworld.managers.MiniGameManager;
import com.worldbiomusic.minigameworld.managers.party.PartyManager;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameEventDetector;
import com.worldbiomusic.minigameworld.observer.MiniGameObserver;
import com.worldbiomusic.minigameworld.util.Setting;
import com.worldbiomusic.minigameworld.util.Utils;
import com.worldbiomusic.minigameworld.util.VersionChecker;
import com.worldbiomusic.minigameworld.util.VersionChecker.Different;

/**
 * MiniGameWorld plugin API
 * 
 */
public class MiniGameWorld {

	/**
	 * MiniGameWorld API version
	 */
	public static final String VERSION = Setting.API_VERSION;

	/**
	 * instance
	 */
	private static MiniGameWorld instance = new MiniGameWorld();

	/**
	 * MiniGameManager
	 */
	private MiniGameManager minigameManager;

	/**
	 * private constructor
	 */
	private MiniGameWorld() {
	}

	/**
	 * MiniGameWorld singleton instance<br>
	 * <br>
	 * 
	 * Version format: {@code <MAJOR>.<MINOR>.<PATCH>}<br>
	 * MAJOR: not change <br>
	 * MINOR: API changed <br>
	 * PATCH: logic, bug, error fixed<br>
	 * 
	 * <br>
	 * Version is only compatible with the same "MINOR" version, different "PATCH"
	 * version is permit<br>
	 * - "1.0.0" and 1.0.5" is compatible for different "PATCH"<br>
	 * - "1.0.0" and 1.1.5" is NOT compatible for different "MINOR"<br>
	 * 
	 * <br>
	 * Current API version: {@code MiniGameWorld.VERSION}
	 * 
	 * @param version Using version
	 * @return True if version is compatible
	 */
	public static MiniGameWorld create(String version) {
		if (checkCompatibleVersion(version)) {
			return instance;
		}

		return null;
	}

	/**
	 * Check version is compatible with API
	 * 
	 * @param version Using version
	 * @return True if compatible
	 */
	public static boolean checkCompatibleVersion(String version) {
		Different diff = VersionChecker.getDifferent(MiniGameWorld.VERSION, version);
		// only return instance when diff is PATCH
		if (diff == Different.PATCH) {
			return true;
		}
		// MINER, MAJOR or null
		Utils.debug("Version not matched");
		return false;
	}

	/**
	 * Set MiniGameManager only once when plugin loaded automatically by
	 * MiniGameWorld plugin
	 * 
	 * @param minigameM MiniGameManager
	 */
	public void setMiniGameManager(MiniGameManager minigameM) {
		if (this.minigameManager == null) {
			this.minigameManager = minigameM;
		}
	}

	/**
	 * Join player to minigame
	 * 
	 * @param p     Player who treis to join
	 * @param title MiniGame title
	 */
	public void joinGame(Player p, String title) {
		this.minigameManager.joinGame(p, title);
	}

	/**
	 * Leave player from playing minigame
	 * 
	 * @param p Player who tries to leave
	 */
	public void leaveGame(Player p) {
		this.minigameManager.leaveGame(p);
	}

	/**
	 * Sends exception to minigame
	 * 
	 * @param p         Playing minigame player
	 * @param exception Exception to send
	 */
	public void handleException(Player p, MiniGame.Exception exception) {
		this.minigameManager.handleException(p, exception);
	}

	/**
	 * Gets MiniGameEventDetector<br>
	 * 
	 * Can check specific event can be detected
	 * 
	 * @return Instance
	 */
	public MiniGameEventDetector getMiniGameEventDetector() {
		return this.minigameManager.getMiniGameEventDetector();
	}

	/**
	 * Checks a player is playing any minigame
	 * 
	 * @param p Target player
	 * @return True if player is playing any minigame
	 */
	public boolean checkPlayerIsPlayingMiniGame(Player p) {
		return this.minigameManager.isPlayingMiniGame(p);
	}

	/**
	 * Gets MiniGameAccessor with class name
	 * 
	 * @param className Minigame class name
	 * @return Null if class name minigame not exist
	 */
	public MiniGameAccessor getMiniGameWithClassName(String className) {
		MiniGame minigame = this.minigameManager.getMiniGameWithClassName(className);
		return new MiniGameAccessor(minigame);
	}

	/**
	 * Gets player's playing minigame
	 * 
	 * @param p Playing minigame
	 * @return Null if player is not playing any minigame
	 */
	public MiniGameAccessor getPlayingMiniGame(Player p) {
		MiniGame minigame = this.minigameManager.getPlayingMiniGame(p);
		return new MiniGameAccessor(minigame);
	}

	/**
	 * Gets minigame list
	 * 
	 * @return MiniGameAccessor list
	 */
	public List<MiniGameAccessor> getMiniGameList() {
		List<MiniGame> minigames = this.minigameManager.getMiniGameList();
		List<MiniGameAccessor> minigameAccessors = new ArrayList<MiniGameAccessor>();
		minigames.forEach(game -> minigameAccessors.add(new MiniGameAccessor(game)));
		return minigameAccessors;
	}

	/**
	 * Registers new minigame to MiniGameWorld plugin<br>
	 * 
	 * The Same class name minigame can't be registered in the same server
	 * 
	 * @param newGame Minigame to register
	 * @return False if same class name minigame already exists
	 */
	public boolean registerMiniGame(MiniGame newGame) {
		return this.minigameManager.registerMiniGame(newGame);
	}

	/**
	 * Unregisters minigame from server
	 * 
	 * @param minigame Minigame to unregister
	 * @return False if not exist
	 */
	public boolean unregisterMiniGame(MiniGame minigame) {
		return this.minigameManager.unregisterMiniGame(minigame);
	}

	/**
	 * Registers minigame observer<br>
	 * - Can reserve task with MiniGameEventNotifier.MiniGameEvent
	 * 
	 * @param observer Observer to register
	 */
	public void registerMiniGameObserver(MiniGameObserver observer) {
		// register observer on All MiniGames
		// ex. give reward with each minigames
		this.minigameManager.getMiniGameList().forEach(minigame -> minigame.registerObserver(observer));
	}

	/**
	 * Unregisters minigame observer
	 * 
	 * @param observer Observer to unregister
	 */
	public void unregisterMiniGameObserver(MiniGameObserver observer) {
		// unregister observer from All MiniGames
		this.minigameManager.getMiniGameList().forEach(minigame -> minigame.unregisterObserver(observer));
	}

	/**
	 * Opens Menu GUI<br>
	 * - Can edit Menu with returned inventory instance
	 * 
	 * @param p Player to show menu
	 * @return Menu GUI invenotry instance
	 */
	public Inventory openMiniGameMenu(Player p) {
		return this.minigameManager.getMiniGameMenuManager().openMenu(p);
	}

	/**
	 * Gets Party Manager<br>
	 * - Can change party access methods (e.g. Right-Click player to ask)
	 * 
	 * @return PartyManager
	 */
	public PartyManager getPartyManager() {
		return this.minigameManager.getPartyManager();
	}
}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
