package com.minigameworld.minigameframes.helpers;

import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.minigameworld.events.minigame.MiniGameExceptionEvent;
import com.minigameworld.events.minigame.MiniGamePlayerExceptionEvent;
import com.minigameworld.events.minigame.player.MiniGamePlayerUnviewEvent;
import com.minigameworld.events.minigame.player.MiniGamePlayerViewEvent;
import com.minigameworld.managers.event.GameEvent;
import com.minigameworld.managers.event.GameEvent.State;
import com.minigameworld.managers.event.GameEventListener;
import com.minigameworld.minigameframes.MiniGame;
import com.minigameworld.util.Utils;

public class MiniGameViewManager implements GameEventListener {
	private MiniGame minigame;
	private Queue<MiniGamePlayerState> viewers;

	public MiniGameViewManager(MiniGame minigame) {
		this.minigame = minigame;
		this.viewers = new ConcurrentLinkedQueue<>();
	}

	/**
	 * Get viewer list
	 * 
	 * @return Viewers
	 */
	public Set<Player> getViewers() {
		Set<Player> players = new HashSet<>();
		this.viewers.forEach(v -> players.add(v.getPlayer()));
		return players;
	}

	/**
	 * Check a player is viewing a minigame
	 * 
	 * @param p Playe to check
	 * @return True if a player is viewing a minigame
	 */
	public boolean isViewing(Player p) {
		return !this.viewers.stream().filter(v -> v.isSamePlayer(p)).toList().isEmpty();
	}

	/**
	 * Only each viewers in the same game can read the message when game is in
	 * playing
	 * 
	 * @param e Chat event
	 */
	@GameEvent(state = State.PLAY)
	protected void onChat(AsyncPlayerChatEvent e) {
		Set<Player> recipients = e.getRecipients();

		List<Player> nonViewers = recipients.stream().filter(r -> !isViewing(r)).toList();
		recipients.removeAll(nonViewers);
	}

	/**
	 * Set a minigame location to minigame location
	 * 
	 * @param e event
	 */
	@GameEvent(state = State.ALL)
	protected void onRespawn(PlayerRespawnEvent e) {
		if (!isViewing(e.getPlayer())) {
			return;
		}

		e.setRespawnLocation(this.minigame.getLocation());
	}

	/**
	 * Add player as a viewer to the minigame<br>
	 * 
	 * @param p Viewer
	 */
	public boolean viewGame(Player p) {
		// call player view event (check event is cancelled)
		if (Utils.callEvent(new MiniGamePlayerViewEvent(this.minigame, p))) {
			return false;
		}

		// add a viewer
		MiniGamePlayerState viewer = new MiniGamePlayerState(minigame, p);
		this.viewers.add(viewer);

		// manage state
		// [IMPORTANT] call before change a player's state to save origin state
		viewer.savePlayerState();
		viewer.makePureState();

		// set gamemode to sepectator
		p.setGameMode(GameMode.SPECTATOR);

		// teleport player
		p.teleport(this.minigame.getLocation());

		// send info
		this.minigame.sendTitle(p, ChatColor.BOLD + "View", "");
		this.minigame.sendMessage(p, "Input " + ChatColor.BOLD + "/mw leave " + ChatColor.RESET + "to leave view");
		this.minigame.sendMessage(p,
				"Input " + ChatColor.BOLD + "/mw menu and click leave icon " + ChatColor.RESET + "to leave view");

		/* hook method
		 [IMPORTANT] must be called after the player state is isolated from the outside */
		this.minigame.onView(p);
		return true;
	}

	/**
	 * Remove a viewer from the minigame
	 * 
	 * @param p Viewer
	 */
	public boolean unviewGame(Player p) {
		// check player is a viewer
		if (isViewing(p)) {
			// call player unview event (check event is cancelled)
			if (Utils.callEvent(new MiniGamePlayerUnviewEvent(minigame, p))) {
				return false;
			}

			/* hook method
			 [IMPORTANT] must be called when the player state is still isolated from the outside */
			this.minigame.onUnview(p);

			MiniGamePlayerState viewer = getViewerState(p);
			viewer.restorePlayerState();

			// [IMPORTANT] call after restore a player's state
			this.viewers.remove(viewer);

			// send title
			this.minigame.sendTitle(p, ChatColor.BOLD + "Leave", "");

			return true;
		}
		return false;
	}

	/**
	 * Get a player state of a player
	 * 
	 * @param p Player
	 * @return Player state
	 */
	private MiniGamePlayerState getViewerState(Player p) {
		if (!isViewing(p)) {
			return null;
		}
		return this.viewers.stream().filter(v -> v.isSamePlayer(p)).toList().get(0);
	}

	/**
	 * Handle a exception of a minigame
	 * 
	 * @param exception MiniGameExceptionEvent
	 */
	public void handleException(MiniGameExceptionEvent exception) {
		if (exception instanceof MiniGamePlayerExceptionEvent) {
			MiniGamePlayerExceptionEvent e = (MiniGamePlayerExceptionEvent) exception;
			Player p = e.getPlayer();

			// make the player unview game
			unviewGame(p);
		}

		// check event is server exception
		else {
			// make all viewers unview game
			this.viewers.forEach(v -> unviewGame(v.getPlayer()));
		}
	}

	@Override
	public MiniGame minigame() {
		return this.minigame;
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
//
//
//
