package com.wbm.minigamemaker;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.wbm.minigamemaker.manager.CommonEventListener;
import com.wbm.minigamemaker.manager.MiniGameManager;

public class Main extends JavaPlugin {
	private static Main main;
	MiniGameManager minigameManager;
	
	public static Main getInstance() {
		return main;
	}
	
	@Override
	public void onEnable() {
		main = this;
		getLogger().info(ChatColor.GREEN + "MiniGameMaker ON");
		
		this.minigameManager = new MiniGameManager();
		
		getServer().getPluginManager().registerEvents(new CommonEventListener(this.minigameManager), this);
	}

	@Override
	public void onDisable() {
		getLogger().info(ChatColor.RED + "MiniGameMaker OFF");
	}
}
