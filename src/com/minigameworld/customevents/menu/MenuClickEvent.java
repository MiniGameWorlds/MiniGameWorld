package com.minigameworld.customevents.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Called when menu is clicked
 */
public class MenuClickEvent extends MenuEvent implements Cancellable {
	private InventoryClickEvent event;
	private String area;
	private boolean isCancelled;

	public MenuClickEvent(InventoryClickEvent event, String area) {
		super(event.getInventory());
		this.event = event;
		this.area = area;
	}

	public InventoryClickEvent getInventoryClickEvent() {
		return this.event;
	}

	public Player getPlayer() {
		return (Player) this.event.getWhoClicked();
	}

	public ClickType getClick() {
		return this.event.getClick();
	}

	public int getSlot() {
		return this.event.getSlot();
	}

	public ItemStack getIcon() {
		return this.event.getCurrentItem();
	}
	
	public String getArea() {
		return this.area;
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
