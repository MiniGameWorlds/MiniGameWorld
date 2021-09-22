package com.minigameworld.observer;

public interface MiniGameEventNotifier {
	// called in MiniGame
	public enum MiniGameEvent {
		START, FINISH, EXCEPTION;
	}

	public void registerObserver(MiniGameObserver observer);

	public void unregisterObserver(MiniGameObserver observer);

	public void notifyObservers(MiniGameEvent event);
}
