package com.worldbiomusic.minigameworld.customevents.minigame;

/**
 * Called when a server exception has occured<br>
 * [IMPORTANT] all minigames will be finished
 *
 */
public class MiniGameServerExceptionEvent extends MiniGameExceptionEvent {

	public MiniGameServerExceptionEvent(String reason) {
		super(null, reason);
	}

}
