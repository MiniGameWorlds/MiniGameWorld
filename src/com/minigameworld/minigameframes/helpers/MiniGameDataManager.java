package com.minigameworld.minigameframes.helpers;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.io.Files;
import com.minigameworld.api.MiniGameWorldUtils;
import com.minigameworld.minigameframes.MiniGame;
import com.minigameworld.util.Setting;
import com.minigameworld.util.Utils;
import com.wbm.plugin.util.data.yaml.YamlHelper;
import com.wbm.plugin.util.data.yaml.YamlManager;
import com.wbm.plugin.util.data.yaml.YamlMember;

public class MiniGameDataManager implements YamlMember {
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
		this.taskAfterDataSetup();
	}

	public boolean isMinigameDataExists() {
		// search minigame file in folder
		File file = getMiniGameFile();
		if (file == null) {
			return false;
		}

		// if file exist
		return !this.data.isEmpty();
	}

	public File getMiniGameFile() {
		File gamesFolder = MiniGameWorldUtils.getMiniGamesDir();
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
		Utils.syncMapKeys(this.data, pureSettingData);

		// apply settings
		this.minigame.getSetting().setFileSetting(this.data);

		// restore edited values to fixed values
		if (this.minigame.getSetting().isSettingFixed()) {
			// minPlayers
			this.data.put(Setting.GAMES_MIN_PLAYERS, this.minigame.getMinPlayers());

			// maxPlayers
			this.data.put(Setting.GAMES_MAX_PLAYERS, this.minigame.getMaxPlayers());

			// playTime
			this.data.put(Setting.GAMES_PLAY_TIME, this.minigame.getPlayTime());

			// customData
			this.data.put(Setting.GAMES_CUSTOM_DATA, this.minigame.getCustomData());
		}

		// process exception
		// [IMPORTANT] this method needs for "/mw reload" (data reload)
		this.taskAfterDataSetup();
	}

	private void taskAfterDataSetup() {
		// load custom data
		this.minigame.loadCustomData();

		// init settings
		// [IMPORTANT] must be called after other settings(e.g. custom-data) setup
		this.minigame.initSettings();
	}

	public String getClassName() {
		return this.minigame.getClassName();
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public Map<String, Object> getData() {
		return this.data;
	}

	@Override
	public void setData(YamlManager yamlManager, FileConfiguration config) {
		this.yamlManager = yamlManager;

		if (config.isSet(this.getClassName())) {
			this.data = YamlHelper.ObjectToMap(config.getConfigurationSection(this.getClassName()));
		}
		config.set(this.getClassName(), this.data);

		// [IMPORTANT] This called after yaml reload (apply file data to minigame)
		applyMiniGameDataToInstance();
	}

	@Override
	public String getFileName() {
		// in "games" directory
		return Setting.MINIGAMES_DIR + File.separator + this.getClassName() + ".yml";
	}
}
