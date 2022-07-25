package com.minigameworld.minigameframes.helpers.scoreboard;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import com.minigameworld.minigameframes.MiniGame;

public class MiniGameScoreboardSidebarUpdater extends MiniGameScoreboardUpdater {

	public MiniGameScoreboardSidebarUpdater(MiniGame minigame) {
		super(minigame);
	}

	@Override
	public void updateScoreboard() {
		// sidebar objective
		Objective sidebarObjective = scoreboard.getObjective(DisplaySlot.SIDEBAR);

		// title
		sidebarObjective.setDisplayName(minigame.getColoredTitle());

		// init line
		this.sidebarScoreLine = 0;
		
		// init empty line string
		this.emptyLineString = "";

		// empty line
		addEmptyLineToSiderbar();
	}

}
