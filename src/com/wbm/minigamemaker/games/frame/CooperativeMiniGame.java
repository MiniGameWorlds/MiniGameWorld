package com.wbm.minigamemaker.games.frame;

import org.bukkit.entity.Player;

public abstract class CooperativeMiniGame extends MiniGame {
	/*
	 * [협동 미니게임]
	 * 
	 * - 점수 공동 관리: 기존 plusScore, minusScore final로 막고,
	 * plusScoreToTeam/minusScoreToTeam으로 팀 점수 관리
	 * 
	 * - 점수 출력: 공동의 점수 출력됨
	 */

	public CooperativeMiniGame(String title, int maxPlayerCount, int timeLimit, int waitingTime) {
		super(title, maxPlayerCount, timeLimit, waitingTime);
	}

	@Override
	protected final void plusScore(Player p, int score) {
		// 개인 플레이어 점수 관리 금지: final로 선언, 대신 plusScoreToTeam() 사용
	}

	@Override
	protected final void minusScore(Player p, int score) {
		// 개인 플레이어 점수 관리 금지: final로 선언, 대신 minusScoreToTeam() 사용
	}

	protected void plusScoreToTeam(int score) {
		this.getPlayers().forEach(p -> this.plusScoreMiniGameOriginal(p, score));
	}

	protected void minusScoreToTeam(int score) {
		this.getPlayers().forEach(p -> this.minusScoreMiniGameOriginal(p, score));
	}
	
	private void plusScoreMiniGameOriginal(Player p, int score) {
		// final로 선언한 메소드 말고, 원래 MiniGame의 메소드 사용
		super.plusScore(p, score);
	}

	private void minusScoreMiniGameOriginal(Player p, int score) {
		// final로 선언한 메소드 말고, 원래 MiniGame의 메소드 사용
		super.minusScore(p, score);
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
