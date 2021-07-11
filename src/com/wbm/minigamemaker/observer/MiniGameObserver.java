package com.wbm.minigamemaker.observer;

import com.wbm.minigamemaker.wrapper.MiniGameAccessor;

public interface MiniGameObserver {
	public void update(MiniGameEventNotifier.MiniGameEvent event, MiniGameAccessor minigameAccessor);
}
