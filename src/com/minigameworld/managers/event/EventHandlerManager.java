package com.minigameworld.managers.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import com.minigameworld.MiniGameWorldMain;
import com.minigameworld.minigameframes.MiniGame;
import com.minigameworld.minigameframes.helpers.MiniGameEventDetector;
import com.minigameworld.util.Utils;

public class EventHandlerManager {
	private Map<Class<? extends Event>, List<GameListener>> gameListeners;
	private MiniGameEventDetector eventDetector;

	public EventHandlerManager(MiniGameEventDetector eventDetector) {
		this.gameListeners = new HashMap<>();
		this.eventDetector = eventDetector;
	}

	public void registerGameListener(GameEventListener instance) {
		for (Class<? extends Event> event : getHandlerEvents(instance)) {
			if (!this.gameListeners.containsKey(event)) {
				gameListeners.put(event, new CopyOnWriteArrayList<>());
				registerEventListener(event);
			}
			addGameListener(event, instance);

			Utils.warning("=======registerGameListener=======");
			d(this.gameListeners.get(event));
		}

	}

	private void d(List<GameListener> gameListeners) {
		gameListeners.forEach(lis -> {
			Utils.warning("- lis: " + lis.getClass().getSimpleName());
			lis.handlers().forEach(h -> Utils.warning("- - handlers: " + h.getName()));
		});
	}

	public void unregisterGameListener(GameEventListener instance) {
		getHandlerEvents(instance).forEach(e -> removeGameListener(e, instance));
	}

	private void registerEventListener(Class<? extends Event> event) {
		Listener listener = new Listener() {
		};
		EventExecutor executor = (ignored, e) -> onEvent(e);

		Bukkit.getPluginManager().registerEvent(event, listener, EventPriority.HIGHEST, executor,
				MiniGameWorldMain.getInstance());
	}

	private void onEvent(Event event) {
		// check event detector
		Set<Player> players = this.eventDetector.detectPlayers(event);
		Set<GameListener> targetListeners = new HashSet<>();

		// if event is not related with any player, pass to all listeners
		if (players.isEmpty() && gameListeners.containsKey(event.getClass())) {
			targetListeners.addAll(this.gameListeners.get(event.getClass()));
		}
		// if event is related with some player, pass to playing listener only
		else {
			players.forEach(p -> targetListeners.addAll(getPlayingGameListeners(event, p)));
		}

		Utils.warning("=======onEvent=======");
		d(new ArrayList<>(targetListeners));

		// invoke handler method
		for (GameListener lis : targetListeners) {
			GameEventListener instance = lis.instance();

			// check instance is minigame then invoke if MiniGame.passEvent() return true
			MiniGame game = instance.minigame();
			GameEvent.State state = game.passEvent(event) ? GameEvent.State.PLAY : GameEvent.State.WAIT;
			lis.invoke(event, state);
		}

		// invoke forced handlers
		invokeForcedHandlers(targetListeners, event);
	}

	private void invokeForcedHandlers(Set<GameListener> invokedListeners, Event event) {
		List<GameListener> forcedListeners = new ArrayList<>();
		this.gameListeners.get(event.getClass()).stream().filter(l -> !l.forcedHandlers().isEmpty())
				.forEach(l -> forcedListeners.add(l));

		// remove duplication
		forcedListeners.removeAll(invokedListeners);

		// invoke forced handlers for each listeners
		forcedListeners.forEach(lis -> lis.invoke(event, null));
	}

	private List<GameListener> getPlayingGameListeners(Event event, Player p) {
		List<GameListener> listeners = this.gameListeners.get(event.getClass()).stream()
				.filter(l -> l.instance().minigame().containsPlayer(p)).toList();
		return listeners;
	}

	@SuppressWarnings("unchecked")
	private List<Class<? extends Event>> getHandlerEvents(GameEventListener instance) {
		Set<Class<? extends Event>> events = new HashSet<>();
		GameListener.gameEventHandlers(instance)
				.forEach(m -> events.add((Class<? extends Event>) m.getParameterTypes()[0]));
		return new ArrayList<>(events);
	}

	private void addGameListener(Class<? extends Event> event, GameEventListener instance) {
		GameListener listener = new GameListener(event, instance);
		this.gameListeners.get(event).add(listener);
	}

	private void removeGameListener(Class<? extends Event> event, GameEventListener instance) {
		List<GameListener> listeners = this.gameListeners.get(event);
		if (instance instanceof MiniGame) {
			MiniGame game = ((MiniGame) instance);
			listeners.stream().filter(l -> game.equals(l.instance().minigame())).forEach(listeners::remove);
		} else {
			listeners.stream().filter(l -> l.instance().equals(instance)).forEach(listeners::remove);
		}
	}
}
