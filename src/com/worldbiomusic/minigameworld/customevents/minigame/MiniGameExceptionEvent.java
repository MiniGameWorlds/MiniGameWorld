package com.worldbiomusic.minigameworld.customevents.minigame;

import com.worldbiomusic.minigameworld.api.MiniGameAccessor;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

/**
 * Called when a exception related minigame has occurred<br>
 * [IMPORTANT] This event is passed to a only minigame which is passed with
 * argument in this
 * constructor<br>
 * [IMPORTANT] Other plugin has to call this event to pass exception to a
 * minigame<br>
 * <br>
 * 
 *
 */
public class MiniGameExceptionEvent extends MinigGameEvent {

	private String reason;

	public MiniGameExceptionEvent(MiniGameAccessor minigame, String reason) {
		super(minigame);
		this.reason = reason;
	}

	/**
	 * Get exception reason
	 * 
	 * @return Exception reason
	 */
	public String getReason() {
		return this.reason;
	}

	@Override
	public String toString() {
		return "Exception Reason: " + this.reason;
	}
}
