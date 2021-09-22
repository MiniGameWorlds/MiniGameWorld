package com.minigameworld.minigameframes;

import com.minigameworld.util.Utils;

public abstract class SoloBattleMiniGame extends MiniGame {

	/*
	 * [Info]
	 * - individual play
	 * - must be more than 2 players
	 * 
	 * [Rule]
	 */
	public SoloBattleMiniGame(String title, int minPlayerCount, int maxPlayerCount, int timeLimit, int waitingTime) {
		super(title, minPlayerCount, maxPlayerCount, timeLimit, waitingTime);
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
