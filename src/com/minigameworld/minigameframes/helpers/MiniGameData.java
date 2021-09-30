package com.minigameworld.minigameframes.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.io.Files;
import com.minigameworld.minigameframes.MiniGame;
import com.minigameworld.util.Utils;
import com.wbm.plugin.util.data.yaml.YamlHelper;
import com.wbm.plugin.util.data.yaml.YamlManager;
import com.wbm.plugin.util.data.yaml.YamlMember;

public class MiniGameData implements YamlMember {
	public static final String FOLDER_NAME = "minigames";
	private MiniGame minigame;
	private YamlManager yamlManager;
	private Map<String, Object> data;

	public MiniGameData(MiniGame minigame) {
		this.minigame = minigame;
		this.data = new HashMap<>();
	}

	public void addMiniGameData() {
		// [IMPORTANT] if do "this.data = minigame.getSetting().getFileSetting()", YamlManager's config
		// will lost this.data
		for (Entry<String, Object> entry : minigame.getSetting().getFileSetting().entrySet()) {
			this.data.put(entry.getKey(), entry.getValue());
		}
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
		// apply settings
		this.minigame.getSetting().setFileSetting(this.data);

		// when settingFixed is true: restore fixed values to file
		if (this.minigame.isSettingFixed()) {
			// minPlayerCount
			this.data.put("minPlayerCount", this.minigame.getMinPlayerCount());

			// maxPlayerCount
			this.data.put("maxPlayerCount", this.minigame.getMaxPlayerCount());

			// timeLimit
			this.data.put("timeLimit", this.minigame.getTimeLimit());

			// customData
			this.data.put("customData", this.minigame.getCustomData());
		}
	}

	public String getClassName() {
		return this.minigame.getClassName();
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

//package com.minigameworld.minigameframes.utils;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.bukkit.configuration.file.FileConfiguration;
//
//import com.google.common.io.Files;
//import com.minigameworld.minigameframes.MiniGame;
//import com.minigameworld.util.Utils;
//import com.wbm.plugin.util.data.yaml.YamlHelper;
//import com.wbm.plugin.util.data.yaml.YamlManager;
//import com.wbm.plugin.util.data.yaml.YamlMember;
//
//public class MiniGameData implements YamlMember {
//	public static final String FOLDER_NAME = "minigames";
//	private MiniGame minigame;
//	private YamlManager yamlManager;
//	private Map<String, Object> data;
//
//	public MiniGameData(MiniGame minigame) {
//		this.minigame = minigame;
//		this.data = new HashMap<>();
//		this.data.put(this.getClassName(), new HashMap<String, Object>());
//	}
//
//	public void addMiniGameData() {
//		this.data.put(this.getClassName(), minigame.getSetting().getFileSetting());
//	}
//
//	public boolean isMinigameDataExists() {
//		// search minigame file in folder
//		File file = this.getMiniGameFile();
//		if (file == null) {
//			return false;
//		}
//
//		// if file exist
//		if (((Map<String, Object>) this.data.get(this.getClassName())).isEmpty()) {
//			return false;
//		} else {
//			return true;
//		}
//	}
//
//	public File getMiniGameFile() {
//		File gamesFolder = Utils.getMiniGamesFolder();
//		for (File f : gamesFolder.listFiles()) {
//			String fileName = Files.getNameWithoutExtension(f.getName());
//			if (fileName.equals(this.getClassName())) {
//				return f;
//			}
//		}
//		return null;
//	}
//
//	// then overwrite saved minigame data to MiniGame instance
//	public void applyMiniGameDataToInstance() {
//		// apply settings
//		this.minigame.getSetting().setFileSetting(this.data);
//
//		// when settingFixed is true: restore fixed values to file
//		if (this.minigame.isSettingFixed()) {
//			// minPlayerCount
//			this.data.put("minPlayerCount", this.minigame.getMinPlayerCount());
//
//			// maxPlayerCount
//			this.data.put("maxPlayerCount", this.minigame.getMaxPlayerCount());
//
//			// timeLimit
//			this.data.put("timeLimit", this.minigame.getTimeLimit());
//
//			// customData
//			this.data.put("customData", this.minigame.getCustomData());
//		}
//	}
//
//	public String getClassName() {
//		return this.minigame.getClassName();
//	}
//
//	@Override
//	public void reload() {
//		this.yamlManager.reload(this);
//
//		// apply file data to minigame
//		this.applyMiniGameDataToInstance();
//	}
//
//	@Override
//	public void setData(YamlManager yamlManager, FileConfiguration config) {
//		this.yamlManager = yamlManager;
//
//		if (config.isSet("data")) {
//			this.data = YamlHelper.ObjectToMap(config.getConfigurationSection("data"));
//		}
//		config.set("data", this.data);
//	}
//
//	@Override
//	public String getFileName() {
//		// in "games" directory
//		return FOLDER_NAME + "/" + this.getClassName() + ".yml";
//	}
//}
