package com.worldbiomusic.minigameworld.minigameframes.helpers;

import java.util.List;

import org.bukkit.entity.Player;

/**
 * Interface for rank data
 */
public interface MiniGameRankResult extends Comparable<MiniGameRankResult> {

	/**
	 * Gets rank players
	 * 
	 * @return Players
	 */
	public List<Player> getPlayers();

	/**
	 * Gets score
	 * 
	 * @return
	 */
	public int getScore();

	@Override
	default int compareTo(MiniGameRankResult other) {
		return other.getScore() - getScore();
	}

}
