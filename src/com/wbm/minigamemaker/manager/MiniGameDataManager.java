package com.wbm.minigamemaker.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wbm.plugin.util.BroadcastTool;
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

		// data 추가 (className, data)
		this.minigameData.put(minigame.getClassName(), data);
	}

	public void removeMiniGame(String title) {
		this.minigameData.remove(title);
	}

	public boolean isMinigameDataExists(MiniGame minigame) {
		return this.getMiniGameData(minigame) != null;
	}

	public Map<String, Object> getMiniGameData(MiniGame minigame) {
		/*
		 * ClassName으로 미니게임 구분
		 */
		return this.minigameData.get(minigame.getClassName());
	}

	public void applyMiniGameDataToInstance(MiniGame minigame) {
		Map<String, Object> data = this.getMiniGameData(minigame);

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
		int maxPlayerCount = (int) ((double) data.get("maxPlayerCount"));

		// waitingTime
		int waitingTime = (int) ((double) data.get("waitingTime"));

		// timeLimit
		int timeLimit = (int) ((double) data.get("timeLimit"));

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
		this.minigameData = gson.fromJson(jsonString, Map.class);
	}

	private void removeNotExistMiniGameData() {
		MiniGameManager miniGameM = MiniGameManager.getInstance();
		List<MiniGame> gameList = miniGameM.getMiniGameList();
		List<String> removedGames = new ArrayList<String>();
		OUT: for (String gameClassName : this.minigameData.keySet()) {
			for (MiniGame game : gameList) {
				// gameClassName이 있으면 통과
				if (gameClassName.equalsIgnoreCase(game.getTitle())) {
					continue OUT;
				}
			}
			// gameClassName이 없으면 minigameData에서 삭제 (= 파일에서 삭제)
//			this.minigameData.remove(gameClassName);
			removedGames.add(gameClassName);
		}

		for (String removedGameTitle : removedGames) {
			this.minigameData.remove(removedGameTitle);
			BroadcastTool.info(removedGameTitle + " minigame removed from minigames.json");
		}
	}

	@Override
	public Object getData() {
		// 데이터 반환하기 전에 minigames.json에서 없는 미니게임 제거하기
		this.removeNotExistMiniGameData();

		// 데이터 반환
		return this.minigameData;
	}

	@Override
	public String getFileName() {
		return "minigames.json";
	}

}
