package com.worldbiomusic.minigameworld.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.worldbiomusic.minigameworld.api.MiniGameAccessor;
import com.worldbiomusic.minigameworld.api.MiniGameWorld;
import com.worldbiomusic.minigameworld.customevents.minigame.MiniGameExceptionEvent;
import com.worldbiomusic.minigameworld.customevents.minigame.MiniGameStartEvent;
import com.worldbiomusic.minigameworld.customevents.minigame.player.MiniGamePlayerJoinEvent;
import com.worldbiomusic.minigameworld.managers.MiniGameManager;
import com.worldbiomusic.minigameworld.managers.party.Party;
import com.worldbiomusic.minigameworld.managers.party.PartyManager;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.minigameframes.TeamBattleMiniGame;
import com.worldbiomusic.minigameworld.minigameframes.TeamBattleMiniGame.TeamRegisterMode;

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
		MiniGameAccessor game = e.getMiniGame();
		MiniGame minigame = null;

		// get minigame instance
		for (MiniGame m : this.minigameManager.getMiniGameList()) {
			if (game.equals(m)) {
				minigame = m;
			}
		}

		// check minigame is TeamBattleMiniGame
		if (!(minigame instanceof TeamBattleMiniGame)) {
			return;
		}

		TeamBattleMiniGame teamBattleMiniGame = (TeamBattleMiniGame) minigame;

		// check team register mode is "PARTY"
		if (teamBattleMiniGame.getTeamRegisterMode() == TeamRegisterMode.PARTY) {
			Player p = e.getPlayer();

			// check entered party count
			int playersPartyCount = PartyManager.getPartyCountBetweenPlayers(teamBattleMiniGame.getPlayers());
			if (playersPartyCount >= teamBattleMiniGame.getTeamCountLimit()) {
				teamBattleMiniGame.sendMessage(p, teamBattleMiniGame.getTitle() + " already has full parties");
				e.setCancelled(true);
				return;
			}

			// check party member count
			MiniGameWorld mw = MiniGameWorld.create(MiniGameWorld.API_VERSION);
			Party party = mw.getPartyManager().getPlayerParty(p);
			if (party.getMembers().size() > teamBattleMiniGame.getTeamSize()) {
				teamBattleMiniGame.sendMessage(p,
						"This game allows only " + teamBattleMiniGame.getTeamSize() + " or below party members");
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onMiniGameStart(MiniGameStartEvent e) {
		MiniGameAccessor game = e.getMiniGame();
		MiniGame minigame = null;

		for (MiniGame m : this.minigameManager.getMiniGameList()) {
			if (game.equals(m)) {
				minigame = m;
			}
		}

		if (!(minigame instanceof TeamBattleMiniGame)) {
			return;
		}

		TeamBattleMiniGame teamBattleMiniGame = (TeamBattleMiniGame) minigame;

		if (teamBattleMiniGame.getTeamRegisterMode() == TeamRegisterMode.PARTY) {
			int partyCount = PartyManager.getPartyCountBetweenPlayers(teamBattleMiniGame.getPlayers());
			if (partyCount <= 1) {
				teamBattleMiniGame.sendMessageToAllPlayers("Game can't start with only one party(team)");
				e.setCancelled(true);
			}
		}
	}
}
