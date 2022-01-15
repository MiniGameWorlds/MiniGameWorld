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
import com.wbm.plugin.util.CollectionTool;
import com.wbm.plugin.util.data.yaml.YamlHelper;
import com.wbm.plugin.util.data.yaml.YamlManager;
import com.wbm.plugin.util.data.yaml.YamlMember;
import com.worldbiomusic.minigameworld.api.MiniGameAccessor;
import com.worldbiomusic.minigameworld.api.observer.MiniGameEventNotifier;
import com.worldbiomusic.minigameworld.api.observer.MiniGameObserver;
import com.worldbiomusic.minigameworld.managers.menu.MiniGameMenuManager;
import com.worldbiomusic.minigameworld.managers.party.PartyManager;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameEventDetector;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameViewManager;
import com.worldbiomusic.minigameworld.util.Setting;
import com.worldbiomusic.minigameworld.util.Utils;

public class MiniGameManager implements YamlMember, MiniGameEventNotifier {
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
		this.partyManager = new PartyManager(this);
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
	 * Set basic setting.yml data
	 */
	private void initSettingData() {
		Map<String, Object> pureData = new LinkedHashMap<>();
		pureData.put(Setting.SETTINGS_MESSAGE_PREFIX, Utils.messagePrefix);
		pureData.put(Setting.SETTINGS_BACKUP_DATA_SAVE_DELAY, Setting.BACKUP_DATA_SAVE_DELAY);
		pureData.put(Setting.SETTINGS_MINIGAME_SIGN, true);
		pureData.put(Setting.SETTINGS_DEBUG_MODE, Setting.DEBUG_MODE);
		pureData.put(Setting.SETTINGS_ISOLATED_CHAT, Setting.ISOLATED_CHAT);
		pureData.put(Setting.SETTINGS_ISOLATED_JOIN_QUIT_MESSAGE, Setting.ISOLATED_JOIN_QUIT_MESSAGE);

		syncMapKeys(this.settings, pureData);

		Utils.messagePrefix = (String) this.settings.get(Setting.SETTINGS_MESSAGE_PREFIX);
		Setting.BACKUP_DATA_SAVE_DELAY = (int) this.settings.get(Setting.SETTINGS_BACKUP_DATA_SAVE_DELAY);
		Setting.DEBUG_MODE = (boolean) this.settings.get(Setting.SETTINGS_DEBUG_MODE);
		Setting.ISOLATED_CHAT = (boolean) this.settings.get(Setting.SETTINGS_ISOLATED_CHAT);
		Setting.ISOLATED_JOIN_QUIT_MESSAGE = (boolean) this.settings.get(Setting.SETTINGS_ISOLATED_JOIN_QUIT_MESSAGE);

		// create "minigames" directory
		if (!Utils.getMiniGamesFolder().exists()) {
			Utils.getMiniGamesFolder().mkdir();
		}
	}

	private void syncMapKeys(Map<String, Object> configMap, Map<String, Object> pureMap) {
		// remove not necessary keys to avoid error (i.e. keys for some updates)
		CollectionTool.removeNotNecessaryKeys(configMap, pureMap);

		// Restore before apply to avoid error (i.e. keys for some updates)
		CollectionTool.restoreMissedKeys(configMap, pureMap);

		// sync map keys
		CollectionTool.syncKeyOrder(configMap, pureMap);
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

		if (!this.partyManager.canPartyJoin(p, game)) {
			return;
		}

		// check a player is playing or viewing a minigame
		MiniGameViewManager viewManager = game.getViewManager();
		if (isPlayingMiniGame(p) || viewManager.isViewing(p)) {
			Utils.sendMsg(p, "You are already playing or viewing another minigame");
			return;
		}

		// notify message to party members
		if (this.partyManager.getPlayerParty(p).getSize() > 1) {
			this.partyManager.sendMessageToPlayerPartyMembers(p, p.getName() + " joined minigame with party");
		}

		List<Player> members = this.partyManager.getMembers(p);
		// join with party member who is not playing or viewing a minigame now
		for (Player member : members) {
			if (!(isPlayingMiniGame(member) || isViewingMiniGame(member))) {
				// join
				game.joinGame(member);
			}
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

		// leave party members
		List<Player> members = this.partyManager.getMembers(p);

		// check player is playing minigame
		if (!isPlayingMiniGame(p)) {
			Utils.sendMsg(p, "You're not playing a minigame");
			return;
		}

		// notify message to party members
		if (this.partyManager.getPlayerParty(p).getSize() > 1) {
			this.partyManager.sendMessageToPlayerPartyMembers(p, p.getName() + " left minigame with party");
		}

		MiniGame playingGame = getPlayingMiniGame(p);
		for (Player member : members) {
			// leave with members who is playing the same minigame with "p"
			if (playingGame.equals(getPlayingMiniGame(member))) {
				playingGame.leaveGame(member);
			}
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

		// check a player is playing or viewing a minigame
		MiniGameViewManager viewManager = game.getViewManager();
		if (isPlayingMiniGame(p) || viewManager.isViewing(p)) {
			Utils.sendMsg(p, "You are already playing or viewing another minigame");
			return;
		}

		// add the player as a viewer
		game.getViewManager().addViewer(p);
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

		// unview (leave) from a minigame
		MiniGame minigame = getViewingMiniGame(p);
		minigame.getViewManager().removeViewer(p);
	}

	public void createException(Player p, MiniGame.Exception exception) {
		// check player is playing a minigame
		if (this.isPlayingMiniGame(p)) {
			MiniGame playingGame = this.getPlayingMiniGame(p);
			playingGame.handleException(p, exception);
		}
	}

	/*
	 * - check player is playing minigame and process event to minigame
	*/
	public void passEvent(Event e) {
		// check server down
		if (checkPluginStartToBeDisabled(e)) {
			return;
		}

		if (passEventToViewManager(e)) {
			return;
		}

		// detect event
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
				if (this.isPlayingMiniGame(p)) {
					MiniGame playingGame = this.getPlayingMiniGame(p);
					playingGame.passEvent(e);
				}
			}
		} else {
			// pass undetectable event to MiniGame which permit
			this.passUndetectableEventToMiniGame(e);
		}
	}

	private boolean checkPluginStartToBeDisabled(Event e) {
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

	private void passUndetectableEventToMiniGame(Event e) {
		for (MiniGame minigame : this.minigames) {
			if (minigame.getSetting().isPassUndetectableEvent()) {
				minigame.passEvent(e);
			}
		}
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

		// register missed observers
		registerMissedMinigameObservers(newGame);

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
		notifyObservers(newGame, MiniGameEvent.REGISTRATION);

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
			notifyObservers(minigame, MiniGameEvent.UNREGISTRATION);
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

		File minigamesFolder = Utils.getMiniGamesFolder();
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

	public int getNonPlayingPlayerCount(List<Player> players) {
		int Count = 0;
		for (Player p : players) {
			if (!this.isPlayingMiniGame(p)) {
				Count += 1;
			}
		}
		return Count;
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

	/**
	 * Register observer to minigames which will be registered in the future
	 * 
	 * @param newGame Minigame to register observer
	 */
	private void registerMissedMinigameObservers(MiniGame newGame) {
		this.observers.forEach(obs -> newGame.registerObserver(obs));
	}

	@Override
	public void registerObserver(MiniGameObserver observer) {
		// register observer to former minigames
		this.minigames.forEach(m -> {
			m.registerObserver(observer);

			// notify registration of former minigames
			observer.update(new MiniGameAccessor(m), MiniGameEvent.REGISTRATION);
		});

		if (!this.observers.contains(observer)) {
			this.observers.add(observer);
		}
	}

	@Override
	public void unregisterObserver(MiniGameObserver observer) {
		this.minigames.forEach(m -> m.unregisterObserver(observer));

		this.observers.remove(observer);
	}

	@Override
	public void notifyObservers(MiniGame minigame, MiniGameEvent event) {
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
