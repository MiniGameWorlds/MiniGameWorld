package com.minigameworld.manager.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.minigameworld.manager.MiniGameManager;
import com.minigameworld.minigameframes.MiniGame;
import com.minigameworld.util.Setting;
import com.wbm.plugin.util.ItemStackTool;

import net.kyori.adventure.text.Component;

public class MiniGameGUI {
	private MiniGameManager minigameManager;
	private Inventory inv;
	private List<MiniGame> minigames;
	private int currentPage;

	private final int minigameIconListSize = 27;

	private enum BaseIcon {

		LEAVE_GAME(ItemStackTool.item(Material.WHITE_BED, "Leave Game")),
		HORIZON_LINE(ItemStackTool.item(Material.BARRIER, " ")),
		PREVIOUS_PAGE(ItemStackTool.item(Material.REDSTONE_TORCH, "<")),
		CURRENT_PAGE(ItemStackTool.item(Material.PAPER, "1")), NEXT_PAGE(ItemStackTool.item(Material.TORCH, ">"));

		private ItemStack item;

		BaseIcon(ItemStack item) {
			this.item = item;
		}

		public ItemStack getItem() {
			return this.item;
		}
	}

	public MiniGameGUI(MiniGameManager minigameManager, List<MiniGame> minigames) {
		this.minigameManager = minigameManager;
		this.minigames = minigames;
		this.currentPage = 1;
		this.makeBaseIcons();
	}

	private void makeBaseIcons() {
		this.inv = Bukkit.createInventory(null, 9 * 6, Component.text(Setting.GUI_TITLE));

		// leave game button
		this.inv.setItem(1, BaseIcon.LEAVE_GAME.getItem());

		// horizon line
		for (int i = 9; i < 18; i++) {
			this.inv.setItem(i, BaseIcon.HORIZON_LINE.getItem());
		}

		// page buttons
		this.inv.setItem(48, BaseIcon.PREVIOUS_PAGE.getItem());
		this.inv.setItem(49, BaseIcon.CURRENT_PAGE.getItem());
		this.inv.setItem(50, BaseIcon.NEXT_PAGE.getItem());
	}

	private void emptyMiniGameIconList() {
		for (int i = 18; i < 18 + minigameIconListSize; i++) {
			this.inv.setItem(i, null);
		}
	}

	public Inventory createGUI(int page) {
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

	public Inventory updateGUI() {
		this.updateCurrentPage();
		return this.createGUI(this.currentPage);
	}

	private ItemStack getMiniGameIcon(MiniGame minigame) {
		ItemStack item = new ItemStack(minigame.getSetting().getIcon());

		// display name
		String displayName = minigame.getTitle();
		if (minigame.isStarted()) {
			displayName = ChatColor.RED + displayName;
		} else {
			displayName = ChatColor.GREEN + displayName;
			item = ItemStackTool.enchant(item, Enchantment.LUCK, 1);
		}

		// lore
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.WHITE + "- players: " + minigame.getPlayerCount() + "/" + minigame.getMaxPlayerCount());
		lore.add(ChatColor.WHITE + "- Time Limit: " + minigame.getTimeLimit());

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
			if (this.currentPage < this.getMaxPage()) {
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

	private int getMaxPage() {
		return (int) Math.ceil(this.minigames.size() / (double) minigameIconListSize);
	}

	private void updateCurrentPage() {
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
