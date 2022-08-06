package com.minigameworld.minigameframes.helpers;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.minigameworld.managers.event.GameEvent;
import com.minigameworld.managers.event.GameEvent.State;
import com.minigameworld.managers.event.GameEventListener;
import com.minigameworld.minigameframes.MiniGame;
import com.wbm.plugin.util.Utils;

/**
 * Below custom options are created in `custom-data` section by default<br>
 * Must serialize/deserialize value of each options in get(), set() method<br>
 */
public class MiniGameCustomOption implements GameEventListener {

	public enum Option {
		/**
		 * Init: true<br>
		 * Description: Chatting in minigame with players
		 */
		CHAT("chat"),
		/**
		 * Init: true<br>
		 * Description: Notify changed score
		 */
		SCORE_NOTIFYING("score-notifying"),
		/**
		 * Init: false<br>
		 * Description: Can break block if true
		 */
		BLOCK_BREAK("block-break"),
		/**
		 * Init: false<br>
		 * Description: Can place block if true
		 */
		BLOCK_PLACE("block-place"),
		/**
		 * Init: false<br>
		 * Description: Players can damage each other if true (contains damage by
		 * projectile)
		 */
		PVP("pvp"),
		/**
		 * Init: true<br>
		 * Description: Players and Mobs can damage each other if true (contains damage
		 * by projectile)
		 */
		PVE("pve"),
		/**
		 * Init: true<br>
		 * Description: Not drops items when die if true
		 */
		INVENTORY_SAVE("inventory-save"),
		/**
		 * Init: true<br>
		 * Description: Will Respawn in location of minigame if true
		 */
		MINIGAME_RESPAWN("minigame-respawn"),
		/**
		 * Init: Survival<br>
		 * Description: GameMode when a player join minigame (used in
		 * {@link MiniGamePlayerData#setLive(boolean)},
		 * {@link MiniGamePlayerState#makePureState()})
		 */
		LIVE_GAMEMODE("live-gamemode"),
		/**
		 * Init: Spectator<br>
		 * Description: GameMode when a player die (used in
		 * {@link MiniGamePlayerData#setLive(boolean)})
		 */
		DEAD_GAMEMODE("dead-gamemode"),
		/**
		 * Init: ChatColor.RESET<br>
		 * Description: MiniGame personal color
		 */
		COLOR("color"),

		/**
		 * Init: true<br>
		 * Description: Whether a player's food level changes
		 */
		FOOD_LEVEL_CHANGE("food-level-change"),

		/**
		 * Init: true<br>
		 * Description: Whether a player damaged by something
		 */
		PLAYER_HURT("player-hurt");

		private String keyString;

		private Option(String keyString) {
			this.keyString = keyString;
		}

		public String getKeyString() {
			return this.keyString;
		}
	}

	private MiniGame minigame;

	public MiniGameCustomOption(MiniGame minigame) {
		this.minigame = minigame;

		// init custom options
		this.set(Option.CHAT, true);
		this.set(Option.SCORE_NOTIFYING, true);
		this.set(Option.BLOCK_BREAK, false);
		this.set(Option.BLOCK_PLACE, false);
		this.set(Option.PVP, false);
		this.set(Option.PVE, true);
		this.set(Option.INVENTORY_SAVE, true);
		this.set(Option.MINIGAME_RESPAWN, true);
		// used in MiniGamePlayerData, MiniGamePlayerState(makePureState())
		this.set(Option.LIVE_GAMEMODE, GameMode.SURVIVAL);
		// used in MiniGamePlayerData
		this.set(Option.DEAD_GAMEMODE, GameMode.SPECTATOR);
		this.set(Option.COLOR, ChatColor.RESET);
		this.set(Option.FOOD_LEVEL_CHANGE, true);
		this.set(Option.PLAYER_HURT, true);
	}

	private void setOptionData(String option, Object data) {
		this.minigame.getCustomData().put(option, data);
	}

	private Object getOptionData(String option) {
		return this.minigame.getCustomData().get(option);
	}

	public void set(Option option, Object value) {
		// Serialize enum type to String
		if (value instanceof Enum) {
			value = ((Enum<?>) value).name();
		}
		this.setOptionData(option.getKeyString(), value);
	}

	public Object get(Option option) {
		Object object = this.getOptionData(option.getKeyString());

		// Deserialize string with fit type
		switch (option) {
		case LIVE_GAMEMODE:
		case DEAD_GAMEMODE:
			return GameMode.valueOf((String) object);
		case COLOR:
			return ChatColor.valueOf((String) object);
		default:
			return object;
		}
	}

	@GameEvent(state = State.ALL)
	protected void onAsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
		Utils.warning("chat message!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		e.setCancelled(!(boolean) get(Option.CHAT));
	}

	@GameEvent(state = State.ALL)
	protected void onBlockBreakEvent(BlockBreakEvent e) {
		Utils.warning("BlockBreakEvent!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		e.setCancelled(!(boolean) this.get(Option.BLOCK_BREAK));
	}

	@GameEvent(state = State.ALL)
	protected void onBlockPlaceEvent(BlockPlaceEvent e) {
		Utils.warning("BlockPlaceEvent!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		e.setCancelled(!(boolean) this.get(Option.BLOCK_PLACE));
	}

	@GameEvent(state = State.ALL)
	protected void onPlayerDeathEvent(PlayerDeathEvent e) {
		if ((boolean) this.get(Option.INVENTORY_SAVE)) {
			// keep inv
			e.setKeepInventory(true);

			// remove drops
			e.getDrops().clear();
		} else {
			e.setKeepInventory(false);
		}
	}

	@GameEvent(state = State.ALL)
	protected void onPlayerRespawnEvent(PlayerRespawnEvent e) {
		if ((boolean) this.get(Option.MINIGAME_RESPAWN)) {
			e.setRespawnLocation(this.minigame.getLocation());
		}
	}

	@GameEvent(state = State.ALL)
	protected void onFoodLevelChangeEvent(FoodLevelChangeEvent e) {
		e.setCancelled(!(boolean) get(Option.FOOD_LEVEL_CHANGE));
	}

	@GameEvent(state = State.ALL)
	protected void onEntityDamageEvent(EntityDamageEvent event) {
		/*
		* PLAYER_HURT
		*/
		Utils.debug("EntityDamageEvent");

		EntityDamageEvent damageEvent = (EntityDamageEvent) event;
		if (damageEvent.getEntity() instanceof Player) {
			damageEvent.setCancelled(!(boolean) get(Option.PLAYER_HURT));
		}

	}

	@GameEvent(state = State.ALL)
	protected void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
		Utils.debug("EntityDamageByEntityEvent");
		// cancel damage by entity (
		// when victim == minigame player && damager == minigame player

		// PVP
		onPvp(e);

		// PVE
		onPve(e);
	}

	private void onPvp(EntityDamageByEntityEvent e) {
		Entity victim = e.getEntity();
		Entity damager = e.getDamager();
		if (victim instanceof Player && this.minigame.containsPlayer((Player) victim)) {
			// direct damage
			if (damager instanceof Player && this.minigame.containsPlayer((Player) damager)) {
				e.setCancelled(!(boolean) this.get(Option.PVP));
			}

			// projectile damage
			else if (damager instanceof Projectile) {
				Projectile proj = (Projectile) damager;
				ProjectileSource shooter = proj.getShooter();
				if (shooter instanceof Player && this.minigame.containsPlayer((Player) shooter)) {
					e.setCancelled(!(boolean) this.get(Option.PVP));
				}
			}
		}
	}

	private void onPve(EntityDamageByEntityEvent e) {
		Entity victim = e.getEntity();
		Entity damager = e.getDamager();
		if (victim instanceof Mob) {
			// direct damage
			if (damager instanceof Player) {
				e.setCancelled(!(boolean) this.get(Option.PVE));
			}

			// projectile damage
			else if (damager instanceof Projectile) {
				Projectile proj = (Projectile) damager;
				ProjectileSource shooter = proj.getShooter();
				if (shooter instanceof Player) {
					e.setCancelled(!(boolean) this.get(Option.PVE));
				}
			}
		} else if (victim instanceof Player) {
			// direct damage
			if (damager instanceof Mob) {
				e.setCancelled(!(boolean) this.get(Option.PVE));
			}

			// projectile damage
			else if (damager instanceof Projectile) {
				Projectile proj = (Projectile) damager;
				ProjectileSource shooter = proj.getShooter();
				if (shooter instanceof Mob) {
					e.setCancelled(!(boolean) this.get(Option.PVE));
				}
			}
		}
	}

	@Override
	public MiniGame minigame() {
		return this.minigame;
	}

}