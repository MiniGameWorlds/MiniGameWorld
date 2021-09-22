package com.minigameworld.minigameframes;

import org.bukkit.entity.Player;

import com.minigameworld.util.Utils;

public abstract class SoloBattleMiniGame extends MiniGame {

	/*
	 * [Info]
	 * - individual play
	 * - must be more than 2 players
	 * 
	 * [Rule]
	 * - when use runTaskAfterStart(), must call super.runTaskAfterStart()
	 */
	public SoloBattleMiniGame(String title, int maxPlayerCount, int timeLimit, int waitingTime) {
		super(title, maxPlayerCount, timeLimit, waitingTime);
	}

	@Override
	protected void runTaskAfterStart() {
		super.runTaskAfterStart();
		if(checkAtLeastPlayerRemains(2)) {
			this.restartWaitingTask();
			return;
		}
	}

	protected boolean checkAtLeastPlayerRemains(int count) {
		return this.getPlayerCount() < count;
	}

	@Override
	protected void handleGameException(Player p, Exception exception, Object arg) {
		super.handleGameException(p, exception, arg);
		// handled player will remove after run "handleGameException()"
		if (this.checkAtLeastPlayerRemains(3)) {
			this.sendMessageToAllPlayers("Game End: need more players");
			this.endGame();
		}
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
