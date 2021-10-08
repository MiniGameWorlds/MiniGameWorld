package com.minigameworld.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import com.minigameworld.minigameframes.MiniGame;
import com.minigameworld.minigameframes.helpers.MiniGameCustomOption;

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
	protected int getPlayerScore(Player p) {
		return this.minigame.getScore(p);
	}

	/**
	 * Gets setting data
	 * 
	 * @return Minigame setting data
	 */
	public Map<String, Object> getSettings() {
		// copied
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
	 * Gets score rank list
	 * 
	 * @return Score rank
	 */
	public List<Entry<Player, Integer>> getScoreRank() {
		// copied
		return new ArrayList<>(this.minigame.getRank(this.minigame.getPlayers()));
	}

	public Class<?> getClassType() {
		return this.minigame.getClass();
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
