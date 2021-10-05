package com.minigameworld.observer;

import com.minigameworld.api.MiniGameAccessor;

/**
 * MiniGame phaze(MiniGameEvent) observer<br>
 * - Usage: see wiki
 */
public interface MiniGameObserver {
	/**
	 * Process sended minigame event
	 * 
	 * @param event    Sended phaze(MiniGameEvent)
	 * @param minigame MiniGame that sended event
	 */
	public void update(MiniGameEventNotifier.MiniGameEvent event, MiniGameAccessor minigame);
}