package com.minigameworld.minigameframes.games;

import java.util.List;

import com.minigameworld.minigameframes.TeamBattleMiniGame;

public class HiddenArcher extends TeamBattleMiniGame {
	/*
	 * show hiding players with bow
	 */

	public HiddenArcher() {
		super("HiddenArcher", 6, 60 * 3, 20, 3, 2);
	}
	
	@Override
	protected void initGameSetting() {
		super.initGameSetting();
	}

	@Override
	protected void registerAllPlayersToTeam() {
	}

	@Override
	protected List<String> registerTutorial() {
		return null;
	}

}
