package com.minigameworld.frames;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * <b>[Info]</b><br>
 * - Minigame frame only 1 player can play<br>
 * - solo play <br>
 * - min player count: 1 <br>
 * - solo util methods<br>
 * <br>
 * 
 * <b>[Rule]</b><br>
 * - nothing
 * 
 */
public abstract class SoloMiniGame extends MiniGame {

	/**
	 * Sets minPlayers and maxPlayers to 1 automatically
	 */
	public SoloMiniGame(String title, int playTime, int waitingTime) {
		super(title, 1, 1, playTime, waitingTime);

		setting().setGameFinishConditionPlayerCount(1);
	}

	/**
	 * Returns solo player
	 * 
	 * @return Solo player
	 */
	protected Player getSoloPlayer() {
		return this.players().get(0);
	}

	/**
	 * Plus solo player score
	 * 
	 * @param amount Amount to plus
	 */
	protected void plusScore(int amount) {
		super.plusScore(getSoloPlayer(), amount);
	}

	/**
	 * Minus solo player score
	 * 
	 * @param amount Amount to minus
	 */
	protected void minusScore(int amount) {
		super.minusScore(getSoloPlayer(), amount);
	}

	/**
	 * Get solo player's score
	 * 
	 * @return Solo player's score
	 */
	protected int getScore() {
		return this.score(getSoloPlayer());
	}

	@Override
	protected void printScores() {
		// print just score
		sendMessage(getSoloPlayer(), ChatColor.BOLD + "[" + this.messenger.getMsg(getSoloPlayer(), "score") + "]", false);
		sendMessage(getSoloPlayer(), getSoloPlayer().getName() + ": " + ChatColor.GOLD + getScore(), false);
	}

	@Override
	public String frameType() {
		return "Solo";
	}

}
