package com.minigameworld.managers.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GameEvent {
	/**
	 * No check player is related or not, so just event will be processed<br>
	 * [IMPORTANT] If true, {@link GameEvent#state()} is ignored
	 * 
	 * @return
	 */
	public boolean forced() default false;

	public enum State {
		/**
		 * On waiting game
		 */
		WAIT,
		/**
		 * On playing game
		 */
		PLAY,
		/**
		 * WAIT and PLAY
		 */
		ALL;
	}

	public State state() default State.PLAY;

//	public EventPriority priority() default EventPriority.NORMAL;
}
