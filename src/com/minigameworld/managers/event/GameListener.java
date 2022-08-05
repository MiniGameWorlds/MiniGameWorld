package com.minigameworld.managers.event;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.event.Event;

import com.minigameworld.managers.event.GameEvent.State;
import com.wbm.plugin.util.Utils;

public class GameListener {
	private Class<? extends Event> event;
	private GameEventListener listener;
	private List<Method> handlers;

	public GameListener(Class<? extends Event> event, GameEventListener lis) {
		this.event = event;
		this.listener = lis;
		this.handlers = new ArrayList<>();
		addHandlers(this.listener.getClass());

//		if (listener instanceof MiniGame) {
//			printSuperClass(listener.getClass());
//		}
	}

	static void printClassMethods(Class<?> c) {
		Arrays.asList(c.getDeclaredMethods()).forEach(m -> Utils.debug(m.getName()));
	}

	static void printSuperClass(Class<?> obj) {
		try {
			/*
			 *Class<? extends Object> 변수에 담지 말고 이어서 해보기 
			 */
			Class<?> superClass = obj.getSuperclass();
			Utils.debug("\n\n===========Class: " + obj);
			printClassMethods(obj);
			Utils.debug("- super class: " + superClass + " of " + obj);
			if (superClass != null) {
				printSuperClass(superClass);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void addHandlers(Class<?> lis) {
		/*
		 * GameEventListener의 @GameEvent annotation이 있는 메서드를 
		 * 등록할 때 (EventHandlerManager.register..()), 
		 * 상위 클래스를 여행하면서 getDeclaredMethods()로 
		 * private method들까지 모두 handler로써 추가하기 
		 * (MiniGame 관련 클래스들은 대부분 상속을 하는데, 
		 * getDeclaredMethods()는 현재 클래스의 메서드들만 참조가 가능하므로)
		 * 
		 * 매개변수를 무조건 Class<?> type으로 받아서 reflection이 정상 작동됨
		 */

		if (lis == Object.class) {
			return;
		}

		addHandlers(lis.getSuperclass());

//		Utils.debug("==========addHanlders(): " + lis.getSimpleName() + "=========\n@GameEvent");
//		gameEventHandlers(lis).forEach(m -> Utils.debug("- " + m.getName()));
//		Utils.debug("\nAll method");
//		Arrays.asList(lis.getDeclaredMethods()).forEach(m -> Utils.debug("- " + m.getName()));
//		Utils.debug("\n");

		gameEventHandlers(lis).forEach(m -> {
			m.setAccessible(true);
			Class<?> paramType = m.getParameterTypes()[0];

			if (paramType.equals(event)) {
				handlers.add(m);
			}
		});
	}

	public void invoke(Event e, GameEvent.State state) {
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

				Utils.debug("invoke(), handlerState: " + handlerState + ", method state: " + state);
				if (handlerState == State.WAIT) {
					Utils.debug("invoke: " + (handlerState != State.ALL && handlerState != state));
				}

				if (handlerState != State.ALL && handlerState != state) {
					continue;
				}

				h.invoke(listener, e);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public static List<Method> gameEventHandlers(Class<?> lis) {
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
				.forEach(methods::add);

		return new ArrayList<>(methods);
	}

	public Class<? extends Event> event() {
		return event;
	}

	public GameEventListener listener() {
		return listener;
	}

	public List<Method> handlers() {
		return handlers;
	}

	public List<Method> forcedHandlers() {
		return handlers.stream().filter(h -> h.getAnnotation(GameEvent.class).forced()).toList();
	}

}
