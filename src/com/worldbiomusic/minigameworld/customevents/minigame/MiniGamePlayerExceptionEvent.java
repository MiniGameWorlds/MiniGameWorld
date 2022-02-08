package com.worldbiomusic.minigameworld.customevents.minigame;

import org.bukkit.entity.Player;

/**
 * Called when a player playing minigame exception has occured<br>
 * <br>
 * <b>Defined reason</b><br>
 * <code>player-quit-server</code>: when a player quit the server
 *
 */
public class MiniGamePlayerExceptionEvent extends MiniGameExceptionEvent {
	private Player player;

	public MiniGamePlayerExceptionEvent(String reason, Player player) {
		super(reason);
		this.player = player;
	}

	/**
	 * Get player
	 * 
	 * @return Player
	 */
	public Player getPlayer() {
		return this.player;
	}

}
