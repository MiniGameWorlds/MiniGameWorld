package com.wbm.minigamemaker.games.frame;

import org.bukkit.entity.Player;

public abstract class SoloMiniGame extends MiniGame {

	/*
	 * [솔로 미니게임]
	 * 
	 * - 최대인원 1명으로 고정
	 */
	public SoloMiniGame(String title, int timeLimit, int waitingTime) {
		super(title, 1, timeLimit, waitingTime);
	}

	@Override
	protected final void checkAttributes() {
		super.checkAttributes();
	}

	/*
	 * 솔로 1명의 관리 편의 메소드
	 */

	protected Player getSoloPlayer() {
		return this.getPlayers().get(0);
	}

	protected void plusScoreToSolo(int score) {
		this.plusScore(getSoloPlayer(), score);
	}

	protected void minusScoreToSolo(int score) {
		this.minusScore(getSoloPlayer(), score);
	}

	protected int getSoloScore() {
		return this.getScore(getSoloPlayer());
	}

	@Override
	protected void printScore() {
		// 스코어 결과만 출력
		this.sendMessageToAllPlayers("[Score]");
		int score = this.getSoloScore();
		this.sendMessageToAllPlayers(this.getSoloPlayer().getName() + ": " + score);
	}

}
