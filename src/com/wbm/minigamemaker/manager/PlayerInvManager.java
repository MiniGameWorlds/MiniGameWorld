package com.wbm.minigamemaker.manager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerInvManager {
	private Map<Player, ItemStack[]> invs;

	public PlayerInvManager() {
		this.invs = new HashMap<Player, ItemStack[]>();
	}

	public void savePlayerInv(Player p) {
		this.invs.put(p, p.getInventory().getContents());
	}

	public boolean restorePlayerInv(Player p) {
		if (this.invs.containsKey(p)) {
			p.getInventory().setContents(this.invs.get(p));
			return true;
		}
		return false;
	}
}
