package com.wbm.minigamemaker;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.wbm.minigamemaker.games.FitTool;
import com.wbm.minigamemaker.games.MoreHit;
import com.wbm.minigamemaker.games.RandomScore;
import com.wbm.minigamemaker.games.RelayJump;
import com.wbm.minigamemaker.games.ScoreClimbing;
import com.wbm.minigamemaker.manager.CommonEventListener;
import com.wbm.minigamemaker.manager.MiniGameCommand;
import com.wbm.minigamemaker.manager.MiniGameDataManager;
import com.wbm.minigamemaker.manager.MiniGameManager;
import com.wbm.plugin.util.BroadcastTool;
import com.wbm.plugin.util.data.json.JsonDataManager;

public class Main extends JavaPlugin {
	public static void main(String[] args) {
		
	}
	private static Main main;
	MiniGameManager minigameManager;
	MiniGameDataManager minigameDataM;
	JsonDataManager jsonDataM;

	CommonEventListener commonLis;
	MiniGameCommand minigameCommand;

	public static Main getInstance() {
		return main;
	}

	@Override
	public void onEnable() {
		main = this;
		BroadcastTool.info(ChatColor.GREEN + "MiniGameMaker ON");

		this.minigameDataM = new MiniGameDataManager();
		this.minigameManager = MiniGameManager.getInstance();
		this.minigameManager.setMiniGameDataManager(this.minigameDataM);

		// setup data
		this.setupData();

		// listener
		this.commonLis = new CommonEventListener(this.minigameManager);
		getServer().getPluginManager().registerEvents(this.commonLis, this);

		// command
		this.minigameCommand = new MiniGameCommand(this.minigameManager);
		getCommand("minigame").setExecutor(this.minigameCommand);

		// 예시 미니게임
		this.minigameManager.registerMiniGame(new FitTool());
		this.minigameManager.registerMiniGame(new RandomScore());
		this.minigameManager.registerMiniGame(new MoreHit());
		this.minigameManager.registerMiniGame(new ScoreClimbing());
		this.minigameManager.registerMiniGame(new RelayJump());
	}

	void setupData() {
		this.jsonDataM = new JsonDataManager(this.getDataFolder());
		this.jsonDataM.registerMember(this.minigameDataM);
		this.jsonDataM.registerMember(this.minigameManager);

		this.jsonDataM.distributeAllData();
	}

	@Override
	public void onDisable() {

		this.jsonDataM.saveAllData();
		BroadcastTool.warn(ChatColor.RED + "MiniGameMaker OFF");
	}
}
