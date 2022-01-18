package com.worldbiomusic.minigameworld.api.observer;

import com.worldbiomusic.minigameworld.api.MiniGameAccessor;

/**
 * MiniGame timing observer<br>
 * - Usage: 3rd-party wiki
 * 
 * @see MiniGameTimingNotifier
 */
public interface MiniGameObserver {
	/**
	 * Process given minigame timing
	 * 
	 * @param timing    Given timing
	 * @param minigame Minigame related with the given timing
	 */
	public void update(MiniGameAccessor minigame, MiniGameTimingNotifier.Timing timing);
}