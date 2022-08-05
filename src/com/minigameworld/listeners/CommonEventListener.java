package com.minigameworld.listeners;

import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.ServerCommandEvent;

import com.minigameworld.events.minigame.MiniGamePlayerExceptionEvent;
import com.minigameworld.events.minigame.MiniGameServerExceptionEvent;
import com.minigameworld.managers.MiniGameManager;
import com.minigameworld.minigameframes.MiniGame;
import com.minigameworld.util.Setting;
import com.minigameworld.util.Utils;

public class CommonEventListener implements Listener {
	private MiniGameManager minigameManager;

	public CommonEventListener(MiniGameManager minigameManager) {
		this.minigameManager = minigameManager;
	}

	/*
	 * Join / Quit
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		this.minigameManager.todoOnPlayerJoin(e.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		this.minigameManager.todoOnPlayerQuit(p);

		// call minigame exception event
		Utils.callEvent(new MiniGamePlayerExceptionEvent("player-quit-server", p));
	}
	
	@EventHandler
	public void onPluginDisableEvent(PluginDisableEvent e) {
		this.minigameManager.onPluginDisabled(e);
	}

	/*
	 * Inventory
	 */
	@EventHandler
	private void onPlayerClickMenu(InventoryClickEvent e) {
		this.minigameManager.getMenuManager().onInventoryEvent(e);
	}

	@EventHandler
	private void onPlayerCloseGUI(InventoryCloseEvent e) {
		this.minigameManager.getMenuManager().onInventoryEvent(e);
	}

	/*
	 * MiniGame Sign
	 */
	@EventHandler
	private void onPlayerInteractMiniGameSign(PlayerInteractEvent e) {
		// check player is trying to join or leaving with sign
		Player p = e.getPlayer();

		Block block = e.getClickedBlock();
		if (block == null) {
			return;
		}

		// check clicked block is a Sign block
		if (!(block.getState() instanceof Sign)) {
			return;
		}

		Sign sign = (Sign) block.getState();
		String[] lines = sign.getLines();
		String minigame = lines[0];
		String title = lines[1];

		// check sign lines
		if (minigame.equals(Setting.JOIN_SIGN_CAPTION) || minigame.equals(Setting.LEAVE_SIGN_CAPTION)) {
			// check minigameSign option
			if (!Utils.checkPerm(p, "signblock")) {
				return;
			}
		}

		// check sign
		if (minigame.equals(Setting.JOIN_SIGN_CAPTION)) {
			// join
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				this.minigameManager.joinGame(p, title);
			}
			// view
			else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
				this.minigameManager.viewGame(p, title);
			}
		} else if (minigame.equals(Setting.LEAVE_SIGN_CAPTION)) {
			// leave or unview
			if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (this.minigameManager.isPlayingGame(p)) {
					this.minigameManager.leaveGame(p);
				} else if (this.minigameManager.isViewingGame(p)) {
					this.minigameManager.unviewGame(p);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		// Don't send message to players playing minigame from outside
		Player p = e.getPlayer();

		if (this.minigameManager.isPlayingGame(p)) {
			return;
		}

		if (Setting.ISOLATED_CHAT) {
			Set<Player> playingMinigamePlayers = e.getRecipients();
			for (MiniGame m : this.minigameManager.getTemplateGames()) {
				playingMinigamePlayers.removeAll(m.getPlayers());
			}
		}
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		String cmd = e.getMessage();

		if (isStopCommand(cmd, true)) {
			Utils.callEvent(new MiniGameServerExceptionEvent("server-stop-by-player"));
		}
	}

	@EventHandler
	public void onServerCommandProcess(ServerCommandEvent e) {
		String cmd = e.getCommand();

		if (isStopCommand(cmd, false)) {
			Utils.callEvent(new MiniGameServerExceptionEvent("server-stop-by-non-player"));
		}
	}

	private boolean isStopCommand(String cmd, boolean withSlash) {
		if (withSlash) {
			cmd = cmd.substring(1);
		}

		switch (cmd) {
		case "stop":
		case "end":
		case "reload":
		case "reload confirm":
		case "restart":
			return true;
		}
		return false;
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
