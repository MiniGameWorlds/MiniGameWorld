package com.worldbiomusic.minigameworld.observer;

/**
 * Event notifier with minigame phaze(MiniGameEvent)<br>
 * Already implemented in "MiniGame" class<br><br>
 * 
 * [IMPORTANT] Consider to change to use custom event <br>
 * 
 * 
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
		EXCEPTION,
		
		/**
		 * When event passed to the minigame<br>
		 * But minigame still process event in last<br> 
		 */
		EVENT_PASSED;
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
