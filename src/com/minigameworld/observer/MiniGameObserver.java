package com.minigameworld.observer;

import com.minigameworld.api.MiniGameAccessor;

public interface MiniGameObserver {
	// called from MiniGame
	public void update(MiniGameEventNotifier.MiniGameEvent event, MiniGameAccessor minigame);
}