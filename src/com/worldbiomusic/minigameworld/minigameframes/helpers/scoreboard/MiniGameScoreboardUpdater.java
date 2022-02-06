package com.worldbiomusic.minigameworld.minigameframes.helpers.scoreboard;

import org.bukkit.scoreboard.Scoreboard;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

public abstract class MiniGameScoreboardUpdater {

	protected Scoreboard scoreboard;
	protected MiniGame minigame;
	protected int sidebarScoreLine;

	public MiniGameScoreboardUpdater(Scoreboard scoreboard, MiniGame minigame) {
		this.scoreboard = scoreboard;
		this.minigame = minigame;
		this.sidebarScoreLine = 0;
	}

	public abstract void updateScoreboard();

	/**
	 * Get last score line of sidebar objective in scoreboard<br>
	 * Useful when update in hook method or 3rd-party<br>
	 * [IMPORTANT] score line is descending order (from 0)
	 * 
	 * @return Last score line of sidebar objective
	 */
	public int getLastSidebarScoreLine() {
		return this.sidebarScoreLine + 1;
	}
}
