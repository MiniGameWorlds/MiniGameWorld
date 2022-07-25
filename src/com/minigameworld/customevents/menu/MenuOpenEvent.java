package com.minigameworld.customevents.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.Inventory;

/**
 * Called when a player opens menu<br>
 */
public class MenuOpenEvent extends MenuEvent implements Cancellable {
	private Player player;
	private boolean isCancelled;

	public MenuOpenEvent(Inventory menu, Player player) {
		super(menu);
		this.player = player;
	}

	public Player getPlayer() {
		return this.player;
	}

	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

}
