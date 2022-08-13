package com.minigameworld.managers.event;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.event.Event;

import com.minigameworld.managers.event.GameEvent.State;

public class GameListener {
	private Class<? extends Event> event;
	private GameEventListener listener;
	private Set<Method> handlers;

	/*
	 * Prevent when a event calls the same GameListeners twice at once  
	 */
	private Event latestUsedEvent;

	public GameListener(Class<? extends Event> event, GameEventListener lis) {
		this.event = event;
		this.listener = lis;
		this.handlers = new HashSet<>();
		this.latestUsedEvent = null;
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

	private boolean isLatestUsedEvent(Event event) {
		if (this.latestUsedEvent == null) {
			this.latestUsedEvent = event;
			return false;
		}

		boolean isUsed = this.latestUsedEvent.equals(event);
		this.latestUsedEvent = event;
		return isUsed;
	}

	public void invoke(Event e, GameEvent.State state) {
		if (isLatestUsedEvent(e)) {
			return;
		}

		for (Method h : this.handlers) {
			try {
				// check forced
				boolean forced = h.getAnnotation(GameEvent.class).forced();
				if (forced) {
					h.invoke(listener, e);
					continue;
				}

				// check state
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
				exception.printStackTrace();
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
