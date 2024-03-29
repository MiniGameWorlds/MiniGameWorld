package com.minigameworld.events.minigame;

import org.bukkit.event.Cancellable;
import org.bukkit.scoreboard.Scoreboard;

import com.minigameworld.frames.MiniGame;

/**
 * Called when a scoreboard of minigame is updated
 *
 */
public class MiniGameScoreboardUpdateEvent extends MiniGameEvent implements Cancellable{
	private boolean cancelled;
	
	public MiniGameScoreboardUpdateEvent(MiniGame minigame) {
		super(minigame);
		this.cancelled = false;
	}

	/**
	 * Get scoreboard of minigame
	 * 
	 * @return Scoreboard
	 */
	public Scoreboard getScoreboard() {
		return this.getMiniGame().scoreboard();
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
