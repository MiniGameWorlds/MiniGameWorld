package com.minigameworld.minigameframes.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import com.minigameworld.minigameframes.MiniGame;
import com.wbm.plugin.util.SortTool;

public class MiniGameRankManager {
	private MiniGame minigame;

	public MiniGameRankManager(MiniGame minigame) {
		this.minigame = minigame;
	}

	private Map<Player, Integer> getPlayerScoreList(List<Player> targetPlayers) {
		Map<Player, Integer> playersScoreList = new HashMap<>();
		for (Player p : targetPlayers) {
			int score = this.minigame.getPlayerData(p).getScore();
			playersScoreList.put(p, score);
		}

		return playersScoreList;
	}

	public List<Entry<Player, Integer>> getAscendingScoreRanking(List<Player> targetPlayers) {
		Map<Player, Integer> playersScoreList = this.getPlayerScoreList(targetPlayers);
		List<Entry<Player, Integer>> entries = SortTool.getAscendingSortedList(playersScoreList);
		return entries;
	}

	public List<Entry<Player, Integer>> getDescendingScoreRanking(List<Player> targetPlayers) {
		Map<Player, Integer> playersScoreList = this.getPlayerScoreList(targetPlayers);
		List<Entry<Player, Integer>> entries = SortTool.getDescendingSortedList(playersScoreList);
		return entries;
	}
}
