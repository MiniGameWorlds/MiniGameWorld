package com.worldbiomusic.minigameworld.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.worldbiomusic.minigameworld.api.observer.MiniGameObserver;
import com.worldbiomusic.minigameworld.api.observer.MiniGameTimingNotifier;
import com.worldbiomusic.minigameworld.managers.MiniGameManager;
import com.worldbiomusic.minigameworld.managers.party.PartyManager;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameEventDetector;
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
	public static final String API_VERSION = Setting.API_VERSION;

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
		Different diff = VersionChecker.getDifferent(MiniGameWorld.API_VERSION, version);
		// only return instance when diff is PATCH
		if (diff == Different.PATCH) {
			return true;
		}
		// MINER, MAJOR or null
		Utils.debug("Version not matched (Try version: " + version + ")");
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
	 * @param p     Player who tries to join
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
	 * Make the player view minigame
	 * 
	 * @param p     Player who tries to view
	 * @param title MiniGame title
	 */
	public void viewGame(Player p, String title) {
		this.minigameManager.viewGame(p, title);
	}

	/**
	 * Unview(leave) player from viewing minigame
	 * 
	 * @param p Player who tries to unview
	 */
	public void unviewGame(Player p) {
		this.minigameManager.unviewGame(p);
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
	 * [IMPORTANT] Observer must be registered with {@link MiniGameManager}, not
	 * directly with MiniGame (only {@link MiniGameManager} can register to all
	 * minigames)<br>
	 * 
	 * @param observer Observer to register
	 * @see MiniGameTimingNotifier
	 * @see MiniGameObserver
	 */
	public void registerMiniGameObserver(MiniGameObserver observer) {
		// register observer on All MiniGames
		// ex. give reward with each minigames
		this.minigameManager.registerObserver(observer);
	}

	/**
	 * Unregisters minigame observer<br>
	 * [IMPORTANT] Observer must be unregistered from {@link MiniGameManager}, not
	 * directly from MiniGame (only {@link MiniGameManager} can unregister from all
	 * minigames)<br>
	 * 
	 * @param observer Observer to unregister
	 * @see MiniGameTimingNotifier
	 * @see MiniGameObserver
	 */
	public void unregisterMiniGameObserver(MiniGameObserver observer) {
		// unregister observer from All MiniGames
		this.minigameManager.unregisterObserver(observer);
	}

	/**
	 * Opens Menu GUI<br>
	 * - Can edit Menu with returned inventory instance
	 * 
	 * @param p Player to show menu
	 * @return Menu GUI invenotry instance
	 */
	public Inventory openMenu(Player p) {
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

	/**
	 * Get MiniGameWorld API setting data of <b>settings.yml</b>
	 * 
	 * @return Setting data
	 */
	public Map<String, Object> getSettings() {
		return this.minigameManager.getSettings();
	}

	/**
	 * Registers custom minigame event external detector<br>
	 * Event detected by detector will be able to be passed processEvent() of
	 * minigame
	 * 
	 * @param detector Registering Detector
	 * @see MiniGameEventExternalDetector
	 */
	public void registerMiniGameEventExternalDetector(MiniGameEventExternalDetector detector) {
		this.minigameManager.getMiniGameEventDetector().registerExternalDetector(detector);
	}

	/**
	 * Unregisters custom minigame event external detector<br>
	 * 
	 * @param detector Unregistering Detector
	 * @see MiniGameEventExternalDetector
	 */
	public void unregisterMiniGameEventExternalDetector(MiniGameEventExternalDetector detector) {
		this.minigameManager.getMiniGameEventDetector().unregisterExternalDetector(detector);
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
