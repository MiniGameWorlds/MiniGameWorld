package com.minigameworld.util;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.util.ItemStackTool;

public enum FunctionItem {

	// 9th slot in hotbar
	MENU_OPENER(ItemStackTool.item(Material.BOOK, ChatColor.GREEN + "Menu Opener" + ChatColor.RESET,
			ChatColor.WHITE + "Click to open minigame menu"), 8);

	private ItemStack item;
	private int slot;

	FunctionItem(ItemStack item, int slot) {
		this.item = item;
		this.slot = slot;
	}

	public ItemStack item() {
		return item;
	}

	public int slot() {
		return slot;
	}

	public static boolean give(Player p, FunctionItem item) {
		Inventory inv = p.getInventory();
		String itemName = "" + ChatColor.GREEN + ChatColor.BOLD + item.name() + ChatColor.RESET;

		// check already have
		if (inv.contains(item.item())) {
			Utils.sendMsg(p, "You already have " + itemName + " in your inventory");
			return false;
		}

		// check inv is full
		if (inv.firstEmpty() == -1) {
			Utils.sendMsg(p, "Your inventory is full");
			return false;
		}

		// check inv slot
		if (inv.getItem(item.slot()) == null) {
			inv.setItem(item.slot(), item.item());
		} else {
			inv.addItem(item.item());
		}

		// msg
		Utils.sendMsg(p, "Get [" + itemName + "]");

		return true;
	}

	public static void giveAll(Player p) {
		Arrays.asList(FunctionItem.values()).forEach(item -> give(p, item));
	}

	public static boolean checkPlayerContainsInSlot(Player p, FunctionItem functionItem) {
		Inventory inv = p.getInventory();
		ItemStack item = inv.getItem(functionItem.slot());
		if (item == null) {
			return false;
		}

		return item.equals(functionItem.item());
	}

	public static boolean isFunctionItem(ItemStack item) {
		for (FunctionItem functionItem : values()) {
			if (functionItem.item().equals(item)) {
				return true;
			}
		}
		return false;
	}

	public static List<FunctionItem> inMinigameItems() {
		return List.of(MENU_OPENER);
	}

}
