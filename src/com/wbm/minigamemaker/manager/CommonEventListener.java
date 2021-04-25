package com.wbm.minigamemaker.manager;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredListener;

import com.wbm.minigamemaker.Main;

public class CommonEventListener implements Listener {

	MiniGameManager minigameManager;

	public CommonEventListener(MiniGameManager minigameManager) {
		this.minigameManager = minigameManager;

		this.registerAllEventHandler();
	}

	public void registerAllEventHandler() {
		// TODO: 버그가 있음 -> 처음에 버킷 실행하면 이벤트가 잘 전달이 안되는데, reload 하면 잘됨........
		RegisteredListener registeredListener = new RegisteredListener(this, (listener, event) -> onEvent(event),
				EventPriority.NORMAL, Main.getInstance(), false);
		for (HandlerList handler : HandlerList.getHandlerLists()) {
			handler.register(registeredListener);
		}
	}

	private Object onEvent(Event event) {
		/*
		 * 모든 이벤트 발생 캐치하는 메소드
		 */
		if (this.minigameManager.isPossibleEvent(event)) {
			this.minigameManager.processEvent(event);
		}

		return null;
	}

	@EventHandler
	public void onPlayerTouchMiniGameSign(PlayerInteractEvent e) {
		/*
		 * 플레이어가 미니게임에 참여하는 이벤트
		 */
		Block block = e.getClickedBlock();
		if (block != null) {
			if (block.getType() == Material.OAK_SIGN || block.getType() == Material.OAK_WALL_SIGN) {
				if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Sign sign = (Sign) block.getState();
					String minigame = sign.getLines()[0];
					String title = sign.getLines()[1];
					if (minigame.equalsIgnoreCase("[MiniGame]")) {
						boolean usingSign = (boolean) this.minigameManager.getGameSetting().get("signJoin");
						if (usingSign) {
							Player p = e.getPlayer();
							this.minigameManager.joinGame(p, title);
						}
					}
				}
			}
		}
	}

//	@EventHandler
//	public void onMiniGamePlayerInteractEvent(PlayerInteractEvent e) {
//		/*
//		 * 이벤트 매니저에서 이벤트 형에 따라서 구분해서 처리하게 전달
//		 */
//		this.minigameManager.processEvent(e);
//	}
//
//	@EventHandler
//	public void onMiniGameBlockBreakEvent(BlockBreakEvent e) {
//		/*
//		 * 이벤트 매니저에서 이벤트 형에 따라서 구분해서 처리하게 전달
//		 */
//		this.minigameManager.processEvent(e);
//	}
//
//	@EventHandler
//	public void onMiniGameBlockPlaceEvent(BlockPlaceEvent e) {
//		/*
//		 * 이벤트 매니저에서 이벤트 형에 따라서 구분해서 처리하게 전달
//		 */
//		this.minigameManager.processEvent(e);
//	}
//
//	@EventHandler
//	public void onPlayerExitServerWhenMiniGamePlaying(EntityDamageEvent e) {
//		this.minigameManager.processEvent(e);
//	}

	@EventHandler
	public void onPlayerExitServerWhenMiniGamePlaying(PlayerQuitEvent e) {
		this.minigameManager.processException(e.getPlayer());
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
