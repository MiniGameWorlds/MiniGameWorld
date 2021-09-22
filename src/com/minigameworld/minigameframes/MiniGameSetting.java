package com.minigameworld.minigameframes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;

public class MiniGameSetting {
	/*
	 * file control: O
	 * init value: setup value
	 * description: minigame title
	 */
	private String title;

	/*
	 * file control: O
	 * init value: new Location(Bukkit.getWorld("world"), 0, 4, 0)
	 * description: minigame playing location
	 */
	private Location location;

	/*
	 * file control: O
	 * init value: setup value
	 * description: max participating players
	 */
	private int maxPlayerCount;

	/*
	 * file control: O
	 * init value: setup value
	 * description: waiting time for starting (sec)
	 */
	private int waitingTime;

	/*
	 * file control: O
	 * init value: setup value
	 * description: minigame running time (sec)
	 */
	private int timeLimit;

	/*
	 * file control: O
	 * init value: true
	 * description: whether minigame is active or not 
	 */
	private boolean active;

	/*
	 * file control: X
	 * init value: false
	 * description: option for specific MiniGameSettings fix 
	 */
	private boolean settingFixed;

	/*
	 * file control: O
	 * init value: false
	 * description: option to notify when score changes 
	 */
	private boolean scoreNotifying;

	/*
	 * file control: O
	 * init value: false
	 * description: start game when "current player count" == "max player count"
	 */
	private boolean forceFullPlayer;

	/*
	 * file control: O
	 * init value: none
	 * description: tutorial
	 */
	private List<String> tutorial;

	/*
	 * file control: O
	 * init value: setup value
	 * description: custom data
	 */
	private Map<String, Object> customData;

	/*
	 * file control: O
	 * init value: setup value
	 * description: icon item for GUI inventory
	 */
	private Material icon;

	public MiniGameSetting(String title, Location location, int maxPlayerCount, int timeLimit, int waitingTime) {
		this.title = title;
		this.location = location;
		this.maxPlayerCount = maxPlayerCount;
		this.waitingTime = waitingTime;
		this.timeLimit = timeLimit;

		this.active = true;
		this.settingFixed = false;
		this.scoreNotifying = false;
		this.forceFullPlayer = false;
		this.tutorial = new ArrayList<String>();
		this.customData = new HashMap<String, Object>();
		this.icon = Material.STONE;
	}

	// set

	public void setSettingFixed(boolean settingFixed) {
		this.settingFixed = settingFixed;
	}

	public void setScoreNotifying(boolean scoreNotifying) {
		this.scoreNotifying = scoreNotifying;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setLocation(Location location) {
		this.location = location;
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

	public void setForceFullPlayer(boolean forceFullPlayer) {
		this.forceFullPlayer = forceFullPlayer;
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

	// get

	public String getTitle() {
		return title;
	}

	public Location getLocation() {
		return location;
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

	public boolean isScoreNotifying() {
		return scoreNotifying;
	}

	public boolean isForceFullPlayer() {
		return forceFullPlayer;
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

	// setting

	public Map<String, Object> getFileSetting() {
		// return settings that exist in minigames.yml
		Map<String, Object> setting = new HashMap<String, Object>();

		setting.put("title", this.title);
		setting.put("location", this.location);
		setting.put("maxPlayerCount", this.maxPlayerCount);
		setting.put("waitingTime", this.waitingTime);
		setting.put("timeLimit", this.timeLimit);
		setting.put("active", this.active);
		setting.put("scoreNotifying", this.scoreNotifying);
		setting.put("forceFullPlayer", this.forceFullPlayer);
		setting.put("tutorial", this.tutorial);
		setting.put("customData", this.customData);
		setting.put("icon", this.icon.name());

		return setting;
	}

	@SuppressWarnings("unchecked")
	public void setFileSetting(Map<String, Object> setting) {
		// title
		this.setTitle((String) setting.get("title"));

		// location
		this.setLocation((Location) setting.get("location"));

		// waitingTime
		this.setWaitingTime((int) setting.get("waitingTime"));

		// when settingFixed is false
		if (!isSettingFixed()) {
			// maxPlayerCount
			this.setMaxPlayerCount((int) setting.get("maxPlayerCount"));

			// timeLimit
			this.setTimeLimit((int) setting.get("timeLimit"));

			// customData
			this.setCustomData((Map<String, Object>) setting.get("customData"));

			// forceFullPlayer
			this.setForceFullPlayer((boolean) setting.get("forceFullPlayer"));
		}

		// active
		this.setActive((boolean) setting.get("active"));

		// scoreNotifying
		this.setScoreNotifying((boolean) setting.get("scoreNotifying"));

		// tutorial
		this.setTutorial((List<String>) setting.get("tutorial"));

		// display item
		this.setIcon(Material.valueOf((String) setting.get("icon")));
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
