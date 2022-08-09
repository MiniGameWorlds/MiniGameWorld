package com.minigameworld.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.minigameworld.events.minigame.MiniGameExceptionEvent;
import com.minigameworld.managers.MiniGameManager;

public class MiniGameEventListener implements Listener {
	private MiniGameManager minigameManager;

	public MiniGameEventListener(MiniGameManager minigameManager) {
		this.minigameManager = minigameManager;
	}

	@EventHandler
	public void onMiniGameExceptionEvent(MiniGameExceptionEvent e) {
		this.minigameManager.handleException(e);
	}

	
}
