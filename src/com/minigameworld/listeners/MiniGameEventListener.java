package com.minigameworld.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.minigameworld.events.minigame.MiniGameExceptionEvent;
import com.minigameworld.events.minigame.MiniGameStartEvent;
import com.minigameworld.events.minigame.player.MiniGamePlayerJoinEvent;
import com.minigameworld.frames.MiniGame;
import com.minigameworld.frames.TeamBattleMiniGame;
import com.minigameworld.frames.TeamBattleMiniGame.TeamRegisterMode;
import com.minigameworld.managers.MiniGameManager;
import com.minigameworld.managers.party.Party;
import com.minigameworld.managers.party.PartyManager;

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
	protected void onMiniGamePlayerJoinTeamMiniGame(MiniGamePlayerJoinEvent e) {
		// check minigame is this TeamBattleMiniGame
		MiniGame minigame = e.getMiniGame().minigame();
		if (!(minigame instanceof TeamBattleMiniGame)) {
			return;
		}

		TeamBattleMiniGame game = (TeamBattleMiniGame) minigame;

		// check team register mode is "PARTY"
		if (game.getTeamRegisterMode() != TeamRegisterMode.PARTY) {
			return;
		}

		Player p = e.getPlayer();

		// check entered party count
		int playersPartyCount = PartyManager.getPartyCountOfPlayers(game.getPlayers());
		if (playersPartyCount >= game.getTeamCountLimit()) {
			game.sendMessage(p, game.getTitle() + " already has full parties");
			e.setCancelled(true);
			return;
		}

		// check party member count
		Party party = MiniGameManager.getInstance().getPartyManager().getPlayerParty(p);
		if (party.getSize() > game.getTeamSize()) {
			game.sendMessage(p, "This party game allows only " + game.getTeamSize() + " or below party members");
			e.setCancelled(true);
			return;
		}
	}

	@EventHandler
	protected void onMiniGameStart(MiniGameStartEvent e) {
		// check minigame is this TeamBattleMiniGame
		MiniGame minigame = e.getMiniGame().minigame();
		if (!(minigame instanceof TeamBattleMiniGame)) {
			return;
		}
		TeamBattleMiniGame game = (TeamBattleMiniGame) minigame;

		// check team register mode is "PARTY"
		if (game.getTeamRegisterMode() != TeamRegisterMode.PARTY) {
			return;
		}

		int partyCount = PartyManager.getPartyCountOfPlayers(game.getPlayers());
		if (partyCount <= 1) {
			game.sendMessages("Game can't start with only one party (team)");
			e.setCancelled(true);
		}
	}
}
