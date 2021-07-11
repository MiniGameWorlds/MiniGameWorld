package com.wbm.minigamemaker.wrapper;

import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.wbm.minigamemaker.games.frame.MiniGame;
import com.wbm.minigamemaker.games.frame.MiniGame.Exception;

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
	- `handleException`: 
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

	protected int getScore(Player p) {
		return this.minigame.getScore(p);
	}

	public final void handleException(Player p, Exception exception, Object arg) {
		this.minigame.handleException(p, exception, arg);
	}

	public String getTitle() {
		return this.minigame.getTitle();
	}

	public Location getLocation() {
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

	public String getClassName() {
		return this.minigame.getClassName();
	}

	public int getLeftWaitTime() {
		return this.minigame.getLeftWaitTime();
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