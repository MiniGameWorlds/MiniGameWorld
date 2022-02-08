package com.worldbiomusic.minigameworld.customevents.minigame.player;

import org.bukkit.entity.Player;

import com.worldbiomusic.minigameworld.customevents.minigame.MinigGameEvent;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

public class MiniGamePlayerEvent extends MinigGameEvent {
	private Player player;

	public MiniGamePlayerEvent(MiniGame minigame, Player player) {
		super(minigame);
		this.player = player;
	}

	public Player getPlayer() {
		return this.player;
	}
	
}
