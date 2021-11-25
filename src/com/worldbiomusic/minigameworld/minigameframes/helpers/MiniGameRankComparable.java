package com.worldbiomusic.minigameworld.minigameframes.helpers;

import java.util.List;

import org.bukkit.entity.Player;

public interface MiniGameRankComparable extends Comparable<MiniGameRankComparable> {

	public List<Player> getPlayers();

	public int getScore();

	@Override
	default int compareTo(MiniGameRankComparable other) {
		if (getScore() > other.getScore()) {
			return 1;
		} else if (getScore() < other.getScore()) {
			return -1;
		} else {
			return 0;
		}
	}
}
