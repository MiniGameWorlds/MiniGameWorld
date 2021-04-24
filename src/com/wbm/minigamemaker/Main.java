package com.wbm.minigamemaker;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.wbm.minigamemaker.manager.CommonEventListener;
import com.wbm.minigamemaker.manager.MiniGameManager;
import com.wbm.plugin.util.BroadcastTool;
import com.wbm.plugin.util.data.json.JsonDataManager;

public class Main extends JavaPlugin {
	private static Main main;
	MiniGameManager minigameManager;
	JsonDataManager jsonDataM;

	public static Main getInstance() {
		return main;
	}

	@Override
	public void onEnable() {
		main = this;
		BroadcastTool.info(ChatColor.GREEN + "MiniGameMaker ON");

		this.minigameManager = MiniGameManager.getInstance();

		// setup data
		this.setupData();

		getServer().getPluginManager().registerEvents(new CommonEventListener(this.minigameManager), this);
	}

	void setupData() {
		this.jsonDataM = new JsonDataManager(this.getDataFolder());
		this.jsonDataM.registerMember(this.minigameManager);

		this.jsonDataM.distributeAllData();
	}

	@Override
	public void onDisable() {

		this.jsonDataM.saveAllData();
		BroadcastTool.warn(ChatColor.RED + "MiniGameMaker OFF");
	}
}
