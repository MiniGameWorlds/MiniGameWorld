package com.worldbiomusic.minigameworld.customevents.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

/**
 * Called when a player try to unview a minigame
 *
 */
public class MiniGamePlayerUnviewEvent extends MiniGamePlayerEvent implements Cancellable{
	private boolean cancelled;

	public MiniGamePlayerUnviewEvent(MiniGame minigame, Player player) {
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
