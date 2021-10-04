package com.minigameworld.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import com.minigameworld.minigameframes.MiniGame;
import com.minigameworld.minigameframes.helpers.MiniGameCustomOption;

public class MiniGameAccessor {

	private MiniGame minigame;

	public MiniGameAccessor(MiniGame minigame) {
		this.minigame = minigame;
	}

	public boolean isEmpty() {
		return this.minigame.isEmpty();
	}

	public boolean isFull() {
		return this.minigame.isFull();
	}

	public boolean containsPlayer(Player p) {
		return this.minigame.containsPlayer(p);
	}

	public List<Player> getPlayers() {
		return this.minigame.getPlayers();
	}

	protected int getPlayerScore(Player p) {
		return this.minigame.getScore(p);
	}

	public Map<String, Object> getSettings() {
		// copied
		return new HashMap<>(this.minigame.getDataManager().getData());
	}

	public Object getCustomOption(MiniGameCustomOption.Option option) {
		return this.minigame.getCustomOption().get(option);
	}

	public String getClassName() {
		return this.minigame.getClassName();
	}

	public int getLeftWaitTime() {
		return this.minigame.getLeftWaitingTime();
	}

	public int getLeftFinishTime() {
		return this.minigame.getLeftFinishTime();
	}

	public List<Entry<Player, Integer>> getScoreRank() {
		// copied
		return new ArrayList<>(this.minigame.getRank(this.minigame.getPlayers()));
	}

}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
