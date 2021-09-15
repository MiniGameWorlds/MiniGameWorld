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

import net.kyori.adventure.text.Component;

public class MiniGameGUIManager {
	private MiniGameManager minigameManager;
	private Map<Player, MiniGameGUI> guis;

	public MiniGameGUIManager(MiniGameManager minigameManager) {
		this.minigameManager = minigameManager;
		this.guis = new HashMap<>();

		this.startUpdatingGUITask();
	}

	private void startUpdatingGUITask() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player p : guis.keySet()) {
					MiniGameGUI gui = guis.get(p);
					// update minigame list state
					gui.updateGUI();
					p.updateInventory();
				}
			}
		}.runTaskTimer(MiniGameWorldMain.getInstance(), 0, 4);
	}

	public void openGUI(Player p) {
		this.guis.put(p, new MiniGameGUI(this.minigameManager, this.minigameManager.getMiniGameList()));

		// open 1 page gui
		Inventory inv = this.guis.get(p).createGUI(1);
		p.openInventory(inv);
	}

	public void processInventoryEvent(InventoryEvent event) {
		if (event instanceof InventoryClickEvent) {
			InventoryClickEvent e = (InventoryClickEvent) event;
			if (this.isMiniGameWorldGUI(e.getView().title())) {
				e.setCancelled(true);

				// process inventory event
				if (e.getClickedInventory() != null) {
					this.getClickedGUI(e.getClickedInventory()).processClickEvent(e);
				}
			}
		} else if (event instanceof InventoryClickEvent) {
			InventoryCloseEvent e = (InventoryCloseEvent) event;
			if (this.isMiniGameWorldGUI(e.getView().title())) {
				this.guis.remove(e.getPlayer());
			}
		}
	}

	private MiniGameGUI getClickedGUI(Inventory inv) {
		for (MiniGameGUI gui : this.guis.values()) {
			if (gui.isSameInventory(inv)) {
				return gui;
			}
		}
		return null;
	}

	private boolean isMiniGameWorldGUI(Component component) {
		return Component.text("MiniGameWorld").equals(component);
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
