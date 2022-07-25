package com.minigameworld.customevents.minigame;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import com.minigameworld.minigameframes.MiniGame;

/**
 * Called when a event passed to a started minigame<br>
 * [IMPORTANT] minigame will process event in last<br>
 * [IMPORTANT] only synchronous events will be passed
 */
public class MiniGameEventPassEvent extends MinigGameEvent implements Cancellable{
	private boolean cancelled;
	private Event passevent;

	public MiniGameEventPassEvent(MiniGame minigame, Event passEvent) {
		super(minigame);
		this.passevent = passEvent;
		this.cancelled = false;
	}

	/**
	 * Get passed event
	 * 
	 * @return Event passed to the minigame
	 */
	public Event getPassEvent() {
		return this.passevent;
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
