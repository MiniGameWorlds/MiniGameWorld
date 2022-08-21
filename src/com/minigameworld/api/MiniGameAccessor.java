package com.minigameworld.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import com.minigameworld.frames.MiniGame;
import com.minigameworld.frames.helpers.MiniGameCustomOption;
import com.minigameworld.frames.helpers.MiniGamePlayer;
import com.minigameworld.frames.helpers.MiniGameRank;
import com.minigameworld.frames.helpers.MiniGameSetting;

/**
 * MiniGameWorld plugin "MiniGame" wrapper api
 *
 */
public class MiniGameAccessor {

	/**
	 * Wrapped minigame
	 */
	private MiniGame minigame;

	/**
	 * MiniGame wrapper constructor
	 * 
	 * @param minigame
	 */
	public MiniGameAccessor(MiniGame minigame) {
		this.minigame = minigame;
	}

	/**
	 * Checks minigame has no players
	 * 
	 * @return True if no players in the minigame
	 */
	public boolean isEmpty() {
		return this.minigame.isEmpty();
	}

	/**
	 * Checks minigame has full players
	 * 
	 * @return True if "current players" = "max player count"
	 */
	public boolean isFull() {
		return this.minigame.isFull();
	}

	/**
	 * Check minigame has started or not
	 * 
	 * @return True if already started, false if waiting players
	 */
	public boolean isStarted() {
		return this.minigame.isStarted();
	}

	/**
	 * Checks player is playing minigame
	 * 
	 * @param p Checking player
	 * @return True if player is playing this minigame
	 */
	public boolean containsPlayer(Player p) {
		return this.minigame.containsPlayer(p);
	}

	/**
	 * Gets playing players list
	 * 
	 * @return Playing players list
	 */
	public List<Player> players() {
		return this.minigame.players();
	}

	/**
	 * Gets player's score
	 * 
	 * @param p Target player
	 * @return Player's score
	 */
	public int score(Player p) {
		return this.minigame.score(p);
	}

	/**
	 * Gets copied PlayerData list in minigame (Data change will not be applied)
	 * 
	 * @return PlayerData list
	 */
	public List<MiniGamePlayer> gamePlayers() {
		// copied
		List<MiniGamePlayer> copiedMinigamePlayerData = new ArrayList<>();
		for (MiniGamePlayer minigamePData : this.minigame.gamePlayers()) {
			copiedMinigamePlayerData.add((MiniGamePlayer) minigamePData.clone());
		}

		return copiedMinigamePlayerData;
	}

	/**
	 * Gets setting data<br>
	 * 
	 * @return MiniGameSetting setting data
	 */
	public MiniGameSetting settings() {
		return this.minigame.setting();
	}

	/**
	 * Gets settings data with Map<br>
	 * 
	 * @return Map setting data
	 */
	public Map<String, Object> settingsData() {
		return new HashMap<>(this.minigame.dataManager().getData());
	}

	/**
	 * Gets custom option
	 * 
	 * @param option Option to get data
	 * @return Option data
	 */
	public Object customOption(MiniGameCustomOption.Option option) {
		return this.minigame.customOption().get(option);
	}

	/**
	 * Gets minigame class name
	 * 
	 * @return
	 */
	public String className() {
		return this.minigame.className();
	}

	/**
	 * Gets minigame left waiting time (sec)
	 * 
	 * @return Left waiting time
	 */
	public int leftWaitTime() {
		return this.minigame.leftWaitingTime();
	}

	/**
	 * Gets left play time (sec)
	 * 
	 * @return Left play time
	 */
	public int leftPlayTime() {
		return this.minigame.leftPlayTime();
	}

	/**
	 * Gets rank list by score<br>
	 * Will be return clone object in the future
	 * 
	 * @return Rank list
	 */
	public List<? extends MiniGameRank> rank() {
		return this.minigame.rank();

	}

	/**
	 * Get class
	 * 
	 * @return Class
	 */
	public Class<?> classType() {
		return this.minigame.getClass();
	}

	/**
	 * Get minigame frame type
	 * 
	 * @return Minigame frame type
	 * @see MiniGame#frameType()
	 */
	public String frameType() {
		return this.minigame.frameType();
	}

	/**
	 * Get viewers
	 * 
	 * @return Viewers
	 */
	public Set<Player> viewers() {
		return this.minigame.viewManager().getViewers();
	}

	/**
	 * Get scoreboard
	 * 
	 * @return Scoreboard
	 */
	public Scoreboard scoreboard() {
		return this.minigame.scoreboardManager().getScoreboard();
	}

	/**
	 * Return minigame instance
	 * 
	 * @return Minigame instance
	 */
	public MiniGame minigame() {
		return this.minigame;
	}

	/**
	 * Check two games drived from the same template game or not
	 * 
	 * @param game Checking game
	 * @return True if two games are derived from the same template game
	 */
	public boolean isSameTemplate(MiniGameAccessor game) {
		return minigame.isSameTemplate(game.minigame);
	}

	/**
	 * Compare with {@link MiniGame#isSameTemplate(MiniGame)} and
	 * {@link MiniGameSetting#getId()}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || this.minigame == null) {
			return false;
		} else if (obj instanceof MiniGame) {
			return this.minigame.equals(obj);
		} else if (getClass() == obj.getClass()) {
			return this.minigame.equals(obj);
		}
		return false;
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
