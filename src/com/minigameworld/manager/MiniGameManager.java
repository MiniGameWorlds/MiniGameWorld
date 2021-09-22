package com.minigameworld.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.Inventory;

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

	// detectable events
	private Set<Class<? extends Event>> detectableEventList;
	// players for event process (use for memory)
	private List<Player> eventPlayers;

	// setting.yml
	private Map<String, Object> setting;

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
		this.eventPlayers = new ArrayList<Player>();
		this.setting = new HashMap<String, Object>();
		this.initSettingData();

		// register detectable events
		this.registerDetectableEvent();

		this.minigameDataM = new MiniGameDataManager(this);
		this.guiManager = new MiniGameGUIManager(this);
		this.partyManager = new PartyManager();
	}

	private void registerDetectableEvent() {
		// init
		this.detectableEventList = new HashSet<>();

		// events only related with Player
		this.detectableEventList.add(BlockBreakEvent.class);
		this.detectableEventList.add(BlockPlaceEvent.class);
		this.detectableEventList.add(PlayerEvent.class);
		this.detectableEventList.add(EntityEvent.class);
		this.detectableEventList.add(HangingEvent.class);
		this.detectableEventList.add(InventoryEvent.class);
		this.detectableEventList.add(InventoryMoveItemEvent.class);
		this.detectableEventList.add(PlayerLeashEntityEvent.class);
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

		// party members can join game without other members that already playing game
		if (!this.canPartyJoin(p, game)) {
			return;
		}

		// check player is not playing minigame
		List<Player> members = this.partyManager.getMembers(p);
		if (!this.checkPlayerIsPlayingMiniGame(p)) {
			for (Player member : members) {
				if (!this.checkPlayerIsPlayingMiniGame(member)) {
					game.joinGame(member);
				}

				// message to everyone
				Party.sendMessage(member, p.getName() + " joined " + game.getTitle() + " with party");
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
		if (this.checkPlayerIsPlayingMiniGame(p)) {
			MiniGame playingGame = this.getPlayingMiniGame(p);
			for (Player member : members) {
				// leave with members who is playing the same minigame with "p"
				if (playingGame.equals(this.getPlayingMiniGame(member))) {
					playingGame.leaveGame(p);
				}

				// message to everyone
				Party.sendMessage(member, p.getName() + " leaved " + playingGame.getTitle() + " with party");
			}
		} else {
			Utils.sendMsg(p, "You're not playing any minigame");
		}

	}

	// check player is playing minigame (API)
	public void handleException(Player p, MiniGame.Exception exception, Object arg) {

		if (this.checkPlayerIsPlayingMiniGame(p)) {
			MiniGame playingGame = this.getPlayingMiniGame(p);
			playingGame.handleException(p, exception, arg);
		}
	}

	// check detectable event (using isAssignableFrom())
	public boolean isDetectableEvent(Event event) {
		for (Class<? extends Event> c : this.detectableEventList) {
			if (c.isAssignableFrom(event.getClass())) {
				return true;
			}
		}
		return false;
	}

	// check detectable event (using isAssignableFrom())
	public boolean isDetectableEvent(Class<? extends Event> event) {
		for (Class<? extends Event> c : this.detectableEventList) {
			if (c.isAssignableFrom(event)) {
				return true;
			}
		}
		return false;
	}

	/* 
	 * process event
	 * - check inventory event
	 * - check sign click
	 * - check player is playing minigame and process event to minigame
	*/
	public boolean processEvent(Event e) {
		// check event is detectable
		if (!this.isDetectableEvent(e)) {
			return false;
		}

		// get players
		List<Player> players = this.getPlayersFromEvent(e);

		// check empty
		if (players.isEmpty()) {
			return false;
		}

		// pass evnet to minigame
		for (Player p : players) {
			// check player is playing minigame
			if (this.checkPlayerIsPlayingMiniGame(p)) {
				MiniGame playingGame = this.getPlayingMiniGame(p);
				playingGame.passEvent(e);
			}
		}
		return true;
	}

	public boolean checkPlayerIsPlayingMiniGame(Player p) {
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

	private List<Player> getPlayersFromEvent(Event e) {
		// clear
		this.eventPlayers.clear();

		// get players from each Event
		if (e instanceof BlockBreakEvent) {
			eventPlayers.add(((BlockBreakEvent) e).getPlayer());
		} else if (e instanceof BlockPlaceEvent) {
			eventPlayers.add(((BlockPlaceEvent) e).getPlayer());
		} else if (e instanceof PlayerEvent) {
			eventPlayers.add(((PlayerEvent) e).getPlayer());
		} else if (e instanceof EntityEvent) {
			Entity entity = ((EntityEvent) e).getEntity();
			if (entity instanceof Player) {
				eventPlayers.add((Player) entity);
			}
		} else if (e instanceof HangingEvent) {
			Entity entity = ((HangingEvent) e).getEntity();
			if (entity instanceof Player) {
				eventPlayers.add((Player) entity);
			}
		} else if (e instanceof InventoryEvent) {
			HumanEntity entity = (((InventoryEvent) e).getView()).getPlayer();
			if (entity instanceof Player) {
				eventPlayers.add((Player) entity);
			}
		} else if (e instanceof InventoryMoveItemEvent) {
			Inventory inv = ((InventoryMoveItemEvent) e).getInitiator();
			List<HumanEntity> viewers = inv.getViewers();
			for (HumanEntity humanEntity : viewers) {
				if (humanEntity instanceof Player) {
					eventPlayers.add((Player) humanEntity);
				}
			}
		} else if (e instanceof PlayerLeashEntityEvent) {
			eventPlayers.add(((PlayerLeashEntityEvent) e).getPlayer());
		}

		return eventPlayers;
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

	private int getNonPlayingPlayerCount(List<Player> players) {
		int Count = 0;
		for (Player p : players) {
			if (!this.checkPlayerIsPlayingMiniGame(p)) {
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
