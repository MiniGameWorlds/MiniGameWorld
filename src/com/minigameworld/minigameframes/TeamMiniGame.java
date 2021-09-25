package com.minigameworld.minigameframes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
