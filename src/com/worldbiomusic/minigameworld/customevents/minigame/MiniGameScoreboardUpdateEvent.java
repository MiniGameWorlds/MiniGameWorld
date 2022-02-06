package com.worldbiomusic.minigameworld.customevents.minigame;

import org.bukkit.scoreboard.Scoreboard;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

/**
 * Called when a scoreboard of minigame is updated
 *
 */
public class MiniGameScoreboardUpdateEvent extends MinigGameEvent {

	public MiniGameScoreboardUpdateEvent(MiniGame minigame) {
		super(minigame);
	}

	/**
	 * Get scoreboard of minigame
	 * 
	 * @return Scoreboard
	 */
	public Scoreboard getScoreboard() {
		return this.getMiniGame().getScoreboard();
	}
}
