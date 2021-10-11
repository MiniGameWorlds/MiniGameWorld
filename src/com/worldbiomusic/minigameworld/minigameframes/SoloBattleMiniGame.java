package com.worldbiomusic.minigameworld.minigameframes;

/**
 * <b>[Info]</b><br>
 * - Minigame frame several players can battle individually<br>
 * - min player count: 2 or more <br>
 * <br>
 * 
 * <b>[Rule]</b><br>
 * - nothing
 * 
 */
public abstract class SoloBattleMiniGame extends MiniGame {
	public SoloBattleMiniGame(String title, int minPlayerCount, int maxPlayerCount, int timeLimit, int waitingTime) {
		super(title, minPlayerCount, maxPlayerCount, timeLimit, waitingTime);
	}

}