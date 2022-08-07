package com.minigameworld.frames.helpers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;

import com.minigameworld.commands.MiniGameGamesConfigCommand;
import com.minigameworld.frames.MiniGame;
import com.minigameworld.util.Setting;
import com.wbm.plugin.util.CollectionTool;
import com.wbm.plugin.util.CryptoTool;

/**
 * If create option<br>
 * - Add getter and setter<br>
 * <br>
 * Also if file control is true<br>
 * - Add option in {@link #getFileSetting()} and
 * {@link #setFileSetting(Map)}<br>
 * - Add access method to {@link MiniGameGamesConfigCommand}<br>
 */
public class MiniGameSetting {
	/**
	 * - File control: X <br>
	 * - Init value: hash value of current nano time <br>
	 * - Description: useful for comparing different minigames
	 */
	private String id;

	/**
	 * - File control: X <br>
	 * - Init value: false <br>
	 * - Description: Whether game started or not
	 */
	private boolean started;

	/**
	 * - File control: O <br>
	 * - Init value: setup value <br>
	 * - Description: minigame title, must be no blank in title
	 */
	private String title;

	/**
	 * - File control: X <br>
	 * - Init value: new Location(Bukkit.getWorld("world"), 0, 4, 0) <br>
	 * - Description: minigame playing location
	 */
	private Location location;

	/**
	 * - File control: O <br>
	 * - Init value: 1 <br>
	 * - Description: minigame instance amount
	 */
	private int instances;

	/**
	 * - File control: O <br>
	 * - Init value: true <br>
	 * - Description: If true, create new instance world per each game
	 */
	private boolean instanceWorld;

	/**
	 * - File control: O <br>
	 * - Init value: [ new Location(Bukkit.getWorld("world"), 0, 4, 0) ] <br>
	 * - Description: minigame playing location list
	 */
	private List<Location> locations;

	/**
	 * - File control: O <br>
	 * - Init value: setup value <br>
	 * - Description: min participating players
	 */
	private int minPlayers;

	/**
	 * - File control: O <br>
	 * - Init value: setup value <br>
	 * - Description: max participating players
	 */
	private int maxPlayers;

	/**
	 * - File control: O <br>
	 * - Init value: setup value <br>
	 * - Description: waiting time for starting (sec)
	 */
	private int waitingTime;

	/**
	 * - File control: O <br>
	 * - Init value: setup value <br>
	 * - Description: minigame play time (sec)
	 */
	private int playTime;

	/**
	 * - File control: O <br>
	 * - Init value: true <br>
	 * - Description: whether minigame is active or not
	 */
	private boolean active;

	/**
	 * - File control: X <br>
	 * - Init value: false <br>
	 * - Description: option for specific MiniGameSettings fix
	 */
	private boolean settingFixed;

	/**
	 * - File control: O <br>
	 * - Init value: none <br>
	 * - Description: tutorial
	 */
	private List<String> tutorial;

	/**
	 * - File control: O <br>
	 * - Init value: setup value <br>
	 * - Description: custom data
	 */
	private Map<String, Object> customData;

	/**
	 * - File control: O <br>
	 * - Init value: setup value <br>
	 * - Description: icon item for GUI inventory
	 */
	private Material icon;

	/**
	 * Check game finish condition
	 * 
	 * @see MiniGame#handleException
	 */
	public enum GameFinishCondition {
		/**
		 * Check nothing<br>
		 * Have to process in minigame with overriding {@link MiniGame#handleException}
		 * <br>
		 */
		NONE,

		/**
		 * Finish minigame if live player are less than
		 * {@link #gameFinishConditionPlayerCount}
		 */
		LESS_THAN_PLAYERS_LIVE,

		/**
		 * Finish minigame if live players are more than
		 * {@link #gameFinishConditionPlayerCount}
		 */
		MORE_THAN_PLAYERS_LIVE,

		/**
		 * Finish minigame if left players are less than
		 * {@link #gameFinishConditionPlayerCount}<br>
		 * Contains also dead(not live) players
		 */
		LESS_THAN_PLAYERS_LEFT;
	}

	/**
	 * - File control: X<br>
	 * - Init value: LESS_THAN_PLAYERS_LIVE<br>
	 * - Description: Checked in {@link MiniGame#handleException} and
	 * {@link MiniGamePlayer#setLive(boolean)}
	 */
	private GameFinishCondition gameFinishCondition;

	/**
	 * - File control: X<br>
	 * - Init value: 2<br>
	 * - Description: Variable used with {@link #gameFinishCondition}
	 */
	private int gameFinishConditionPlayerCount;

	/**
	 * - File control: O<br>
	 * - Init value: true<br>
	 * - Description: Set players can view this minigame
	 */
	private boolean view;

	/**
	 * - File control: O<br>
	 * - Init value: true<br>
	 * - Description: if false, scoreboard not works (also players can not see)
	 */
	private boolean scoreboard;

	public enum JOIN_PRIORITY {
		MAX_PLAYERS, MIN_PLAYERS, RANDOM;
	}

	public MiniGameSetting(String title, Location location, int minPlayers, int maxPlayers, int playTime,
			int waitingTime) {
		this.id = CryptoTool.hashToHex(String.valueOf(System.nanoTime()).getBytes()).substring(0, 10);
		this.started = false;
		this.title = title;
		this.location = location;
		this.instances = 1;
		this.instanceWorld = false;
		this.locations = new ArrayList<>();
		this.locations.add(location);
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.waitingTime = waitingTime;
		this.playTime = playTime;

		this.active = true;
		this.settingFixed = false;
		this.tutorial = new ArrayList<String>();
		this.customData = new LinkedHashMap<String, Object>();
		this.icon = Material.STONE;
		this.gameFinishCondition = GameFinishCondition.LESS_THAN_PLAYERS_LIVE;
		this.gameFinishConditionPlayerCount = 2;
		this.view = true;
		this.scoreboard = true;
	}

	/*
	 * Setters
	 */

	public void setStarted(boolean started) {
		this.started = started;
	}

	public void setSettingFixed(boolean settingFixed) {
		this.settingFixed = settingFixed;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setInstances(int instances) {
		this.instances = instances;
	}

	public void setInstanceWorld(boolean instanceWorld) {
		this.instanceWorld = instanceWorld;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	public void setMinPlayers(int minPlayers) {
		this.minPlayers = minPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}

	public void setPlayTime(int playTime) {
		this.playTime = playTime;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setTutorial(List<String> tutorial) {
		this.tutorial = tutorial;
	}

	public void setCustomData(Map<String, Object> customData) {
		this.customData = customData;
	}

	public void setIcon(Material icon) {
		this.icon = icon;
	}

	public void setGameFinishCondition(GameFinishCondition gameFinishCondition) {
		this.gameFinishCondition = gameFinishCondition;
	}

	public void setGameFinishConditionPlayerCount(int gameFinishConditionPlayerCount) {
		this.gameFinishConditionPlayerCount = gameFinishConditionPlayerCount;
	}

	public void setView(boolean view) {
		this.view = view;
	}

	public void setScoreboard(boolean scoreboard) {
		this.scoreboard = scoreboard;
	}

	/* 
	 * Getters
	 */

	public String getId() {
		return id;
	}

	public boolean isStarted() {
		return started;
	}

	public String getTitle() {
		return title;
	}

	public Location getLocation() {
		return location;
	}

	public int getInstances() {
		return instances;
	}

	public boolean isInstanceWorld() {
		return instanceWorld;
	}

	public Location getRandomLocation() {
		return CollectionTool.random(this.locations).get();
	}

	public List<Location> getLocations() {
		return locations;
	}

	public List<Location> getNotUsingLocations() {
		return locations.stream().filter(loc -> !LocationManager.getUsingLocations().contains(loc)).toList();
	}

	public int getMinPlayers() {
		return minPlayers;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public int getWaitingTime() {
		return waitingTime;
	}

	public int getPlayTime() {
		return playTime;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isSettingFixed() {
		return settingFixed;
	}

	public List<String> getTutorial() {
		return this.tutorial;
	}

	public Map<String, Object> getCustomData() {
		return this.customData;
	}

	public Material getIcon() {
		return this.icon;
	}

	public GameFinishCondition getGameFinishCondition() {
		return gameFinishCondition;
	}

	public int getGameFinishConditionPlayerCount() {
		return gameFinishConditionPlayerCount;
	}

	public boolean canView() {
		return view;
	}

	public boolean isScoreboardEnabled() {
		return scoreboard;
	}

	// file (only file control)

	public Map<String, Object> getFileSetting() {
		// return settings that exist in minigames.yml
		Map<String, Object> setting = new LinkedHashMap<String, Object>();

		setting.put(Setting.GAMES_TITLE, this.title);
		setting.put(Setting.GAMES_MIN_PLAYERS, this.minPlayers);
		setting.put(Setting.GAMES_MAX_PLAYERS, this.maxPlayers);
		setting.put(Setting.GAMES_WAITING_TIME, this.waitingTime);
		setting.put(Setting.GAMES_PLAY_TIME, this.playTime);
		setting.put(Setting.GAMES_ACTIVE, this.active);
		setting.put(Setting.GAMES_ICON, this.icon.name());
		setting.put(Setting.GAMES_VIEW, this.view);
		setting.put(Setting.GAMES_SCOREBOARD, this.scoreboard);
		setting.put(Setting.GAMES_INSTANCES, this.instances);
		setting.put(Setting.GAMES_INSTANCE_WORLD, this.instanceWorld);
		setting.put(Setting.GAMES_LOCATIONS, this.locations);
		setting.put(Setting.GAMES_TUTORIAL, this.tutorial);
		setting.put(Setting.GAMES_CUSTOM_DATA, this.customData);

		return setting;
	}

	@SuppressWarnings("unchecked")
	public void setFileSetting(Map<String, Object> setting) {
		// title
		setTitle((String) setting.get(Setting.GAMES_TITLE));

		// instances
		setInstances((int) setting.get(Setting.GAMES_INSTANCES));

		// instance world
		setInstanceWorld((boolean) setting.get(Setting.GAMES_INSTANCE_WORLD));

		// locations
		setLocations((List<Location>) setting.get(Setting.GAMES_LOCATIONS));

		// waitingTime
		setWaitingTime((int) setting.get(Setting.GAMES_WAITING_TIME));

		// when settingFixed is false
		if (!isSettingFixed()) {
			// minPlayers
			setMinPlayers((int) setting.get(Setting.GAMES_MIN_PLAYERS));

			// maxPlayers
			setMaxPlayers((int) setting.get(Setting.GAMES_MAX_PLAYERS));

			// playTime
			setPlayTime((int) setting.get(Setting.GAMES_PLAY_TIME));

			// customData
			setCustomData((Map<String, Object>) setting.get(Setting.GAMES_CUSTOM_DATA));
		}

		// active
		setActive((boolean) setting.get(Setting.GAMES_ACTIVE));

		// tutorial
		setTutorial((List<String>) setting.get(Setting.GAMES_TUTORIAL));

		// menu icon
		setIcon(Material.valueOf(((String) setting.get(Setting.GAMES_ICON)).toUpperCase()));

		// view
		setView((boolean) setting.get(Setting.GAMES_VIEW));

		// scoreboard
		setScoreboard((boolean) setting.get(Setting.GAMES_SCOREBOARD));
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
