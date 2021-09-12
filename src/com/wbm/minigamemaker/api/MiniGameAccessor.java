package com.wbm.minigamemaker.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.wbm.minigamemaker.minigameframes.MiniGame;

public class MiniGameAccessor {

	private MiniGame minigame;

	public MiniGameAccessor(MiniGame minigame) {
		this.minigame = minigame;
	}

	/*
	 * API
	 * 
	- `isEmpty`: 
	- `isFull`: 
	- `containsPlayer`: 
	- `getPlayers`: 
	- `getPlayerCount`: 
	- `getScore`: 
//	- `handleException`: 
	- `getTitle`: 
	- `getLocation`: 
	- `getWaitingTime`: 
	- `getTimeLimit`: 
	- `getMaxPlayerCount`: 
	- `isActive`: 
	- `isSettingFixed`: 
	- `getClassName`: 
	- `getLeftWaitTime`: 
	- `getLeftFinishTime`: 
	- `getEveryoneNameString`: 
	- `getCurrentScoreRanking`: 
	 */
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

	public int getPlayerCount() {
		return this.minigame.getPlayerCount();
	}

	protected int getPlayerScore(Player p) {
		return this.minigame.getScore(p);
	}

	// minigame can hurt
//	public void handleException(Player p, Exception exception, Object arg) {
//		this.minigame.handleException(p, exception, arg);
//	}

	public String getTitle() {
		return this.minigame.getTitle();
	}

	public Location getLocation() {
		// copied instance
		return this.minigame.getLocation().clone();
	}

	public int getWaitingTime() {
		return this.minigame.getWaitingTime();
	}

	public int getTimeLimit() {
		return this.minigame.getTimeLimit();
	}

	public int getMaxPlayerCount() {
		return this.minigame.getMaxPlayerCount();
	}

	public boolean isActive() {
		return this.minigame.isActive();
	}

	public boolean isSettingFixed() {
		return this.minigame.isSettingFixed();
	}

	public boolean isScoreNotifying() {
		return this.minigame.getSetting().isScoreNotifying();
	}

	public boolean isForcePlayerCount() {
		return this.minigame.getSetting().isForcePlayerCount();
	}

	public List<String> getTutorial() {
		// copied instance
		return new ArrayList<>(this.minigame.getTutorial());
	}

	public Map<String, Object> getCustomData() {
		// copied instance
		return new HashMap<>(this.minigame.getCustomData());
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

	public String getEveryoneName() {
		return this.minigame.getEveryoneName();
	}

	public List<Entry<Player, Integer>> getScoreRanking() {
		return this.minigame.getScoreRanking();
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
