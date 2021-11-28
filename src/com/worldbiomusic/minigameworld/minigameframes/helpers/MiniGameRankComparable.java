package com.worldbiomusic.minigameworld.minigameframes.helpers;

import java.util.List;

import org.bukkit.entity.Player;

public interface MiniGameRankComparable extends Comparable<MiniGameRankComparable>{

	public List<Player> getPlayers();

	public int getScore();

	@Override
	default int compareTo(MiniGameRankComparable other) {
		return other.getScore() - getScore();
	}

	
}
