package com.wbm.minigamemaker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.wbm.minigamemaker.commands.MiniGameCommand;
import com.wbm.minigamemaker.games.FitTool;
import com.wbm.minigamemaker.games.MoreHit;
import com.wbm.minigamemaker.games.PVP;
import com.wbm.minigamemaker.games.RandomScore;
import com.wbm.minigamemaker.games.RemoveBlock;
import com.wbm.minigamemaker.games.RockScissorPaper;
import com.wbm.minigamemaker.games.ScoreClimbing;
import com.wbm.minigamemaker.games.frame.MiniGame;
import com.wbm.minigamemaker.manager.CommonEventListener;
import com.wbm.minigamemaker.manager.MiniGameDataManager;
import com.wbm.minigamemaker.manager.MiniGameManager;
import com.wbm.minigamemaker.observer.MiniGameEventNotifier.MiniGameEvent;
import com.wbm.minigamemaker.util.Utils;
import com.wbm.minigamemaker.wrapper.MiniGameMaker;
import com.wbm.plugin.util.data.yaml.YamlManager;

public class MiniGameMakerMain extends JavaPlugin {
	public static void main(String[] args) {
		// eclipse에서 "Runnable Jar"로 추출할 때 main메소드가 필요해서 넣은 임시 메소드
		// Runnable Jar로 추출하면 사용하는 라이브러리를 한 jar파일에 넣을 수 있는 장점이 있음
		System.out.println("MiniGameMaker launched");
	}

	private static MiniGameMakerMain instance;
	private MiniGameManager minigameManager;
	private MiniGameDataManager minigameDataM;

	private CommonEventListener commonLis;
	private MiniGameCommand minigameCommand;
	private YamlManager yamlM;

	public static MiniGameMakerMain getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;
		Utils.log(ChatColor.GREEN + "================= MiniGameMaker =================");

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
		this.minigameDataM = new MiniGameDataManager(this.minigameManager);
		this.minigameManager.setMiniGameDataManager(this.minigameDataM);
		// MiniGameMaker wrapper class: set MiniGameManager
		MiniGameMaker minigameMaker = MiniGameMaker.create();
		minigameMaker.setMiniGameManager(this.minigameManager);
	}

	private void setupData() {
		// yaml data manager
		this.yamlM = new YamlManager(this.getDataFolder());
		this.yamlM.registerMember(this.minigameDataM);
		this.yamlM.registerMember(this.minigameManager);
	}

	private void registerEventListeners() {
		this.commonLis = new CommonEventListener(this.minigameManager);
		getServer().getPluginManager().registerEvents(this.commonLis, this);
	}

	private void setCommandExecutors() {
		this.minigameCommand = new MiniGameCommand(this.minigameManager, this.minigameDataM);
		getCommand("minigame").setExecutor(this.minigameCommand);

	}

	private void registerMiniGames() {
		// register minigames
		MiniGameMaker minigameMaker = MiniGameMaker.create();
		minigameMaker.registerMiniGame(new FitTool());
		minigameMaker.registerMiniGame(new RandomScore());
		minigameMaker.registerMiniGame(new MoreHit());
		minigameMaker.registerMiniGame(new ScoreClimbing());
		minigameMaker.registerMiniGame(new RockScissorPaper());
		minigameMaker.registerMiniGame(new PVP());
		minigameMaker.registerMiniGame(new RemoveBlock());
	}

	@Override
	public void onDisable() {
		// send all MiniGame with SEVER_STOP exception
		for (Player p : Bukkit.getOnlinePlayers()) {
			this.minigameManager.handleException(p, MiniGame.Exception.SERVER_STOP, null);
		}

		// remove not registered minigames setting data in minigames.yml
		this.minigameDataM.removeNotExistMiniGameData();

		// save all data
		this.yamlM.saveAllData();
		Utils.log(ChatColor.RED + "================= MiniGameMaker =================");
	}
}
