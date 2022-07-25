package com.minigameworld.customevents.menu;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

public class MenuEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Inventory menu;

	public MenuEvent(Inventory menu) {
		this.menu = menu;
	}

	public Inventory getMenu() {
		return this.menu;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
