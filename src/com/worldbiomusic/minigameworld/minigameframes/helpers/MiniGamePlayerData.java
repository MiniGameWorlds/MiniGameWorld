package com.worldbiomusic.minigameworld.minigameframes.helpers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameCustomOption.Option;

/**
 * Player data with score, live<br>
 * [IMPORTANT] live is only valid in the minigame play (not related with
 * player's health)
 *
 */
public class MiniGamePlayerData implements MiniGameRankResult, Cloneable {

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

	/**
	 * Gets a player
	 * 
	 * @return Player
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * Check player are the same
	 * 
	 * @param other Other player
	 * @return True if the same player
	 */
	public boolean isSamePlayer(Player other) {
		return this.player.equals(other);
	}

	/**
	 * Gets player's score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Plus player's score
	 * 
	 * @param amount Score amount
	 */
	public void plusScore(int amount) {
		this.score += amount;
	}

	/**
	 * Minus player's score
	 * 
	 * @param amount Score amount
	 */
	public void minusScore(int amount) {
		this.score -= amount;
	}

	/**
	 * Check player is live in the minigame play
	 * 
	 * @return True if alive
	 */
	public boolean isLive() {
		return live;
	}

	/**
	 * Sets player's live in the minigame player
	 * 
	 * @param live False if make death a player
	 */
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
