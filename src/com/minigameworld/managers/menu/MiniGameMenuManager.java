package com.minigameworld.managers.menu;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.minigameworld.MiniGameWorldMain;
import com.minigameworld.customevents.menu.MenuCloseEvent;
import com.minigameworld.customevents.menu.MenuOpenEvent;
import com.minigameworld.managers.MiniGameManager;
import com.minigameworld.util.Setting;
import com.minigameworld.util.Utils;

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
	 *         "minigameworld.menu" permission or {@link MenuOpenEvent} is cancelled
	 */
	public Inventory openMenu(Player p) {
		// check permission
		if (!Utils.checkPerm(p, "menu")) {
			return null;
		}

		MiniGameMenu menu = new MiniGameMenu(p, this.minigameManager);
		Inventory inv = menu.createMenu(1);

		// call MenuOpenEvent (check event is cancelled)
		if (Utils.callEvent(new MenuOpenEvent(inv, p))) {
			return null;
		}

		this.menuList.put(p, menu);

		// open 1 page menu
		p.openInventory(inv);
		return inv;
	}

	public void onInventoryEvent(InventoryEvent event) {
		// when menu clicked
		if (event instanceof InventoryClickEvent) {
			InventoryClickEvent e = (InventoryClickEvent) event;

			// check title
			if (this.isMiniGameWorldMenu(e.getView().getTitle())) {
				// cancel click event
				e.setCancelled(true);

				// process inventory event
				if (e.getClickedInventory() != null) {
					MiniGameMenu menu = getClickedMenu(e.getClickedInventory());
					if (menu != null) {
						menu.processClickEvent(e);
					}
				}
			}
		}

		// when menu closed
		else if (event instanceof InventoryCloseEvent) {
			InventoryCloseEvent e = (InventoryCloseEvent) event;
			if (this.isMiniGameWorldMenu(e.getView().getTitle())) {
				Player p = (Player) e.getPlayer();
				// call MenuCloseEvent (check event is cancelled)
				if (Utils.callEvent(new MenuCloseEvent(e.getInventory(), p))) {
					return;
				}

				this.menuList.remove(p);
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
