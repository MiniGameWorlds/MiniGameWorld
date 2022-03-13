package com.worldbiomusic.minigameworld.managers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import com.worldbiomusic.minigameworld.MiniGameWorldMain;
import com.worldbiomusic.minigameworld.listeners.CommonEventListener;
import com.worldbiomusic.minigameworld.util.Setting;
import com.worldbiomusic.minigameworld.util.Utils;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;

public class EventListenerManager {
	private CommonEventListener commonListener;

	public EventListenerManager(CommonEventListener commonListener) {
		this.commonListener = commonListener;

		this.registerAllEventListener();
//		this.registerAllEventListener_Burningwave();
	}

	/**
	 * Register all events<br>
	 * - Spigot doesn't need other setup<br>
	 * - Paper needs additional setup like below<br>
	 */
	private void registerAllEventListener() {
		// set cache directory can be found
		List<URL> cacheDirJarURLs = new ArrayList<>();

		boolean isPapermc = false;

		try {
			isPapermc = Class.forName("com.destroystokyo.paper.utils.PaperPluginLogger") != null;
		} catch (ClassNotFoundException exception) {
			// ignore for not paper bukkit
		}

		String[] versionSrc = Bukkit.getBukkitVersion().split("\\.");
		int version = Integer.parseInt(versionSrc[1]);

		// Can use upper than 1.12
		if (version < 12) {
			Utils.warning("MiniGameWorld must be used upper than 1.12 version of bukkit");
			return;
		} else if (isPapermc && version < 18) {
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
		}

		ClassGraph classGraph = new ClassGraph();
		if (!cacheDirJarURLs.isEmpty()) {
			classGraph.addClassLoader(new URLClassLoader(cacheDirJarURLs.toArray(new URL[0])));
		}

		// find classes
		ClassInfoList events = classGraph.enableClassInfo().scan() // you should use try-catch-resources instead
				.getClassInfo(Event.class.getName()).getSubclasses().filter(info -> !info.isAbstract());

		Listener listener = new Listener() {
		};
		EventExecutor executor = (ignored, event) -> this.commonListener.onEvent(event);

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

					Bukkit.getPluginManager().registerEvent(eventClass, listener, EventPriority.HIGHEST, executor,
							MiniGameWorldMain.getInstance());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
//			throw new AssertionError("Scanned class wasn't found", e);
		}

		if (Setting.DEBUG_MODE) {
			Utils.info("                Event Detector               ");
			Utils.info(" - Events found: " + events.size());
			Utils.info(ChatColor.GREEN + "=============================================");
		}
	}

	// private void registerAllEventListener_Burningwave() {
	// Collection<Class<?>> events = findEvents();
	// Listener listener = new Listener() {};
	// EventExecutor executor = (ignored, event) -> onEvent(event);
	// try {
	// for (Class<?> eventClass : events) {
	// Bukkit.getPluginManager().registerEvent((Class<? extends Event>)eventClass,
	// listener, EventPriority.HIGHEST, executor,
	// MiniGameWorldMain.getInstance());
	// System.out.println("class " + eventClass + " registered");
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// public Collection<Class<?>> findEvents() {
	// ComponentSupplier componentSupplier = ComponentContainer.getInstance();
	// ClassHunter classHunter = componentSupplier.getClassHunter();
	//
	//
	// SearchConfig searchConfig = SearchConfig.byCriteria(
	// ClassCriteria.create().allThoseThatMatch(currentScannedClass ->
	// !Modifier.isAbstract(currentScannedClass.getModifiers()) &&
	// Event.class.isAssignableFrom(currentScannedClass)
	// ).and().byMembers(
	// MethodCriteria.forEntireClassHierarchy().allThoseThatMatch((method) ->
	// method.getParameterCount() == 0 && method.getName().equals("getHandlers")
	// )
	// )
	// );
	// try(org.burningwave.core.classes.ClassHunter.SearchResult searchResult =
	// classHunter.loadInCache(searchConfig).find()) {
	// return searchResult.getClasses();
	// }
	// }
}
