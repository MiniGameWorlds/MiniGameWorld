package com.minigameworld;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.minigameworld.api.MiniGameWorld;
import com.minigameworld.commands.MiniGameCommand;
import com.minigameworld.listeners.CommonEventListener;
import com.minigameworld.manager.MiniGameManager;
import com.minigameworld.minigameframes.MiniGame;
import com.minigameworld.minigameframes.games.FitTool;
import com.minigameworld.minigameframes.games.MoreHit;
import com.minigameworld.minigameframes.games.PVP;
import com.minigameworld.minigameframes.games.RandomScore;
import com.minigameworld.minigameframes.games.RemoveBlock;
import com.minigameworld.minigameframes.games.RockScissorPaper;
import com.minigameworld.minigameframes.games.ScoreClimbing;
import com.minigameworld.util.Utils;
import com.wbm.plugin.util.data.yaml.YamlManager;

public class MiniGameWorldMain extends JavaPlugin {
	public static void main(String[] args) {
		// eclipse에서 "Runnable Jar"로 추출할 때 main메소드가 필요해서 넣은 임시 메소드
		// Runnable Jar로 추출하면 사용하는 라이브러리를 한 jar파일에 넣을 수 있는 장점이 있음
		System.out.println("MiniGameWorld launched");
	}

	private static MiniGameWorldMain instance;
	private MiniGameManager minigameManager;

	private CommonEventListener commonLis;
	private MiniGameCommand minigameCommand;
	private YamlManager yamlM;

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

		// register minigames
		this.registerMiniGames();
	}

	private void setupSettings() {
		this.minigameManager = MiniGameManager.getInstance();
		// MiniGameWorld wrapper class: set MiniGameManager
		MiniGameWorld minigameWorld = MiniGameWorld.create();
		minigameWorld.setMiniGameManager(this.minigameManager);
	}

	private void setupData() {
		// yaml data manager
		this.yamlM = new YamlManager(this.getDataFolder());
		this.yamlM.registerMember(this.minigameManager.getMiniGameDataManager());
		this.yamlM.registerMember(this.minigameManager);
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
		MiniGameWorld minigameWorld = MiniGameWorld.create();
		minigameWorld.registerMiniGame(new FitTool());
		minigameWorld.registerMiniGame(new RandomScore());
		minigameWorld.registerMiniGame(new MoreHit());
		minigameWorld.registerMiniGame(new ScoreClimbing());
		minigameWorld.registerMiniGame(new RockScissorPaper());
		minigameWorld.registerMiniGame(new PVP());
		minigameWorld.registerMiniGame(new RemoveBlock());
	}

	@Override
	public void onDisable() {
		// send all MiniGame with SEVER_STOP exception
		for (Player p : Bukkit.getOnlinePlayers()) {
			this.minigameManager.handleException(p, MiniGame.Exception.SERVER_STOP, null);
		}

		// remove not registered minigames setting data in minigames.yml
		minigameManager.getMiniGameDataManager().removeNotExistMiniGameData();

		// save all data
		this.yamlM.saveAllData();
		Utils.info(ChatColor.RED + "================= MiniGameWorld =================");
	}
}
