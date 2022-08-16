package com.minigameworld.frames.helpers;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.minigameworld.frames.MiniGame;
import com.minigameworld.frames.helpers.MiniGameCustomOption.Option;

/**
 * Player data with score, live<br>
 * [IMPORTANT] live is only valid in the minigame play (not related with
 * player's health)<br>
 * [IMPORTANT] Player state will be saved when a instance is created (in constructor)
 *
 */
public class MiniGamePlayer implements MiniGameRank, Cloneable {

	private MiniGame minigame;
	private Player player;
	private int score;
	private boolean live;
	private MiniGamePlayerState state;

	public MiniGamePlayer(MiniGame minigame, Player p) {
		this.minigame = minigame;
		this.player = p;
		this.score = 0;

		this.state = new MiniGamePlayerState(minigame, p);
		// save player data
		this.state.savePlayerState();
		// make pure state
		this.state.makePureState();

		// [IMPORTANT] setLive() must be called after "new MiniGamePlayerState(minigame,
		// p);" because, will change player's gamemode (player gamemode has to be saved
		// in MiniGamePlayerState before changed)
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
	@Override
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
	 * Sets player's live in the minigame player<br>
	 * This method will check {@link MiniGame#checkGameFinishCondition()}
	 * 
	 * @param live False if make death a player
	 */
	public void setLive(boolean live) {
		this.live = live;

		// set gamemode with custom option
		GameMode liveGameMode = (GameMode) this.minigame.getCustomOption().get(Option.LIVE_GAMEMODE);
		GameMode deadGameMode = (GameMode) this.minigame.getCustomOption().get(Option.DEAD_GAMEMODE);

		this.player.setGameMode(live ? liveGameMode : deadGameMode);

		// [IMPORTANT] Must be called after change player's gamemode
		if (this.minigame.isStarted()) {
			this.minigame.checkGameFinishCondition();
		}
	}

	/**
	 * Gets player state instance
	 * 
	 * @return MiniGamePlayerState
	 */
	public MiniGamePlayerState getState() {
		return this.state;
	}

	@Override
	public List<Player> getPlayers() {
		return List.of(this.player);
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
