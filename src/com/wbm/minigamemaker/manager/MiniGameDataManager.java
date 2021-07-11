package com.wbm.minigamemaker.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import com.wbm.minigamemaker.games.frame.MiniGame;
import com.wbm.plugin.util.BroadcastTool;
import com.wbm.plugin.util.data.yaml.YamlHelper;
import com.wbm.plugin.util.data.yaml.YamlManager;
import com.wbm.plugin.util.data.yaml.YamlMember;

import net.md_5.bungee.api.ChatColor;

public class MiniGameDataManager implements YamlMember {
	private Map<String, Object> minigameData;
	private MiniGameManager minigameM;
	//	private FileConfiguration config;

	public MiniGameDataManager(MiniGameManager minigameM) {
		this.minigameData = new HashMap<String, Object>();
		this.minigameM = minigameM;
	}

	public void addMiniGameData(MiniGame minigame) {
		Map<String, Object> data = new HashMap<String, Object>();

		// title
		data.put("title", minigame.getTitle());

		// location
		data.put("location", minigame.getLocation());

		// maxPlayerCount
		data.put("maxPlayerCount", minigame.getMaxPlayerCount());

		// waitingTime
		data.put("waitingTime", minigame.getWaitingTime());

		// timeLimit
		data.put("timeLimit", minigame.getTimeLimit());

		// active
		data.put("active", minigame.isActive());

		// customData
		Map<String, Object> customData = minigame.getCustomData();
		data.put("customData", customData);

		// data 추가 (className, data)
		this.minigameData.put(minigame.getClassName(), data);
	}

	public void removeMiniGame(String title) {
		this.minigameData.remove(title);
	}

	public boolean isMinigameDataExists(MiniGame minigame) {
		return this.getMiniGameData(minigame) != null;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getMiniGameData(MiniGame minigame) {
		/*
		 * ClassName으로 미니게임 구분
		 */
		//		Object obj = this.minigameData.get(minigame.getClassName());
		//		return YamlHelper.ObjectToMap(obj);
		return (Map<String, Object>) this.minigameData.get(minigame.getClassName());
	}

	@SuppressWarnings("unchecked")
	public void applyMiniGameDataToInstance(MiniGame minigame) {
		/*
		 * If minigames.yml file has same MiniGame, then overwrite saved minigame data
		 * to instance
		 */
		Map<String, Object> data = this.getMiniGameData(minigame);

		// title
		String title = (String) data.get("title");

		// location
		Location location = (Location) data.get("location");

		// maxPlayerCount
		int maxPlayerCount = (int) data.get("maxPlayerCount");

		// waitingTime
		int waitingTime = (int) data.get("waitingTime");

		// timeLimit
		int timeLimit = (int) data.get("timeLimit");

		// active
		boolean active = (boolean) data.get("active");

		// settingFixed: 파일의 값으로 설정을 하지 않고, 미니게임의 기본값 고정
		boolean settingFixed = minigame.isSettingFixed();

		// 세팅값 고정일때: maxPlayerCount, timeLimit, waitingTime 미니게임의 기본값으로 고정
		if (settingFixed) {
			// maxPlayerCount
			maxPlayerCount = minigame.getMaxPlayerCount();
			data.put("maxPlayerCount", maxPlayerCount);

			// waitingTime
			waitingTime = minigame.getWaitingTime();
			data.put("waitingTime", waitingTime);

			// timeLimit
			timeLimit = minigame.getTimeLimit();
			data.put("timeLimit", timeLimit);
		}

		// apply basic data
		minigame.setAttributes(title, location, maxPlayerCount, waitingTime, timeLimit, active, settingFixed);

		// apply customData
		Map<String, Object> customData = (Map<String, Object>) data.get("customData");
		minigame.setCustomData(customData);
	}

	public void removeNotExistMiniGameData() {
		// remove deleted minigame before save minigames.yml file
		List<MiniGame> gameList = this.minigameM.getMiniGameList();
		List<String> removedGames = new ArrayList<String>();
		OUT: for (String gameClassName : this.minigameData.keySet()) {
			for (MiniGame game : gameList) {
				// gameClassName이 있으면 통과
				if (gameClassName.equalsIgnoreCase(game.getClassName())) {
					continue OUT;
				}
			}
			// gameClassName이 없으면 minigameData에서 삭제 (= 파일에서 삭제)
			removedGames.add(gameClassName);
		}

		BroadcastTool.info("" + ChatColor.RED + ChatColor.BOLD + "[Removed MiniGame List in minigames.yml]");
		for (String removedGameTitle : removedGames) {
			this.minigameData.remove(removedGameTitle);
			BroadcastTool.info(ChatColor.RED + removedGameTitle + " minigame removed from minigames.yml");
		}
	}

	@Override
	public void setData(YamlManager yamlM, FileConfiguration config) {
		// sync config minigames with variable minigames
		if (config.isSet("minigames")) {
			this.minigameData = YamlHelper.ObjectToMap(config.getConfigurationSection("minigames"));
		}
		config.set("minigames", this.minigameData);
	}

	@Override
	public String getFileName() {
		return "minigames.yml";
	}

}
