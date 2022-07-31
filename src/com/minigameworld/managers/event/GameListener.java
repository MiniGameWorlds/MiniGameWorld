package com.minigameworld.managers.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.event.Event;

import com.minigameworld.managers.event.GameEvent.State;

public class GameListener {
	private Class<? extends Event> event;
	private GameEventListener instance;
	private List<Method> handlers;

	public GameListener(Class<? extends Event> event, GameEventListener instance) {
		this.event = event;
		this.instance = instance;
		this.handlers = new ArrayList<>();
		addHandlers();
	}

	private void addHandlers() {
		gameEventHandlers(this.instance).forEach(m -> {
			m.setAccessible(true);
			Class<?> paramType = m.getParameterTypes()[0];

			if (paramType.equals(event)) {
				handlers.add(m);
			}
		});
	}

	public void invoke(Event e) {
		invoke(e, GameEvent.State.PLAY);
	}

	public void invoke(Event e, GameEvent.State state) {
		for (Method h : this.handlers) {
			try {
				// check forced
				boolean forced = h.getAnnotation(GameEvent.class).forced();
				if (forced) {
					h.invoke(instance, e);
					continue;
				}

				// check state
				GameEvent.State handlerState = h.getAnnotation(GameEvent.class).state();
				if (handlerState != State.ALL && handlerState != state) {
					continue;
				}

				h.invoke(instance, e);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public static List<Method> gameEventHandlers(GameEventListener instance) {
		List<Method> methods = Arrays.asList(instance.getClass().getDeclaredMethods());
		return methods.stream().filter(m -> m.isAnnotationPresent(GameEvent.class) && m.getParameterCount() > 0)
				.toList();
	}

	public Class<? extends Event> event() {
		return event;
	}

	public GameEventListener instance() {
		return instance;
	}

	public List<Method> handlers() {
		return handlers;
	}

	public List<Method> forcedHandlers() {
		return handlers.stream().filter(h -> h.getAnnotation(GameEvent.class).forced()).toList();
	}

}
