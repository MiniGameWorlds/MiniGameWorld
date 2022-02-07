package com.worldbiomusic.minigameworld.minigameframes.helpers.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
		Score playerListTitle = sidebarObjective.getScore(ChatColor.BOLD + "Player List");
		playerListTitle.setScore(sidebarScoreLine--);

		// player list
		for (Player p : minigame.getPlayers()) {
			String playerStr = "- ";

			MiniGamePlayerData pData = minigame.getPlayerData(p);
			if (pData.isLive()) {
				playerStr = playerStr + ChatColor.WHITE + p.getName();
			} else {
				playerStr = playerStr + ChatColor.GRAY + ChatColor.STRIKETHROUGH + p.getName();
			}

			playerStr += ": " + ChatColor.RESET + ChatColor.GOLD + ChatColor.BOLD + pData.getScore();

			Score playerList = sidebarObjective.getScore(playerStr);
			playerList.setScore(this.sidebarScoreLine--);
		}

		// empty line
		addEmptyLineToSiderbar();

		// left time
		String leftTimeStr = "Time left: " + ChatColor.RED + ChatColor.BOLD + minigame.getLeftFinishTime();
		Score leftTime = sidebarObjective.getScore(leftTimeStr);
		leftTime.setScore(this.sidebarScoreLine--);
	}
}
