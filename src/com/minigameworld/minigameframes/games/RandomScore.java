package com.minigameworld.minigameframes.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.minigameworld.minigameframes.SoloBattleMiniGame;

public class RandomScore extends SoloBattleMiniGame {
	/*
	 * Get random score when sneaking
	 */

	private List<Integer> randomScores;

	public RandomScore() {
		super("RandomScore", 2, 10, 10);
		this.randomScores = new ArrayList<Integer>();
		this.getSetting().setSettingFixed(true);
		this.getSetting().setScoreNotifying(true);
	}

	@Override
	protected void initGameSettings() {
		this.randomScores.clear();
		for (int i = 1; i <= 10; i++) {
			this.randomScores.add(i);
		}
	}

	@Override
	protected void processEvent(Event event) {
		if (event instanceof PlayerToggleSneakEvent) {
			PlayerToggleSneakEvent e = (PlayerToggleSneakEvent) event;
			Player p = e.getPlayer();
			// first sneaking
			if (this.getScore(p) == 0) {
				int randomIndex = (int) (Math.random() * this.randomScores.size());
				int randomScore = this.randomScores.remove(randomIndex);
				this.plusScore(p, randomScore);
			}
		}
	}

	@Override
	protected List<String> registerTutorial() {
		List<String> tutorial = new ArrayList<>();
		tutorial.add("Sneak: get random score");
		return tutorial;
	}

}
