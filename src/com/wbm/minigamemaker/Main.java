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
import com.wbm.plugin.util.data.json.JsonDataManager;

public class Main extends JavaPlugin {
	private static Main main;
	private MiniGameManager minigameManager;
	private MiniGameDataManager minigameDataM;
	private JsonDataManager jsonDataM;

	private CommonEventListener commonLis;
	private MiniGameCommand minigameCommand;

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
		// json data
		this.jsonDataM = new JsonDataManager(this.getDataFolder());
		this.jsonDataM.registerMember(this.minigameDataM);
		this.jsonDataM.registerMember(this.minigameManager);

		// distribute data to all members
		this.jsonDataM.distributeAllData();
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

		this.jsonDataM.saveAllData();
		BroadcastTool.warn(ChatColor.RED + "MiniGameMaker OFF");
	}
}
