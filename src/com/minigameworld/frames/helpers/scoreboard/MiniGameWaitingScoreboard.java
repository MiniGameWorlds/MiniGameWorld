package com.minigameworld.frames.helpers.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import com.minigameworld.frames.MiniGame;

public class MiniGameWaitingScoreboard extends MiniGameScoreboardSidebarUpdater {

	public MiniGameWaitingScoreboard(MiniGame minigame) {
		super(minigame);
	}

	@Override
	public void updateScoreboard() {
		super.updateScoreboard();

		Objective sidebarObjective = scoreboard.getObjective(DisplaySlot.SIDEBAR);

		// player count
		String playerCountStr = ChatColor.BOLD + "Players: " + ChatColor.RESET;

		int currentPlayerCount = minigame.playerCount();
		if (currentPlayerCount < minigame.minPlayers()) {
			playerCountStr = playerCountStr + ChatColor.RED + currentPlayerCount + ChatColor.RESET;
		} else {
			playerCountStr = playerCountStr + ChatColor.GREEN + currentPlayerCount + ChatColor.RESET;
		}

		playerCountStr += " / " + minigame.maxPlayers();

		String minPlayerCountStr = " (Min: " + ChatColor.GOLD + minigame.minPlayers() + ChatColor.RESET + ")";
		playerCountStr += minPlayerCountStr;

		Score playerCount = sidebarObjective.getScore(playerCountStr);
		playerCount.setScore(sidebarScoreLine--);

		// empty line
		addEmptyLineToSiderbar();

		// left waiting time
		String leftWaitingTimeStr = "Starting in... " + ChatColor.RED + minigame.leftWaitingTime();

		Score leftWaitingTime = sidebarObjective.getScore(leftWaitingTimeStr);
		leftWaitingTime.setScore(sidebarScoreLine--);
	}
}
