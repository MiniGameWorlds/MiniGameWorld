package com.minigameworld.events.minigame;

import org.bukkit.event.Cancellable;

import com.minigameworld.frames.MiniGame;

/**
 * Called when a minigame starts
 *
 */
public class MiniGameStartEvent extends MiniGameEvent implements Cancellable {

	private boolean cancelled;

	public MiniGameStartEvent(MiniGame minigame) {
		super(minigame);
		this.cancelled = false;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
