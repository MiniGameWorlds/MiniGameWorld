package com.minigameworld.observer;

/**
 * Event notifier with minigame phaze(MiniGameEvent)<br>
 * Already implemented in "MiniGame" class
 */
public interface MiniGameEventNotifier {
	/**
	 * MiniGame phaze
	 */
	public enum MiniGameEvent {
		/**
		 * When minigame started
		 */
		START,
		/**
		 * When minigame finished
		 */
		FINISH,

		/**
		 * When minigame gets exception
		 */
		EXCEPTION;
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
	 * @param event
	 */
	public void notifyObservers(MiniGameEvent event);
}
