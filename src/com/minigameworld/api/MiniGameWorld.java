package com.minigameworld.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.minigameworld.managers.MiniGameManager;
import com.minigameworld.managers.party.PartyManager;
import com.minigameworld.minigameframes.MiniGame;
import com.minigameworld.minigameframes.utils.MiniGameEventDetector;
import com.minigameworld.observer.MiniGameObserver;

public class MiniGameWorld {
	/*
	 * MiniGameManager wrapper(api) class
	 */
	private static MiniGameWorld instance = new MiniGameWorld();
	private MiniGameManager minigameManager;

	private MiniGameWorld() {
	}

	public static MiniGameWorld create() {
		return instance;
	}

	public void setMiniGameManager(MiniGameManager minigameM) {
		// [IMPORTANT] set MiniGameManager only once when plugin loaded
		if (this.minigameManager == null) {
			this.minigameManager = minigameM;
		}
	}

	/*
	 * API
	 * 
	 * - joinGame
	 * - leaveGame
	 * - handleException
	 * - checkPlayerIsPlayingMiniGame
	 * - getMiniGameWithClassName
	 * - getPlayingGame
	 * - getMiniGameList
	 * - registerMiniGame
	 * - unregisterMiniGame
	 * - getLobby
	 * - openGUI
	 * - getPartyManager
	 */

	public void joinGame(Player p, String title) {
		this.minigameManager.joinGame(p, title);
	}

	public void leaveGame(Player p) {
		this.minigameManager.leaveGame(p);
	}

	public void handleException(Player p, MiniGame.GameException exception, Object arg) {
		this.minigameManager.handleException(p, exception, arg);
	}

	public MiniGameEventDetector getMiniGameEventDetector() {
		return this.minigameManager.getMiniGameEventDetector();
	}

	public boolean checkPlayerIsPlayingMiniGame(Player p) {
		return this.minigameManager.isPlayingMiniGame(p);
	}

	public MiniGameAccessor getMiniGameWithClassName(String className) {
		MiniGame minigame = this.minigameManager.getMiniGameWithClassName(className);
		return new MiniGameAccessor(minigame);
	}

	public MiniGameAccessor getPlayingMiniGame(Player p) {
		MiniGame minigame = this.minigameManager.getPlayingMiniGame(p);
		return new MiniGameAccessor(minigame);
	}

	public List<MiniGameAccessor> getMiniGameList() {
		List<MiniGame> minigames = this.minigameManager.getMiniGameList();
		List<MiniGameAccessor> minigameAccessors = new ArrayList<MiniGameAccessor>();
		minigames.forEach(game -> minigameAccessors.add(new MiniGameAccessor(game)));
		return minigameAccessors;
	}

	public boolean registerMiniGame(MiniGame newGame) {
		return this.minigameManager.registerMiniGame(newGame);
	}

	public boolean unregisterMiniGame(MiniGame minigame) {
		return this.minigameManager.unregisterMiniGame(minigame);
	}

	public Location getLobby() {
		return MiniGameManager.getLobby().clone();
	}

	public void registerMiniGameObserver(MiniGameObserver observer) {
		// register observer on All MiniGames
		// ex. give reward with each minigames
		this.minigameManager.getMiniGameList().forEach(minigame -> minigame.registerObserver(observer));
	}

	public void unregisterMiniGameObserver(MiniGameObserver observer) {
		// unregister observer from All MiniGames
		this.minigameManager.getMiniGameList().forEach(minigame -> minigame.unregisterObserver(observer));
	}

	public Inventory openMiniGameGUI(Player p) {
		return this.minigameManager.getMiniGameGUIManager().openGUI(p);
	}

	public PartyManager getPartyManager() {
		return this.minigameManager.getPartyManager();
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
