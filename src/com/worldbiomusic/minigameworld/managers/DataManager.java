package com.worldbiomusic.minigameworld.managers;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import com.wbm.plugin.util.data.yaml.YamlManager;
import com.wbm.plugin.util.data.yaml.YamlMember;
import com.wbm.plugin.util.instance.BackupDataManager;
import com.worldbiomusic.minigameworld.util.Setting;

public class DataManager {
	private YamlManager yamlManager;
	private BackupDataManager backupDataManager;

	public DataManager(JavaPlugin main) {
		// YamlManager
		this.yamlManager = new YamlManager(main.getDataFolder());

		// BackupDataManager
		this.backupDataManager = new BackupDataManager(main);
		this.backupDataManager.startSavingBackupDataTask(Setting.BACKUP_DELAY);
	}

	public void registerYamlMember(YamlMember member) {
		this.yamlManager.registerMember(member);
	}

	public void saveAllData() {
		this.yamlManager.saveAllData();
	}

	public void saveBackupData() {
		saveBackupData(null);
	}

	public File saveBackupData(String directory) {
		// save all data to file befre backup data
		saveAllData();

		return this.backupDataManager.saveBackupData(directory);
	}

	public boolean loadBackupData(String directory, File dstDir) {
		return this.backupDataManager.loadBackupData(directory, dstDir);
	}

	public boolean existBackupData(String directory) {
		return this.backupDataManager.existBackupData(directory);
	}
	
	public File getBackupDir() {
		return this.backupDataManager.getBackupDir();
	}

	public void reloadAllData() {
		this.yamlManager.reloadAllData();
	}

	public void reload(YamlMember member) {
		this.yamlManager.reload(member);
	}
}
