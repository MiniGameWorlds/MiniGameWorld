package com.minigameworld.managers.event;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.event.Event;

import com.minigameworld.frames.MiniGame;
import com.minigameworld.managers.event.GameEvent.State;
import com.minigameworld.util.Setting;
import com.wbm.plugin.util.Utils;

public class GameListener {
	private Class<? extends Event> event;
	private GameEventListener listener;
	private Set<Method> handlers;

	/*
	 * Prevent when a event calls the same GameListeners twice at once  
	 */
	private Event latestUsedEventOfNonForced; // for non-forced handlers
	private Event latestUsedEventOfForced; // for forced handlers

	public GameListener(Class<? extends Event> event, GameEventListener lis) {
		this.event = event;
		this.listener = lis;
		this.handlers = new HashSet<>();
		this.latestUsedEventOfNonForced = null;
		addHandlers(this.listener.getClass());
	}

	/*
	 * Must use Class<> to make reflection work (Object type doensn't work)
	 * 
	 * [IMPORTANT]
	 * Register only protected or public method that has 
	 * GameEvent annotation traveling all super classes recursively
	 * 
	 */
	private void addHandlers(Class<?> lis) {
		if (lis == Object.class) {
			return;
		}

		addHandlers(lis.getSuperclass());

		gameEventHandlers(lis).forEach(m -> {
			m.setAccessible(true);
			Class<?> paramType = m.getParameterTypes()[0];

			if (paramType.equals(event)) {
				handlers.add(m);
			}
		});
	}

	private boolean isLatestUsedEvent(Event event, boolean onlyForcedHandlers) {
		Event targetLastestUsedEvent = onlyForcedHandlers ? this.latestUsedEventOfForced
				: this.latestUsedEventOfNonForced;

		if (onlyForcedHandlers) {
			this.latestUsedEventOfForced = event;
		} else {
			this.latestUsedEventOfNonForced = event;
		}

		if (targetLastestUsedEvent == null) {
			return false;
		}

		boolean isUsed = targetLastestUsedEvent.equals(event);
		return isUsed;
	}

	public void invoke(Event e, boolean onlyForcedHandlers) {
		if (isLatestUsedEvent(e, onlyForcedHandlers)) {
			return;
		}

		Set<Method> targetHandlers = new HashSet<>();
		if (onlyForcedHandlers) {
			targetHandlers.addAll(this.handlers.stream().filter(h -> h.getAnnotation(GameEvent.class).forced())
					.collect(Collectors.toSet()));
		} else {
			targetHandlers.addAll(this.handlers.stream().filter(h -> !h.getAnnotation(GameEvent.class).forced())
					.collect(Collectors.toSet()));
		}

		for (Method h : targetHandlers) {
			try {
				// check state
				MiniGame game = this.listener.minigame();
				GameEvent.State state = game.isStarted() ? GameEvent.State.PLAY : GameEvent.State.WAIT;

				GameEvent.State handlerState = h.getAnnotation(GameEvent.class).state();
				if (handlerState == State.ALL) {
					h.invoke(listener, e);
					continue;
				}

				if (handlerState != state) {
					continue;
				}

				h.invoke(listener, e);
			} catch (Exception exception) {
				Utils.debug(exception.getCause().toString());
				if (Setting.DEBUG_MODE) {
					exception.printStackTrace();
				}
			}
		}
	}

	public static Set<Method> gameEventHandlers(Class<?> lis) {
		Set<Method> methods = new HashSet<>();
		if (lis != Object.class) {
			methods.addAll(gameEventHandlers(lis.getSuperclass()));
		}

		/*
		 * [IMPORTANT] Handler method modifier of GameEventListener must be "public" or "protected" to be invoked  
		 */
		Arrays.asList(lis.getDeclaredMethods()).stream()
				.filter(m -> m.isAnnotationPresent(GameEvent.class) && m.getParameterCount() > 0)
				.filter(m -> (m.getModifiers() == Modifier.PUBLIC) || (m.getModifiers() == Modifier.PROTECTED))
				.filter(m -> !methods.contains(m)).forEach(methods::add);

		return methods;
	}

	public Class<? extends Event> event() {
		return event;
	}

	public GameEventListener listener() {
		return listener;
	}

	public Set<Method> handlers() {
		return handlers;
	}

	public Set<Method> forcedHandlers() {
		return handlers.stream().filter(h -> h.getAnnotation(GameEvent.class).forced()).collect(Collectors.toSet());
	}

	@Override
	public String toString() {
		return "\nEvent: " + this.event.getName() + ", handlers: " + this.handlers.toString();
	}

}
