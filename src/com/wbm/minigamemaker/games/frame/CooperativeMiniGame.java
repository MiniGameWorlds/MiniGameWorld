package com.wbm.minigamemaker.games.frame;

import org.bukkit.entity.Player;

public abstract class CooperativeMiniGame extends MiniGame {
	/*
	 * 협동 미니게임
	 * 
	 * - 점수 공동 관리: 기존 plusScore, minusScore final로 막고, 새로운것으로 전체 관리
	 * 
	 * - 점수 출력: 공동의 점수 출력됨
	 */

	public CooperativeMiniGame(String title, int maxPlayerCount, int timeLimit, int waitingTime) {
		super(title, maxPlayerCount, timeLimit, waitingTime);
	}

	@Override
	protected final void plusScore(Player p, int score) {
		super.plusScore(p, score);
	}

	@Override
	protected final void minusScore(Player p, int score) {
		super.minusScore(p, score);
	}

	protected void plusScore(int score) {
		for (Player p : this.getPlayers()) {
			this.plusScore(p, score);
		}
	}

	protected void minusScore(int score) {
		for (Player p : this.getPlayers()) {
			this.minusScore(p, score);
		}
	}

	@Override
	protected void printScore() {
		this.sendMessageToAllPlayers("[Score]");
		Player p = this.getPlayers().get(0);
		this.sendMessageToAllPlayers("Team Score: " + this.getScore(p));
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
