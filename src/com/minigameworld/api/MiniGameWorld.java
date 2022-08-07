package com.minigameworld.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.minigameworld.api.observer.MiniGameObserver;
import com.minigameworld.api.observer.MiniGameTimingNotifier;
import com.minigameworld.frames.MiniGame;
import com.minigameworld.frames.helpers.MiniGameEventDetector;
import com.minigameworld.managers.MiniGameManager;
import com.minigameworld.managers.party.PartyManager;
import com.minigameworld.util.Setting;
import com.minigameworld.util.Utils;
import com.minigameworld.util.VersionChecker;
import com.minigameworld.util.VersionChecker.Different;

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
		if (checkVersion(version)) {
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
	public static boolean checkVersion(String version) {
		Different diff = VersionChecker.getDifferent(MiniGameWorld.API_VERSION, version);
		// only return instance when diff is PATCH
		if (diff == Different.PATCH) {
			return true;
		}
		// MINER, MAJOR or null
		Utils.warning("Version not matched (Try version: " + version + ")");
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
	 * Join a minigame with party members who are available to join with<br>
	 * - Join into already waiting instance game or create new game instance if need
	 * 
	 * @param p     Player who tries to join
	 * @param title MiniGame title
	 * @return False if player failed to join
	 */
	public boolean joinGame(Player p, String title) {
		return this.minigameManager.joinGame(p, title);
	}

	/**
	 * Join into minigame instance already created<br>
	 * (NEVER create new game instance)
	 * 
	 * @param p     Player who tries to join
	 * @param title MiniGame title
	 * @param id    Minigame id
	 * @return False if player failed to join
	 */
	public boolean joinGame(Player p, String title, String id) {
		return this.minigameManager.joinGame(p, title, id);
	}

	/**
	 * Leave player from playing minigame
	 * 
	 * @param p Player who tries to leave
	 * @return False if player failed to leave
	 */
	public boolean leaveGame(Player p) {
		return this.minigameManager.leaveGame(p);
	}

	/**
	 * Make the player view <b>random</b> minigame
	 * 
	 * @param p     Player who tries to view
	 * @param title MiniGame title
	 * @return False if player failed to view
	 */
	public boolean viewGame(Player p, String title) {
		return this.minigameManager.viewGame(p, title);
	}

	/**
	 * Make the player view minigame
	 * 
	 * @param p     Player who tries to view
	 * @param title MiniGame title
	 * @param id    Minigame id
	 * @return False if player failed to view
	 */
	public boolean viewGame(Player p, String title, String id) {
		return this.minigameManager.viewGame(p, title, id);
	}

	/**
	 * Unview(leave) player from viewing minigame
	 * 
	 * @param p Player who tries to unview
	 * @return False if player failed to unview
	 */
	public boolean unviewGame(Player p) {
		return this.minigameManager.unviewGame(p);
	}

	/**
	 * Start minigame
	 * 
	 * @param title Minigame title
	 * @param id    instance id
	 * @return False if minigame failed to start
	 */
	public boolean startGame(String title, String id) {
		return this.minigameManager.startGame(title, id);
	}

	/**
	 * Gets MiniGameEventDetector<br>
	 * 
	 * Can check specific event can be detected
	 * 
	 * @return Instance
	 */
	public MiniGameEventDetector getEventDetector() {
		return this.minigameManager.getEventDetector();
	}

	/**
	 * Gets template minigame list
	 * 
	 * @return MiniGameAccessor list
	 */
	public List<MiniGameAccessor> getTemplateGames() {
		List<MiniGame> minigames = this.minigameManager.getTemplateGames();
		List<MiniGameAccessor> minigameAccessors = new ArrayList<MiniGameAccessor>();
		minigames.forEach(game -> minigameAccessors.add(new MiniGameAccessor(game)));
		return minigameAccessors;
	}

	/**
	 * Get instance minigame list
	 * 
	 * @return MiniGameAccessor list
	 */
	public List<MiniGameAccessor> getInstanceGames() {
		List<MiniGame> minigames = this.minigameManager.getInstanceGames();
		List<MiniGameAccessor> minigameAccessors = new ArrayList<MiniGameAccessor>();
		minigames.forEach(game -> minigameAccessors.add(new MiniGameAccessor(game)));
		return minigameAccessors;
	}

	/**
	 * Registers new template minigame to MiniGameWorld plugin<br>
	 * 
	 * The Same class name minigame can't be registered in the same server
	 * 
	 * @param newGame Minigame to register
	 * @return False if same class name minigame already exists
	 */
	public boolean registerGame(MiniGame newGame) {
		return this.minigameManager.registerTemplateGame(newGame);
	}

	/**
	 * Unregisters template minigame from server
	 * 
	 * @param minigame Minigame to unregister
	 * @return False if not exist
	 */
	public boolean unregisterGame(MiniGame minigame) {
		return this.minigameManager.unregisterTemplateGame(minigame);
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
	public void registerObserver(MiniGameObserver observer) {
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
	public void unregisterObserver(MiniGameObserver observer) {
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
		return this.minigameManager.getMenuManager().openMenu(p);
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
	 * Get {@link MiniGameManager}
	 * 
	 * @return {@link MiniGameManager}
	 */
	public MiniGameManager getManager() {
		return this.minigameManager;
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
	 * Event detected by detector will be able to be passed onEvent() of minigame
	 * 
	 * @param detector Registering Detector
	 * @see MiniGameExternalEventDetector
	 */
	public void registerExternalEventDetector(MiniGameExternalEventDetector detector) {
		this.minigameManager.getEventDetector().registerExternalDetector(detector);
	}

	/**
	 * Unregisters custom minigame event external detector<br>
	 * 
	 * @param detector Unregistering Detector
	 * @see MiniGameExternalEventDetector
	 */
	public void unregisterExternalEventDetector(MiniGameExternalEventDetector detector) {
		this.minigameManager.getEventDetector().unregisterExternalDetector(detector);
	}

	/**
	 * Update instance minigame data with template minigame data
	 * 
	 * @param instance Minigame which will be updated
	 */
	public void updateInstanceGameData(MiniGame instance) {
		this.minigameManager.updateInstanceGameData(instance);
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
