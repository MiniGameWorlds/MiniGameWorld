package com.worldbiomusic.minigameworld.minigameframes.helpers;

import java.util.Collection;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.wbm.plugin.util.PlayerTool;
import com.worldbiomusic.minigameworld.api.MiniGameAccessor;
import com.worldbiomusic.minigameworld.api.MiniGameWorld;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameCustomOption.Option;

public class MiniGamePlayerState {
	private Player player;
	private Location joinedLocation;
	private double healthScale;
	private double health;
	private int foodLevel;
	private int level;
	private float exp;
	private ItemStack[] inv;
	private Collection<PotionEffect> potionEffects;
	private boolean isGlowing;
	private List<Player> canNotSeePlayers;
	private GameMode gameMode;
//	private boolean visualFire; // After 1.17 API
	private int fireTicks;
//	private int freezeTicks; // After 1.17 API
	private boolean invulnerable;
	private boolean silent;
	private boolean gravity;

	public MiniGamePlayerState(Player player) {
		this.player = player;

		// save player state
		this.savePlayerState();
	}

	public boolean isSamePlayer(Player p) {
		return this.player.equals(p);
	}

	public void savePlayerState() {
		// joined location
		this.joinedLocation = this.player.getLocation().clone();

		// health scale
		this.healthScale = this.player.getHealthScale();

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

		// fire ticks
		this.fireTicks = this.player.getFireTicks();

		// invulnerable
		this.invulnerable = this.player.isInvulnerable();

		// silent
		this.silent = this.player.isSilent();

		// gravity
		this.gravity = this.player.hasGravity();
	}

	public void restorePlayerState() {
		// joined location
		this.player.teleport(this.joinedLocation);

		// health scale
		this.player.setHealthScale(this.healthScale);

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

		// fire ticks
		this.player.setFireTicks(this.fireTicks);

		// invulnerable
		this.player.setInvulnerable(this.invulnerable);

		// silent
		this.player.setSilent(this.silent);

		// gravity
		this.player.setGravity(this.gravity);
	}

	public void makePureState() {
		// joined location
		// nothing (tp to minigame location)

		// set normal health scale (20)
		this.player.setHealthScale(20);

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
		MiniGameWorld mw = MiniGameWorld.create(MiniGameWorld.API_VERSION);
		MiniGameAccessor minigame = mw.getPlayingMiniGame(this.player);
		GameMode liveGameMode = (GameMode) minigame.getCustomOption(Option.LIVE_GAMEMODE);
		this.player.setGameMode(liveGameMode);

		// fire ticks
		this.player.setFireTicks(0);

		// invulnerable
		this.player.setInvulnerable(false);

		// silent
		this.player.setSilent(false);

		// gravity
		this.player.setGravity(true);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof MiniGamePlayerState) {
			return ((MiniGamePlayerState) other).isSamePlayer(this.player);
		}

		return false;
	}
}