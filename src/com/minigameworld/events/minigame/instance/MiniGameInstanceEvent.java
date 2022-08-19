package com.minigameworld.events.minigame.instance;

import java.util.List;

import com.minigameworld.api.MiniGameAccessor;
import com.minigameworld.api.MiniGameWorld;
import com.minigameworld.events.minigame.MiniGameEvent;
import com.minigameworld.frames.MiniGame;

public abstract class MiniGameInstanceEvent extends MiniGameEvent {

	public MiniGameInstanceEvent(MiniGame minigame) {
		super(minigame);
	}

	public List<MiniGameAccessor> instances() {
		MiniGameWorld mw = MiniGameWorld.create(MiniGameWorld.API_VERSION);
		return mw.instanceGames().stream().filter(g->g.isSameTemplate(getMiniGame())).toList();
	}
}
