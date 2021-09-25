package com.minigameworld.minigameframes.games;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.minigameworld.minigameframes.SoloBattleMiniGame;
import com.wbm.plugin.util.InventoryTool;

public class PVP extends SoloBattleMiniGame {
	/*
	 * pvp game
	 * 
	 * rule:
	 * - change dead player's gamemode to spectator
	 * - kit: stone sword(1), bow(1), arrow(32), pork(10), golden apple(1)
	 */

	private double health;
	private List<ItemStack> items;

	public PVP() {
		super("PVP", 2, 5, 60 * 5, 10);
		this.getSetting().setSettingFixed(true);
	}

	@Override
	protected void registerCustomData() {
		Map<String, Object> customData = this.getCustomData();
		customData.put("health", 30);
		List<ItemStack> items = new ArrayList<>();
		items.add(new ItemStack(Material.STONE_SWORD));
		items.add(new ItemStack(Material.BOW));
		items.add(new ItemStack(Material.ARROW, 32));
		items.add(new ItemStack(Material.COOKED_PORKCHOP, 10));
		items.add(new ItemStack(Material.GOLDEN_APPLE));
		customData.put("items", items);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initGameSettings() {
		this.health = (int) this.getCustomData().get("health");
		this.items = (List<ItemStack>) this.getCustomData().get("items");
	}

	@Override
	protected void runTaskAfterStart() {
		super.runTaskAfterStart();
		// set health scale
		// give kit items
		for (Player p : this.getPlayers()) {
			p.setHealthScale(this.health);
			InventoryTool.addItemsToPlayer(p, this.items);
		}
	}

	@Override
	protected void processEvent(Event event) {
		if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent e = (PlayerDeathEvent) event;
			Player victim = e.getEntity();
			Player killer = victim.getKiller();

			// change victim gamemode to spectator
			victim.setGameMode(GameMode.SPECTATOR);

			// killer +1 score
			if (killer != null) {
				this.plusScore(killer, 1);
			}

			// game end when only 1 player remains
			this.checkGameEnd();
		}
	}

	private void checkGameEnd() {
		int remain = 0;
		for (Player p : this.getPlayers()) {
			if (p.getGameMode() == GameMode.SURVIVAL) {
				remain += 1;
			}
		}

		if (remain <= 1) {
			this.endGame();
		}
	}

	@Override
	protected void runTaskBeforeFinish() {
		super.runTaskBeforeFinish();
		for (Player p : this.getPlayers()) {
			p.setGameMode(GameMode.SURVIVAL);
			p.setHealthScale(20);
		}
	}

	@Override
	protected List<String> registerTutorial() {
		List<String> tutorial = new ArrayList<>();
		tutorial.add("kill: +1");
		return tutorial;
	}

	@Override
	protected void handleGameException(Player p, GameException exception, Object arg) {
		super.handleGameException(p, exception, arg);

		// set player health scale to 20
		p.setHealthScale(20);
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
