package com.worldbiomusic.minigameworld.customevents.minigame;

import org.bukkit.event.Cancellable;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

/**
 * Called when a minigame starts
 *
 */
public class MiniGameStartEvent extends MinigGameEvent implements Cancellable {

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
