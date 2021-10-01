package com.minigameworld.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.google.common.io.Files;
import com.minigameworld.managers.menu.MiniGameMenuManager;
import com.minigameworld.managers.party.Party;
import com.minigameworld.managers.party.PartyManager;
import com.minigameworld.minigameframes.MiniGame;
import com.minigameworld.minigameframes.helpers.MiniGameEventDetector;
import com.minigameworld.util.Setting;
import com.minigameworld.util.Utils;
import com.wbm.plugin.util.data.yaml.YamlHelper;
import com.wbm.plugin.util.data.yaml.YamlManager;
import com.wbm.plugin.util.data.yaml.YamlMember;

public class MiniGameManager implements YamlMember {
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

	// minigame lobby
	private static Location lobby;

	// party
	private PartyManager partyManager;

	// yaml data manager
	YamlManager yamlManager;

	// use getInstance()
	private MiniGameManager() {
		// init
		this.minigames = new ArrayList<>();
		this.settings = new HashMap<String, Object>();
		this.initSettingData();
		this.minigameEventDetector = new MiniGameEventDetector();

		this.guiManager = new MiniGameMenuManager(this);
		this.partyManager = new PartyManager(this);
	}

	public void processPlayerJoinWorks(Player p) {
		this.getPartyManager().createParty(p);
	}

	public void processPlayerQuitWorks(Player p) {
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

	private void initSettingData() {
		/*
		 * set basic setting.yml
		 */
		// lobby
		if (!this.settings.containsKey(Setting.SETTINGS_LOBBY)) {
			this.settings.put(Setting.SETTINGS_LOBBY, new Location(Bukkit.getWorld("world"), 0, 4, 0, 90, 0));
		}
		lobby = (Location) this.settings.get(Setting.SETTINGS_LOBBY);

		// minigameSign
		if (!this.settings.containsKey(Setting.SETTINGS_MINIGAME_SIGN)) {
			this.settings.put(Setting.SETTINGS_MINIGAME_SIGN, true);
		}

		// minigameCommand
		if (!this.settings.containsKey(Setting.SETTINGS_MINIGAME_COMMAND)) {
			this.settings.put(Setting.SETTINGS_MINIGAME_COMMAND, true);
		}

		// messagePrefix
		if (!this.settings.containsKey(Setting.SETTINGS_MESSAGE_PREFIX)) {
			this.settings.put(Setting.SETTINGS_MESSAGE_PREFIX, ChatColor.BOLD + "MiniGameWorld");
		}
		Utils.messagePrefix = (String) this.settings.get(Setting.SETTINGS_MESSAGE_PREFIX);
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
				}

				// join
				if (game.joinGame(member)) {
					// message to everyone
					Utils.broadcast(member.getName() + " joined " + game.getTitle());
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

			boolean canLeave = false;
			for (Player member : members) {
				// leave with members who is playing the same minigame with "p"
				if (playingGame.equals(this.getPlayingMiniGame(member))) {
					canLeave = playingGame.leaveGame(member);
				}
			}

			// message to everyone
			if (canLeave) {
				members.forEach(
						m -> Party.sendMessage(m, p.getName() + " leaved " + playingGame.getTitle() + " with party"));
			}

		} else {
			Utils.sendMsg(p, "You're not playing any minigame");
		}
	}

	// check player is playing minigame (API)
	public void handleException(Player p, MiniGame.GameException exception, Object arg) {

		if (this.isPlayingMiniGame(p)) {
			MiniGame playingGame = this.getPlayingMiniGame(p);
			playingGame.handleException(p, exception, arg);
		}
	}

	/*
	 * - check player is playing minigame and process event to minigame
	*/
	public void passEvent(Event e) {
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

		// check already existing data
		if (newGame.getDataManager().isMinigameDataExists()) {
			newGame.getDataManager().applyMiniGameDataToInstance();
		} else {
			newGame.getDataManager().createMiniGameData();
		}

		// add
		this.minigames.add(newGame);

		Utils.info("" + ChatColor.GREEN + ChatColor.BOLD + newGame.getTitleWithClassName() + ChatColor.WHITE
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
			Utils.info(ChatColor.RED + removedGameTitle + " minigame file is deleted");
		}
	}

	public Map<String, Object> getSettings() {
		return this.settings;
	}

	public static Location getLobby() {
		return lobby;
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

	public int getNonPlayingPlayerCount(List<Player> players) {
		int Count = 0;
		for (Player p : players) {
			if (!this.isPlayingMiniGame(p)) {
				Count += 1;
			}
		}
		return Count;
	}

	@Override
	public void reload() {
		this.yamlManager.reload(this);

		// reload minigames
		this.minigames.forEach(m -> m.getDataManager().reload());
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
