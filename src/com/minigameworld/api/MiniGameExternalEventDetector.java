package com.minigameworld.api;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Custom event external detector
 *
 * @see MiniGameWorld#registerExternalEventDetector(MiniGameEventExternalDetector)
 * @see MiniGameWorld#unregisterExternalEventDetector(MiniGameEventExternalDetector)
 */
public interface MiniGameExternalEventDetector {
	/**
	 * Returns players related with event<br>
	 * [IMPORTANT] Event will be passed to minigame if player list is not empty<br>
	 * 
	 * @param e Event created in the server
	 * @return Player list related with event
	 */
	public Set<Player> getPlayersFromEvent(Event e);
}
