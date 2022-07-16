package com.worldbiomusic.minigameworld.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.worldbiomusic.minigameworld.managers.MiniGameManager;
import com.worldbiomusic.minigameworld.util.FunctionItem;
import com.worldbiomusic.minigameworld.util.Utils;

public class FunctionItemListener implements Listener {
	private MiniGameManager minigameManager;

	public FunctionItemListener(MiniGameManager minigameManager) {
		this.minigameManager = minigameManager;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerClickInvWithFunctionItem(InventoryClickEvent e) {
		ItemStack currentItem = e.getCurrentItem();
		ItemStack cursorItem = e.getCursor();
		Inventory clickedInv = e.getClickedInventory();

		// check item is menu opener
		boolean isMenuOpener = false;
		if (currentItem != null && FunctionItem.isFunctionItem(currentItem)) {
			isMenuOpener = true;
		}
		if (cursorItem != null && FunctionItem.isFunctionItem(cursorItem)) {
			isMenuOpener = true;
		}

		// check inventory is not player inv
		boolean isNotPlayerInv = false;
		if (clickedInv != null && clickedInv.getType() != InventoryType.PLAYER) {
			isNotPlayerInv = true;
		}

//		Utils.debug("current: " + currentItem);
//		Utils.debug("cursor: " + cursorItem);
//		Utils.debug("upperInv: " + upperInv);
//		Utils.debug("clickedInv: " + clickedInv + "\n");

		// cancel event if try to craft or something with menu opener
		if ((isMenuOpener && isNotPlayerInv) || (isMenuOpener && e.isShiftClick())) {
//			e.setResult(Result.DENY);

			if (FunctionItem.isFunctionItem(currentItem)) {
				e.setCurrentItem(new ItemStack(Material.AIR));
			}
			if (FunctionItem.isFunctionItem(cursorItem)) {
				e.setCursor(new ItemStack(Material.AIR));
			}
		}
	}

	@EventHandler
	public void onPlayerInteractWithFunctionItem(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack item = e.getItem();

		// check item
		if (item == null || !FunctionItem.isFunctionItem(item)) {
			return;
		}

		// check click type
		Action action = e.getAction();
		if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
			return;
		}

		// cancel event (function item can be used for some purposes)
		// e.g. place book(Menu opener) on Lectern
		e.setCancelled(true);

		if (item.equals(FunctionItem.MENU_OPENER.item())) {
			// check permission
			if (!Utils.checkPerm(p, "function-item.menu-opener")) {
				return;
			}
			
			// open menu
			this.minigameManager.getMenuManager().openMenu(p);
		}
	}
}
