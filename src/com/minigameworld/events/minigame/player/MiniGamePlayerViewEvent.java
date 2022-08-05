package com.minigameworld.events.minigame.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.minigameworld.minigameframes.MiniGame;

/**
 * Called when a player try to view a minigame
 *
 */
public class MiniGamePlayerViewEvent extends MiniGamePlayerEvent implements Cancellable{
	private boolean cancelled;

	public MiniGamePlayerViewEvent(MiniGame minigame, Player player) {
		super(minigame, player);
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
