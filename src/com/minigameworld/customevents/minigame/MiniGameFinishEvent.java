package com.minigameworld.customevents.minigame;

import com.minigameworld.minigameframes.MiniGame;

/**
 * Called when a minigame finished<br>
 * [IMPORTANT] players are already out of the minigame<br>
 *
 */
public class MiniGameFinishEvent extends MiniGameEvent {

	public MiniGameFinishEvent(MiniGame minigame) {
		super(minigame);
	}

}
