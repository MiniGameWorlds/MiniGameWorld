package com.minigameworld.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import com.minigameworld.minigameframes.MiniGame;
import com.minigameworld.minigameframes.helpers.MiniGameCustomOption;
import com.minigameworld.minigameframes.helpers.MiniGamePlayerData;
import com.minigameworld.minigameframes.helpers.MiniGameRank;
import com.minigameworld.minigameframes.helpers.MiniGameSetting;

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
	public List<Player> getPlayers() {
		return this.minigame.getPlayers();
	}

	/**
	 * Gets player's score
	 * 
	 * @param p Target player
	 * @return Player's score
	 */
	public int getPlayerScore(Player p) {
		return this.minigame.getScore(p);
	}

	/**
	 * Gets copied PlayerData list in minigame (Data change will not be applied)
	 * 
	 * @return PlayerData list
	 */
	public List<MiniGamePlayerData> getPlayerDataList() {
		// copied
		List<MiniGamePlayerData> copiedMinigamePlayerData = new ArrayList<>();
		for (MiniGamePlayerData minigamePData : this.minigame.getPlayerDataList()) {
			copiedMinigamePlayerData.add((MiniGamePlayerData) minigamePData.clone());
		}

		return copiedMinigamePlayerData;
	}

	/**
	 * Gets setting data<br>
	 * 
	 * @return MiniGameSetting setting data
	 */
	public MiniGameSetting getSettings() {
		return this.minigame.getSetting();
	}

	/**
	 * Gets settings data with Map<br>
	 * 
	 * @return Map setting data
	 */
	public Map<String, Object> getSettingsData() {
		return new HashMap<>(this.minigame.getDataManager().getData());
	}

	/**
	 * Gets custom option
	 * 
	 * @param option Option to get data
	 * @return Option data
	 */
	public Object getCustomOption(MiniGameCustomOption.Option option) {
		return this.minigame.getCustomOption().get(option);
	}

	/**
	 * Gets minigame class name
	 * 
	 * @return
	 */
	public String getClassName() {
		return this.minigame.getClassName();
	}

	/**
	 * Gets minigame left waiting time (sec)
	 * 
	 * @return Left waiting time
	 */
	public int getLeftWaitTime() {
		return this.minigame.getLeftWaitingTime();
	}

	/**
	 * Gets left play time (sec)
	 * 
	 * @return Left play time
	 */
	public int getLeftPlayTime() {
		return this.minigame.getLeftPlayTime();
	}

	/**
	 * Gets rank list by score<br>
	 * Will be return clone object in the future
	 * 
	 * @return Rank list
	 */
	public List<? extends MiniGameRank> getRank() {
		return this.minigame.getRank();

	}

	/**
	 * Get class
	 * 
	 * @return Class
	 */
	public Class<?> getClassType() {
		return this.minigame.getClass();
	}

	/**
	 * Get minigame frame type
	 * 
	 * @return Minigame frame type
	 * @see MiniGame#getFrameType()
	 */
	public String getFrameType() {
		return this.minigame.getFrameType();
	}

	/**
	 * Get viewers
	 * 
	 * @return Viewers
	 */
	public Set<Player> getViewers() {
		return this.minigame.getViewManager().getViewers();
	}

	/**
	 * Get scoreboard
	 * 
	 * @return Scoreboard
	 */
	public Scoreboard getScoreboard() {
		return this.minigame.getScoreboardManager().getScoreboard();
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
	 * Compare with {@link #isSameTemplate(MiniGame)} and
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
