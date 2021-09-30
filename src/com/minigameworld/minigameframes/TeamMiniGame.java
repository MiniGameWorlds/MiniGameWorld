package com.minigameworld.minigameframes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.wbm.plugin.util.BroadcastTool;
import com.wbm.plugin.util.PlayerTool;

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
		int randomIndex = (int) (Math.random() * this.getPlayerCount());
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
		BroadcastTool.sendMessage(this.getPlayers(), ChatColor.BOLD + "[Score]");

		String allPlayersName = PlayerTool.getPlayersNameString(this.getPlayers(), ",");
		BroadcastTool.sendMessage(this.getPlayers(), "Team(" + allPlayersName + ")" + ": " + getTeamScore());
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
