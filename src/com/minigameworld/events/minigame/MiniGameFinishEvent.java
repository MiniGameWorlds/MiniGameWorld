package com.minigameworld.events.minigame;

import java.util.List;

import com.minigameworld.frames.MiniGame;
import com.minigameworld.frames.helpers.MiniGamePlayer;

/**
 * Called when a minigame finished<br>
 * [IMPORTANT] players are already out of the minigame<br>
 *
 */
public class MiniGameFinishEvent extends MiniGameEvent {
	private List<MiniGamePlayer> players;

	public MiniGameFinishEvent(MiniGame minigame, List<MiniGamePlayer> players) {
		super(minigame);
	}

	public List<MiniGamePlayer> players() {
		return this.players;
	}
}
