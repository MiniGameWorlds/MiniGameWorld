package com.minigameworld.minigameframes.games;

import java.util.List;

import org.bukkit.event.Event;

import com.minigameworld.minigameframes.TeamBattleMiniGame;

public class HiddenArcher extends TeamBattleMiniGame {
	/*
	 * shoot hiding players with bow
	 */

	public HiddenArcher() {
		super("HiddenArcher", 4, 60 * 3, 20);
	}

	@Override
	protected void initGameSettings() {
		super.initGameSettings();
	}

	@Override
	protected void createTeams() {
	}

	@Override
	protected void registerPlayersToTeam() {
	}

	@Override
	protected List<String> registerTutorial() {
		return null;
	}

	@Override
	protected void processEvent(Event event) {
	}

}
