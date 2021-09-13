package com.minigameworld.observer;

public interface MiniGameEventNotifier {
	public enum MiniGameEvent {
		START, FINISH
	}

	public void registerObserver(MiniGameObserver observer);

	public void unregisterObserver(MiniGameObserver observer);

	public void notifyObservers(MiniGameEvent event);
}
