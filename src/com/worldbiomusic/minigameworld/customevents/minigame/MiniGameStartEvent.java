package com.worldbiomusic.minigameworld.customevents.minigame;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

/**
 * Called when a minigame starts
 *
 */
public class MiniGameStartEvent extends MinigGameEvent {

	public MiniGameStartEvent(MiniGame minigame) {
		super(minigame);
	}

}
