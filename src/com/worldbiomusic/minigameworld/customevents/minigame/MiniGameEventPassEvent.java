package com.worldbiomusic.minigameworld.customevents.minigame;

import org.bukkit.event.Event;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameSetting;

/**
 * Called when a event passed to a minigame<br>
 * [IMPORTANT] minigame will process event in last<br>
 * [IMPORTANT] If {@link MiniGameSetting#isPassUndetectableEvent()} option of
 * minigame is true, undetectable events will be passed to the minigame even
 * there is no player exist in, so must check
 * {@link MiniGameSetting#isPassUndetectableEvent()} before process event
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
