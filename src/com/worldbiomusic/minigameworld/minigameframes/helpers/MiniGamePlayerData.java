package com.worldbiomusic.minigameworld.minigameframes.helpers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameCustomOption.Option;

public class MiniGamePlayerData implements MiniGameRankComparable, Cloneable {

	private MiniGame minigame;
	private Player player;
	private int score;
	private boolean live;

	public MiniGamePlayerData(MiniGame minigame, Player p) {
		this.minigame = minigame;
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

		// set gamemode with custom option
		GameMode liveGameMode = (GameMode) this.minigame.getCustomOption().get(Option.LIVE_GAMEMODE);
		GameMode deadGameMode = (GameMode) this.minigame.getCustomOption().get(Option.DEAD_GAMEMODE);

		this.player.setGameMode(live ? liveGameMode : deadGameMode);
	}

	@Override
	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();
		players.add(this.player);
		return players;
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
