package com.wbm.minigamemaker;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.wbm.minigamemaker.games.FitTool;
import com.wbm.minigamemaker.games.MoreHit;
import com.wbm.minigamemaker.games.RandomScore;
import com.wbm.minigamemaker.manager.CommonEventListener;
import com.wbm.minigamemaker.manager.MiniGameDataManager;
import com.wbm.minigamemaker.manager.MiniGameManager;
import com.wbm.plugin.util.BroadcastTool;
import com.wbm.plugin.util.data.json.JsonDataManager;

public class Main extends JavaPlugin {
	private static Main main;
	MiniGameManager minigameManager;
	MiniGameDataManager minigameDataM;
	JsonDataManager jsonDataM;

	CommonEventListener commonLis;

	public static Main getInstance() {
		return main;
	}

//	public static void main(String[] args) {
//		Logger logger = Logger.getLogger(Main.class.getName());
//		logger.setLevel(Level.INFO); // INFO 이하의 로그 호출은 무시됨
//		logger.severe("severe log");
//		logger.warning("warning log");
//		logger.info("info log");
//	}

	@Override
	public void onEnable() {
		main = this;
		BroadcastTool.info(ChatColor.GREEN + "MiniGameMaker ON");

		this.minigameDataM = new MiniGameDataManager();
		this.minigameManager = MiniGameManager.getInstance();
		this.minigameManager.setMiniGameDataManager(this.minigameDataM);

		// setup data
		this.setupData();

		this.commonLis = new CommonEventListener(this.minigameManager);
		getServer().getPluginManager().registerEvents(this.commonLis, this);

		// 예시 미니게임
		this.minigameManager.registerMiniGame(new FitTool());
		this.minigameManager.registerMiniGame(new RandomScore());
		this.minigameManager.registerMiniGame(new MoreHit());

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
