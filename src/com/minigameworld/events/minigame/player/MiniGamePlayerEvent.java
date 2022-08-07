package com.minigameworld.events.minigame.player;

import org.bukkit.entity.Player;

import com.minigameworld.events.minigame.MiniGameEvent;
import com.minigameworld.frames.MiniGame;

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
