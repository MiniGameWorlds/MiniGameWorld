package com.wbm.minigamemaker.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.wbm.minigamemaker.games.frame.MiniGame;
import com.wbm.minigamemaker.util.Utils;
import com.wbm.plugin.util.data.yaml.YamlHelper;
import com.wbm.plugin.util.data.yaml.YamlManager;
import com.wbm.plugin.util.data.yaml.YamlMember;

import net.md_5.bungee.api.ChatColor;

public class MiniGameDataManager implements YamlMember {
	private Map<String, Object> minigameData;
	private MiniGameManager minigameM;
	private YamlManager yamlM;

	public MiniGameDataManager(MiniGameManager minigameM) {
		this.minigameData = new HashMap<String, Object>();
		this.minigameM = minigameM;
	}

	public void addMiniGameData(MiniGame minigame) {
		Map<String, Object> data = minigame.getSetting().getFileSetting();

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
		return (Map<String, Object>) this.minigameData.get(minigame.getClassName());
	}

	public void applyMiniGameDataToInstance(MiniGame minigame) {
		/*
		 * If minigames.yml file has same MiniGame, then overwrite saved minigame data
		 * to instance
		 */
		Map<String, Object> data = this.getMiniGameData(minigame);

		// apply settings
		minigame.getSetting().setFileSetting(data);

		// when settingFixed is true: restore "maxPlayerCount", "waitingTime", "timeLimit" values to file
		if (minigame.isSettingFixed()) {
			// maxPlayerCount
			data.put("maxPlayerCount", minigame.getMaxPlayerCount());

			// waitingTime
			data.put("waitingTime", minigame.getWaitingTime());

			// timeLimit
			data.put("timeLimit", minigame.getTimeLimit());
			
			// customData
			data.put("customData", minigame.getCustomData());
		}
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

		Utils.log("" + ChatColor.RED + ChatColor.BOLD + "[ Removed MiniGame List in minigames.yml ]");
		for (String removedGameTitle : removedGames) {
			this.minigameData.remove(removedGameTitle);
			Utils.log(ChatColor.RED + removedGameTitle + " minigame removed from minigames.yml");
		}
	}

	@Override
	public void reload() {
		this.yamlM.reload(this);

		// apply reloaded config file values
		this.minigameM.getMiniGameList().forEach(minigame -> this.applyMiniGameDataToInstance(minigame));
	}

	@Override
	public void setData(YamlManager yamlM, FileConfiguration config) {
		this.yamlM = yamlM;

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
