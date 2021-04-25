package com.wbm.minigamemaker.manager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wbm.plugin.util.data.json.JsonDataMember;

public class MiniGameDataManager implements JsonDataMember {
	Map<String, Map<String, Object>> minigameData;

	public MiniGameDataManager() {
		this.minigameData = new HashMap<String, Map<String, Object>>();
	}

	public void addMiniGameData(MiniGame minigame) {
		Map<String, Object> data = new HashMap<String, Object>();

		// title
		data.put("title", minigame.getTitle());

		// location
		Map<String, Object> locationData = new HashMap<String, Object>();
		Location gameLoc = minigame.getLocation();
		locationData.put("world", gameLoc.getWorld().getName());
		locationData.put("x", gameLoc.getX());
		locationData.put("y", gameLoc.getY());
		locationData.put("z", gameLoc.getZ());
		locationData.put("pitch", gameLoc.getPitch());
		locationData.put("yaw", gameLoc.getYaw());
		data.put("location", locationData);

		// maxPlayerCount
		data.put("maxPlayerCount", minigame.getMaxPlayerCount());

		// waitingTime
		data.put("waitingTime", minigame.getWaitingTime());

		// timeLimit
		data.put("timeLimit", minigame.getTimeLimit());

		// data 추가
		this.minigameData.put(minigame.getTitle(), data);
	}

	public void removeMiniGame(String title) {
		this.minigameData.remove(title);
	}

	public boolean minigameDataExists(String title) {
		return this.minigameData.containsKey(title);
	}

	public void applyMiniGameData(MiniGame minigame) {
		Map<String, Object> data = this.minigameData.get(minigame.getTitle());

		// title
		String title = (String) data.get("title");

		// location
		@SuppressWarnings("unchecked")
		Map<String, Object> locationData = (Map<String, Object>) data.get("location");
		String world = (String) locationData.get("world");
		double x = (double) locationData.get("x");
		double y = (double) locationData.get("y");
		double z = (double) locationData.get("z");
		double pitch = (double) locationData.get("pitch");
		double yaw = (double) locationData.get("yaw");
		Location location = new Location(Bukkit.getWorld(world), x, y, z, (float) pitch, (float) yaw);

		// maxPlayerCount
		int maxPlayerCount = (int)((double) data.get("maxPlayerCount"));

		// waitingTime
		int waitingTime = (int)((double) data.get("waitingTime"));

		// timeLimit
		int timeLimit = (int)((double) data.get("timeLimit"));

		// apply data
		minigame.setAttributes(title, location, maxPlayerCount, waitingTime, timeLimit);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void distributeData(String jsonString) {
		if (jsonString == null) {
			return;
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		this.minigameData =gson.fromJson(jsonString, Map.class);
	}

	@Override
	public Object getData() {
		return this.minigameData;
	}

	@Override
	public String getFileName() {
		return "minigames.json";
	}

}
