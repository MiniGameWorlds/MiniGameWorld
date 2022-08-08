package com.minigameworld.events.minigame.instance;

import java.util.List;

import com.minigameworld.api.MiniGameAccessor;
import com.minigameworld.frames.MiniGame;

/**
 * Called after the instance is removed
 */
public class MiniGameInstanceRemoveEvent extends MiniGameInstanceEvent {

	public MiniGameInstanceRemoveEvent(MiniGame minigame) {
		super(minigame);
	}

	/**
	 * Get the same instance games after this event processed
	 * 
	 * @return The ssme minigame instances
	 */
	@Override
	public List<MiniGameAccessor> instances() {
		return super.instances();
	}
}
