package com.wbm.minigamemaker;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.wbm.minigamemaker.games.FitTool;
import com.wbm.minigamemaker.games.MoreHit;
import com.wbm.minigamemaker.games.PVP;
import com.wbm.minigamemaker.games.RandomScore;
import com.wbm.minigamemaker.games.RemoveBlock;
import com.wbm.minigamemaker.games.RockScissorPaper;
import com.wbm.minigamemaker.games.ScoreClimbing;
import com.wbm.minigamemaker.manager.CommonEventListener;
import com.wbm.minigamemaker.manager.MiniGameCommand;
import com.wbm.minigamemaker.manager.MiniGameDataManager;
import com.wbm.minigamemaker.manager.MiniGameManager;
import com.wbm.minigamemaker.util.Setting;
import com.wbm.minigamemaker.wrapper.MiniGameMaker;
import com.wbm.plugin.util.data.yaml.YamlManager;

public class Main extends JavaPlugin {
	private static Main main;
	private MiniGameManager minigameManager;
	private MiniGameDataManager minigameDataM;

	private CommonEventListener commonLis;
	private MiniGameCommand minigameCommand;
	private YamlManager yamlM;

	public static Main getInstance() {
		return main;
	}

	@Override
	public void onEnable() {
		main = this;
		Setting.log(ChatColor.GREEN + "================= MiniGameMaker =================");

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

		// remove not registered minigames setting data in minigames.yml
		this.minigameDataM.removeNotExistMiniGameData();

		// save all data
		this.yamlM.saveAllData();
		Setting.log(ChatColor.RED + "================= MiniGameMaker =================");
	}
}
