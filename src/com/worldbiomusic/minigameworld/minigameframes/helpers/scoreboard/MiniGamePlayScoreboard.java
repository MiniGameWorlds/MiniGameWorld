package com.worldbiomusic.minigameworld.minigameframes.helpers.scoreboard;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGamePlayerData;

public class MiniGamePlayScoreboard extends MiniGameScoreboardSidebarUpdater {

	public MiniGamePlayScoreboard(MiniGame minigame) {
		super(minigame);
	}

	@Override
	public void updateScoreboard() {
		super.updateScoreboard();

		Objective sidebarObjective = scoreboard.getObjective(DisplaySlot.SIDEBAR);

		// player list title
		Score playerListTitle = sidebarObjective.getScore(ChatColor.BOLD + "Players");
		playerListTitle.setScore(sidebarScoreLine--);

		List<MiniGamePlayerData> playerDataList = minigame.getPlayerDataList();
		// sort by score
		Collections.sort(playerDataList);

		// player list
		for (MiniGamePlayerData pData : playerDataList) {
			String playerLine = "- ";
			String pName = pData.getPlayer().getName();

			// color
			if (pData.isLive()) {
				playerLine = playerLine + ChatColor.WHITE + pName + ChatColor.RESET;
			} else {
				playerLine = playerLine + ChatColor.GRAY + ChatColor.STRIKETHROUGH + pName + ChatColor.RESET;
			}

			playerLine += ": " + ChatColor.GOLD + ChatColor.BOLD + pData.getScore();

			Score playerList = sidebarObjective.getScore(playerLine);
			playerList.setScore(this.sidebarScoreLine--);
		}

		// empty line
		addEmptyLineToSiderbar();

		// left time
		String leftTimeStr = "Time left: " + ChatColor.RED + ChatColor.BOLD + minigame.getLeftPlayTime();
		Score leftTime = sidebarObjective.getScore(leftTimeStr);
		leftTime.setScore(this.sidebarScoreLine--);
	}
}
