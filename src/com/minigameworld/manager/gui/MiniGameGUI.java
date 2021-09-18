package com.minigameworld.manager.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.minigameworld.manager.MiniGameManager;
import com.minigameworld.minigameframes.MiniGame;
import com.minigameworld.util.Setting;
import com.wbm.plugin.util.ItemStackTool;
import com.wbm.plugin.util.PlayerTool;

import net.kyori.adventure.text.Component;

public class MiniGameGUI {
	/*
	 * GUI Inventory
	 */
	private Player player;
	private MiniGameManager minigameManager;
	private Inventory inv;
	private List<MiniGame> minigames;
	private int currentPage;

	private final int minigameIconListSize = 27;

	private enum BaseIcon {
		LEAVE_GAME(ItemStackTool.item(Material.WHITE_BED, "Leave Game"), 1),
		HORIZON_LINE(ItemStackTool.item(Material.BARRIER, " "), 9),
		PREVIOUS_PAGE(ItemStackTool.item(Material.REDSTONE_TORCH, "<"), 48),
		CURRENT_PAGE(ItemStackTool.item(Material.PAPER, "1"), 49),
		NEXT_PAGE(ItemStackTool.item(Material.TORCH, ">"), 50);

		private ItemStack item;
		private int slot;

		BaseIcon(ItemStack item, int slot) {
			this.item = item;
			this.slot = slot;
		}

		public ItemStack getItem() {
			return this.item;
		}

		public int getSlot() {
			return this.slot;
		}
	}

	public MiniGameGUI(Player player, MiniGameManager minigameManager, List<MiniGame> minigames) {
		this.player = player;
		this.minigameManager = minigameManager;
		this.minigames = minigames;
		this.currentPage = 1;
		this.makeBaseIcons();
	}

	private void makeBaseIcons() {
		this.inv = Bukkit.createInventory(null, 9 * 6, Component.text(Setting.GUI_INV_TITLE));

		// player head
		ItemStack playerHead = this.getPlayerHead(this.player);
		this.inv.setItem(0, playerHead);

		// leave game button
		this.inv.setItem(BaseIcon.LEAVE_GAME.getSlot(), BaseIcon.LEAVE_GAME.getItem());

		// horizon line
		for (int i = 0; i < 9; i++) {
			this.inv.setItem(BaseIcon.HORIZON_LINE.getSlot(), BaseIcon.HORIZON_LINE.getItem());
		}

		// page buttons
		this.inv.setItem(BaseIcon.PREVIOUS_PAGE.getSlot(), BaseIcon.PREVIOUS_PAGE.getItem());
		this.inv.setItem(BaseIcon.CURRENT_PAGE.getSlot(), BaseIcon.CURRENT_PAGE.getItem());
		this.inv.setItem(BaseIcon.NEXT_PAGE.getSlot(), BaseIcon.NEXT_PAGE.getItem());
	}

	private ItemStack getPlayerHead(Player p) {
		ItemStack item = PlayerTool.getPlayerHead(this.player);
		ItemMeta meta = item.getItemMeta();
		meta.displayName(Component.text(ChatColor.BOLD + "INFO"));
		item.setItemMeta(meta);
		return item;
	}

	private void updatePlayerHead() {
		ItemStack playerHead = this.inv.getItem(0);

		MiniGame playingMinigame = this.minigameManager.getPlayingMiniGame(this.player);
		String title = (playingMinigame == null) ? "None" : playingMinigame.getTitle();

		ItemMeta meta = playerHead.getItemMeta();
		List<Component> lore = new ArrayList<>();
		lore.add(Component.text(ChatColor.WHITE + "- Minigame: " + title));
		meta.lore(lore);
		playerHead.setItemMeta(meta);
	}

	private void emptyMiniGameIconList() {
		for (int i = 18; i < 18 + minigameIconListSize; i++) {
			this.inv.setItem(i, null);
		}
	}

	public Inventory createGUI(int page) {
		// set player head content
		this.updatePlayerHead();

		// update page
		this.updateCurrentPageNumber();

		// first, empty list
		this.emptyMiniGameIconList();

		// slot: 18 ~ 44 (count: 27)
		int minigameIndex = 0 + ((page - 1) * minigameIconListSize);
//		for (int i = 18; i < 45; i++, minigameIndex++) {
		for (int i = 18; i < 18 + minigameIconListSize; i++, minigameIndex++) {
			if (minigameIndex >= this.minigames.size()) {
				break;
			}
			MiniGame minigame = this.minigames.get(minigameIndex);
			this.inv.setItem(i, this.getMiniGameIcon(minigame));
		}

		return this.inv;
	}

	public void updateGUI() {
		this.createGUI(this.currentPage);
	}

	private ItemStack getMiniGameIcon(MiniGame minigame) {
		ItemStack item = new ItemStack(minigame.getSetting().getIcon());

		// display name
		String displayName = minigame.getTitle();
		if (minigame.isStarted()) {
			displayName = ChatColor.RED + displayName;
		} else {
			displayName = ChatColor.GREEN + displayName;
		}

		// lore
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.WHITE + "- Players: " + minigame.getPlayerCount() + "/" + minigame.getMaxPlayerCount());
		lore.add(ChatColor.WHITE + "- Time Limit: " + minigame.getTimeLimit() + " secs");

		// apply
		item = ItemStackTool.item(item.getType(), displayName, lore);

		return item;
	}

	public boolean isSameInventory(Inventory inv) {
		return this.inv.equals(inv);
	}

	@SuppressWarnings("deprecation")
	public void processClickEvent(InventoryClickEvent e) {
		// suppose inventory has only 1 viewer
		ItemStack ClickedItem = e.getCurrentItem();
		if (ClickedItem == null) {
			return;
		}

		Player p = (Player) e.getViewers().get(0);
		if (ClickedItem.equals(BaseIcon.LEAVE_GAME.getItem())) {
			this.minigameManager.leaveGame(p);
		} else if (ClickedItem.equals(BaseIcon.LEAVE_GAME.getItem())) {
			this.minigameManager.leaveGame(p);
		} else if (ClickedItem.equals(BaseIcon.PREVIOUS_PAGE.getItem())) {
			if (this.currentPage > 1) {
				this.currentPage -= 1;
			}
		} else if (ClickedItem.equals(BaseIcon.NEXT_PAGE.getItem())) {
			if (this.currentPage < this.getMaxPageNumber()) {
				this.currentPage += 1;
			}
		} else if (this.isMiniGameIconSlot(e.getSlot())) {
			ItemStack item = e.getCurrentItem();
			this.minigameManager.joinGame(p, item.getItemMeta().getDisplayName());
		}
	}

	private boolean isMiniGameIconSlot(int slot) {
		return (18 <= slot) && (slot <= 18 + this.minigameIconListSize);
	}

	private int getMaxPageNumber() {
		return (int) Math.ceil(this.minigames.size() / (double) minigameIconListSize);
	}

	private void updateCurrentPageNumber() {
		this.inv.setItem(49, ItemStackTool.item(BaseIcon.CURRENT_PAGE.getItem().getType(), "" + this.currentPage));
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
//
//
//
//
//
//
