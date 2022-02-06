package com.worldbiomusic.minigameworld.minigameframes.helpers.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

public class MiniGameWaitingScoreboard extends MiniGameScoreboardUpdater {

	public MiniGameWaitingScoreboard(Scoreboard scoreboard, MiniGame minigame) {
		super(scoreboard, minigame);
	}

	@Override
	public void updateScoreboard() {
		// sidebar objective
		Objective sidebarObjective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
		
		// title
		sidebarObjective.setDisplayName(minigame.getColoredTitle());

		this.sidebarScoreLine = 0;

		// player count
		String playerCountStr = ChatColor.BOLD + "Players: " + ChatColor.RESET;

		int currentPlayerCount = minigame.getPlayerCount();
		if (currentPlayerCount < minigame.getMinPlayerCount()) {
			playerCountStr = playerCountStr + ChatColor.RED + currentPlayerCount + ChatColor.RESET;
		} else {
			playerCountStr = playerCountStr + ChatColor.GREEN + currentPlayerCount + ChatColor.RESET;
		}

		playerCountStr += " / " + minigame.getMaxPlayerCount();

		String minPlayerCountStr = " (Min: " + ChatColor.GOLD + minigame.getMinPlayerCount() + ChatColor.RESET + ")";
		playerCountStr += minPlayerCountStr;

		Score playerCount = sidebarObjective.getScore(playerCountStr);
		playerCount.setScore(sidebarScoreLine--);

		// empty line
		Score emptyScore2 = sidebarObjective.getScore("");
		emptyScore2.setScore(sidebarScoreLine--);

		// left waiting time
		String leftWaitingTimeStr = "Starting in... " + ChatColor.RED + minigame.getLeftWaitingTime();

		Score leftWaitingTime = sidebarObjective.getScore(leftWaitingTimeStr);
		leftWaitingTime.setScore(sidebarScoreLine--);
	}
}
