package com.minigameworld.customevents.minigame.instance;

import org.bukkit.event.Cancellable;

import com.minigameworld.api.MiniGameAccessor;
import com.minigameworld.minigameframes.MiniGame;

/**
 * Called when instance game tries to be created
 *
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
}
