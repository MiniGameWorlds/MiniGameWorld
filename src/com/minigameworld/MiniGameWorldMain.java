package com.minigameworld;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.minigameworld.api.MiniGameWorld;
import com.minigameworld.commands.MiniGameCommand;
import com.minigameworld.listeners.CommonEventListener;
import com.minigameworld.managers.DataManager;
import com.minigameworld.managers.MiniGameManager;
import com.minigameworld.minigameframes.games.BreedMob;
import com.minigameworld.minigameframes.games.FallingBlock;
import com.minigameworld.minigameframes.games.FitTool;
import com.minigameworld.minigameframes.games.HiddenArcher;
import com.minigameworld.minigameframes.games.MoreHit;
import com.minigameworld.minigameframes.games.PVP;
import com.minigameworld.minigameframes.games.PassMob;
import com.minigameworld.minigameframes.games.RandomScore;
import com.minigameworld.minigameframes.games.RemoveBlock;
import com.minigameworld.minigameframes.games.RockScissorPaper;
import com.minigameworld.minigameframes.games.ScoreClimbing;
import com.minigameworld.minigameframes.games.SuperMob;
import com.minigameworld.util.Setting;
import com.minigameworld.util.Utils;

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

//		// register minigames
//		this.registerMiniGames();

		// process works for remained players
		this.processRemainedPlayersWhenServerStart();
	}

	private void setupSettings() {
		this.minigameManager = MiniGameManager.getInstance();
		// MiniGameWorld wrapper class: set MiniGameManager
		MiniGameWorld minigameWorld = MiniGameWorld.create(Setting.VERSION);
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
		this.minigameCommand = new MiniGameCommand(this.minigameManager);
		getCommand("minigame").setExecutor(this.minigameCommand);
	}

	private void registerMiniGames() {
		// register minigames
		if (!MiniGameWorld.checkCompatibleVersion(Setting.VERSION)) {
			return;
		}
		MiniGameWorld minigameWorld = MiniGameWorld.create(Setting.VERSION);
		minigameWorld.registerMiniGame(new FitTool());
		minigameWorld.registerMiniGame(new RandomScore());
		minigameWorld.registerMiniGame(new MoreHit());
		minigameWorld.registerMiniGame(new ScoreClimbing());
		minigameWorld.registerMiniGame(new RockScissorPaper());
		minigameWorld.registerMiniGame(new PVP());
		minigameWorld.registerMiniGame(new RemoveBlock());
		minigameWorld.registerMiniGame(new HiddenArcher());
		minigameWorld.registerMiniGame(new BreedMob());
		minigameWorld.registerMiniGame(new SuperMob());
		minigameWorld.registerMiniGame(new PassMob());
		minigameWorld.registerMiniGame(new FallingBlock());
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

	private void processRemainedPlayersWhenServerStop() {
		// finish all minigames
		this.minigameManager.getMiniGameList().forEach(game -> game.finishGame());

		// process player quit work
		Bukkit.getOnlinePlayers().forEach(p -> minigameManager.processPlayerQuitWorks(p));
	}
}
