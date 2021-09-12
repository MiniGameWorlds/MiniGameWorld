package com.wbm.minigamemaker.minigameframes;

import com.wbm.minigamemaker.util.Utils;

public abstract class TeamMiniGame extends MiniGame {
	/*
	 * [Info]
	 * - team play
	 * - all players has same score
	 * - team util methods
	 * 
	 * [Rule]
	 * - must use plusScoreToTeam() or minusScoreToTeam() for managing score
	 */

	public TeamMiniGame(String title, int maxPlayerCount, int timeLimit, int waitingTime) {
		super(title, maxPlayerCount, timeLimit, waitingTime);
	}

	protected int getTeamScore() {
		return this.getScore(this.getPlayers().get(0));
	}

	@Override
	protected void printScore() {
		this.sendMessageToAllPlayers("[Score]");
		this.sendMessageToAllPlayers("Team(" + this.getEveryoneName() + ")" + ": " + getTeamScore());
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
			Utils.warning(this.getTitleWithClassName()
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
