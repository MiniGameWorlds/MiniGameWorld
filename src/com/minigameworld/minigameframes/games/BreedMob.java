package com.minigameworld.minigameframes.games;

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.minigameworld.minigameframes.TeamMiniGame;

public class BreedMob extends TeamMiniGame {

	public BreedMob() {
		super("BreedMob", 2, 4, 60 * 3, 10);
	}

	@Override
	protected void initGameSettings() {
	}

	@Override
	protected void processEvent(Event event) {
		if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent e = (PlayerDeathEvent) event;
			e.getEntity().setGameMode(GameMode.);
		}
	}

	@Override
	protected List<String> registerTutorial() {
		return null;
	}

}
