package com.worldbiomusic.minigameworld.customevents.minigame;

import org.bukkit.entity.Player;

/**
 * Called when a exception related minigamae has occurred<br>
 * [IMPORTANT] This event is handled by all minigames<br>
 * [IMPORTANT] Server has to call this event to make exceptions<br>
 * <br>
 * <b>Defined reason</b><br>
 * <code>player-quit-server</code>: when a player quit the server
 *
 */
public class MiniGameExceptionEvent extends MinigGameEvent {

	private String reason;
	private Player player;

	public MiniGameExceptionEvent(String reason) {
		this(reason, null);
	}

	public MiniGameExceptionEvent(String reason, Player player) {
		super(null);
		this.reason = reason;
		this.player = player;
	}

	/**
	 * Get exception reason
	 * 
	 * @return Exception reason
	 */
	public String getReason() {
		return this.reason;
	}

	/**
	 * Check event is a player's exception
	 * 
	 * @return True if event is a player's exception
	 */
	public boolean isPlayerException() {
		return this.player != null;
	}

	/**
	 * Check event is a server exception
	 * 
	 * @return True if event is a server exception
	 */
	public boolean isServerException() {
		return !isPlayerException();
	}

	/**
	 * Get player
	 * 
	 * @return Player
	 */
	public Player getPlayer() {
		return this.player;
	}

	@Override
	public String toString() {
		return "Exception Reason: " + this.reason;
	}
}
