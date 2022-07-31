package com.minigameworld;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.minigameworld.api.MiniGameWorld;
import com.minigameworld.api.MwUtil;
import com.minigameworld.commands.MiniGameCommand;
import com.minigameworld.dev.TestGame;
import com.minigameworld.listeners.CommonEventListener;
import com.minigameworld.listeners.FunctionItemListener;
import com.minigameworld.listeners.MiniGameEventListener;
import com.minigameworld.managers.DataManager;
import com.minigameworld.managers.MiniGameManager;
import com.minigameworld.managers.language.LanguageManager;
import com.minigameworld.util.DependencyChecker;
import com.minigameworld.util.Setting;
import com.minigameworld.util.UpdateChecker;
import com.minigameworld.util.Utils;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.wbm.plugin.util.Metrics;

public class MiniGameWorldMain extends JavaPlugin {
	public static void main(String[] args) {
		// main Method for "Runnable Jar" option in Eclipse
		// TODO: Make JFrame
		System.out.println("MiniGameWorld launched");
	}

	private static MiniGameWorldMain instance;
	private static MultiverseCore multiverseCore;

	private MiniGameManager minigameManager;
	private DataManager dataManager;
	private LanguageManager languageManager;

	private CommonEventListener commonListener;
	private FunctionItemListener functionItemListener;
	private MiniGameEventListener miniGameEventListener;

	private MiniGameCommand minigameCommand;

	public static MiniGameWorldMain getInstance() {
		return instance;
	}

	public static MultiverseCore multiverseCore() {
		return multiverseCore;
	}

	@Override
	public void onEnable() {
		instance = this;

		printPluginInfo();

		// check all dependencies are installed
		if (!checkDependencies()) {
			return;
		}

		// setup settings
		setupSettings();

		// setup data
		setupData();

		// register listener
		registerEventListeners();

		// set command
		setCommandExecutors();

		// process works for remained players
		onServerRestart();

		dev();
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

	private boolean checkDependencies() {
		Utils.info(ChatColor.RESET + "                  Dependency                 ");
		boolean dependencyCheck = new DependencyChecker().checkAll();
		Utils.info(ChatColor.GREEN + "=============================================");

		// check all dependencies exist
		if (dependencyCheck) {
			// setup dependency instance
			multiverseCore = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		} else {
			Bukkit.getPluginManager().disablePlugin(MiniGameWorldMain.getInstance());
		}

		return dependencyCheck;
	}

	private void setupSettings() {
		// BStats
		new Metrics(this, Setting.BSTATS_PLUGIN_ID);

		this.minigameManager = MiniGameManager.getInstance();
		// MiniGameWorld wrapper class: set MiniGameManager
		MiniGameWorld minigameWorld = MiniGameWorld.create(Setting.API_VERSION);
		minigameWorld.setMiniGameManager(this.minigameManager);

		// setup MwUtil
		MwUtil.setMiniGameManager(minigameManager);
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

		// load all worlds (cause of world-instance-system requires worlds are pre
		// loaded)
		Utils.loadTemplateWorlds();

		Utils.warning("@@@@@@@ Loaded worlds @@@@@@@@@");
		Bukkit.getWorlds().forEach(w -> Utils.warning("world name: " + w.getName()));
	}

	private void registerEventListeners() {
		this.commonListener = new CommonEventListener(this.minigameManager);
		this.miniGameEventListener = new MiniGameEventListener(this.minigameManager);
		this.functionItemListener = new FunctionItemListener(minigameManager);
		Utils.registerEventListener(this.commonListener);
		Utils.registerEventListener(this.miniGameEventListener);
		Utils.registerEventListener(this.functionItemListener);
	}

	private void setCommandExecutors() {
		this.minigameCommand = new MiniGameCommand(this.minigameManager, this.dataManager);
		getCommand("minigame").setExecutor(this.minigameCommand);
	}

	private void onServerRestart() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			this.minigameManager.todoOnPlayerJoin(p);
		}
	}

	private void dev() {
		// only in debug
		if (!Setting.DEBUG_MODE) {
			return;
		}
		
		MiniGameWorld mw = MiniGameWorld.create(MiniGameWorld.API_VERSION);
		mw.registerGame(new TestGame());
	}

	@Override
	public void onDisable() {
		this.onRemainPlayers();

		// remove not registered minigames setting data in minigames.yml
		this.minigameManager.removeNotExistGameData();

		Utils.info(ChatColor.RED + "=============================================");
		Utils.info(ChatColor.RESET + "                MiniGameWorld                ");
		Utils.info(ChatColor.RED + "=============================================");

		Utils.info(ChatColor.RESET + "                Data Manager                 ");

		// save backup data
		this.dataManager.saveBackupData();
		Utils.info(" - Server data saved");
		Utils.info(" - Backup data created");

		Utils.info(ChatColor.RED + "=============================================");
//
//		Utils.info(ChatColor.RED + "Deleting used instance worlds...");
//		LocationManager.getUsedLocations().forEach(w -> {
//			Utils.info(ChatColor.RED + "- " + w);
//		});
	}

	/**
	 * [PROBLEM] other .jar minigames instance already put down from JVM, so game
	 * instances are not found <br>
	 * See: {@link MiniGameManager#checkPluginIsDisabled}
	 */
	private void onRemainPlayers() {
		// process player quit work
		Bukkit.getOnlinePlayers().forEach(p -> minigameManager.todoOnPlayerQuit(p));
	}
}
