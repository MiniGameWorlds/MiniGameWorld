package com.minigameworld.managers.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import com.minigameworld.MiniGameWorldMain;
import com.minigameworld.frames.MiniGame;
import com.minigameworld.frames.helpers.MiniGameEventDetector;
import com.minigameworld.managers.MiniGameManager;
import com.wbm.plugin.util.Utils;

/**
 * <b>[Rules]</b><br>
 * - Must register instance which processes playing game player's only (not
 * about viewers and outers)<br>
 * - If event is detectable with players by {@link MiniGameEventDetector}, the
 * event will be only passed to the player's playing game listeners.<br>
 * - If event is not detectable with players by {@link MiniGameEventDetector},
 * the event will be passed to all listeners related with event.<br>
 * In this {@link GameListenerManager} class, use {@link #getListeners(Class)}
 * to get listeners with event<br>
 * <br>
 *
 * <b>[Listener registration tutorial]</b><br>
 * If listener related with minigame needs to handle events<br>
 * 1. Implements {@link GameEventListener}<br>
 * 2. Add method as a protected or public with {@link GameEvent} annotation<br>
 * 3. Register in {@link MiniGameManager#createGameInstance()} and unregister in
 * {@link MiniGameManager#removeGameInstance(MiniGame)}<br>
 */
public class GameListenerManager implements Listener {
	private Map<Class<? extends Event>, Set<GameListener>> gameListeners;
	private MiniGameEventDetector eventDetector;
	private EventExecutor executor;

	public GameListenerManager(MiniGameEventDetector eventDetector) {
		this.gameListeners = new HashMap<>();
		this.eventDetector = eventDetector;
		this.executor = (listener, e) -> onEvent(e);
	}

	public void printAllListener() {
		this.gameListeners.forEach((e, l) -> {
			Utils.debug(e.getName() + ": " + l);
		});
	}

	public void registerGameListener(GameEventListener instance) {
		for (Class<? extends Event> event : getHandlerEvents(instance)) {
			if (!this.gameListeners.containsKey(event)) {
				gameListeners.put(event, new CopyOnWriteArraySet<>());
				registerEventListener(event);
			}
			addGameListener(event, instance);
		}
	}

	public void unregisterGameListener(GameEventListener instance) {
		getHandlerEvents(instance).forEach(e -> removeGameListener(e, instance));
	}

	private void registerEventListener(Class<? extends Event> event) {
		Bukkit.getPluginManager().registerEvent(event, this, EventPriority.HIGHEST, this.executor,
				MiniGameWorldMain.getInstance());
	}

	@EventHandler
	private void onEvent(Event event) {
		// check event detector
		Set<Player> players = this.eventDetector.detectPlayers(event);
		Set<GameListener> targetListeners = new HashSet<>();

		// 1. invoke forced handlers of all listener
		getListeners(event.getClass()).stream().forEach(l -> l.invoke(event, true));

		// if event is related with some player, pass to playing listener only
		players.forEach(p -> targetListeners.addAll(getPlayingGameListeners(event, p)));
		// 2. invoke not forced handlers of player-related listeners
		targetListeners.forEach(l -> l.invoke(event, false));
	}

	private Set<GameListener> getPlayingGameListeners(Event event, Player p) {
		Set<GameListener> listeners = getListeners(event.getClass()).stream()
				.filter(l -> l.listener().minigame().containsPlayer(p)).collect(Collectors.toSet());
		return listeners;
	}

	@SuppressWarnings("unchecked")
	private Set<Class<? extends Event>> getHandlerEvents(GameEventListener instance) {
		Set<Class<? extends Event>> events = new HashSet<>();
		GameListener.gameEventHandlers(instance.getClass())
				.forEach(m -> events.add((Class<? extends Event>) m.getParameterTypes()[0]));
		return events;
	}

	private void addGameListener(Class<? extends Event> event, GameEventListener instance) {
		GameListener listener = new GameListener(event, instance);
		this.gameListeners.get(event).add(listener);
	}

	private void removeGameListener(Class<? extends Event> event, GameEventListener instance) {
		Set<GameListener> listeners = this.gameListeners.get(event);
		listeners.stream().filter(l -> l.listener().equals(instance)).forEach(listeners::remove);
	}

	/**
	 * Must use this method to get {@code List<GameListener>}.<br>
	 * Because derived event class can be invoked but can't find registered event
	 * so, will search super classes in this method
	 * 
	 * @param event
	 * @return
	 */
	private Set<GameListener> getListeners(Class<? extends Event> event) {
		Set<GameListener> lis = new CopyOnWriteArraySet<>();
		for (Class<? extends Event> key : this.gameListeners.keySet()) {
			if (key.isAssignableFrom(event)) {
				lis.addAll(this.gameListeners.get(key));
			}
		}
		return lis;
	}
}
