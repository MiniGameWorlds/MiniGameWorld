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
import com.minigameworld.util.Utils;
import com.wbm.plugin.util.ItemStackTool;

import net.kyori.adventure.text.Component;

public class MiniGameGUI {
	private Inventory inv;
	private int currentPage;

	private enum BaseElement {

		LEAVE_GAME(ItemStackTool.item(Material.WHITE_BED, "Leave Game")),
		HORIZON_LINE(ItemStackTool.item(Material.BARRIER, " ")),
		PREVIOUS_PAGE(ItemStackTool.item(Material.REDSTONE_TORCH, "<")),
		CURRENT_PAGE(ItemStackTool.item(Material.PAPER, "1")), NEXT_PAGE(ItemStackTool.item(Material.TORCH, ">"));

		private ItemStack item;

		BaseElement(ItemStack item) {
			this.item = item;
		}

		public ItemStack getItem() {
			return this.item;
		}
	}

	private MiniGameManager minigameManager;

	public MiniGameGUI(MiniGameManager minigameManager) {
		this.minigameManager = minigameManager;
		this.currentPage = 1;
		this.makeBase();
	}

	private void makeBase() {
		this.inv = Bukkit.createInventory(null, 9 * 6, Component.text("MiniGameWorld"));

		// leave game button
		this.inv.setItem(1, BaseElement.LEAVE_GAME.getItem());

		// horizon line
		for (int i = 9; i < 18; i++) {
			this.inv.setItem(i, BaseElement.HORIZON_LINE.getItem());
		}

		// page buttons
		this.inv.setItem(48, BaseElement.PREVIOUS_PAGE.getItem());
		this.inv.setItem(49, BaseElement.CURRENT_PAGE.getItem());
		this.inv.setItem(50, BaseElement.NEXT_PAGE.getItem());
	}

	public Inventory getGUI(List<MiniGame> minigames, int page) {
		// slot: 18 ~ 44 (count: 27)
		int minigameIndex = 0 + ((page - 1) * 27);
		for (int i = 18; i < 45; i++, minigameIndex++) {
			if (minigameIndex >= minigames.size()) {
				break;
			}
			MiniGame minigame = minigames.get(minigameIndex);
			this.inv.setItem(i, this.getMiniGameIcon(minigame));
		}

		return this.inv;
	}

	public Inventory updateGUI(List<MiniGame> minigames) {
		return this.getGUI(minigames, this.currentPage);
	}

	private ItemStack getMiniGameIcon(MiniGame minigame) {
		ItemStack item = new ItemStack(minigame.getSetting().getIcon());

		// display name
		String displayName = minigame.getTitle();
		if (minigame.isStarted()) {
			displayName = ChatColor.RED + displayName;
		} else {
			displayName = ChatColor.GREEN + displayName;
			ItemStackTool.enchant(item, Enchantment.LUCK, 1);
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

	public void processClickEvent(InventoryClickEvent e) {
		if (e.getCurrentItem().equals(BaseElement.LEAVE_GAME.getItem())) {
			Utils.debug("leave game");
			Player p = (Player)e.getViewers().get(0);
			this.minigameManager.leaveGame(p);
		}
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
