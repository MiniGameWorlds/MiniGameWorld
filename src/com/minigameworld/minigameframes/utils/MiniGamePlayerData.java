package com.minigameworld.minigameframes.utils;

import org.bukkit.entity.Player;

public class MiniGamePlayerData {
	private Player player;
	private int score;
	private boolean live;

	public MiniGamePlayerData(Player p) {
		this.player = p;
		this.score = 0;
		this.setLive(true);
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public boolean isSamePlayer(Player other) {
		return this.player.equals(other);
	}

	public int getScore() {
		return score;
	}

	public void plusScore(int amount) {
		this.score += amount;
	}

	public void minusScore(int amount) {
		this.score -= amount;
	}

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

}
