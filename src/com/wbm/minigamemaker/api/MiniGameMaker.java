package com.wbm.minigamemaker.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.wbm.minigamemaker.manager.MiniGameManager;
import com.wbm.minigamemaker.minigameframes.MiniGame;
import com.wbm.minigamemaker.observer.MiniGameObserver;

public class MiniGameMaker {
	/*
	 * MiniGameManager wrapper(api) class
	 */
	private static MiniGameMaker instance = new MiniGameMaker();
	private MiniGameManager minigameM;

	private MiniGameMaker() {
	}

	public static MiniGameMaker create() {
		return instance;
	}

	public void setMiniGameManager(MiniGameManager minigameM) {
		// MiniGameMaker플러그인 시작될 떄 최초 1회만 등록되게 설정
		if (this.minigameM == null) {
			this.minigameM = minigameM;
		}
	}

	/*
	 * API
	 * 
	 * - joinGame
	 * - leaveGame
	 * - handleException
	 * - isPossibleEvent
	 * - checkPlayerIsPlayingMiniGame
	 * - getMiniGameWithClassName
	 * - getPlayingGame
	 * - getMiniGameList
	 * - registerMiniGame
	 * - unregisterMiniGame
	 * - getLobby
	 */

	public boolean joinGame(Player p, String title) {
		return this.minigameM.joinGame(p, title);
	}

	public boolean leaveGame(Player p) {
		return this.minigameM.leaveGame(p);
	}

	public void handleException(Player p, MiniGame.Exception exception, Object arg) {
		this.minigameM.handleException(p, exception, arg);
	}

	public boolean isPossibleEvent(Event event) {
		return this.minigameM.isPossibleEvent(event);
	}

	public boolean isPossibleEvent(Class<? extends Event> event) {
		return this.minigameM.isPossibleEvent(event);
	}

	public boolean checkPlayerIsPlayingMiniGame(Player p) {
		return this.minigameM.checkPlayerIsPlayingMiniGame(p);
	}

	public MiniGameAccessor getMiniGameWithClassName(String className) {
		MiniGame minigame = this.minigameM.getMiniGameWithClassName(className);
		return new MiniGameAccessor(minigame);
	}

	public MiniGameAccessor getPlayingMiniGame(Player p) {
		MiniGame minigame = this.minigameM.getPlayingMiniGame(p);
		return new MiniGameAccessor(minigame);
	}

	public List<MiniGameAccessor> getMiniGameList() {
		List<MiniGame> minigames = this.minigameM.getMiniGameList();
		List<MiniGameAccessor> minigameAccessors = new ArrayList<MiniGameAccessor>();
		minigames.forEach(game -> minigameAccessors.add(new MiniGameAccessor(game)));
		return minigameAccessors;
	}

	public boolean registerMiniGame(MiniGame newGame) {
		return this.minigameM.registerMiniGame(newGame);
	}

	public boolean unregisterMiniGame(MiniGame minigame) {
		return this.minigameM.unregisterMiniGame(minigame);
	}

	public Location getLobby() {
		return this.minigameM.getLobby().clone();
	}

	public void registerMiniGameObserver(MiniGameObserver observer) {
		// register observer on All MiniGames
		// ex. give reward with each minigames
		this.minigameM.getMiniGameList().forEach(minigame -> minigame.registerObserver(observer));
	}

	public void unregisterMiniGameObserver(MiniGameObserver observer) {
		// unregister observer from All MiniGames
		this.minigameM.getMiniGameList().forEach(minigame -> minigame.unregisterObserver(observer));
	}

}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
