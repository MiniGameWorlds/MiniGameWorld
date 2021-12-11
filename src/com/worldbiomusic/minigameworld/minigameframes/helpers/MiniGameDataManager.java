package com.worldbiomusic.minigameworld.minigameframes.helpers;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.io.Files;
import com.wbm.plugin.util.CollectionTool;
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

		// sync map keys
		syncMapKeys(this.data, pureSettingData);

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

		// process exception
		this.taskAfterDataSet();
	}

	private void syncMapKeys(Map<String, Object> configMap, Map<String, Object> pureMap) {
		// remove not necessary keys to avoid error (i.e. keys for some updates)
		CollectionTool.removeNotNecessaryKeys(this.data, pureMap);

		// Restore before apply to avoid error (i.e. keys for some updates)
		CollectionTool.restoreMissedKeys(this.data, pureMap);

		// sync map keys
		CollectionTool.syncKeyOrder(this.data, pureMap);
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
		return FOLDER_NAME + File.separator + this.getClassName() + ".yml";
	}
}
