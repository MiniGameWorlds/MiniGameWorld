package com.wbm.minigamemaker.manager;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CommonEventListener implements Listener {
	MiniGameManager minigameManager;

	public CommonEventListener(MiniGameManager minigameManager) {
		this.minigameManager = minigameManager;
	}

	@EventHandler
	public void onPlayerTouchMiniGameSign(PlayerInteractEvent e) {
		/*
		 * 플레이어가 미니게임에 참여하는 이벤트
		 */
		Block b = e.getClickedBlock();
		if (b != null) {
			if (b.getType() == Material.OAK_SIGN || b.getType() == Material.OAK_WALL_SIGN) {
				if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					e.getPlayer().sendMessage("click minigame");
					Sign sign = (Sign) b.getState();
					if (sign.getLines()[0].equalsIgnoreCase("[MiniGame]")) {
						MiniGameType minigameType = MiniGameType.FIT_TOOl;
						Player p = e.getPlayer();
						this.minigameManager.joinGame(p, minigameType);
					}
				}
			}
		}
	}

	@EventHandler
	public void onMiniGamePlayerInteractEvent(PlayerInteractEvent e) {
		/*
		 * 이벤트 매니저에서 이벤트 형에 따라서 구분해서 처리하게 전달
		 */
		this.minigameManager.processEvent(e);
	}

	@EventHandler
	public void onMiniGameBlockBreakEvent(BlockBreakEvent e) {
		/*
		 * 이벤트 매니저에서 이벤트 형에 따라서 구분해서 처리하게 전달
		 */
		this.minigameManager.processEvent(e);
	}

	@EventHandler
	public void onMiniGameBlockPlaceEvent(BlockPlaceEvent e) {
		/*
		 * 이벤트 매니저에서 이벤트 형에 따라서 구분해서 처리하게 전달
		 */
		this.minigameManager.processEvent(e);
	}

	@EventHandler
	public void onPlayerExitServerWhenMiniGamePlaying(PlayerQuitEvent e) {
		this.minigameManager.processException(e.getPlayer());
	}

	@EventHandler
	public void onPlayerExitServerWhenMiniGamePlaying(EntityDamageEvent e) {
		this.minigameManager.processEvent(e);
	}

}
