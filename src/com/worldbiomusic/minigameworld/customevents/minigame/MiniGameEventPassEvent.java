package com.worldbiomusic.minigameworld.customevents.minigame;

import org.bukkit.event.Event;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

/**
 * Called when a event passed to a started minigame<br>
 * [IMPORTANT] minigame will process event in last<br>
 *
 */
public class MiniGameEventPassEvent extends MinigGameEvent {

	private Event passevent;

	public MiniGameEventPassEvent(MiniGame minigame, Event passEvent) {
		super(minigame);
		this.passevent = passEvent;
	}

	/**
	 * Get passed event
	 * 
	 * @return Event passed to the minigame
	 */
	public Event getPassEvent() {
		return this.passevent;
	}
}
