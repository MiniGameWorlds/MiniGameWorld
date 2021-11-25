package com.worldbiomusic.minigameworld.minigameframes.helpers;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

public class MiniGamePlayerStateManager {
	/*
	 * manage player state for enjoying minigame
	 */
	private MiniGame minigame;
	private Set<MiniGamePlayerState> playerStates;

	public MiniGamePlayerStateManager(MiniGame minigame) {
		this.minigame = minigame;
		this.playerStates = new HashSet<>();
	}

	public void savePlayerState(Player p) {
		this.playerStates.add(new MiniGamePlayerState(this.minigame, p));
	}

	public void restorePlayerState(Player p) {
		MiniGamePlayerState pState = this.getPlayerState(p);
		if (pState != null) {
			pState.restorePlayerState();
		}
	}

	public void makePureState(Player p) {
		// ready for playing minigame
		MiniGamePlayerState pState = this.getPlayerState(p);
		if (pState != null) {
			pState.makePureState();
		}
	}

	private MiniGamePlayerState getPlayerState(Player p) {
		for (MiniGamePlayerState pState : this.playerStates) {
			if (pState.isSamePlayer(p)) {
				return pState;
			}
		}
		return null;
	}

	public void clearAllPlayers() {
		this.playerStates.clear();
	}
}
