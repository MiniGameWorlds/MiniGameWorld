package com.worldbiomusic.minigameworld.minigameframes.helpers.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

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

		int currentPlayerCount = minigame.getPlayerCount();
		if (currentPlayerCount < minigame.getMinPlayers()) {
			playerCountStr = playerCountStr + ChatColor.RED + currentPlayerCount + ChatColor.RESET;
		} else {
			playerCountStr = playerCountStr + ChatColor.GREEN + currentPlayerCount + ChatColor.RESET;
		}

		playerCountStr += " / " + minigame.getMaxPlayers();

		String minPlayerCountStr = " (Min: " + ChatColor.GOLD + minigame.getMinPlayers() + ChatColor.RESET + ")";
		playerCountStr += minPlayerCountStr;

		Score playerCount = sidebarObjective.getScore(playerCountStr);
		playerCount.setScore(sidebarScoreLine--);

		// empty line
		addEmptyLineToSiderbar();

		// left waiting time
		String leftWaitingTimeStr = "Starting in... " + ChatColor.RED + minigame.getLeftWaitingTime();

		Score leftWaitingTime = sidebarObjective.getScore(leftWaitingTimeStr);
		leftWaitingTime.setScore(sidebarScoreLine--);
	}
}
