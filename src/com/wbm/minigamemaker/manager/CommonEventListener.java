package com.wbm.minigamemaker.manager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;

import com.wbm.minigamemaker.MiniGameMakerMain;
import com.wbm.minigamemaker.util.Utils;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;

public class CommonEventListener implements Listener {
	/*
	 * BUG: 서버에 이벤트 리스너가 등록이 잘 안됨(reload 하면 등록이 또 잘됨)
	 */

	private MiniGameManager minigameManager;

	public CommonEventListener(MiniGameManager minigameManager) {
		this.minigameManager = minigameManager;

		this.registerAllEventListener();
	}

	private void registerAllEventListener() {
		Utils.info("[ Register EventHandler ]");
		Utils.info("wait for all EventHandler registration...");
		Utils.info("Event class name: " + Event.class.getName());

		long startTime = System.currentTimeMillis();

		List<URL> cacheDirJarURLs = new ArrayList<>();
		for (String cpEntry : System.getProperty("java.class.path").split(File.pathSeparator)) {
			File cpFile = new File(cpEntry);
			if (cpFile.canRead()) {
				File cachedDir = new File(cpFile.isDirectory() ? cpFile : cpFile.getParentFile(), "cache");
				if (cachedDir.canRead() && cachedDir.isDirectory()) {
					try {
						for (File cachedDirJar : cachedDir.listFiles()) {
							if (cachedDirJar.getName().toLowerCase().endsWith(".jar")) {
								cacheDirJarURLs.add(cachedDirJar.toURI().toURL());
							}
						}
					} catch (MalformedURLException e) {
						// Ignore
					}
				}
			}
		}
		ClassGraph classGraph = new ClassGraph();
		if (!cacheDirJarURLs.isEmpty()) {
			classGraph.addClassLoader(new URLClassLoader(cacheDirJarURLs.toArray(new URL[0])));
		}

		ClassInfoList events = classGraph.enableClassInfo().scan() // you should use try-catch-resources instead
				.getClassInfo(Event.class.getName()).getSubclasses().filter(info -> !info.isAbstract());

		Listener listener = new Listener() {
		};
		EventExecutor executor = (ignored, event) -> onEvent(event);

		try {
			for (ClassInfo event : events) {
				// noinspection unchecked
				@SuppressWarnings("unchecked")
				Class<? extends Event> eventClass = (Class<? extends Event>) event.loadClass(); // Class.forName(event.getName());

				if (Arrays.stream(eventClass.getDeclaredMethods()).anyMatch(
						method -> method.getParameterCount() == 0 && method.getName().equals("getHandlers"))) {
					// We could do this further filtering on the ClassInfoList instance instead,
					// but that would mean that we have to enable method info scanning.
					// I believe the overhead of initializing ~20 more classes
					// is better than that alternative.

					// register Event Class MiniGameManager ONLY can process
					// if (this.minigameManager.isPossibleEvent(eventClass)) {
					Bukkit.getPluginManager().registerEvent(eventClass, listener, EventPriority.NORMAL, executor,
							MiniGameMakerMain.getInstance());

					// }
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
//			throw new AssertionError("Scanned class wasn't found", e);
		}

		// String[] eventNames = events.stream()
		// .map(info -> info.getName().substring(info.getName().lastIndexOf('.') + 1))
		// .toArray(String[]::new);
		//
		// Bukkit.getLogger().info("List of events: " + String.join(", ", eventNames));
		Utils.info("Events found: " + events.size());
		Utils.info("HandlerList size: " + HandlerList.getHandlerLists().size());
		// Bukkit.getLogger().info("registered EventHandler: " + eventCount);

		long takenTime = System.currentTimeMillis() - startTime;
		System.out.println("Duration: " + takenTime / 1000 + " secs");
	}

	private Object onEvent(Event event) {
		/*
		 * 모든 이벤트 발생 캐치하는 메소드
		 */
		this.minigameManager.processEvent(event);
		return null;
	}

	Map<Player, ItemStack[]> invs = new HashMap<>();

//	@EventHandler
//	public void onChat(PlayerChatEvent e) {
//		Player p = e.getPlayer();
//		p.sendMessage("check slot number");
//		if (e.getMessage().equals("save")) {
//			p.sendMessage("saved");
//			this.invs.put(p, p.getInventory().getContents());
//		} else {
//			p.sendMessage("array size: " + invs.get(p).length);
//
//			for (int i = 0; i < this.invs.get(p).length; i++) {
//				if (this.invs.get(p)[i] != null) {
//					p.sendMessage("slot number: " + i);
//					p.sendMessage(this.invs.get(p)[i].displayName());
//				}
//			}
//			p.sendMessage("loaded");
//			p.getInventory().setContents(invs.get(p));
//		}
////		p.getInventory().setItem(0, new ItemStack(Material.SLIME_BALL));
////		p.getInventory().setItem(18, new ItemStack(Material.SLIME_BLOCK));
//	}
	
	// @EventHandler
	// public void onPlayerTouchMiniGameSign(PlayerInteractEvent e) {
	// /*
	// * OAK_SIGN으로 미니게임에 참여하는 이벤트
	// */
	//
	// Player p = e.getPlayer();
	//
	// // check minigameSign (setting.yml)
	// boolean minigameSign = (boolean)
	// this.minigameManager.getGameSetting().get("minigameSign");
	// if (!minigameSign) {
	// p.sendMessage("minigameSign option is false");
	// return;
	// }
	//
	// // process
	// Block block = e.getClickedBlock();
	// if (block != null) {
	// if (block.getType() == Material.OAK_SIGN || block.getType() ==
	// Material.OAK_WALL_SIGN) {
	// if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
	// Sign sign = (Sign) block.getState();
	// String minigame = sign.getLines()[0];
	// String title = sign.getLines()[1];
	// if (minigame.equalsIgnoreCase("[MiniGame]")) {
	// // setting.yml에서 signJoin값을 통해서 표지판 입장 유무 결정
	// this.minigameManager.joinGame(p, title);
	// } else if (minigame.equalsIgnoreCase("[Leave MiniGame]")) {
	// this.minigameManager.leaveGame(p);
	// }
	// }
	// }
	// }
	// }

	// @EventHandler
	// public void onPlayerExitServerWhenMiniGamePlaying(PlayerQuitEvent e) {
	// this.minigameManager.processException(e.getPlayer());
	// }
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
