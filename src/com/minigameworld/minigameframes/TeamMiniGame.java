package com.minigameworld.minigameframes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.minigameworld.util.Utils;

public abstract class TeamMiniGame extends MiniGame {
	/*
	 * [Info]
	 * - team play
	 * - all players has same score
	 * - team util methods
	 * 
	 * [Rule]
	 * - must use plusEveryoneScore() or minusEveryoneScore() for team score
	 */

	public TeamMiniGame(String title, int minPlayerCount, int maxPlayerCount, int timeLimit, int waitingTime) {
		super(title, minPlayerCount, maxPlayerCount, timeLimit, waitingTime);
	}

	protected int getTeamScore() {
		return this.getScore(this.getPlayers().get(0));
	}

	@Override
	protected void plusScore(Player p, int score) {
		this.plusEveryoneScore(score);
	}

	@Override
	protected void minusScore(Player p, int score) {
		this.minusEveryoneScore(score);
	}

	@Override
	protected void printScore() {
		this.sendMessageToAllPlayers(ChatColor.BOLD + "[Score]");
		this.sendMessageToAllPlayers("Team(" + this.getEveryoneName() + ")" + ": " + getTeamScore());
	}

	@Override
	protected final void checkAttributes() {
		super.checkAttributes();
		// waitingTime
		if (this.getWaitingTime() <= 0) {
			Utils.warning(this.getTitleWithClassName() + ": waitingTime must be at least 1 sec");
		}
		// maxPlayerCount
		if (this.getMaxPlayerCount() <= 1) {
			Utils.warning(this.getTitleWithClassName() + ": maxPlayer is recommended at least 2 players");
		}

	}
}

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
