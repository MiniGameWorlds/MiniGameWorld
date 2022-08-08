package com.minigameworld.events.minigame.instance;

import java.util.List;

import org.bukkit.event.Cancellable;

import com.minigameworld.api.MiniGameAccessor;
import com.minigameworld.frames.MiniGame;

/**
 * Called before the instance is created
 */
public class MiniGameInstanceCreateEvent extends MiniGameInstanceEvent implements Cancellable {
	private boolean cancelled;

	public MiniGameInstanceCreateEvent(MiniGame minigame) {
		super(minigame);
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	/**
	 * Get template minigame, not created instance minigame
	 */
	@Override
	public MiniGameAccessor getMiniGame() {
		return super.getMiniGame();
	}

	/**
	 * Get the same instance games before this event processes
	 * 
	 * @return The ssme minigame instances
	 */
	@Override
	public List<MiniGameAccessor> instances() {
		return super.instances();
	}
}
