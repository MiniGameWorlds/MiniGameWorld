package com.minigameworld.frames.helpers;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import com.minigameworld.frames.MiniGame;
import com.minigameworld.frames.helpers.MiniGameCustomOption.Option;
import com.wbm.plugin.util.PlayerTool;

/**
 * [Managed list]<br>
 * joined location, health scale, health, food level, exhaustion, saturation,
 * level, exp, inventory, potion effects, glowing, hide, game mode, fire ticks,
 * invulnerable, silent, gravity, allow flight, flying, velocity, walk speed,
 * fly speed, scoreboard, held item slot, bed spawn location, ender chest,
 * cooldown items, portal cooldown<br>
 * 
 * 
 * Manage player's everything<br>
 * - When join the game: save state and make pure state<br>
 * - When quit the game: restore saved state<br>
 *
 * [IMPORATNT]<br>
 * - If add a element, check that affects to viewer {@link MiniGameViewManager}
 */
public class MiniGamePlayerState {
	private MiniGame minigame;

	private Player player;
	private Location joinedLocation;
	private double healthScale;
	private double health;
	private int foodLevel;
	private float exhaustion;
	private float saturation;
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
	private boolean allowFlight; // [IMPORTANT] must setup before isFlying
	private boolean isFlying;
	private Vector velocity;
	private float walkSpeed;
	private float flySpeed;
	private Scoreboard scoreboard;
	private int heldItemSlot;
	private Location bedSpawnLocation;
	private ItemStack[] enderChest;
	private Map<Material, Integer> cooldownItems;
	private int portalCooldown;

	public MiniGamePlayerState(MiniGame minigame, Player player) {
		this.minigame = minigame;
		this.player = player;
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

		// exhaustion
		this.exhaustion = this.player.getExhaustion();

		// saturation
		this.saturation = this.player.getSaturation();

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

		// allow flight
		this.allowFlight = this.player.getAllowFlight();

		// flying
		this.isFlying = this.player.isFlying();

		// velocity
		this.velocity = this.player.getVelocity().clone();

		// walk speed
		this.walkSpeed = this.player.getWalkSpeed();

		// fly speed
		this.flySpeed = this.player.getFlySpeed();

		// scoreboard
		this.scoreboard = this.player.getScoreboard();

		// held item slot
		this.heldItemSlot = this.player.getInventory().getHeldItemSlot();

		// bedSpawnLocation
		if (this.player.getBedSpawnLocation() != null) {
			this.bedSpawnLocation = this.player.getBedSpawnLocation().clone();
		}

		// ender chest
		this.enderChest = this.player.getEnderChest().getContents();

		// cooldown items
		this.cooldownItems = new HashMap<>();
		Arrays.asList(this.player.getInventory().getContents()).stream()
				.filter(item -> item != null && player.hasCooldown(item.getType())).forEach(item -> {
					int cooldown = player.getCooldown(item.getType());
					cooldownItems.put(item.getType(), cooldown);
				});

		// portal cooldown
		this.portalCooldown = this.player.getPortalCooldown();

	}

	public void restorePlayerState() {
		// joined location
		this.player.teleport(this.joinedLocation.clone());

		// health scale
		this.player.setHealthScale(this.healthScale);

		// health
		this.player.setHealth(this.health);

		// food level
		this.player.setFoodLevel(this.foodLevel);

		// exhaustion
		this.player.setExhaustion(this.exhaustion);

		// saturation
		this.player.setSaturation(this.saturation);

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

		// allow flight
		this.player.setAllowFlight(this.allowFlight);

		// flying
		this.player.setFlying(this.isFlying);

		// velocity
		this.player.setVelocity(this.velocity);

		// walk speed
		this.player.setWalkSpeed(this.walkSpeed);

		// fly speed
		this.player.setFlySpeed(this.flySpeed);

		// scoreboard
		this.player.setScoreboard(this.scoreboard);

		// held item slot
		this.player.getInventory().setHeldItemSlot(this.heldItemSlot);

		// bedSpawnLocation
		this.player.setBedSpawnLocation(this.bedSpawnLocation);

		// ender chest
		this.player.getEnderChest().setContents(this.enderChest);

		// cooldown items
		this.cooldownItems.forEach((type, cooldown) -> player.setCooldown(type, cooldown));

		// portal cooldown
		this.player.setPortalCooldown(this.portalCooldown);
	}

	public void makePureState() {
		// joined location (tp to minigame location)
		// [IMPORTANT] must call after player state saved
		this.player.teleport(this.minigame.setting().getLocation());

		// set normal health scale (20)
		this.player.setHealthScale(20);

		// set health max
		this.player.setHealth(this.player.getHealthScale());

		// set food level max
		this.player.setFoodLevel(20);

		// exhaustion
		this.player.setExhaustion(4);

		// saturation
		this.player.setSaturation(5);

		// set exp 0
		this.player.setLevel(0);
		this.player.setExp(0);

		// clear inv & setup inventory
		this.player.getInventory().clear();
		this.minigame.inventoryManager().setupOnJoin(this.player);

		// remove all potion effects
		PlayerTool.removeAllPotionEffects(this.player);

		// remove glowing
		this.player.setGlowing(false);

		// show player
		PlayerTool.unhidePlayerFromEveryone(this.player);

		// gamemode
		GameMode liveGameMode = (GameMode) minigame.customOption().get(Option.LIVE_GAMEMODE);
		this.player.setGameMode(liveGameMode);

		// fire ticks
		this.player.setFireTicks(0);

		// invulnerable
		this.player.setInvulnerable(false);

		// silent
		this.player.setSilent(false);

		// gravity
		this.player.setGravity(true);

		// allow flight
		if (liveGameMode == GameMode.SURVIVAL || liveGameMode == GameMode.ADVENTURE) {
			this.player.setAllowFlight(false);
		}

		// flying
		this.player.setFlying(false);

		// velocity
		this.player.setVelocity(new Vector(0, 0, 0));

		// walk speed
		this.player.setWalkSpeed(0.2f);

		// fly speed
		this.player.setFlySpeed(0.1f);

		// new scoreboard (= empty)
		this.player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

		// init held item slot
		this.player.getInventory().setHeldItemSlot(0);

		// set bedSpawnLocation to minigame location
		this.player.setBedSpawnLocation(this.minigame.location());

		// clear ender chest
		this.player.getEnderChest().clear();

		// remove all items cooldown
		Arrays.asList(this.player.getInventory().getContents()).stream().filter(item -> item != null)
				.forEach(item -> player.setCooldown(item.getType(), 0));

		// init portal cooldown
		this.player.setPortalCooldown(0);
	}

	public Player getPlayer() {
		return this.player;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (other == null) {
			return false;
		} else if (getClass() == other.getClass()) {
			return ((MiniGamePlayerState) other).isSamePlayer(this.player);
		}

		return false;
	}
}