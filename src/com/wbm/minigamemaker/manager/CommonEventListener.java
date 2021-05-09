package com.wbm.minigamemaker.manager;

import java.util.Arrays;

import org.bukkit.Bukkit;
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
import org.bukkit.plugin.EventExecutor;

import com.wbm.minigamemaker.Main;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;

public class CommonEventListener implements Listener {
	/*
	 * BUG: 서버에 이벤트 리스너가 등록이 잘 안됨(reload 하면 등록이 또 잘됨)
	 */

	MiniGameManager minigameManager;

	public CommonEventListener(MiniGameManager minigameManager) {
		this.minigameManager = minigameManager;

		this.registerAllEventListener();
	}

	public void registerAllEventListener() {
		System.out.println("wait for register AllEventHandler");
		ClassInfoList events = new ClassGraph().enableClassInfo().scan() // you should use try-catch-resources instead
				.getClassInfo(Event.class.getName()).getSubclasses().filter(info -> !info.isAbstract());

		Listener listener = new Listener() {
		};
		EventExecutor executor = (ignored, event) -> onEvent(event);

		try {
			for (ClassInfo event : events) {
				// noinspection unchecked
				@SuppressWarnings("unchecked")
				Class<? extends Event> eventClass = (Class<? extends Event>) Class.forName(event.getName());

				if (Arrays.stream(eventClass.getDeclaredMethods()).anyMatch(
						method -> method.getParameterCount() == 0 && method.getName().equals("getHandlers"))) {
					// We could do this further filtering on the ClassInfoList instance instead,
					// but that would mean that we have to enable method info scanning.
					// I believe the overhead of initializing ~20 more classes
					// is better than that alternative.

					Bukkit.getPluginManager().registerEvent(eventClass, listener, EventPriority.NORMAL, executor,
							Main.getInstance());
				}
			}
		} catch (ClassNotFoundException e) {
			throw new AssertionError("Scanned class wasn't found", e);
		}

//		String[] eventNames = events.stream()
//		        .map(info -> info.getName().substring(info.getName().lastIndexOf('.') + 1))
//		        .toArray(String[]::new);
//
//		Bukkit.getLogger().info("List of events: " + String.join(", ", eventNames));
		Bukkit.getLogger().info("Events found: " + events.size());
		Bukkit.getLogger().info("HandlerList size: " + HandlerList.getHandlerLists().size());
	}

	private Object onEvent(Event event) {
		/*
		 * 모든 이벤트 발생 캐치하는 메소드
		 */
		this.minigameManager.processEvent(event);

		return null;
	}

	@EventHandler
	public void onPlayerTouchMiniGameSign(PlayerInteractEvent e) {
		/*
		 * OAK_SIGN으로 미니게임에 참여하는 이벤트
		 */
		Block block = e.getClickedBlock();
		if (block != null) {
			if (block.getType() == Material.OAK_SIGN || block.getType() == Material.OAK_WALL_SIGN) {
				if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Sign sign = (Sign) block.getState();
					String minigame = sign.getLines()[0];
					String title = sign.getLines()[1];
					if (minigame.equalsIgnoreCase("[MiniGame]")) {
						boolean signJoin = (boolean) this.minigameManager.getGameSetting().get("signJoin");
						// setting.json에서 signJoin값을 통해서 표지판 입장 유무 결정
						if (signJoin) {
							Player p = e.getPlayer();
							this.minigameManager.joinGame(p, title);
						}
					}
				}
			}
		}
	}

//	@EventHandler
//	public void onPlayerExitServerWhenMiniGamePlaying(PlayerQuitEvent e) {
//		this.minigameManager.processException(e.getPlayer());
//	}

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
