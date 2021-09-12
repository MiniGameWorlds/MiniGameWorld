package com.wbm.minigamemaker.observer;

import com.wbm.minigamemaker.api.MiniGameAccessor;

public interface MiniGameObserver {
	public void update(MiniGameEventNotifier.MiniGameEvent event, MiniGameAccessor minigame);
}