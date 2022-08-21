package com.minigameworld.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.google.common.io.Files;
import com.minigameworld.api.MiniGameAccessor;
import com.minigameworld.api.MwUtil;
import com.minigameworld.api.observer.MiniGameObserver;
import com.minigameworld.api.observer.MiniGameTimingNotifier;
import com.minigameworld.commands.MiniGameGamesConfigCommand;
import com.minigameworld.events.minigame.MiniGameExceptionEvent;
import com.minigameworld.events.minigame.MiniGamePlayerExceptionEvent;
import com.minigameworld.events.minigame.MiniGameServerExceptionEvent;
import com.minigameworld.events.minigame.instance.MiniGameInstanceCreateEvent;
import com.minigameworld.events.minigame.instance.MiniGameInstanceRemoveEvent;
import com.minigameworld.frames.MiniGame;
import com.minigameworld.frames.helpers.MiniGameEventDetector;
import com.minigameworld.frames.helpers.MiniGameSetting;
import com.minigameworld.managers.event.GameListenerManager;
import com.minigameworld.managers.menu.MiniGameMenuManager;
import com.minigameworld.managers.party.Party;
import com.minigameworld.managers.party.PartyManager;
import com.minigameworld.util.Setting;
import com.minigameworld.util.Utils;
import com.wbm.plugin.util.CollectionTool;
import com.wbm.plugin.util.data.yaml.YamlHelper;
import com.wbm.plugin.util.data.yaml.YamlManager;
import com.wbm.plugin.util.data.yaml.YamlMember;

public class MiniGameManager implements YamlMember, MiniGameTimingNotifier {
	// Singleton
	private static final MiniGameManager instance = new MiniGameManager();

	// Template minigame list
	private List<MiniGame> templateGames;

	// Instance minigame list
	private List<MiniGame> instanceGames;

	// setting.yml
	private Map<String, Object> settings;

	// event detector
	private MiniGameEventDetector eventDetector;

	// minigame gui manager
	private MiniGameMenuManager menuManager;

	// party
	private PartyManager partyManager;

	private GameListenerManager gameListenerManager;

	// yaml data manager
	private YamlManager yamlManager;

	// Minigame Observer
	private List<MiniGameObserver> observers;

	// use getInstance()
	private MiniGameManager() {
		// init
		this.templateGames = new CopyOnWriteArrayList<>();
		this.instanceGames = new CopyOnWriteArrayList<>();
		this.settings = new LinkedHashMap<String, Object>();
		this.initSettingData();
		this.eventDetector = new MiniGameEventDetector(this);

		this.menuManager = new MiniGameMenuManager(this);
		this.partyManager = new PartyManager();
		this.gameListenerManager = new GameListenerManager(eventDetector);
		this.observers = new ArrayList<>();
	}

	/**
	 * Todo list when a player joined the server
	 * 
	 * @param p Joined player
	 */
	public void todoOnPlayerJoin(Player p) {
		this.getPartyManager().createParty(p);
	}

	/**
	 * Todo list when a player try to quitting the server
	 * 
	 * @param p Quitting player
	 */
	public void todoOnPlayerQuit(Player p) {
		// party
		this.getPartyManager().leaveParty(p);
		this.getPartyManager().deleteParty(p);

		// menu GUI
		String invTitle = p.getOpenInventory().getTitle();
		if (invTitle.equals(Setting.MENU_INV_TITLE)) {
			p.closeInventory();
		}
	}

	public static MiniGameManager getInstance() {
		return instance;
	}

	/**
	 * Set basic setting.yml data<br>
	 * [IMPORTANT]<br>
	 * - If add option, add access method to {@link MiniGameGamesConfigCommand}<br>
	 */
	@SuppressWarnings("unchecked")
	private void initSettingData() {
		Map<String, Object> pureData = new LinkedHashMap<>();
		pureData.put(Setting.SETTINGS_MESSAGE_PREFIX, Setting.MESSAGE_PREFIX);
		pureData.put(Setting.SETTINGS_BACKUP_DELAY, Setting.BACKUP_DELAY);
		pureData.put(Setting.SETTINGS_DEBUG_MODE, Setting.DEBUG_MODE);
		pureData.put(Setting.SETTINGS_ISOLATED_CHAT, Setting.ISOLATED_CHAT);
		pureData.put(Setting.SETTINGS_ISOLATED_JOIN_QUIT_MESSAGE, Setting.ISOLATED_JOIN_QUIT_MESSAGE);
		pureData.put(Setting.SETTINGS_JOIN_SIGN_CAPTION, Setting.JOIN_SIGN_CAPTION);
		pureData.put(Setting.SETTINGS_LEAVE_SIGN_CAPTION, Setting.LEAVE_SIGN_CAPTION);
		pureData.put(Setting.SETTINGS_SCOREBOARD, Setting.SCOREBOARD);
		pureData.put(Setting.SETTINGS_SCOREBOARD_UPDATE_DELAY, Setting.SCOREBOARD_UPDATE_DELAY);
		pureData.put(Setting.SETTINGS_REMOVE_NOT_NECESSARY_KEYS, Setting.REMOVE_NOT_NECESSARY_KEYS);
		pureData.put(Setting.SETTINGS_MIN_LEAVE_TIME, Setting.MIN_LEAVE_TIME);
		pureData.put(Setting.SETTINGS_START_SOUND, Setting.START_SOUND.name());
		pureData.put(Setting.SETTINGS_FINISH_SOUND, Setting.FINISH_SOUND.name());
		pureData.put(Setting.SETTINGS_CHECK_UPDATE, Setting.CHECK_UPDATE);
		pureData.put(Setting.SETTINGS_EDIT_MESSAGES, Setting.EDIT_MESSAGES);
		pureData.put(Setting.SETTINGS_INGAME_LEAVE, Setting.INGAME_LEAVE);
		pureData.put(Setting.SETTINGS_TEMPLATE_WORLDS, Setting.TEMPLATE_WORLDS);
		pureData.put(Setting.SETTINGS_JOIN_PRIORITY, Setting.JOIN_PRIORITY.name());
		pureData.put(Setting.SETTINGS_PARTY_INVITE_TIMEOUT, Setting.PARTY_INVITE_TIMEOUT);
		pureData.put(Setting.SETTINGS_PARTY_ASK_TIMEOUT, Setting.PARTY_ASK_TIMEOUT);

		Utils.syncMapKeys(this.settings, pureData);

		Setting.MESSAGE_PREFIX = (String) this.settings.get(Setting.SETTINGS_MESSAGE_PREFIX);
		Setting.BACKUP_DELAY = (int) this.settings.get(Setting.SETTINGS_BACKUP_DELAY);
		Setting.DEBUG_MODE = (boolean) this.settings.get(Setting.SETTINGS_DEBUG_MODE);
		Setting.ISOLATED_CHAT = (boolean) this.settings.get(Setting.SETTINGS_ISOLATED_CHAT);
		Setting.ISOLATED_JOIN_QUIT_MESSAGE = (boolean) this.settings.get(Setting.SETTINGS_ISOLATED_JOIN_QUIT_MESSAGE);
		Setting.JOIN_SIGN_CAPTION = (String) this.settings.get(Setting.SETTINGS_JOIN_SIGN_CAPTION);
		Setting.LEAVE_SIGN_CAPTION = (String) this.settings.get(Setting.SETTINGS_LEAVE_SIGN_CAPTION);
		Setting.SCOREBOARD = (boolean) this.settings.get(Setting.SETTINGS_SCOREBOARD);
		Setting.SCOREBOARD_UPDATE_DELAY = (int) this.settings.get(Setting.SETTINGS_SCOREBOARD_UPDATE_DELAY);
		Setting.REMOVE_NOT_NECESSARY_KEYS = (boolean) this.settings.get(Setting.SETTINGS_REMOVE_NOT_NECESSARY_KEYS);
		Setting.MIN_LEAVE_TIME = (int) this.settings.get(Setting.SETTINGS_MIN_LEAVE_TIME);
		Setting.START_SOUND = Sound.valueOf(((String) this.settings.get(Setting.SETTINGS_START_SOUND)).toUpperCase());
		Setting.FINISH_SOUND = Sound.valueOf(((String) this.settings.get(Setting.SETTINGS_FINISH_SOUND)).toUpperCase());
		Setting.CHECK_UPDATE = (boolean) this.settings.get(Setting.SETTINGS_CHECK_UPDATE);
		Setting.EDIT_MESSAGES = (boolean) this.settings.get(Setting.SETTINGS_EDIT_MESSAGES);
		Setting.INGAME_LEAVE = (boolean) this.settings.get(Setting.SETTINGS_INGAME_LEAVE);
		Setting.TEMPLATE_WORLDS = (List<String>) this.settings.get(Setting.SETTINGS_TEMPLATE_WORLDS);
		Setting.JOIN_PRIORITY = MiniGameSetting.JOIN_PRIORITY
				.valueOf((String) this.settings.get(Setting.SETTINGS_JOIN_PRIORITY));
		Setting.PARTY_INVITE_TIMEOUT = (int) this.settings.get(Setting.SETTINGS_PARTY_INVITE_TIMEOUT);
		Setting.PARTY_ASK_TIMEOUT = (int) this.settings.get(Setting.SETTINGS_PARTY_ASK_TIMEOUT);

		// create "minigames" directory
		if (!MwUtil.getMiniGamesDir().exists()) {
			MwUtil.getMiniGamesDir().mkdir();
		}
	}

	/**
	 * Join a minigame with party members who are available to join with<br>
	 * - Join into already waiting instance game or create new game instance if need
	 * 
	 * @param p     Player who tries to join
	 * @param title Minigame title
	 * @return False if player failed to join
	 */
	public boolean joinGame(Player p, String rawTitle) {
		String title = ChatColor.stripColor(rawTitle);

		if (!canJoinGame(p, title)) {
			return false;
		}

		MiniGame templateGame = getTemplateGame(title);
		MiniGame instanceGame = null;

		Party party = this.partyManager.getPlayerParty(p);
		List<MiniGame> waitingGames = this.instanceGames.stream().filter(g -> g.title().equals(title))
				.filter(Predicate.not(MiniGame::isStarted)).filter(party::canJoinGame).toList();

		// create new game instance
		if (waitingGames.isEmpty()) {
			// check instance count
			int maxInstances = templateGame.setting().getInstances();
			if (maxInstances != -1 && countInstances(templateGame) >= maxInstances) {
				Utils.sendMsg(p, "Can not create more game instance");
				return false;
			}

			// check location
			if (!templateGame.locationManager().remainsExtra()) {
				Utils.sendMsg(p, "All game worlds are in using");
				return false;
			}

			// check party can join new instance game
			if (!party.canJoinGame(templateGame)) {
				Utils.sendMsg(p, "Your party(" + MwUtil.getInGamePlayers(party.getMembers(), true).size()
						+ ") is too big to join the minigame(" + templateGame.maxPlayers() + ")");
				return false;
			}

			// create new instance
			instanceGame = createGameInstance(templateGame);
			if (instanceGame == null) {
				return false;
			}
		}

		// join one of waiting(not started) games by join priority
		else {
			if (Setting.JOIN_PRIORITY == MiniGameSetting.JOIN_PRIORITY.MAX_PLAYERS) {
				instanceGame = waitingGames.stream().sorted((g1, g2) -> g2.playerCount() - g1.playerCount()).toList()
						.get(0);
			} else if (Setting.JOIN_PRIORITY == MiniGameSetting.JOIN_PRIORITY.MIN_PLAYERS) {
				instanceGame = waitingGames.stream().sorted((g1, g2) -> g1.playerCount() - g2.playerCount()).toList()
						.get(0);
			} else {// random
				instanceGame = CollectionTool.random(waitingGames).get();
			}
		}

		Utils.debug("[Game try to join]");
		Utils.debug("Title: " + instanceGame.title() + ", Id: " + instanceGame.setting().getId());
		return joinGame(p, title, instanceGame.setting().getId());
	}

	/**
	 * Join into minigame instance already created<br>
	 * (NEVER create new game instance)
	 * 
	 * @param p     Joining player
	 * @param title minigame title
	 * @param id    minigame id
	 * @return False if player failed to join
	 */
	public boolean joinGame(Player p, String title, String id) {
		title = ChatColor.stripColor(title);

		if (!canJoinGame(p, title)) {
			return false;
		}

		MiniGame templateGame = getTemplateGame(title);

		// search
		MiniGame instanceGame = getInstanceGame(title, id);

		// check instance game exists
		if (instanceGame == null) {
			Utils.debug("not exist");
			Utils.sendMsg(p, title + " minigame with the id does not exist");
			return false;
		}

		// check party members can join or not
		Party party = this.partyManager.getPlayerParty(p);
		List<Player> notInGameMembers = MwUtil.getInGamePlayers(party.getMembers(), true);
		if (!party.canJoinGame(instanceGame)) {
			Utils.sendMsg(p, "Your party(" + notInGameMembers.size() + ") is too big to join the minigame("
					+ templateGame.maxPlayers() + ")");
			return false;
		}

		// join with party member who is not playing or viewing a minigame now
		notInGameMembers.forEach(instanceGame::joinGame);
		return true;
	}

	private boolean canJoinGame(Player p, String title) {
		// check permission
		if (!Utils.checkPerm(p, "play.join")) {
			return false;
		}

		// strip "color code" of title
		title = ChatColor.stripColor(title);
		MiniGame templateGame = this.getTemplateGame(title);

		// check template game exists
		if (templateGame == null) {
			Utils.sendMsg(p, title + " minigame does not exist");
			return false;
		}

		// check the player is playing or viewing another minigame
		if (isInGame(p)) {
			Utils.sendMsg(p, "You are already playing or viewing another minigame");
			return false;
		}

		// check active
		if (!templateGame.isActive()) {
			Utils.sendMsg(p, title + " minigame is not active");
			return false;
		}

		return true;
	}

	/**
	 * Leave a minigame with party members in the same minigame
	 * 
	 * @param p Player who tries to leave
	 * @return False if player failed to leave
	 */
	public boolean leaveGame(Player p) {
		// check permission
		if (!Utils.checkPerm(p, "play.leave")) {
			return false;
		}

		// check player is playing minigame
		if (!isPlayingGame(p)) {
			Utils.sendMsg(p, "You're not playing any minigame");
			return false;
		}

		MiniGame playingGame = getPlayingGame(p);

		// check minigame is started
		if (playingGame.isStarted()) {
			// check "ingame-leave" option
			if (Setting.INGAME_LEAVE) {
				Utils.callEvent(new MiniGamePlayerExceptionEvent(Setting.PLAYER_EXCEPTION_INGAME_LEAVE, p));
			} else {
				Utils.sendMsg(p, "You can't leave game (Reason: already has started)");
			}
			return Setting.INGAME_LEAVE;
		}

		// check left waiting time
		if (playingGame.leftWaitingTime() <= Setting.MIN_LEAVE_TIME) {
			Utils.sendMsg(p, "You can't leave game (Reason: will start soon)");
			return false;
		}

		// leave with party members who is playing the same minigame with
		List<Player> members = this.partyManager.getMembers(p);
		members.stream().filter(playingGame::containsPlayer).forEach(playingGame::leaveGame);
		return true;
	}

	/**
	 * View random minigame (enter without party members)
	 * 
	 * @param p     Player who tries to view minigame
	 * @param title Minigame title
	 * @return False if player failed to view
	 */
	public boolean viewGame(Player p, String rawTitle) {
		String title = ChatColor.stripColor(rawTitle);

		// check active and view setting
		List<MiniGame> candidates = this.instanceGames.stream()
				.filter(g -> g.title().equals(title) && g.isActive() && g.setting().canView()).toList();

		if (candidates.isEmpty()) {
			Utils.sendMsg(p, "There is no available game instance to view");
			return false;
		}

		MiniGame randomGame = null;
		List<MiniGame> startedGames = candidates.stream().filter(g -> g.isStarted()).toList();
		if (startedGames.isEmpty()) {
			randomGame = CollectionTool.random(candidates).get();
		} else {
			randomGame = CollectionTool.random(startedGames).get();
		}
		return viewGame(p, title, randomGame.setting().getId());
	}

	/**
	 * View minigame (enter without party members)
	 * 
	 * @param p     Player who tries to view minigame
	 * @param title Minigame title
	 * @param id    Minigame id
	 * @return False if player failed to view
	 */
	public boolean viewGame(Player p, String title, String id) {
		title = ChatColor.stripColor(title);

		if (!canViewGame(p, title)) {
			return false;
		}

		MiniGame game = getInstanceGame(title, id);

		// check acitve
		if (!game.isActive()) {
			Utils.sendMsg(p, game.coloredTitle() + " game is not active");
			return false;
		}

		// check view setting
		if (!game.setting().canView()) {
			Utils.sendMsg(p, game.coloredTitle() + " game is not permitted to view");
			return false;
		}

		// send player as a viewer
		return game.viewManager().viewGame(p);
	}

	private boolean canViewGame(Player p, String title) {
		// check permission
		if (!Utils.checkPerm(p, "play.view")) {
			return false;
		}

		// strip "color code" of title
		title = ChatColor.stripColor(title);
		MiniGame templateGame = this.getTemplateGame(title);
		if (templateGame == null) {
			Utils.sendMsg(p, title + " minigame does not exist");
			return false;
		}

		// check a player is playing or viewing a minigame
		if (isInGame(p)) {
			Utils.sendMsg(p, "You are already playing or viewing another minigame");
			return false;
		}

		return true;
	}

	/**
	 * Unview(leave) from a minigame alone
	 * 
	 * @param p Player who tries to unview
	 * 
	 * @return False if player failed to unview
	 */
	public boolean unviewGame(Player p) {
		// check permission
		if (!Utils.checkPerm(p, "play.unview")) {
			return false;
		}

		if (!isViewingGame(p)) {
			Utils.sendMsg(p, "You're not viewing any minigame");
			return false;
		}

		// unview (leave) from the minigame
		MiniGame minigame = getViewingGame(p);
		return minigame.viewManager().unviewGame(p);
	}

	/**
	 * Make minigame start if can
	 * 
	 * @param title Minigame title
	 * @param id    Minigame id
	 * @return False if minigame failed to start
	 */
	public boolean startGame(String title, String id) {
		// strip "color code" of title
		title = ChatColor.stripColor(title);

		MiniGame templateGame = this.getTemplateGame(title);
		if (templateGame == null) {
			Utils.debug(title + " game is not exist");
			return false;
		}

		// search
		MiniGame instanceGame = getInstanceGame(title, id);

		// check instance game exists
		if (instanceGame == null) {
			Utils.debug(title + " minigame with the id does not exist");
			return false;
		}

		if (!instanceGame.isActive()) {
			Utils.debug("Minigame is not active");
			return false;
		}

		if (instanceGame.isStarted()) {
			Utils.debug("Already started");
			return false;
		}

		if (instanceGame.isEmpty()) {
			Utils.debug("There are no players");
			return false;
		}

		// start game
		return instanceGame.startGame();
	}

	public boolean handleException(MiniGameExceptionEvent exception) {
		// check event is player exception
		if (exception instanceof MiniGamePlayerExceptionEvent) {
			MiniGamePlayerExceptionEvent e = (MiniGamePlayerExceptionEvent) exception;
			Player p = e.getPlayer();

			if (!isInGame(p)) {
				return false;
			}

			// pass
			getInGame(p).handleException(exception);
		}

		// check event is server exception
		else if (exception instanceof MiniGameServerExceptionEvent) {
			// send to all games and make them finish the game
			this.instanceGames.forEach(m -> m.handleException(exception));
		} else { // MiniGameExceptionEvent (send to only instance equals)
			this.instanceGames.stream().filter(m -> m.equals(exception.getMiniGame()))
					.forEach(m -> m.handleException(exception));
		}

		return true;
	}

	/**
	 * Get playing instance minigame
	 * 
	 * @param p Player playing game
	 * @return Playing minigame
	 */
	public MiniGame getPlayingGame(Player p) {
		for (MiniGame game : this.instanceGames) {
			if (game.containsPlayer(p)) {
				return game;
			}
		}
		return null;
	}

	public boolean isPlayingGame(Player p) {
		return this.getPlayingGame(p) != null;
	}

	/**
	 * Get viewing instance minigame
	 * 
	 * @param p Player viewing game
	 * @return Viewing minigame
	 */
	public MiniGame getViewingGame(Player p) {
		for (MiniGame minigame : this.instanceGames) {
			if (minigame.viewManager().isViewing(p)) {
				return minigame;
			}
		}

		return null;
	}

	public boolean isViewingGame(Player p) {
		return this.getViewingGame(p) != null;
	}

	public MiniGame getInGame(Player p) {
		if (isPlayingGame(p)) {
			return getPlayingGame(p);
		} else if (isViewingGame(p)) {
			return getViewingGame(p);
		}
		return null;
	}

	/**
	 * Whether player is playing or viewing a minigame
	 * 
	 * @return True if player is playing or viewing minigame
	 */
	public boolean isInGame(Player p) {
		return isPlayingGame(p) || isViewingGame(p);
	}

	/**
	 * Get template minigame with title
	 * 
	 * @param Minigame title
	 * @return template minigame
	 */
	public MiniGame getTemplateGame(String title) {
		title = ChatColor.stripColor(title);

		for (MiniGame game : this.templateGames) {
			if (game.title().equals(title)) {
				return game;
			}
		}
		return null;
	}

	/**
	 * Get template minigame with class simple name <br>
	 * <b>[USAGE]</b><br>
	 * 
	 * <pre>
	 * getTemplateGame(Class.forName("GameA");
	 * </pre>
	 * 
	 * @param c Minigame class
	 * @return Null if there is no minigame matched
	 * @throws ClassNotFoundException
	 */
	public MiniGame getTemplateGame(Class<?> c) {
		for (MiniGame game : this.templateGames) {
			if (game.className().equals(c.getSimpleName())) {
				return game;
			}
		}
		return null;
	}

	/**
	 * Get instance game with title and minigame id
	 * 
	 * @param title Minigame title
	 * @param id    Minigame instance id
	 * @return Null if there is no minigame matched
	 * @throws ClassNotFoundException
	 */
	public MiniGame getInstanceGame(String title, String id) {
		title = ChatColor.stripColor(title);

		for (MiniGame game : this.instanceGames) {
			if (game.title().equals(title) && game.setting().getId().equals(id)) {
				return game;
			}
		}
		return null;
	}

	/**
	 * Get instance game with class simple name and minigame id
	 * 
	 * @param className Minigame class name
	 * @param id        Minigame instance id
	 * @return Null if there is no minigame matched
	 */
	public MiniGame getInstanceGame(Class<?> c, String id) {
		for (MiniGame game : this.instanceGames) {
			if (game.className().equals(c.getSimpleName()) && game.setting().getId().equals(id)) {
				return game;
			}
		}
		return null;
	}

	/**
	 * Register template minigame
	 * 
	 * @param templateGame Template minigame
	 * @return False if the same minigame was registered already
	 */
	public boolean registerTemplateGame(MiniGame templateGame) {
		// check class has no argument constructor
		try {
			templateGame.getClass().getDeclaredConstructor();
		} catch (NoSuchMethodException e) {
			Utils.warning(templateGame.titleWithClassName() + ChatColor.RED
					+ " can not be registered (Class doesn't have no argument constructor for game instance system)");
			return false;
		}

		// can not register minigame which has same class name with others
		if (this.existTemplateGame(templateGame)) {
			Utils.warning(templateGame.titleWithClassName() + ChatColor.RED
					+ " can not be registered (Same template minigame is already registered)");
			return false;
		}

		// register member to YamlManager
		this.yamlManager.registerMember(templateGame.dataManager());

		// check data already exists or not
		if (templateGame.dataManager().isMinigameDataExists()) {
			templateGame.dataManager().applyMiniGameDataToInstance();
		} else {
			templateGame.dataManager().createMiniGameData();
		}

		// save config directly for first load (data saved in config)
		this.yamlManager.save(templateGame.dataManager());

		// add to minigame list
		this.templateGames.add(templateGame);

		// notify minigame registration to observers
		notifyObservers(templateGame, Timing.REGISTRATION);

		Utils.info("" + ChatColor.GREEN + ChatColor.BOLD + templateGame.titleWithClassName() + ChatColor.RESET
				+ " minigame is registered");
		return true;
	}

	/**
	 * Check the same template minigmae exists or not
	 * 
	 * @param templateGame
	 * @return True if the same minigame exists
	 */
	private boolean existTemplateGame(MiniGame templateGame) {
		// can not register minigame which has same class name with others
		String newGameClassName = templateGame.className();
		for (MiniGame game : this.templateGames) {
			String existGameClassName = game.className();
			// distinguish with MiniGame class name
			if (existGameClassName.equalsIgnoreCase(newGameClassName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Unregister template minigame
	 * 
	 * @param templateGame to unregister
	 * @return False if the minigame doesn't exist
	 */
	public boolean unregisterTemplateGame(MiniGame templateGame) {
		if (!this.templateGames.contains(templateGame)) {
			return false;
		}

		// save and unregister minigame from yaml manager
		this.yamlManager.save(templateGame.dataManager());
		this.yamlManager.unregisterMember(templateGame.dataManager());

		// unregister
		this.templateGames.remove(templateGame);
		Utils.info("" + ChatColor.RED + ChatColor.BOLD + templateGame.titleWithClassName() + ChatColor.RESET
				+ " minigame is unregistered");

		// notify minigame unregistration to observers
		notifyObservers(templateGame, Timing.UNREGISTRATION);
		return true;
	}

	public void removeNotExistGameData() {
		// remove deleted minigame before save minigames.yml file
		List<String> removedGames = new ArrayList<String>();

		List<String> minigameStringList = new ArrayList<String>();
		this.templateGames.forEach(m -> minigameStringList.add(m.className()));

		File minigamesFolder = MwUtil.getMiniGamesDir();
		for (File minigameFile : minigamesFolder.listFiles()) {
			String flieName = Files.getNameWithoutExtension(minigameFile.getName());
			if (!minigameStringList.contains(flieName)) {
				// remove file
				minigameFile.delete();
				// add removed list
				removedGames.add(minigameFile.getName());
			}
		}

		if (!removedGames.isEmpty()) {
			Utils.info("" + ChatColor.RED + ChatColor.BOLD + "[ Removed MiniGame List ]");
			for (String removedGameTitle : removedGames) {
				Utils.info(ChatColor.RED + removedGameTitle + " file is deleted");
			}
		}
	}

	/**
	 * Create a new instance of template(registered) minigame
	 * 
	 * @param templateGame template minigame
	 * @return Null if exception occurs
	 */
	public MiniGame createGameInstance(MiniGame templateGame) {
		// call instance create event before creating
		if (Utils.callEvent(new MiniGameInstanceCreateEvent(templateGame))) {
			return null;
		}

		MiniGame newInstance = null;
		Exception exception = null;
		try {
			// create instance
			newInstance = templateGame.getClass().getDeclaredConstructor().newInstance();

			// apply template minigame data to new instance data
			updateInstanceGameData(newInstance);

			// register game event handler
			this.gameListenerManager.registerGameListener(newInstance);
			this.gameListenerManager.registerGameListener(newInstance.customOption());
			this.gameListenerManager.registerGameListener(newInstance.inventoryManager());

			// add instance to the list
			this.instanceGames.add(newInstance);
		} catch (NoSuchMethodException e) {
			Utils.warning(templateGame.titleWithClassName()
					+ " doesn't have no arguments constructor! (Set \"debug-mode\" in settings.yml to true for details)");
			exception = e;
		} catch (SecurityException e) {
			Utils.warning("Security Exception (Set \"debug-mode\" in settings.yml to true for details)");
			exception = e;
		} catch (Exception e) {
			Utils.warning("ETC Exception (Set \"debug-mode\" in settings.yml to true for details)");
			exception = e;
		}

		if (exception != null && Setting.DEBUG_MODE) {
			exception.printStackTrace();
		} else {
			Utils.debug(templateGame.titleWithClassName() + " instance created");
		}

		return newInstance;
	}

	/**
	 * Update instance game data with the template game data
	 * 
	 * @param instance game which will be updated
	 */
	public void updateInstanceGameData(MiniGame instance) {
		MiniGame templateGame = getTemplateGame(instance.getClass());
		instance.dataManager().setData(templateGame.dataManager().getData());
		instance.dataManager().applyMiniGameDataToInstance();
	}

	public void removeGameInstance(MiniGame instance) {
		// [IMPORTANT] must called after all players left the location
		instance.locationManager().reset();

		// unregister game event handlers
		this.gameListenerManager.unregisterGameListener(instance);
		this.gameListenerManager.unregisterGameListener(instance.customOption());
		this.gameListenerManager.unregisterGameListener(instance.inventoryManager());

		// remove
		this.instanceGames.remove(instance);

		// call after instance removed
		Utils.callEvent(new MiniGameInstanceRemoveEvent(instance));
	}

	public int countInstances(MiniGame game) {
		return this.instanceGames.stream().filter(game::equals).toList().size();
	}

	public List<MiniGame> getTemplateGames() {
		return this.templateGames;
	}

	public List<MiniGame> getInstanceGames() {
		return this.instanceGames;
	}

	public Map<String, Object> getSettings() {
		return this.settings;
	}

	public MiniGameMenuManager getMenuManager() {
		return this.menuManager;
	}

	public PartyManager getPartyManager() {
		return this.partyManager;
	}

	public MiniGameEventDetector getEventDetector() {
		return this.eventDetector;
	}

	public GameListenerManager getGameListenerManager() {
		return this.gameListenerManager;
	}

	public YamlManager getYamlManager() {
		return this.yamlManager;
	}

	@Override
	public void setData(YamlManager yamlM, FileConfiguration config) {
		this.yamlManager = yamlM;

		// sync config setting with variable setting
		if (config.isSet("settings")) {
			this.settings = YamlHelper.ObjectToMap(config.getConfigurationSection("settings"));
		}
		config.set("settings", this.settings);

		// check setting has basic values
		this.initSettingData();
	}

	@Override
	public String getFileName() {
		return "settings.yml";
	}

	@Override
	public void registerObserver(MiniGameObserver observer) {
		if (!this.observers.contains(observer)) {
			// register observer to former minigames
			this.templateGames.forEach(m -> {
				// notify registration of former minigames
				observer.update(new MiniGameAccessor(m), Timing.REGISTRATION);
			});

			this.observers.add(observer);
		}
	}

	@Override
	public void unregisterObserver(MiniGameObserver observer) {
		this.observers.remove(observer);
	}

	@Override
	public void notifyObservers(MiniGame minigame, Timing event) {
		this.observers.forEach(obs -> obs.update(new MiniGameAccessor(minigame), event));
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
//
//
//
