package com.wbm.minigamemaker.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public enum MiniGameType {
	FIT_TOOl(new Location(Bukkit.getWorld("world"), 10, 4, 0), 1, 30, 10);

	private Location spawnLocation;
	private int maxPlayerCount;
	private int waitingTime;
	private int timeLimit;
//	private boolean mustFullPlayer;

	private MiniGameType(Location spawnLocation, int maxPlayerCount, int timeLimit, int waitingTime) {
		this.spawnLocation = spawnLocation;
		this.maxPlayerCount = maxPlayerCount;
		this.timeLimit = timeLimit;
		this.waitingTime = waitingTime;
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public int getMaxPlayerCount() {
		return maxPlayerCount;
	}

	public int getWaitingTime() {
		return waitingTime;
	}

	public int getTimeLimit() {
		return timeLimit;
	}

}
