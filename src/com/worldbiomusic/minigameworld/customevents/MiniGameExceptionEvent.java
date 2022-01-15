package com.worldbiomusic.minigameworld.customevents;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

public class MiniGameExceptionEvent extends MinigGameEvent {

	private String reason;

	public MiniGameExceptionEvent(MiniGame minigame, String reason) {
		super(minigame);
		this.reason = reason;
	}

	public String getReason() {
		return this.reason;
	}

	@Override
	public String toString() {
		return super.toString() + " Reason: " + this.reason;
	}
}
