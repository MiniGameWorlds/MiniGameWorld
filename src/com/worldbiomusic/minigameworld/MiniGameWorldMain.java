package com.worldbiomusic.minigameworld;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.worldbiomusic.minigameworld.api.MiniGameWorld;
import com.worldbiomusic.minigameworld.commands.MiniGameCommand;
import com.worldbiomusic.minigameworld.listeners.CommonEventListener;
import com.worldbiomusic.minigameworld.managers.DataManager;
import com.worldbiomusic.minigameworld.managers.MiniGameManager;
import com.worldbiomusic.minigameworld.util.Setting;
import com.worldbiomusic.minigameworld.util.Utils;

public class MiniGameWorldMain extends JavaPlugin {
	public static void main(String[] args) {
		// main Method for "Runnable Jar" option in Eclipse
		System.out.println("MiniGameWorld launched");
	}

	private static MiniGameWorldMain instance;
	private MiniGameManager minigameManager;
	private DataManager dataManager;

	private CommonEventListener commonLis;
	private MiniGameCommand minigameCommand;

	public static MiniGameWorldMain getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;
		Utils.info(ChatColor.GREEN + "================= MiniGameWorld =================");

		// setup settings
		this.setupSettings();

		// setup data
		this.setupData();

		// register listener
		this.registerEventListeners();

		// set command
		this.setCommandExecutors();

		// process works for remained players
		this.processRemainedPlayersWhenServerStart();
	}

	private void setupSettings() {
		this.minigameManager = MiniGameManager.getInstance();
		// MiniGameWorld wrapper class: set MiniGameManager
		MiniGameWorld minigameWorld = MiniGameWorld.create(Setting.API_VERSION);
		minigameWorld.setMiniGameManager(this.minigameManager);
	}

	private void setupData() {
		// yaml data manager
		this.dataManager = new DataManager(this);
		this.dataManager.registerYamlMember(this.minigameManager);
	}

	private void registerEventListeners() {
		this.commonLis = new CommonEventListener(this.minigameManager);
		getServer().getPluginManager().registerEvents(this.commonLis, this);
	}

	private void setCommandExecutors() {
		this.minigameCommand = new MiniGameCommand(this.minigameManager, this.dataManager);
		getCommand("minigame").setExecutor(this.minigameCommand);
	}

	private void processRemainedPlayersWhenServerStart() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			this.minigameManager.processPlayerJoinWorks(p);
		}
	}

	@Override
	public void onDisable() {
		this.processRemainedPlayersWhenServerStop();

		// remove not registered minigames setting data in minigames.yml
		this.minigameManager.removeNotExistMiniGameData();

		// save all data
		this.dataManager.saveAllData();

		// save backup data
		this.dataManager.saveBackupData();

		Utils.info(ChatColor.RED + "================= MiniGameWorld =================");
	}

	/**
	 * [PROBLEM] other .jar minigames instance already put down from JVM, so game
	 * instances are not found <br>
	 * See: {@link MiniGameManager#checkPluginIsDisabled}
	 */
	private void processRemainedPlayersWhenServerStop() {

		// process player quit work
		Bukkit.getOnlinePlayers().forEach(p -> minigameManager.processPlayerQuitWorks(p));
	}
}
