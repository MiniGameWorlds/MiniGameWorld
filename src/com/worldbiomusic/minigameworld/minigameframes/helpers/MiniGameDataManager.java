package com.worldbiomusic.minigameworld.minigameframes.helpers;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.io.Files;
import com.wbm.plugin.util.data.yaml.YamlHelper;
import com.wbm.plugin.util.data.yaml.YamlManager;
import com.wbm.plugin.util.data.yaml.YamlMember;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.util.Setting;
import com.worldbiomusic.minigameworld.util.Utils;

public class MiniGameDataManager implements YamlMember {
	public static final String FOLDER_NAME = "minigames";
	private MiniGame minigame;
	private YamlManager yamlManager;
	private Map<String, Object> data;

	public MiniGameDataManager(MiniGame minigame) {
		this.minigame = minigame;
		this.data = new LinkedHashMap<>();
	}

	public void createMiniGameData() {
		// [IMPORTANT] if do "this.data = minigame.getSetting().getFileSetting()",
		// YamlManager's config will lost this.data
		for (Entry<String, Object> entry : minigame.getSetting().getFileSetting().entrySet()) {
			this.data.put(entry.getKey(), entry.getValue());
		}

		// process exception
		this.taskAfterDataSet();
	}

	public boolean isMinigameDataExists() {
		// search minigame file in folder
		File file = this.getMiniGameFile();
		if (file == null) {
			return false;
		}

		// if file exist
		if (this.data.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public File getMiniGameFile() {
		File gamesFolder = Utils.getMiniGamesFolder();
		for (File f : gamesFolder.listFiles()) {
			String fileName = Files.getNameWithoutExtension(f.getName());
			if (fileName.equals(this.getClassName())) {
				return f;
			}
		}
		return null;
	}

	// then overwrite saved minigame data to MiniGame instance
	public void applyMiniGameDataToInstance() {
		// Make copy of pure setting data
		Map<String, Object> pureSettingData = new LinkedHashMap<String, Object>(minigame.getSetting().getFileSetting());
		// Restore before apply to avoid error
		restoreMissedKeys(this.data, pureSettingData);
		
		// apply settings
		this.minigame.getSetting().setFileSetting(this.data);

		// when settingFixed is true: restore fixed values to file
		if (this.minigame.getSetting().isSettingFixed()) {
			// minPlayerCount
			this.data.put(Setting.MINIGAMES_MIN_PLAYER_COUNT, this.minigame.getMinPlayerCount());

			// maxPlayerCount
			this.data.put(Setting.MINIGAMES_MAX_PLAYER_COUNT, this.minigame.getMaxPlayerCount());

			// timeLimit
			this.data.put(Setting.MINIGAMES_TIME_LIMIT, this.minigame.getTimeLimit());

			// customData
			this.data.put(Setting.MINIGAMES_CUSTOM_DATA, this.minigame.getCustomData());
		}

		// Restore missed keys (i.e. keys for some updates)
		restoreMissedKeys(this.data, pureSettingData);

		// process exception
		this.taskAfterDataSet();
	}

	@SuppressWarnings("unchecked")
	private void restoreMissedKeys(Map<String, Object> missedTarget, Map<String, Object> fullTarget) {
		// restore "missedTarget" < "fullTarget"
		for (Entry<String, Object> entry : fullTarget.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (!missedTarget.containsKey(key)) {
				missedTarget.put(key, value);
			}

			// for Map value
			if (value instanceof Map) {
				this.restoreMissedKeys((Map<String, Object>) missedTarget.get(key), (Map<String, Object>) value);
			}
		}
	}

	private void taskAfterDataSet() {
		// load custom data
		this.minigame.loadCustomData();
	}

	public String getClassName() {
		return this.minigame.getClassName();
	}

	public Map<String, Object> getData() {
		return this.data;
	}

	@Override
	public void reload() {
		this.yamlManager.reload(this);

		// apply file data to minigame
		this.applyMiniGameDataToInstance();
	}

	@Override
	public void setData(YamlManager yamlManager, FileConfiguration config) {
		this.yamlManager = yamlManager;

		if (config.isSet(this.getClassName())) {
			this.data = YamlHelper.ObjectToMap(config.getConfigurationSection(this.getClassName()));
		}
		config.set(this.getClassName(), this.data);
	}

	@Override
	public String getFileName() {
		// in "games" directory
		return FOLDER_NAME + "/" + this.getClassName() + ".yml";
	}
}
