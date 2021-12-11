package com.worldbiomusic.minigameworld.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.server.PluginDisableEvent;

import com.google.common.io.Files;
import com.wbm.plugin.util.data.yaml.YamlHelper;
import com.wbm.plugin.util.data.yaml.YamlManager;
import com.wbm.plugin.util.data.yaml.YamlMember;
import com.worldbiomusic.minigameworld.api.MiniGameAccessor;
import com.worldbiomusic.minigameworld.managers.menu.MiniGameMenuManager;
import com.worldbiomusic.minigameworld.managers.party.PartyManager;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameEventDetector;
import com.worldbiomusic.minigameworld.observer.MiniGameEventNotifier;
import com.worldbiomusic.minigameworld.observer.MiniGameObserver;
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
		this.minigameEventDetector = new MiniGameEventDetector();

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

		// minigameSign
		if (!this.settings.containsKey(Setting.SETTINGS_MINIGAME_SIGN)) {
			this.settings.put(Setting.SETTINGS_MINIGAME_SIGN, true);
		}

		// messagePrefix
		if (!this.settings.containsKey(Setting.SETTINGS_MESSAGE_PREFIX)) {
			this.settings.put(Setting.SETTINGS_MESSAGE_PREFIX, Utils.messagePrefix);
		}
		Utils.messagePrefix = (String) this.settings.get(Setting.SETTINGS_MESSAGE_PREFIX);

		// debug-mode
		if (!this.settings.containsKey(Setting.SETTINGS_DEBUG_MODE)) {
			this.settings.put(Setting.SETTINGS_DEBUG_MODE, Setting.DEBUG_MODE);
		}
		Setting.DEBUG_MODE = (boolean) this.settings.get(Setting.SETTINGS_DEBUG_MODE);

		// backup-data-save-delay
		if (!this.settings.containsKey(Setting.SETTINGS_BACKUP_DATA_SAVE_DELAY)) {
			this.settings.put(Setting.SETTINGS_BACKUP_DATA_SAVE_DELAY, Setting.BACKUP_DATA_SAVE_DELAY);
		}
		Setting.BACKUP_DATA_SAVE_DELAY = (int) this.settings.get(Setting.SETTINGS_BACKUP_DATA_SAVE_DELAY);
	}

	public void joinGame(Player p, String title) {
		// strip "color code"
		title = ChatColor.stripColor(title);
		MiniGame game = this.getMiniGameWithTitle(title);
		if (game == null) {
			Utils.sendMsg(p, title + " minigame does not exist");
			return;
		}

		if (!this.partyManager.canPartyJoin(p, game)) {
			return;
		}

		// check player is not playing minigame
		List<Player> members = this.partyManager.getMembers(p);
		if (!this.isPlayingMiniGame(p)) {
			// join with party members who is not playing game
			for (Player member : members) {
				if (!this.isPlayingMiniGame(member)) {
					// join
					game.joinGame(member);
				}
			}
		} else {
			Utils.sendMsg(p, "You already joined other minigame");
		}
	}

	public void leaveGame(Player p) {
		// leave party members
		List<Player> members = this.partyManager.getMembers(p);

		// check player is playing minigame
		if (this.isPlayingMiniGame(p)) {
			MiniGame playingGame = this.getPlayingMiniGame(p);

			for (Player member : members) {
				// leave with members who is playing the same minigame with "p"
				if (playingGame.equals(this.getPlayingMiniGame(member))) {
					playingGame.leaveGame(member);
				}
			}

		} else {
			Utils.sendMsg(p, "You're not playing any minigame");
		}
	}

	public void createException(Player p, MiniGame.Exception exception) {
		// check player is playing minigame
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
		if (this.checkPluginStartToBeDisabled(e)) {
			return;
		}

		// detect event
		if (this.minigameEventDetector.isDetectableEvent(e)) {

			// get players
			List<Player> players = this.minigameEventDetector.getPlayersFromEvent(e);

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

		Utils.info("" + ChatColor.RED + ChatColor.BOLD + "[ Removed MiniGame List ]");
		for (String removedGameTitle : removedGames) {
			Utils.info(ChatColor.RED + removedGameTitle + " file is deleted");
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
