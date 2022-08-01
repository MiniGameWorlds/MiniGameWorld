package com.minigameworld.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.minigameworld.api.MiniGameAccessor;
import com.minigameworld.api.MiniGameWorld;
import com.minigameworld.customevents.minigame.MiniGameExceptionEvent;
import com.minigameworld.customevents.minigame.MiniGameStartEvent;
import com.minigameworld.customevents.minigame.player.MiniGamePlayerJoinEvent;
import com.minigameworld.managers.MiniGameManager;
import com.minigameworld.managers.party.Party;
import com.minigameworld.managers.party.PartyManager;
import com.minigameworld.minigameframes.MiniGame;
import com.minigameworld.minigameframes.TeamBattleMiniGame;
import com.minigameworld.minigameframes.TeamBattleMiniGame.TeamRegisterMode;

public class MiniGameEventListener implements Listener {
	private MiniGameManager minigameManager;

	public MiniGameEventListener(MiniGameManager minigameManager) {
		this.minigameManager = minigameManager;
	}

	@EventHandler
	public void onMiniGameExceptionEvent(MiniGameExceptionEvent e) {
		this.minigameManager.handleException(e);
	}

	/**
	 * If minigame is TeamBattleMiniGame and team register mode is "PARTY", limit
	 * party entrance
	 */
	@EventHandler
	public void onMiniGamePlayerJoin(MiniGamePlayerJoinEvent e) {
		MiniGame minigame = e.getMiniGame().minigame();

		// check minigame is TeamBattleMiniGame
		if (!(minigame instanceof TeamBattleMiniGame)) {
			return;
		}

		TeamBattleMiniGame teamBattleMiniGame = (TeamBattleMiniGame) minigame;

		// check team register mode is "PARTY"
		if (teamBattleMiniGame.getTeamRegisterMode() != TeamRegisterMode.PARTY) {
			return;
		}

		Player p = e.getPlayer();

		// check entered party count
		int playersPartyCount = PartyManager.getPartyCountOfPlayers(teamBattleMiniGame.getPlayers());
		if (playersPartyCount >= teamBattleMiniGame.getTeamCountLimit()) {
			teamBattleMiniGame.sendMessage(p, teamBattleMiniGame.getTitle() + " already has full parties");
			e.setCancelled(true);
			return;
		}

		// check party member count
		Party party = this.minigameManager.getPartyManager().getPlayerParty(p);
		if (party.getSize() > teamBattleMiniGame.getTeamSize()) {
			teamBattleMiniGame.sendMessage(p,
					"This party game allows only " + teamBattleMiniGame.getTeamSize() + " or below party members");
			e.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onMiniGameStart(MiniGameStartEvent e) {
		MiniGame minigame = e.getMiniGame().minigame();

		if (!(minigame instanceof TeamBattleMiniGame)) {
			return;
		}

		TeamBattleMiniGame teamBattleMiniGame = (TeamBattleMiniGame) minigame;

		if (teamBattleMiniGame.getTeamRegisterMode() == TeamRegisterMode.PARTY) {
			int partyCount = PartyManager.getPartyCountOfPlayers(teamBattleMiniGame.getPlayers());
			if (partyCount <= 1) {
				teamBattleMiniGame.sendMessages("Game can't start with only one party(team)");
				e.setCancelled(true);
			}
		}
	}
}
