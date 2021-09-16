package com.minigameworld.listeners;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import com.minigameworld.MiniGameWorldMain;
import com.minigameworld.manager.MiniGameManager;
import com.minigameworld.util.Utils;

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
							MiniGameWorldMain.getInstance());

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
