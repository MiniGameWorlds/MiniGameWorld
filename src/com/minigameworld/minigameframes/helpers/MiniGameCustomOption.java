package com.minigameworld.minigameframes.helpers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.minigameworld.minigameframes.MiniGame;

public class MiniGameCustomOption {
	/*
	 * All options saved in "custom-data" section
	 */
	public enum Option {
		CHATTING("chatting"), SCORE_NOTIFYING("score-notifying"), BLOCK_BREAK("block-break"),
		BLOCK_PLACE("block-place"), PVP("pvp"), INVENTORY_SAVE("inventory-save"), MINIGAME_RESPAWN("minigame-respawn");

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
		this.set(Option.CHATTING, true);
		this.set(Option.SCORE_NOTIFYING, true);
		this.set(Option.BLOCK_BREAK, false);
		this.set(Option.BLOCK_PLACE, false);
		this.set(Option.PVP, false);
		this.set(Option.INVENTORY_SAVE, true);
		this.set(Option.MINIGAME_RESPAWN, true);
	}

	private void setOptionData(String option, Object data) {
		this.minigame.getCustomData().put(option, data);
	}

	private Object getOptionData(String option) {
		return this.minigame.getCustomData().get(option);
	}

	public void set(Option option, Object value) {
		this.setOptionData(option.getKeyString(), value);
	}

	public Object get(Option option) {
		return this.getOptionData(option.getKeyString());
	}

	public void processEvent(Event event) {
		// chat event is managed in Minigame class (for overriding)
//		if (event instanceof AsyncChatEvent) {
//			AsyncChatEvent e = (AsyncChatEvent) event;
//			e.setCancelled(!(boolean) this.get(Option.CHATTING));
//		} else 
		if (event instanceof BlockBreakEvent) {
			((BlockBreakEvent) event).setCancelled(!(boolean) this.get(Option.BLOCK_BREAK));
		} else if (event instanceof BlockPlaceEvent) {
			((BlockPlaceEvent) event).setCancelled(!(boolean) this.get(Option.BLOCK_PLACE));
		} else if (event instanceof EntityDamageByEntityEvent) {
			if ((boolean) this.get(Option.PVP)) {
				return;
			}

			// cancel damage by entity (
			// when victim == minigame player && damager == minigame player
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			Entity victim = e.getEntity();
			Entity damager = e.getDamager();
			if (!(victim instanceof Player) || !this.minigame.containsPlayer((Player) victim)) {
				return;
			}

			// direct damage
			if (damager instanceof Player || this.minigame.containsPlayer((Player) damager)) {
				e.setCancelled(true);
			}
			// projectile damage
			else if (damager instanceof Projectile) {
				Projectile proj = (Projectile) damager;
				ProjectileSource shooter = proj.getShooter();
				if (shooter instanceof Player || this.minigame.containsPlayer((Player) shooter)) {
					e.setCancelled(true);
				}
			}
		} else if (event instanceof PlayerDeathEvent) {
			if ((boolean) this.get(Option.INVENTORY_SAVE)) {
				PlayerDeathEvent e = (PlayerDeathEvent) event;
				// keep inv
				e.setKeepInventory(true);

				// remove drops
				e.getDrops().clear();
			}
		} else if (event instanceof PlayerRespawnEvent) {
			if ((boolean) this.get(Option.MINIGAME_RESPAWN)) {
				PlayerRespawnEvent e = (PlayerRespawnEvent) event;
				e.setRespawnLocation(this.minigame.getLocation());
			}
		}
	}

}
