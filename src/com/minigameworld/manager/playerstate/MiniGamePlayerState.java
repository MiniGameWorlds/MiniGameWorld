package com.minigameworld.manager.playerstate;

import java.util.Collection;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.wbm.plugin.util.PlayerTool;

public class MiniGamePlayerState {
	private Player player;
	private double health; // full
	private int foodLevel; // full
	private int level; // 0
	private float exp; // 0
	private ItemStack[] inv; // empty
	private Collection<PotionEffect> potionEffects; // remove all
	private boolean isGlowing; // false
	private List<Player> canNotSeePlayers; // hide
	private GameMode gameMode; // survival

	public MiniGamePlayerState(Player player) {
		this.player = player;

		// save player state
		this.savePlayerState();
	}

	public boolean isSamePlayer(Player p) {
		return this.player.equals(p);
	}

	public void savePlayerState() {
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
		this.canNotSeePlayers = PlayerTool.getPlayersCannotSeeTarget(this.player);

		// gamemode
		this.gameMode = this.player.getGameMode();
	}

	public void restorePlayerState() {
		// health
		this.player.setHealth(this.health);

		// food level
		this.player.setFoodLevel(this.foodLevel);

		// exp
		this.player.setLevel(this.level);
		this.player.setExp(this.exp);

		// inventory
		this.player.getInventory().setContents(this.inv);

		// potion effects (after remove all effects)
		PlayerTool.removeAllPotionEffects(this.player);
		for (PotionEffect effect : this.potionEffects) {
			this.player.addPotionEffect(effect);
		}

		// glowing
		this.player.setGlowing(this.isGlowing);

		// hiding
		PlayerTool.hidePlayerFromOtherPlayers(this.player, canNotSeePlayers);

		// gamemode
		this.player.setGameMode(this.gameMode);
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

		// gamemode
		this.player.setGameMode(GameMode.SURVIVAL);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof MiniGamePlayerState) {
			return ((MiniGamePlayerState) other).isSamePlayer(this.player);
		}

		return false;
	}
}
