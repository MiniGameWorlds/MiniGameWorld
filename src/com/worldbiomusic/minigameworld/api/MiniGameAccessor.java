package com.worldbiomusic.minigameworld.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameCustomOption;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGamePlayerData;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameRankComparable;

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
	 * Will be return clone object in the future
	 * 
	 * @return Minigame setting data
	 */
	public Map<String, Object> getSettings() {
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
	 * Gets left time to finish minigame (sec)
	 * 
	 * @return Left finish time
	 */
	public int getLeftFinishTime() {
		return this.minigame.getLeftFinishTime();
	}

	/**
	 * Gets rank list by score<br>
	 * Will be return clone object in the future
	 * 
	 * @return Rank list
	 */

	public List<? extends MiniGameRankComparable> getRank() {
		return this.minigame.getRank();

	}

	public Class<?> getClassType() {
		return this.minigame.getClass();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (getClass() == obj.getClass()) {
			return this.minigame.equals(((MiniGameAccessor) obj).minigame);
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
