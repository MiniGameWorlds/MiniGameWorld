package com.minigameworld.events.minigame;

import com.minigameworld.api.MiniGameAccessor;
import com.minigameworld.minigameframes.MiniGame;

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
public class MiniGameExceptionEvent extends MiniGameEvent {

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
