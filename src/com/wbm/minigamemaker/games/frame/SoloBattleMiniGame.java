package com.wbm.minigamemaker.games.frame;

import org.bukkit.entity.Player;

import com.wbm.minigamemaker.util.Setting;

public abstract class SoloBattleMiniGame extends MiniGame {

	/*
	 * [Info]
	 * - individual play
	 * - players must be more than 2
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
		checkOnlyOnePlayerRemains();
	}

	protected void checkOnlyOnePlayerRemains() {
		// 게임시작후 1 명일 떄 게임 종료
		if (this.getPlayerCount() <= 1) {
			this.sendMessageToAllPlayers("Game End: only 1 player remains");
			this.endGame();
		}
	}

	@Override
	protected void handleGameExeption(Player p, Exception exception, Object arg) {
		super.handleGameExeption(p, exception, arg);
		this.checkOnlyOnePlayerRemains();
	}

	@Override
	protected final void checkAttributes() {
		super.checkAttributes();
		// waitingTime
		if (this.getWaitingTime() <= 0) {
			Setting.warning(this.getTitleWithClassName() + ": waitingTime must be at least 1 sec");
		}
		// maxPlayerCount
		if (this.getMaxPlayerCount() <= 1) {
			Setting.warning(this.getTitleWithClassName()
					+ ": maxPlayer is recommended at least 2 players(or extends SoloMiniGame)");
		}
	}
}
