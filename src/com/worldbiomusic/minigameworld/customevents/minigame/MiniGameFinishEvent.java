package com.worldbiomusic.minigameworld.customevents.minigame;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

/**
 * Called when a minigame finished<br>
 * [IMPORTANT] players are already out of the minigame<br>
 *
 */
public class MiniGameFinishEvent extends MinigGameEvent {

	public MiniGameFinishEvent(MiniGame minigame) {
		super(minigame);
	}

}
