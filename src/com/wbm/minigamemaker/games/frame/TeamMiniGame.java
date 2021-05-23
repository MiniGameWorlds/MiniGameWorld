package com.wbm.minigamemaker.games.frame;

import org.bukkit.entity.Player;

import com.wbm.plugin.util.BroadcastTool;

public abstract class TeamMiniGame extends MiniGame {
	/*
	 * [팀 미니게임]
	 * 
	 * - 점수 공동 관리: 기존 plusScore, minusScore final로 막고,
	 * plusScoreToTeam/minusScoreToTeam으로 팀 점수 관리
	 * 
	 * - 점수 출력: 공동의 점수 출력됨
	 */

	public TeamMiniGame(String title, int maxPlayerCount, int timeLimit, int waitingTime) {
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

	protected int getTeamScore() {
		return this.getScore(this.getPlayers().get(0));
	}

	@Override
	protected void printScore() {
		this.sendMessageToAllPlayers("[Score]");
		this.sendMessageToAllPlayers("Team(" + this.getEveryoneNameString() + ")" + ": " + getTeamScore());
	}

	@Override
	protected final void checkAttributes() {
		super.checkAttributes();
		// waitingTime
		if (this.getWaitingTime() <= 0) {
			BroadcastTool.warn(this.getTitleWithClassName() + ": waitingTime must be at least 1 sec");
		}
		// maxPlayerCount
		if (this.getMaxPlayerCount() <= 1) {
			BroadcastTool.warn(this.getTitleWithClassName()
					+ ": maxPlayer is recommended at least 2 players(or extends SoloMiniGame)");
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
