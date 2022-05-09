package com.worldbiomusic.minigameworld;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.wbm.plugin.util.Metrics;
import com.worldbiomusic.minigameworld.api.MiniGameWorld;
import com.worldbiomusic.minigameworld.api.MiniGameWorldUtils;
import com.worldbiomusic.minigameworld.commands.MiniGameCommand;
import com.worldbiomusic.minigameworld.listeners.CommonEventListener;
import com.worldbiomusic.minigameworld.listeners.FunctionItemListener;
import com.worldbiomusic.minigameworld.listeners.MiniGameEventListener;
import com.worldbiomusic.minigameworld.managers.DataManager;
import com.worldbiomusic.minigameworld.managers.EventListenerManager;
import com.worldbiomusic.minigameworld.managers.MiniGameManager;
import com.worldbiomusic.minigameworld.managers.language.LanguageManager;
import com.worldbiomusic.minigameworld.util.Setting;
import com.worldbiomusic.minigameworld.util.UpdateChecker;
import com.worldbiomusic.minigameworld.util.Utils;

public class MiniGameWorldMain extends JavaPlugin {
	public static void main(String[] args) {
		// main Method for "Runnable Jar" option in Eclipse
		// TODO: Make JFrame
		System.out.println("MiniGameWorld launched");
	}

	private static MiniGameWorldMain instance;
	private MiniGameManager minigameManager;
	private DataManager dataManager;
	private LanguageManager languageManager;
	private EventListenerManager eventListenerManager;

	private CommonEventListener commonListener;
	private FunctionItemListener functionItemListener;
	private MiniGameEventListener miniGameEventListener;
	private MiniGameCommand minigameCommand;

	public static MiniGameWorldMain getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;

		printPluginInfo();

		// setup settings
		setupSettings();

		// setup data
		setupData();

		// register listener
		registerEventListeners();

		// set command
		setCommandExecutors();

		// process works for remained players
		processRemainedPlayersWhenServerStart();
	}

	private void printPluginInfo() {
		Utils.info(ChatColor.GREEN + "=============================================");
		Utils.info(ChatColor.RESET + "                MiniGameWorld                ");
		Utils.info(ChatColor.GREEN + "=============================================");
		Utils.info(ChatColor.RESET + "                   Contact                   ");
		Utils.info(ChatColor.RESET + " - Discord: https://discord.com/invite/fJbxSy2EjA");
		Utils.info(ChatColor.RESET + " - E-mail:  worldbiomusic@gmail.com");
		Utils.info(ChatColor.GREEN + "=============================================");
	}

	private void setupSettings() {
		// BStats
		new Metrics(this, Setting.BSTATS_PLUGIN_ID);

		this.minigameManager = MiniGameManager.getInstance();
		// MiniGameWorld wrapper class: set MiniGameManager
		MiniGameWorld minigameWorld = MiniGameWorld.create(Setting.API_VERSION);
		minigameWorld.setMiniGameManager(this.minigameManager);

		// setup MiniGameWorldUtils
		MiniGameWorldUtils.setMiniGameManager(minigameManager);
	}

	private void setupData() {
		// yaml data manager
		this.dataManager = new DataManager(this);
		this.dataManager.registerYamlMember(this.minigameManager);

		// check update
		// [IMPORTANT] run after DataManager init
		if (Setting.CHECK_UPDATE) {
			UpdateChecker.check();
		}

		// language files
		// [IMPORTANT] run after DataManager init
		this.languageManager = new LanguageManager(this.dataManager);
	}

	private void registerEventListeners() {
		this.commonListener = new CommonEventListener(this.minigameManager);
		this.miniGameEventListener = new MiniGameEventListener(this.minigameManager);
		this.functionItemListener = new FunctionItemListener(minigameManager);
		getServer().getPluginManager().registerEvents(this.commonListener, this);
		getServer().getPluginManager().registerEvents(this.miniGameEventListener, this);
		getServer().getPluginManager().registerEvents(this.functionItemListener, this);

		this.eventListenerManager = new EventListenerManager(this.commonListener);
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

		Utils.info(ChatColor.RED + "=============================================");
		Utils.info(ChatColor.RESET + "                MiniGameWorld                ");
		Utils.info(ChatColor.RED + "=============================================");

		Utils.info(ChatColor.RESET + "                Data Manager                 ");


		// save backup data
		this.dataManager.saveBackupData();
		Utils.info(" - Server data saved");
		Utils.info(" - Backup data created");

		Utils.info(ChatColor.RED + "=============================================");
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
