package com.minigameworld.minigameframes;

import org.bukkit.entity.Player;

public abstract class SoloMiniGame extends MiniGame {

	/*
	 * [Info]
	 * - solo play
	 * - max player count: 1
	 * - solo util methods
	 * 
	 * [Rule]
	 * 
	 */
	public SoloMiniGame(String title, int timeLimit, int waitingTime) {
		super(title, 1, timeLimit, waitingTime);
	}

	@Override
	protected final void checkAttributes() {
		super.checkAttributes();
	}

	/*
	 * solo util methods
	 */

	protected Player getSoloPlayer() {
		return this.getPlayers().get(0);
	}

	protected void plusScore(int score) {
		super.plusScore(getSoloPlayer(), score);
	}

	protected void minusScore(int score) {
		super.minusScore(getSoloPlayer(), score);
	}

	protected int getScore() {
		return this.getScore(getSoloPlayer());
	}

	@Override
	protected void printScore() {
		// print just score
		this.sendMessageToAllPlayers("[Score]");
		int score = this.getScore();
		this.sendMessageToAllPlayers(this.getSoloPlayer().getName() + ": " + score);
	}

}
