package com.worldbiomusic.minigameworld.minigameframes.helpers;

import java.util.Collection;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import com.wbm.plugin.util.PlayerTool;
import com.worldbiomusic.minigameworld.minigameframes.MiniGame;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameCustomOption.Option;

public class MiniGamePlayerState {
	private MiniGame minigame;

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
	private boolean isFlying;
	private Vector velocity;
	private float walkSpeed;
	private float flySpeed;
	private boolean allowFlight;

	public MiniGamePlayerState(MiniGame minigame, Player player) {
		this.minigame = minigame;
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

		// flying
		this.isFlying = this.player.isFlying();

		// velocity
		this.velocity = this.player.getVelocity().clone();

		// walk speed
		this.walkSpeed = this.player.getWalkSpeed();

		// fly speed
		this.flySpeed = this.player.getFlySpeed();

		// allow flight
		this.allowFlight = this.player.getAllowFlight();
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

		// flying
		this.player.setFlying(this.isFlying);

		// velocity
		this.player.setVelocity(this.velocity);

		// walk speed
		this.player.setWalkSpeed(this.walkSpeed);

		// fly speed
		this.player.setFlySpeed(this.flySpeed);

		// allow flight
		this.player.setAllowFlight(this.allowFlight);
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
		GameMode liveGameMode = (GameMode) minigame.getCustomOption().get(Option.LIVE_GAMEMODE);
		this.player.setGameMode(liveGameMode);

		// fire ticks
		this.player.setFireTicks(0);

		// invulnerable
		this.player.setInvulnerable(false);

		// silent
		this.player.setSilent(false);

		// gravity
		this.player.setGravity(true);

		// flying
		this.player.setFlying(false);

		// velocity
		this.player.setVelocity(new Vector(0, 0, 0));

		// walk speed
		this.player.setWalkSpeed(0.2f);

		// fly speed
		this.player.setFlySpeed(0.1f);

		// allow flight
		if (liveGameMode == GameMode.SURVIVAL || liveGameMode == GameMode.ADVENTURE) {
			this.player.setAllowFlight(false);
		}
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof MiniGamePlayerState) {
			return ((MiniGamePlayerState) other).isSamePlayer(this.player);
		}

		return false;
	}
}