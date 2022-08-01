package com.minigameworld.customevents.minigame.instance;

import java.util.List;

import com.minigameworld.api.MiniGameAccessor;
import com.minigameworld.api.MiniGameWorld;
import com.minigameworld.customevents.minigame.MiniGameEvent;
import com.minigameworld.minigameframes.MiniGame;

public abstract class MiniGameInstanceEvent extends MiniGameEvent {

	public MiniGameInstanceEvent(MiniGame minigame) {
		super(minigame);
	}

	/**
	 * Get instance count of the minigame before this event processes
	 * 
	 * @return Same minigame instance count
	 */
	public List<MiniGameAccessor> instances() {
		MiniGameWorld mw = MiniGameWorld.create(MiniGameWorld.API_VERSION);
		return mw.getInstanceGames().stream().filter(g->g.isSameTemplate(getMiniGame())).toList();
	}

}
