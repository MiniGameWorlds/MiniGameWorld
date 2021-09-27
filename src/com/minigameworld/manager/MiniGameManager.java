package com.minigameworld.manager;

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

import com.minigameworld.manager.eventdetect.MiniGameEventDetector;
import com.minigameworld.manager.gui.MiniGameGUIManager;
import com.minigameworld.manager.party.Party;
import com.minigameworld.manager.party.PartyManager;
import com.minigameworld.minigameframes.MiniGame;
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
	private Map<String, Object> setting;

	// event detector
	MiniGameEventDetector minigameEventDetector;

	// MiniGame Data Manager
	private MiniGameDataManager minigameDataM;

	// minigame gui manager
	private MiniGameGUIManager guiManager;

	// minigame lobby
	private static Location lobby;

	// party
	private PartyManager partyManager;

	// yaml data manager
	YamlManager yamlM;

	// use getInstance()
	private MiniGameManager() {
		// init
		this.minigames = new ArrayList<>();
		this.setting = new HashMap<String, Object>();
		this.initSettingData();
		this.minigameEventDetector = new MiniGameEventDetector();

		this.minigameDataM = new MiniGameDataManager(this);
		this.guiManager = new MiniGameGUIManager(this);
		this.partyManager = new PartyManager();
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
		if (!this.setting.containsKey("lobby")) {
			this.setting.put("lobby", new Location(Bukkit.getWorld("world"), 0, 4, 0, 90, 0));
		}
		lobby = (Location) this.setting.get("lobby");

		// minigameSign
		if (!this.setting.containsKey("minigameSign")) {
			this.setting.put("minigameSign", true);
		}

		// minigameCommand
		if (!this.setting.containsKey("minigameCommand")) {
			this.setting.put("minigameCommand", true);
		}

		// messagePrefix
		if (!this.setting.containsKey("messagePrefix")) {
			this.setting.put("messagePrefix", "MiniGameWorld");
		}
		Utils.messagePrefix = (String) this.setting.get("messagePrefix");
	}

	public void joinGame(Player p, String title) {
		// strip "color code"
		title = ChatColor.stripColor(title);
		MiniGame game = this.getMiniGameWithTitle(title);
		if (game == null) {
			Utils.sendMsg(p, title + " minigame does not exist");
			return;
		}

		if (!this.canPartyJoin(p, game)) {
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

	private boolean canPartyJoin(Player p, MiniGame game) {
		if (!this.partyManager.hasParty(p)) {
			return true;
		}

		List<Player> members = this.partyManager.getMembers(p);
		int nonPlayingGameMemberCount = this.getNonPlayingPlayerCount(members);
		int leftSeats = game.getMaxPlayerCount() - game.getPlayerCount();

		// check party size
		if (nonPlayingGameMemberCount > leftSeats) {
			Utils.sendMsg(p, "Party members are too many to join the game");
			return false;
		}

		return true;
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
			if (minigame.getSetting().isPassUndetectableEvents()) {
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
		String newGameClassName = newGame.getClassName();
		for (MiniGame game : this.minigames) {
			String existGameClassName = game.getClassName();
			// distinguish with MiniGame class name
			if (existGameClassName.equalsIgnoreCase(newGameClassName)) {
				Utils.warning(newGame.getTitleWithClassName() + " minigame is already registered");
				Utils.warning(
						"Cause: the same minigame " + game.getTitleWithClassName() + " minigame is already registered");
				return false;
			}
		}

		// apply already exsiting minigame data in minigames.yml
		if (this.minigameDataM.isMinigameDataExists(newGame)) {
			this.minigameDataM.applyMiniGameDataToInstance(newGame);
		} else {
			this.minigameDataM.addMiniGameData(newGame);
		}

		// add
		this.minigames.add(newGame);

		Utils.info("" + ChatColor.GREEN + ChatColor.BOLD + newGame.getTitleWithClassName() + ChatColor.WHITE
				+ " minigame is registered");
		return true;
	}

	public boolean unregisterMiniGame(MiniGame minigame) {
		if (this.minigames.remove(minigame)) {
			Utils.info(minigame.getTitleWithClassName() + " minigame is removed");
			return true;
		} else {
			return false;
		}
	}

	public Map<String, Object> getGameSetting() {
		return this.setting;
	}

	public static Location getLobby() {
		return lobby;
	}

	public MiniGameDataManager getMiniGameDataManager() {
		return this.minigameDataM;
	}

	public MiniGameGUIManager getMiniGameGUIManager() {
		return this.guiManager;
	}

	public PartyManager getPartyManager() {
		return this.partyManager;
	}

	public MiniGameEventDetector getMiniGameEventDetector() {
		return this.minigameEventDetector;
	}

	private int getNonPlayingPlayerCount(List<Player> players) {
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
		this.yamlM.reload(this);
	}

	@Override
	public void setData(YamlManager yamlM, FileConfiguration config) {
		this.yamlM = yamlM;

		// sync config setting with variable setting
		if (config.isSet("setting")) {
			this.setting = YamlHelper.ObjectToMap(config.getConfigurationSection("setting"));
		}
		config.set("setting", this.setting);

		// check setting has basic values
		this.initSettingData();
	}

	@Override
	public String getFileName() {
		return "setting.yml";
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
