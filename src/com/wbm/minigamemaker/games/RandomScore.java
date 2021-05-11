package com.wbm.minigamemaker.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.wbm.minigamemaker.games.frame.MiniGame;

public class RandomScore extends MiniGame {

	List<Integer> scores;

	public RandomScore() {
		super("RandomScore", 2, 10, 10);
		this.scores = new ArrayList<Integer>();
		this.setSettingFixed(true);
	}

	@Override
	protected void initGameSetting() {
		this.scores.clear();
		for (int i = 1; i <= 10; i++) {
			this.scores.add(i);
		}
	}

	@Override
	protected void processEvent(Event event) {
		if (event instanceof PlayerToggleSneakEvent) {
			PlayerToggleSneakEvent e = (PlayerToggleSneakEvent) event;
			Player p = e.getPlayer();
			// 한번도 randomScore를 안얻었을 때
			if (this.getScore(p) == 0) {
				int randomIndex = (int) (Math.random() * this.scores.size());
				int randomScore = this.scores.remove(randomIndex);
				this.plusScore(p, randomScore);
				p.sendMessage("+" + randomScore);
			}
		}
	}

	@Override
	protected void runTaskAfterStart() {

	}

	@Override
	protected void runTaskAfterFinish() {

	}

	@Override
	protected List<String> getGameTutorialStrings() {
		List<String> list = new ArrayList<String>();
		list.add("Sneak: get random score");
		return list;
	}

	@Override
	protected void handleGameExeption(Player p) {

	}

}
