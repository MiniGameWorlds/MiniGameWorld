package com.minigameworld.dev;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.minigameworld.MiniGameWorldMain;
import com.minigameworld.frames.SoloBattleMiniGame;

public class GameA extends SoloBattleMiniGame implements Listener {
	Entity mob;

	public GameA() {
		super("GameA", 2, 5, 30, 5);

		Bukkit.getPluginManager().registerEvents(this, MiniGameWorldMain.getInstance());
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (isStarted()) {
			Player p = e.getPlayer();
			if (containsPlayer(p)) {
				sendMessage(p, "You broke a block!");
			}
		}
	}

	@Override
	protected List<String> tutorial() {
		return null;
	}
}
