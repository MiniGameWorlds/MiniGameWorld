package com.worldbiomusic.minigameworld.api.observer;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

/**
 * Timing notifier for minigame<br>
 * 
 */
public interface MiniGameTimingNotifier {
	/**
	 * MiniGame timing
	 */
	public enum Timing {
		/**
		 * When a minigame is registered to MiniGameWolrd plugin
		 */
		REGISTRATION,

		/**
		 * When a minigame is unregistered from MiniGameWorld plugin
		 */
		UNREGISTRATION;
	}

	/**
	 * Reigsters observer
	 * 
	 * @param observer Observer to register
	 */
	public void registerObserver(MiniGameObserver observer);

	/**
	 * Unregisters observer
	 * 
	 * @param observer Observer to unregister
	 */
	public void unregisterObserver(MiniGameObserver observer);

	/**
	 * Notify timing to observers
	 * 
	 * @param minigame Minigame to notify
	 * @param timing   Minigame timing
	 */
	public void notifyObservers(MiniGame minigame, Timing timing);
}