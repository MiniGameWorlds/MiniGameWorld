package com.minigameworld.minigameframes;

import java.util.Random;

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
		return this.getScore(this.getRandomPlayer());
	}
	
	protected Player getRandomPlayer() {
		int randomIndex = (int)(Math.random() * this.getPlayerCount());
		return this.getPlayers().get(randomIndex);
	}

	protected void plusTeamScore(int score) {
		this.plusEveryoneScore(score);
	}

	protected void minusTeamScore(Player p, int score) {
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
