package com.wbm.minigamemaker;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.wbm.minigamemaker.manager.CommonEventListener;
import com.wbm.minigamemaker.manager.MiniGameManager;
import com.wbm.plugin.util.BroadcastTool;
import com.wbm.plugin.util.data.json.JsonDataManager;

public class Main extends JavaPlugin {
	private static Main main;
	MiniGameManager minigameManager;

	public static void main(String[] args) {
		System.out.println("start");
		JsonDataManager jsonDataM = new JsonDataManager(new File("MiniGameMaker"));
		System.out.println("end");
	}

	public static Main getInstance() {
		return main;
	}

	@Override
	public void onEnable() {
		main = this;
		BroadcastTool.info(ChatColor.GREEN + "MiniGameMaker ON");

		this.minigameManager = MiniGameManager.getInstance();

		getServer().getPluginManager().registerEvents(new CommonEventListener(this.minigameManager), this);
	}

	@Override
	public void onDisable() {
		BroadcastTool.warn(ChatColor.RED + "MiniGameMaker OFF");
	}
}
