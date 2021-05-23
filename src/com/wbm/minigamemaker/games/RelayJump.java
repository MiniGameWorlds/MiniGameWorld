package com.wbm.minigamemaker.games;

import java.util.List;

import org.bukkit.event.Event;

import com.wbm.minigamemaker.games.frame.TeamMiniGame;

public class RelayJump extends TeamMiniGame {

	/*
	 * 설명: 일정시간내 접속한 플레이어끼리 순서대로 한번씩 점프하는 게임
	 * 
	 * 타입: Team
	 */
	public RelayJump() {
		super("RelayJump", 4, 10, 10);
	}

	@Override
	protected void initGameSetting() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processEvent(Event event) {
		// TODO Auto-generated method stub

	}

	@Override
	protected List<String> getGameTutorialStrings() {
		// TODO Auto-generated method stub
		return null;
	}

}
