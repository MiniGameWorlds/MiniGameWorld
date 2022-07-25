package com.minigameworld.customevents.minigame.player;

import org.bukkit.entity.Player;

import com.minigameworld.customevents.minigame.MiniGameEvent;
import com.minigameworld.minigameframes.MiniGame;

/**
 * Playing minigame player event
 *
 */
public abstract class MiniGamePlayerEvent extends MiniGameEvent {
	private Player player;

	public MiniGamePlayerEvent(MiniGame minigame, Player player) {
		super(minigame);
		this.player = player;
	}

	public Player getPlayer() {
		return this.player;
	}

}
