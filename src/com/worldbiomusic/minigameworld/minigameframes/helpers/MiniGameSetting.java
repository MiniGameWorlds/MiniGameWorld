package com.worldbiomusic.minigameworld.minigameframes.helpers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.util.Setting;

public class MiniGameSetting {
	/**
	 * - File control: O - Init value: setup value - Description: minigame title,
	 * must be no blank in title
	 */
	private String title;

	/**
	 * - File control: O - Init value: new Location(Bukkit.getWorld("world"), 0, 4,
	 * 0) - Description: minigame playing location
	 */
	private Location location;

	/**
	 * - File control: O - Init value: setup value - Description: min participating
	 * players
	 */
	private int minPlayerCount;

	/**
	 * - File control: O - Init value: setup value - Description: max participating
	 * players
	 */
	private int maxPlayerCount;

	/**
	 * - File control: O - Init value: setup value - Description: waiting time for
	 * starting (sec)
	 */
	private int waitingTime;

	/**
	 * - File control: O - Init value: setup value - Description: minigame running
	 * time (sec)
	 */
	private int timeLimit;

	/**
	 * - File control: O - Init value: true - Description: whether minigame is
	 * active or not
	 */
	private boolean active;

	/**
	 * - File control: X - Init value: false - Description: option for specific
	 * MiniGameSettings fix
	 */
	private boolean settingFixed;

	/**
	 * - File control: O - Init value: none - Description: tutorial
	 */
	private List<String> tutorial;

	/**
	 * - File control: O - Init value: setup value - Description: custom data
	 */
	private Map<String, Object> customData;

	/**
	 * - File control: O - Init value: setup value - Description: icon item for GUI
	 * inventory
	 */
	private Material icon;

	/**
	 * - File control: X - Init value: setup value - Description: whether get all
	 * event without filtering player
	 */
	private boolean passUndetectableEvent;

	/**
	 * Check condition in {@link MiniGame#handleException()}
	 */
	public enum GameFinishCondition {
		/**
		 * Check nothing<br>
		 * Have to process in minigame with overriding
		 * {@link MiniGame#handleException()} <br>
		 */
		NONE,
		/**
		 * Finish minigame if min players are not live
		 */
		MIN_PLAYERS_LIVE,
		/**
		 * Finish minigame if min players are not left
		 */
		MIN_PLAYERS_LEFT;
	}

	/**
	 * - File control: X<br>
	 * - Init value: MIN_PLAYERS_LIVE<br>
	 * - Description: Checked in {@link MiniGame#handleException()}
	 */
	private GameFinishCondition gameFinishCondition;

	public MiniGameSetting(String title, Location location, int minPlayerCount, int maxPlayerCount, int timeLimit,
			int waitingTime) {
		this.title = title;
		this.location = location;
		this.minPlayerCount = minPlayerCount;
		this.maxPlayerCount = maxPlayerCount;
		this.waitingTime = waitingTime;
		this.timeLimit = timeLimit;

		this.active = true;
		this.settingFixed = false;
		this.tutorial = new ArrayList<String>();
		this.customData = new LinkedHashMap<String, Object>();
		this.icon = Material.STONE;
		this.passUndetectableEvent = false;
		this.gameFinishCondition = GameFinishCondition.MIN_PLAYERS_LIVE;
	}

	// set

	public void setSettingFixed(boolean settingFixed) {
		this.settingFixed = settingFixed;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setMinPlayerCount(int minPlayerCount) {
		this.minPlayerCount = minPlayerCount;
	}

	public void setMaxPlayerCount(int maxPlayerCount) {
		this.maxPlayerCount = maxPlayerCount;
	}

	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}

	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
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

	public void setPassUndetectableEvent(boolean passUndetectableEvent) {
		this.passUndetectableEvent = passUndetectableEvent;
	}

	public void setGameFinishCondition(GameFinishCondition gameFinishCondition) {
		this.gameFinishCondition = gameFinishCondition;
	}

	// get

	public String getTitle() {
		return title;
	}

	public Location getLocation() {
		return location;
	}

	public int getMinPlayerCount() {
		return minPlayerCount;
	}

	public int getMaxPlayerCount() {
		return maxPlayerCount;
	}

	public int getWaitingTime() {
		return waitingTime;
	}

	public int getTimeLimit() {
		return timeLimit;
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

	public boolean isPassUndetectableEvent() {
		return passUndetectableEvent;
	}

	public GameFinishCondition getGameFinishCondition() {
		return gameFinishCondition;
	}

	// file (only file control)

	public Map<String, Object> getFileSetting() {
		// return settings that exist in minigames.yml
		Map<String, Object> setting = new LinkedHashMap<String, Object>();

		setting.put(Setting.MINIGAMES_TITLE, this.title);
		setting.put(Setting.MINIGAMES_MIN_PLAYER_COUNT, this.minPlayerCount);
		setting.put(Setting.MINIGAMES_MAX_PLAYER_COUNT, this.maxPlayerCount);
		setting.put(Setting.MINIGAMES_WAITING_TIME, this.waitingTime);
		setting.put(Setting.MINIGAMES_TIME_LIMIT, this.timeLimit);
		setting.put(Setting.MINIGAMES_ACTIVE, this.active);
		setting.put(Setting.MINIGAMES_ICON, this.icon.name());
		setting.put(Setting.MINIGAMES_LOCATION, this.location);
		setting.put(Setting.MINIGAMES_TUTORIAL, this.tutorial);
		setting.put(Setting.MINIGAMES_CUSTOM_DATA, this.customData);

		return setting;
	}

	@SuppressWarnings("unchecked")
	public void setFileSetting(Map<String, Object> setting) {
		// title
		this.setTitle((String) setting.get(Setting.MINIGAMES_TITLE));

		// location
		this.setLocation((Location) setting.get(Setting.MINIGAMES_LOCATION));

		// waitingTime
		this.setWaitingTime((int) setting.get(Setting.MINIGAMES_WAITING_TIME));

		// when settingFixed is false
		if (!isSettingFixed()) {
			// minPlayerCount
			this.setMinPlayerCount((int) setting.get(Setting.MINIGAMES_MIN_PLAYER_COUNT));

			// maxPlayerCount
			this.setMaxPlayerCount((int) setting.get(Setting.MINIGAMES_MAX_PLAYER_COUNT));

			// timeLimit
			this.setTimeLimit((int) setting.get(Setting.MINIGAMES_TIME_LIMIT));

			// customData
			this.setCustomData((Map<String, Object>) setting.get(Setting.MINIGAMES_CUSTOM_DATA));

		}

		// active
		this.setActive((boolean) setting.get(Setting.MINIGAMES_ACTIVE));

		// tutorial
		this.setTutorial((List<String>) setting.get(Setting.MINIGAMES_TUTORIAL));

		// display item
		this.setIcon(Material.valueOf((String) setting.get(Setting.MINIGAMES_ICON)));
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
