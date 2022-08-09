package com.minigameworld.frames;

/**
 * <b>[Info]</b><br>
 * - Minigame frame several players can battle individually like FFA<br>
 * - min player count: 2 or more <br>
 * <br>
 * 
 * <b>[Rule]</b><br>
 * - nothing
 * 
 */
public abstract class SoloBattleMiniGame extends MiniGame {
	public SoloBattleMiniGame(String title, int minPlayers, int maxPlayers, int playTime, int waitingTime) {
		super(title, minPlayers, maxPlayers, playTime, waitingTime);

	}

	@Override
	public String getFrameType() {
		return "SoloBattle";
	}
}
