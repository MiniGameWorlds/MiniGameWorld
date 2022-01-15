package com.worldbiomusic.minigameworld.minigameframes.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.worldbiomusic.minigameworld.customevents.MiniGameExceptionEvent;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.util.Utils;

public class MiniGameViewManager {
	private MiniGame minigame;
	private List<MiniGamePlayerState> viewers;

	public MiniGameViewManager(MiniGame minigame) {
		this.minigame = minigame;
		this.viewers = new ArrayList<>();
	}

	/**
	 * Get viewer list
	 * 
	 * @return Viewers
	 */
	public List<MiniGamePlayerState> getViewers() {
		return this.viewers;
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
	public void processEvent(Event event) {
		if (event instanceof AsyncPlayerChatEvent) {
			processChat((AsyncPlayerChatEvent) event);
		} else if (event instanceof PlayerRespawnEvent) {
			processRespawn((PlayerRespawnEvent) event);
		} else if (event instanceof EntityDamageEvent) {
			processDamage((EntityDamageEvent) event);
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
	 * Cancel damage to views
	 * 
	 * @param e event
	 */
	private void processDamage(EntityDamageEvent e) {
		e.setCancelled(true);
	}

	/**
	 * Add a player as a viewer in a minigmae<br>
	 * 
	 * @param p Viewer
	 */
	public void addViewer(Player p) {
		// return if minigame is not active
		if (!this.minigame.isActive()) {
			Utils.sendMsg(p, "Minigame is not acitve");
			return;
		}

		// add a viewer
		MiniGamePlayerState viewer = new MiniGamePlayerState(minigame, p);
		if (!isViewing(p)) {
			this.viewers.add(viewer);
		}

		// manage state
		// [IMPORTANT] call before change a player's state to save origin state
		viewer.savePlayerState();
		viewer.makePureState();

		// set gamemode to sepectator
		p.setGameMode(GameMode.SPECTATOR);

		// teleport player
		p.teleport(this.minigame.getLocation());
	}

	/**
	 * Remove a viewer from the minigame
	 * 
	 * @param p Viewer
	 */
	public void removeViewer(Player p) {
		if (isViewing(p)) {
			MiniGamePlayerState viewer = getViewerState(p);
			viewer.restorePlayerState();

			// [IMPORTANT] call after restore a player's state
			this.viewers.remove(viewer);
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
	 * @param event MiniGameExceptionEvent
	 */
	public void handleException(MiniGameExceptionEvent event) {
		if (event.getReason().equalsIgnoreCase("player-quit-server")) {
			this.viewers.forEach(v -> removeViewer(v.getPlayer()));
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
