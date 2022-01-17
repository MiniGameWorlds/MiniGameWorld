package com.worldbiomusic.minigameworld.api.observer;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

/**
 * Event notifier for minigame event(MiniGameEvent)<br>
 * 
 */
public interface MiniGameEventNotifier {
	/**
	 * MiniGame event
	 */
	public enum MiniGameEvent {
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
	 * Notify phaze(MiniGameEvent) to observers
	 * 
	 * @param minigame Minigame to notify
	 * @param event    Minigame Event
	 */
	public void notifyObservers(MiniGame minigame, MiniGameEvent event);
}