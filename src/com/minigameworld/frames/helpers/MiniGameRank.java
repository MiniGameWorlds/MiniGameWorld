package com.minigameworld.frames.helpers;

import java.util.List;

import org.bukkit.entity.Player;

/**
 * Interface for rank data
 */
public interface MiniGameRank extends Comparable<MiniGameRank> {

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
	default int compareTo(MiniGameRank other) {
		return other.getScore() - getScore();
	}

}
