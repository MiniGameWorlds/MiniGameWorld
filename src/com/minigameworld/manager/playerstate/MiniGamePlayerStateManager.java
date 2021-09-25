package com.minigameworld.manager.playerstate;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public class MiniGamePlayerStateManager {
	/*
	 * manage player data for enjoying minigame
	 */
	private Set<MiniGamePlayerData> playerData;

	public MiniGamePlayerStateManager() {
		this.playerData = new HashSet<>();
	}

	public void savePlayerState(Player p) {
		this.playerData.add(new MiniGamePlayerData(p));
	}

	public void restorePlayerState(Player p) {
		MiniGamePlayerData pData = this.getPlayerData(p);
		if (pData != null) {
			pData.restorePlayerData();
		}
	}

	public void makePureState(Player p) {
		// ready for playing minigame
		MiniGamePlayerData pData = this.getPlayerData(p);
		if (pData != null) {
			pData.makePureState();
		}
	}

	private MiniGamePlayerData getPlayerData(Player p) {
		for (MiniGamePlayerData pData : this.playerData) {
			if (pData.isSamePlayer(p)) {
				return pData;
			}
		}
		return null;
	}

	public void clearData() {
		this.playerData.clear();
	}
}
