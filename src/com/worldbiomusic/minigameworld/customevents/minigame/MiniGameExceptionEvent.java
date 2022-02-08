package com.worldbiomusic.minigameworld.customevents.minigame;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

/**
 * Called when a exception related minigamae has occurred<br>
 * [IMPORTANT] This event is handled by all minigames<br>
 * [IMPORTANT] Server has to call this event to make exceptions<br>
 * <br>
 * 
 *
 */
public class MiniGameExceptionEvent extends MinigGameEvent {

	private String reason;

	public MiniGameExceptionEvent(MiniGame minigame, String reason) {
		super(minigame);
		this.reason =reason;
	}
	
	public MiniGameExceptionEvent(String reason) {
		this(null, reason);
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
