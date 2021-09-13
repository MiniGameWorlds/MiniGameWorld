package com.minigameworld.observer;

import com.minigameworld.api.MiniGameAccessor;

public interface MiniGameObserver {
	public void update(MiniGameEventNotifier.MiniGameEvent event, MiniGameAccessor minigame);
}