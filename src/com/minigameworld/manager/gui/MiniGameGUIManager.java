package com.minigameworld.manager.gui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.minigameworld.MiniGameWorldMain;
import com.minigameworld.manager.MiniGameManager;
import com.minigameworld.util.Setting;

import net.kyori.adventure.text.Component;

public class MiniGameGUIManager {
	/*
	 * Manage MiniGameGUI List, because player's personal data will show in MiniGameGUI
	 */
	private MiniGameManager minigameManager;
	private Map<Player, MiniGameGUI> guiList;

	public MiniGameGUIManager(MiniGameManager minigameManager) {
		this.minigameManager = minigameManager;
		this.guiList = new HashMap<>();

		this.startUpdatingGUITask();
	}

	private void startUpdatingGUITask() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p : guiList.keySet()) {
					MiniGameGUI gui = guiList.get(p);
					// update minigame list state
					gui.updateGUI();
					p.updateInventory();
				}
			}
		}.runTaskTimer(MiniGameWorldMain.getInstance(), 0, 4);
	}

	public Inventory openGUI(Player p) {
		this.guiList.put(p, new MiniGameGUI(p, this.minigameManager));

		// open 1 page gui
		Inventory inv = this.guiList.get(p).createGUI(1);
		p.openInventory(inv);
		return inv;
	}

	public void processInventoryEvent(InventoryEvent event) {
		// manage Inventory Events
		if (event instanceof InventoryClickEvent) {
			InventoryClickEvent e = (InventoryClickEvent) event;
			
			// check title
			if (this.isMiniGameWorldGUI(e.getView().title())) {
				e.setCancelled(true);

				// process inventory event
				if (e.getClickedInventory() != null) {
					this.getClickedGUI(e.getClickedInventory()).processClickEvent(e);
				}
			}
		} else if (event instanceof InventoryCloseEvent) {
			InventoryCloseEvent e = (InventoryCloseEvent) event;
			if (this.isMiniGameWorldGUI(e.getView().title())) {
				this.guiList.remove(e.getPlayer());
			}
		}
	}

	private MiniGameGUI getClickedGUI(Inventory inv) {
		for (MiniGameGUI gui : this.guiList.values()) {
			if (gui.isSameInventory(inv)) {
				return gui;
			}
		}
		return null;
	}

	private boolean isMiniGameWorldGUI(Component component) {
		return Component.text(Setting.GUI_INV_TITLE).equals(component);
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
