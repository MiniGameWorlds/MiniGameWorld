package com.minigameworld.events.minigame;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.minigameworld.api.MiniGameAccessor;
import com.minigameworld.frames.MiniGame;

/**
 * Event related with minigame
 *
 */
public abstract class MiniGameEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private MiniGameAccessor minigame;

	public MiniGameEvent(MiniGame minigame) {
		this.minigame = new MiniGameAccessor(minigame);
	}

	public MiniGameEvent(MiniGameAccessor minigame) {
		this.minigame = minigame;
	}

	/**
	 * Get minigame accessor
	 * 
	 * @return MiniGameAccessor
	 */
	public MiniGameAccessor getMiniGame() {
		return this.minigame;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public String toString() {
		if (this.minigame == null) {
			return "null";
		}
		return this.minigame.getClass().getSimpleName();
	}
}
