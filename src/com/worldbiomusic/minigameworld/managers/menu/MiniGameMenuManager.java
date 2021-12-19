package com.worldbiomusic.minigameworld.managers.menu;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.worldbiomusic.minigameworld.MiniGameWorldMain;
import com.worldbiomusic.minigameworld.managers.MiniGameManager;
import com.worldbiomusic.minigameworld.util.Setting;
import com.worldbiomusic.minigameworld.util.Utils;

public class MiniGameMenuManager {
	/*
	 * Manage MiniGameMenu List, because player's personal data will show in MiniGameMenu
	 */
	private MiniGameManager minigameManager;
	private Map<Player, MiniGameMenu> menuList;

	public MiniGameMenuManager(MiniGameManager minigameManager) {
		this.minigameManager = minigameManager;
		this.menuList = new HashMap<>();

		this.startUpdatingMenuTask();
	}

	private void startUpdatingMenuTask() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p : menuList.keySet()) {
					MiniGameMenu menu = menuList.get(p);
					// update minigame list state
					menu.updateMenu();
					p.updateInventory();
				}
			}
		}.runTaskTimer(MiniGameWorldMain.getInstance(), 0, 4);
	}

	/**
	 * Open menu inventory to player
	 * 
	 * @param p Player who will open menu inventory
	 * @return Inventory, but return null if player doesn't have
	 *         "minigameworld.menu" permission
	 */
	public Inventory openMenu(Player p) {
		// check permission
		if (!Utils.checkPerm(p, "menu")) {
			return null;
		}

		this.menuList.put(p, new MiniGameMenu(p, this.minigameManager));

		// open 1 page menu
		Inventory inv = this.menuList.get(p).createMenu(1);
		p.openInventory(inv);
		return inv;
	}

	public void processInventoryEvent(InventoryEvent event) {
		// manage Inventory Events
		if (event instanceof InventoryClickEvent) {
			InventoryClickEvent e = (InventoryClickEvent) event;

			// check title
			if (this.isMiniGameWorldMenu(e.getView().getTitle())) {
				e.setCancelled(true);

				// process inventory event
				if (e.getClickedInventory() != null) {
					this.getClickedMenu(e.getClickedInventory()).processClickEvent(e);
				}
			}
		} else if (event instanceof InventoryCloseEvent) {
			InventoryCloseEvent e = (InventoryCloseEvent) event;
			if (this.isMiniGameWorldMenu(e.getView().getTitle())) {
				this.menuList.remove(e.getPlayer());
			}
		}
	}

	private MiniGameMenu getClickedMenu(Inventory inv) {
		for (MiniGameMenu menu : this.menuList.values()) {
			if (menu.isSameInventory(inv)) {
				return menu;
			}
		}
		return null;
	}

	private boolean isMiniGameWorldMenu(String title) {
		return Setting.MENU_INV_TITLE.equals(title);
	}
}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
