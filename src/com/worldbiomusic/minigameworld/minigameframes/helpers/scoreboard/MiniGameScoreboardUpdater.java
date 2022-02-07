package com.worldbiomusic.minigameworld.minigameframes.helpers.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

public abstract class MiniGameScoreboardUpdater {

	protected Scoreboard scoreboard;
	protected MiniGame minigame;
	protected int sidebarScoreLine;
	private String emptyLineString;

	public MiniGameScoreboardUpdater(MiniGame minigame) {
		this.minigame = minigame;
		this.scoreboard = minigame.getScoreboardManager().getScoreboard();
		this.sidebarScoreLine = 0;
		this.emptyLineString = "";
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

	public void addEmptyLineToSiderbar() {
		Objective objective = this.scoreboard.getObjective(DisplaySlot.SIDEBAR);
		Score emptyLine = objective.getScore(this.emptyLineString);
		emptyLine.setScore(this.sidebarScoreLine--);

		// make empty line string different 
		this.emptyLineString += ChatColor.RESET;
	}
}
