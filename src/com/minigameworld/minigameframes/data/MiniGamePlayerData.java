package com.minigameworld.minigameframes.data;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.wbm.plugin.util.PlayerTool;

public class MiniGamePlayerData {
	private Player player;
	private double health;
	private int foodLevel;
	private int level;
	private float exp;
	private ItemStack[] inv;
	private Collection<PotionEffect> potionEffects;
	private boolean isGlowing;
	private boolean isHiding;

	public MiniGamePlayerData(Player player) {
		this.player = player;

		// save player data
		this.savePlayerData();
	}

	public boolean isSamePlayer(Player p) {
		return this.player.equals(p);
	}

	public void savePlayerData() {
		// health
		this.health = this.player.getHealth();

		// food level

		this.foodLevel = this.player.getFoodLevel();

		// exp
		this.level = this.player.getLevel();
		this.exp = this.player.getExp();

		// inventory
		this.inv = this.player.getInventory().getContents();

		// potion effect
		this.potionEffects = this.player.getActivePotionEffects();

		// glowing
		this.isGlowing = this.player.isGlowing();

		// hiding
		this.isHiding = PlayerTool.isHidden(this.player);
	}

	public void restorePlayerData() {
		// health
		this.player.setHealth(this.health);

		// food level
		this.player.setFoodLevel(this.foodLevel);

		// exp
		this.player.setLevel(this.level);
		this.player.setExp(this.exp);

		// inventory
		this.player.getInventory().setContents(this.inv);

		// potion effects
		for (PotionEffect effect : this.potionEffects) {
			this.player.addPotionEffect(effect);
		}

		// glowing
		this.player.setGlowing(this.isGlowing);

		// hiding
		if (this.isHiding) {
			PlayerTool.hidePlayerFromEveryone(this.player);
		}
	}

	public void makePureState() {
		// set health max
		this.player.setHealth(this.player.getHealthScale());

		// set food level max
		this.player.setFoodLevel(20);

		// set exp 0
		this.player.setLevel(0);
		this.player.setExp(0);

		// clear inv
		this.player.getInventory().clear();

		// remove all potion effects
		PlayerTool.removeAllPotionEffects(this.player);

		// remove glowing
		this.player.setGlowing(false);

		// show player
		PlayerTool.unhidePlayerFromEveryone(this.player);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof MiniGamePlayerData) {
			return ((MiniGamePlayerData) other).isSamePlayer(this.player);
		}

		return false;
	}
}
