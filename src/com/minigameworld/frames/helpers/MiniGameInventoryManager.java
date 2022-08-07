package com.minigameworld.frames.helpers;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.minigameworld.frames.MiniGame;
import com.minigameworld.managers.event.GameEvent;
import com.minigameworld.managers.event.GameEvent.State;
import com.minigameworld.managers.event.GameEventListener;
import com.minigameworld.util.FunctionItem;

public class MiniGameInventoryManager implements GameEventListener {
	private MiniGame minigame;

	public MiniGameInventoryManager(MiniGame minigame) {
		this.minigame = minigame;
	}

	public void setupOnJoin(Player p) {
		giveMinigameFunctionItems(p);
	}

	private void giveMinigameFunctionItems(Player p) {
		FunctionItem.inMinigameItems().forEach(item -> giveFunctionItem(p, item));
	}

	private void giveFunctionItem(Player p, FunctionItem item) {
		// forced slot
		// [IMPORTANT] different with "FunctionItem.give()"
		p.getInventory().setItem(item.slot(), item.item());
	}

	@GameEvent(state = State.ALL)
	protected void onPlayerDropItem(PlayerDropItemEvent e) {
		// check item
		if (FunctionItem.isFunctionItem(e.getItemDrop().getItemStack())) {
			e.setCancelled(true);
		}
	}

	@GameEvent(state = State.ALL)
	protected void onPlayerDeath(PlayerDeathEvent e) {
		// remove dropped function items
		FunctionItem.inMinigameItems().forEach(item -> e.getDrops().remove(item.item()));
	}

	@GameEvent(state = State.ALL)
	protected void onPlayerResapwn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		Inventory inv = p.getInventory();

		// give function items only if the slot is empty
		// [IMPORTANT] Minigame can overwrite menu opener slot if want
		FunctionItem.inMinigameItems().forEach(item -> {
			if (inv.getItem(item.slot()) == null) {
				giveFunctionItem(p, item);
			}
		});

	}

	@GameEvent(state = State.ALL)
	protected void onPlayerClickInv(InventoryClickEvent e) {
		int slot = e.getSlot();
		ItemStack item = e.getCurrentItem();

		// cancel event if click function item
		FunctionItem.inMinigameItems().forEach(functionItem -> {
			if (slot == functionItem.slot() && FunctionItem.isFunctionItem(item)) {
				e.setCancelled(true);
				return;
			}
		});
	}

	@GameEvent(state = State.ALL)
	protected void onPlayerSwapHandItem(PlayerSwapHandItemsEvent e) {
		ItemStack mainHandItem = e.getMainHandItem();
		ItemStack offHandItem = e.getOffHandItem();
		if (FunctionItem.isFunctionItem(mainHandItem) || FunctionItem.isFunctionItem(offHandItem)) {
			e.setCancelled(true);
		}
	}

	@Override
	public MiniGame minigame() {
		return this.minigame;
	}

}
