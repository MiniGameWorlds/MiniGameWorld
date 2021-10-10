package com.worldbiomusic.minigameworld.managers;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.wbm.plugin.util.data.yaml.YamlManager;
import com.wbm.plugin.util.data.yaml.YamlMember;
import com.wbm.plugin.util.instance.BackupDataManager;
import com.worldbiomusic.minigameworld.util.Utils;

public class DataManager {
	private YamlManager yamlManager;
	private BackupDataManager backupDataManager;

	public DataManager(JavaPlugin main) {
		// YamlManager
		this.yamlManager = new YamlManager(main.getDataFolder());

		// BackupDataManager
		this.backupDataManager = new BackupDataManager(main);
	}

	public void registerYamlMember(YamlMember member) {
		this.yamlManager.registerMember(member);
	}

	public void saveAllData() {
		this.yamlManager.saveAllData();
		Utils.info(ChatColor.GREEN + "All data saved");
	}

	public void saveBackupData() {
		this.backupDataManager.saveBackupData();
		Utils.info(ChatColor.BLUE + "Backup data saved");
	}
}
