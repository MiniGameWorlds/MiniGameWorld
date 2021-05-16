package com.wbm.minigamemaker.games.frame;

public abstract class SoloMiniGame extends MiniGame {

	/*
	 * [솔로 미니게임]
	 * 
	 * - 최대인원 1명으로 고정
	 */
	public SoloMiniGame(String title, int timeLimit, int waitingTime) {
		super(title, 1, timeLimit, waitingTime);
	}
}
