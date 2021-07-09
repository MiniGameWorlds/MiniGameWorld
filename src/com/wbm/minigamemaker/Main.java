package com.wbm.minigamemaker;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.wbm.minigamemaker.games.FitTool;
import com.wbm.minigamemaker.games.MoreHit;
import com.wbm.minigamemaker.games.PVP;
import com.wbm.minigamemaker.games.RandomScore;
import com.wbm.minigamemaker.games.RelayJump;
import com.wbm.minigamemaker.games.RockScissorPaper;
import com.wbm.minigamemaker.games.ScoreClimbing;
import com.wbm.minigamemaker.manager.CommonEventListener;
import com.wbm.minigamemaker.manager.MiniGameCommand;
import com.wbm.minigamemaker.manager.MiniGameDataManager;
import com.wbm.minigamemaker.manager.MiniGameManager;
import com.wbm.plugin.util.BroadcastTool;
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
		BroadcastTool.info(ChatColor.GREEN + "MiniGameMaker ON");

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
		this.minigameDataM = new MiniGameDataManager();
		this.minigameManager = MiniGameManager.getInstance();
		this.minigameManager.setMiniGameDataManager(this.minigameDataM);
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
		this.minigameCommand = new MiniGameCommand(this.minigameManager);
		getCommand("minigame").setExecutor(this.minigameCommand);

	}

	private void registerMiniGames() {
		// register minigames
		this.minigameManager.registerMiniGame(new FitTool());
		this.minigameManager.registerMiniGame(new RandomScore());
		this.minigameManager.registerMiniGame(new MoreHit());
		this.minigameManager.registerMiniGame(new ScoreClimbing());
		this.minigameManager.registerMiniGame(new RelayJump());
		this.minigameManager.registerMiniGame(new RockScissorPaper());
		this.minigameManager.registerMiniGame(new PVP());
	}

	@Override
	public void onDisable() {

		// remove not registered minigames setting data in minigames.yml 
		this.minigameDataM.removeNotExistMiniGameData();
		
		// save all data
		this.yamlM.saveAllData();
		BroadcastTool.warn(ChatColor.RED + "MiniGameMaker OFF");
	}
}
