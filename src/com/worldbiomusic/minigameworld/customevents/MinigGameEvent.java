package com.worldbiomusic.minigameworld.customevents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.worldbiomusic.minigameworld.api.MiniGameAccessor;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

public abstract class MinigGameEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private MiniGameAccessor minigame;

	public MinigGameEvent(MiniGame minigame) {
		this.minigame = new MiniGameAccessor(minigame);
	}

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
		return this.minigame.getClass().getSimpleName();
	}
}
