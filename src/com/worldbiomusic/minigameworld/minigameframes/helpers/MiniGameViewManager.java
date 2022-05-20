package com.worldbiomusic.minigameworld.minigameframes.helpers;

import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.worldbiomusic.minigameworld.customevents.minigame.MiniGameExceptionEvent;
import com.worldbiomusic.minigameworld.customevents.minigame.MiniGamePlayerExceptionEvent;
import com.worldbiomusic.minigameworld.customevents.minigame.player.MiniGamePlayerUnviewEvent;
import com.worldbiomusic.minigameworld.customevents.minigame.player.MiniGamePlayerViewEvent;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;

public class MiniGameViewManager {
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
	 * Process event related with viewers
	 * 
	 * @param event event
	 */
	public void onEvent(Event event) {
		if (event instanceof AsyncPlayerChatEvent) {
			processChat((AsyncPlayerChatEvent) event);
		} else if (event instanceof PlayerRespawnEvent) {
			processRespawn((PlayerRespawnEvent) event);
		}
	}

	/**
	 * Only viewers in the same minigame can read a message
	 * 
	 * @param e Chat event
	 */
	private void processChat(AsyncPlayerChatEvent e) {
		Set<Player> recipients = e.getRecipients();

		List<Player> nonViewers = recipients.stream().filter(r -> !isViewing(r)).toList();
		recipients.removeAll(nonViewers);
	}

	/**
	 * Set a minigame location to minigame location
	 * 
	 * @param e event
	 */
	private void processRespawn(PlayerRespawnEvent e) {
		e.setRespawnLocation(this.minigame.getLocation());
	}

	/**
	 * Add a player as a viewer in a minigmae<br>
	 * 
	 * @param p Viewer
	 */
	public void viewGame(Player p) {
		// call player view event
		MiniGamePlayerViewEvent viewEvent = new MiniGamePlayerViewEvent(this.minigame, p);
		Bukkit.getServer().getPluginManager().callEvent(viewEvent);
		// check event is cancelled
		if (viewEvent.isCancelled()) {
			return;
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
	}

	/**
	 * Remove a viewer from the minigame
	 * 
	 * @param p Viewer
	 */
	public void unviewGame(Player p) {
		// check player is a viewer
		if (isViewing(p)) {
			// call player unview event
			MiniGamePlayerUnviewEvent unviewEvent = new MiniGamePlayerUnviewEvent(minigame, p);
			Bukkit.getServer().getPluginManager().callEvent(unviewEvent);
			// check event is cancelled
			if (unviewEvent.isCancelled()) {
				return;
			}

			MiniGamePlayerState viewer = getViewerState(p);
			viewer.restorePlayerState();

			// [IMPORTANT] call after restore a player's state
			this.viewers.remove(viewer);

			// send title
			this.minigame.sendTitle(p, ChatColor.BOLD + "Leave", "");
		}
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
