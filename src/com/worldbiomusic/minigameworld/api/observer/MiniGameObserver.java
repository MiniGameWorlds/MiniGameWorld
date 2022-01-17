package com.worldbiomusic.minigameworld.api.observer;

import com.worldbiomusic.minigameworld.api.MiniGameAccessor;

/**
 * MiniGame event observer<br>
 * - Usage: 3rd-party wiki
 * 
 * @see MiniGameEventNotifier
 */
public interface MiniGameObserver {
	/**
	 * Process given minigame event
	 * 
	 * @param event    Given event
	 * @param minigame Minigame related with the given event
	 */
	public void update(MiniGameAccessor minigame, MiniGameEventNotifier.MiniGameEvent event);
}