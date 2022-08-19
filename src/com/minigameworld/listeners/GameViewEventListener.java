package com.minigameworld.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.minigameworld.frames.MiniGame;
import com.minigameworld.frames.helpers.MiniGameViewManager;
import com.minigameworld.managers.MiniGameManager;

/**
 * If player can be checked with event, use {@link #getViewingManager(Player)}
 * and pass event.<br>
 * If player can not be checked, just pass to the all minigame view managers
 *
 */
public class GameViewEventListener implements Listener {
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		MiniGameViewManager viewManager = getViewingManager(p);
		if (viewManager != null) {
			viewManager.onChat(e);
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		MiniGameViewManager viewManager = getViewingManager(p);
		if (viewManager != null) {
			viewManager.onRespawn(e);
		}
	}

	private MiniGameViewManager getViewingManager(Player p) {
		for (MiniGame g : MiniGameManager.getInstance().getInstanceGames()) {
			MiniGameViewManager viewManager = g.viewManager();
			if (viewManager.isViewing(p)) {
				return viewManager;
			}
		}
		return null;
	}
}
