package com.worldbiomusic.minigameworld.customevent;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.worldbiomusic.minigameworld.api.MiniGameAccessor;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

public class MinigGameEvent extends Event {
	enum Timing {
		START, FINISH, EVENT_PASS;
	}

	private static final HandlerList handlers = new HandlerList();
	private MiniGameAccessor minigame;
	private Timing timing;

	public MinigGameEvent(MiniGame minigame, Timing timing) {
		this.minigame = new MiniGameAccessor(minigame);
		this.timing = timing;
	}

	public MiniGameAccessor getMiniGame() {
		return this.minigame;
	}

	public Timing getTiming() {
		return this.timing;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
