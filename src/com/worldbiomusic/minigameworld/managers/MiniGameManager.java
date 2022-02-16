package com.worldbiomusic.minigameworld.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.PluginDisableEvent;

import com.google.common.io.Files;
import com.wbm.plugin.util.data.yaml.YamlHelper;
import com.wbm.plugin.util.data.yaml.YamlManager;
import com.wbm.plugin.util.data.yaml.YamlMember;
import com.worldbiomusic.minigameworld.api.MiniGameAccessor;
import com.worldbiomusic.minigameworld.api.MiniGameWorldUtils;
import com.worldbiomusic.minigameworld.api.observer.MiniGameObserver;
import com.worldbiomusic.minigameworld.api.observer.MiniGameTimingNotifier;
import com.worldbiomusic.minigameworld.commands.MiniGameMinigamesConfigCommand;
import com.worldbiomusic.minigameworld.customevents.minigame.MiniGameExceptionEvent;
import com.worldbiomusic.minigameworld.customevents.minigame.MiniGamePlayerExceptionEvent;
import com.worldbiomusic.minigameworld.customevents.minigame.MiniGameServerExceptionEvent;
import com.worldbiomusic.minigameworld.managers.menu.MiniGameMenuManager;
import com.worldbiomusic.minigameworld.managers.party.Party;
import com.worldbiomusic.minigameworld.managers.party.PartyManager;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameEventDetector;
import com.worldbiomusic.minigameworld.util.Setting;
import com.worldbiomusic.minigameworld.util.Utils;

public class MiniGameManager implements YamlMember, MiniGameTimingNotifier {
	// Singleton
	private static MiniGameManager instance = new MiniGameManager();
	private static boolean instanceCreated = false;

	// MiniGame List
	private List<MiniGame> minigames;

	// setting.yml
	private Map<String, Object> settings;

	// event detector
	private MiniGameEventDetector minigameEventDetector;

	// minigame gui manager
	private MiniGameMenuManager guiManager;

	// party
	private PartyManager partyManager;

	// yaml data manager
	private YamlManager yamlManager;

	// Minigame Observer
	private List<MiniGameObserver> observers;

	// use getInstance()
	private MiniGameManager() {
		// init
		this.minigames = new ArrayList<>();
		this.settings = new LinkedHashMap<String, Object>();
		this.initSettingData();
		this.minigameEventDetector = new MiniGameEventDetector(this);

		this.guiManager = new MiniGameMenuManager(this);
		this.partyManager = new PartyManager();
		this.observers = new ArrayList<>();
	}

	public void processPlayerJoinWorks(Player p) {
		this.getPartyManager().createParty(p);
	}

	public void processPlayerQuitWorks(Player p) {
		// party
		this.getPartyManager().leaveParty(p);
		this.getPartyManager().deleteParty(p);
	}

	public static MiniGameManager getInstance() {
		if (instanceCreated == false) {
			instanceCreated = true;
			return instance;
		}
		return null;
	}

	/**
	 * Set basic setting.yml data<br>
	 * [IMPORTANT]<br>
	 * - If add option, add access method to
	 * {@link MiniGameMinigamesConfigCommand}<br>
	 */
	private void initSettingData() {
		Map<String, Object> pureData = new LinkedHashMap<>();
		pureData.put(Setting.SETTINGS_MESSAGE_PREFIX, Utils.messagePrefix);
		pureData.put(Setting.SETTINGS_BACKUP_DATA_SAVE_DELAY, Setting.BACKUP_DATA_SAVE_DELAY);
		pureData.put(Setting.SETTINGS_DEBUG_MODE, Setting.DEBUG_MODE);
		pureData.put(Setting.SETTINGS_ISOLATED_CHAT, Setting.ISOLATED_CHAT);
		pureData.put(Setting.SETTINGS_ISOLATED_JOIN_QUIT_MESSAGE, Setting.ISOLATED_JOIN_QUIT_MESSAGE);
		pureData.put(Setting.SETTINGS_JOIN_SIGN_CAPTION, Setting.JOIN_SIGN_CAPTION);
		pureData.put(Setting.SETTINGS_LEAVE_SIGN_CAPTION, Setting.LEAVE_SIGN_CAPTION);
		pureData.put(Setting.SETTINGS_SCOREBOARD, Setting.SCOREBOARD);
		pureData.put(Setting.SETTINGS_SCOREBOARD_UPDATE_DELAY, Setting.SCOREBOARD_UPDATE_DELAY);
		pureData.put(Setting.SETTINGS_REMOVE_NOT_NECESSARY_KEYS, Setting.REMOVE_NOT_NECESSARY_KEYS);

		Utils.syncMapKeys(this.settings, pureData);

		Utils.messagePrefix = (String) this.settings.get(Setting.SETTINGS_MESSAGE_PREFIX);
		Setting.BACKUP_DATA_SAVE_DELAY = (int) this.settings.get(Setting.SETTINGS_BACKUP_DATA_SAVE_DELAY);
		Setting.DEBUG_MODE = (boolean) this.settings.get(Setting.SETTINGS_DEBUG_MODE);
		Setting.ISOLATED_CHAT = (boolean) this.settings.get(Setting.SETTINGS_ISOLATED_CHAT);
		Setting.ISOLATED_JOIN_QUIT_MESSAGE = (boolean) this.settings.get(Setting.SETTINGS_ISOLATED_JOIN_QUIT_MESSAGE);
		Setting.JOIN_SIGN_CAPTION = (String) this.settings.get(Setting.SETTINGS_JOIN_SIGN_CAPTION);
		Setting.LEAVE_SIGN_CAPTION = (String) this.settings.get(Setting.SETTINGS_LEAVE_SIGN_CAPTION);
		Setting.SCOREBOARD = (boolean) this.settings.get(Setting.SETTINGS_SCOREBOARD);
		Setting.SCOREBOARD_UPDATE_DELAY = (int) this.settings.get(Setting.SETTINGS_SCOREBOARD_UPDATE_DELAY);
		Setting.REMOVE_NOT_NECESSARY_KEYS = (boolean) this.settings.get(Setting.SETTINGS_REMOVE_NOT_NECESSARY_KEYS);

		// create "minigames" directory
		if (!MiniGameWorldUtils.getMiniGamesDirectory().exists()) {
			MiniGameWorldUtils.getMiniGamesDirectory().mkdir();
		}
	}

	/**
	 * Join a minigame with party members who are available to join with
	 * 
	 * @param p     Player who tries to join
	 * @param title Minigame title
	 */
	public void joinGame(Player p, String title) {
		// check permission
		if (!Utils.checkPerm(p, "play.join")) {
			return;
		}

		// strip "color code" from title
		title = ChatColor.stripColor(title);
		MiniGame game = this.getMiniGameWithTitle(title);
		if (game == null) {
			Utils.sendMsg(p, title + " minigame does not exist");
			return;
		}

		// check a player is playing or viewing a minigame
		if (isInMiniGame(p)) {
			Utils.sendMsg(p, "You are already playing or viewing another minigame");
			return;
		}

		if (!game.isActive()) {
			Utils.sendMsg(p, "Minigame is not active");
			return;
		}

		if (game.isStarted()) {
			Utils.sendMsg(p, "Already started");
			return;
		}

		if (game.isFull()) {
			Utils.sendMsg(p, "Player is full");
			return;
		}

		Party party = this.partyManager.getPlayerParty(p);
		if (!party.canJoinMiniGame(game)) {
			Utils.sendMsg(p, "Your party is too big to join the minigame together");
			return;
		}

		// join with party member who is not playing or viewing a minigame now
		List<Player> notInMiniGameMembers = MiniGameWorldUtils.getNotInMiniGamePlayers(party.getMembers());
		notInMiniGameMembers.forEach(game::joinGame);

		// notify message to party members
		if (party.getSize() > 1) {
			party.sendMessageToAllMembers(p.getName() + " joined minigame with party");
		}
	}

	/**
	 * Leave a minigame with party members in the same minigame
	 * 
	 * @param p Player who tries to leave
	 */
	public void leaveGame(Player p) {
		// check permission
		if (!Utils.checkPerm(p, "play.leave")) {
			return;
		}

		// check player is playing minigame
		if (!isPlayingMiniGame(p)) {
			Utils.sendMsg(p, "You're not playing any minigame to leave");
			return;
		}

		MiniGame playingGame = getPlayingMiniGame(p);

		// check minigame is started
		if (playingGame.isStarted()) {
			Utils.sendMsg(p, "You can't leave game (Reason: already has started)");
			return;
		}

		// check left waiting time
		if (playingGame.getLeftWaitingTime() <= Setting.MINIGAME_MIN_LEAVE_TIME) {
			Utils.sendMsg(p, "You can't leave game (Reason: will start soon)");
			return;
		}

		// leave party members
		List<Player> members = this.partyManager.getMembers(p);
		for (Player member : members) {
			// leave with members who is playing the same minigame with "p"
			if (playingGame.equals(getPlayingMiniGame(member))) {
				playingGame.leaveGame(member);
			}
		}

		// notify message to party members
		if (this.partyManager.getPlayerParty(p).getSize() > 1) {
			this.partyManager.sendMessageToPlayerPartyMembers(p, p.getName() + " left minigame with party");
		}
	}

	/**
	 * View a minigame alone (without party members)
	 * 
	 * @param p     Player who tries to view minigame
	 * @param title Minigame title
	 */
	public void viewGame(Player p, String title) {
		// check permission
		if (!Utils.checkPerm(p, "play.view")) {
			return;
		}

		// strip "color code" from title
		title = ChatColor.stripColor(title);
		MiniGame game = this.getMiniGameWithTitle(title);
		if (game == null) {
			Utils.sendMsg(p, title + " minigame does not exist");
			return;
		}

		// return if minigame is not active
		if (!game.isActive()) {
			Utils.sendMsg(p, "Minigame is not acitve");
			return;
		}

		// check minigame view setting value
		if (!game.getSetting().canView()) {
			Utils.sendMsg(p, "You can't view this minigame");
			return;
		}

		// check a player is playing or viewing a minigame
		if (isInMiniGame(p)) {
			Utils.sendMsg(p, "You are already playing or viewing another minigame");
			return;
		}

		// add the player as a viewer
		game.getViewManager().viewGame(p);
	}

	/**
	 * Unview(leave) from a minigame alone
	 * 
	 * @param p Player who tries to unview
	 */
	public void unviewGame(Player p) {
		// check permission
		if (!Utils.checkPerm(p, "play.unview")) {
			return;
		}

		if (!isViewingMiniGame(p)) {
			Utils.sendMsg(p, "You're not viewing a minigame");
			return;
		}

		// unview (leave) from the minigame
		MiniGame minigame = getViewingMiniGame(p);
		minigame.getViewManager().unviewGame(p);
	}

	public void handleException(MiniGameExceptionEvent exception) {
		// check event is player exception
		if (exception instanceof MiniGamePlayerExceptionEvent) {
			MiniGamePlayerExceptionEvent e = (MiniGamePlayerExceptionEvent) exception;
			Player p = e.getPlayer();

			if (!isInMiniGame(p)) {
				return;
			}

			// get minigame
			MiniGame minigame = null;
			if (isPlayingMiniGame(p)) {
				minigame = getPlayingMiniGame(p);
			} else if (isViewingMiniGame(p)) {
				minigame = getViewingMiniGame(p);
			}

			minigame.handleException(exception);
		}

		// check event is server exception
		else if (exception instanceof MiniGameServerExceptionEvent) {
			// send to all minigames and make finish game
			this.minigames.forEach(m -> m.handleException(exception));
		} else {
			this.minigames.stream().filter(m -> m.equals(exception.getMiniGame()))
					.forEach(m -> m.handleException(exception));
		}
	}

	/*
	 * - check player is playing minigame and process event to minigame
	*/
	public void passEvent(Event e) {
		// check server down
		if (checkPluginIsDisabled(e)) {
			return;
		}

		if (passEventToViewManager(e)) {
			return;
		}

		// check detectable event
		if (this.minigameEventDetector.isDetectableEvent(e)) {
			// get players
			Set<Player> players = this.minigameEventDetector.getPlayersFromEvent(e);

			// check empty
			if (players.isEmpty()) {
				return;
			}

			// pass evnet to minigame
			for (Player p : players) {
				// check player is playing minigame
				if (!this.isPlayingMiniGame(p)) {
					return;
				}

				MiniGame playingGame = this.getPlayingMiniGame(p);

				// check use of basic event detector
				if (!playingGame.getSetting().isUseEventDetector()) {
					return;
				}

				playingGame.passEvent(e);
			}
		} else {
			checkCustomDetectableEvent(e);
		}
	}

	private boolean checkPluginIsDisabled(Event e) {
		if (e instanceof PluginDisableEvent) {
			this.minigames.forEach(m -> {
				m.finishGame();
			});
			return true;
		}
		return false;
	}

	private boolean passEventToViewManager(Event event) {
		Player p = null;
		if (event instanceof AsyncPlayerChatEvent) {
			p = ((AsyncPlayerChatEvent) event).getPlayer();
		} else if (event instanceof PlayerRespawnEvent) {
			p = ((PlayerRespawnEvent) event).getPlayer();
		} else if (event instanceof EntityDamageEvent) {
			EntityDamageEvent e = (EntityDamageEvent) event;
			if (e.getEntity() instanceof Player) {
				p = (Player) e.getEntity();
			}
		}

		// check null
		if (p == null) {
			return false;
		}

		// check player is a viewer
		if (!isViewingMiniGame(p)) {
			return false;
		}

		// pass event to view manager
		MiniGame minigame = getViewingMiniGame(p);
		minigame.getViewManager().processEvent(event);
		return true;
	}

	public void checkCustomDetectableEvent(Event e) {
		this.minigames.stream().filter(m -> m.getSetting().isCustomDetectableEvent(e.getClass()))
				.forEach(m -> m.passEvent(e));
	}

	public boolean isPlayingMiniGame(Player p) {
		return this.getPlayingMiniGame(p) != null;
	}

	public MiniGame getViewingMiniGame(Player p) {
		for (MiniGame minigame : this.minigames) {
			if (minigame.getViewManager().isViewing(p)) {
				return minigame;
			}
		}

		return null;
	}

	public boolean isViewingMiniGame(Player p) {
		return this.getViewingMiniGame(p) != null;
	}

	public MiniGame getInMiniGame(Player p) {
		if (isPlayingMiniGame(p)) {
			return getPlayingMiniGame(p);
		} else if (isViewingMiniGame(p)) {
			return getViewingMiniGame(p);
		}

		return null;
	}

	/**
	 * Whether player is playing or viewing a minigame
	 * 
	 * @return True if player is playing or viewing minigame
	 */
	public boolean isInMiniGame(Player p) {
		return isPlayingMiniGame(p) || isViewingMiniGame(p);
	}

	public MiniGame getMiniGameWithTitle(String title) {
		for (MiniGame game : this.minigames) {
			if (game.getTitle().equalsIgnoreCase(title)) {
				return game;
			}
		}
		return null;
	}

	public MiniGame getMiniGameWithClassName(String className) {
		for (MiniGame game : this.minigames) {
			if (game.getClassName().equalsIgnoreCase(className)) {
				return game;
			}
		}
		return null;
	}

	public MiniGame getPlayingMiniGame(Player p) {
		for (MiniGame game : this.minigames) {
			if (game.containsPlayer(p)) {
				return game;
			}
		}
		return null;
	}

	public List<MiniGame> getMiniGameList() {
		return this.minigames;
	}

	public boolean registerMiniGame(MiniGame newGame) {
		// can not register minigame which has same class name with others
		if (this.hasSameMiniGame(newGame)) {
			return false;
		}

		// reigster member to YamlManager
		this.yamlManager.registerMember(newGame.getDataManager());

		// check already existing data
		if (newGame.getDataManager().isMinigameDataExists()) {
			newGame.getDataManager().applyMiniGameDataToInstance();
		} else {
			newGame.getDataManager().createMiniGameData();
		}

		// save config directly for first load (data saved in config)
		this.yamlManager.save(newGame.getDataManager());

		// add to minigame list
		this.minigames.add(newGame);

		// notify minigame registration to observers
		notifyObservers(newGame, Timing.REGISTRATION);

		Utils.info("" + ChatColor.GREEN + ChatColor.BOLD + newGame.getTitleWithClassName() + ChatColor.RESET
				+ " minigame is registered");
		return true;
	}

	private boolean hasSameMiniGame(MiniGame newGame) {
		// can not register minigame which has same class name with others
		String newGameClassName = newGame.getClassName();
		for (MiniGame game : this.minigames) {
			String existGameClassName = game.getClassName();
			// distinguish with MiniGame class name
			if (existGameClassName.equalsIgnoreCase(newGameClassName)) {
				Utils.warning(newGameClassName + " can not be registered");
				Utils.warning("The same " + game.getTitleWithClassName() + " minigame is already registered");
				return true;
			}
		}
		return false;
	}

	public boolean unregisterMiniGame(MiniGame minigame) {
		if (this.minigames.remove(minigame)) {
			Utils.info(minigame.getTitleWithClassName() + " minigame is removed");

			// notify minigame unregistration to observers
			notifyObservers(minigame, Timing.UNREGISTRATION);
			return true;
		} else {
			return false;
		}
	}

	public void removeNotExistMiniGameData() {
		// remove deleted minigame before save minigames.yml file
		List<String> removedGames = new ArrayList<String>();

		List<String> minigameStringList = new ArrayList<String>();
		this.minigames.forEach(m -> minigameStringList.add(m.getClassName()));

		File minigamesFolder = MiniGameWorldUtils.getMiniGamesDirectory();
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

	public Map<String, Object> getSettings() {
		return this.settings;
	}

	public MiniGameMenuManager getMiniGameMenuManager() {
		return this.guiManager;
	}

	public PartyManager getPartyManager() {
		return this.partyManager;
	}

	public MiniGameEventDetector getMiniGameEventDetector() {
		return this.minigameEventDetector;
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
			this.minigames.forEach(m -> {
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
