package com.minigameworld.minigameframes.utils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChatEvent;

import com.minigameworld.minigameframes.MiniGame;
import com.minigameworld.util.Utils;

public class MiniGameCustomOption {
	public enum Option {
		CHATTING("chatting"), SCORE_NOTIFYING("scoreNotifying"), BLOCK_BREAK("blockBreak"), BLOCK_PLACE("blockPlace"),
		PVP("pvp");

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
		this.setOption(Option.CHATTING, true);
		this.setOption(Option.SCORE_NOTIFYING, true);
		this.setOption(Option.BLOCK_BREAK, false);
		this.setOption(Option.BLOCK_PLACE, false);
		this.setOption(Option.PVP, false);
	}

	public void setOption(Option option, Object value) {
		this.setData(option.getKeyString(), value);
	}

	public Object getOption(Option option) {
		return this.getData(option.getKeyString());
	}

	private void setData(String option, Object data) {
		this.minigame.getCustomData().put(option, data);
	}

	private Object getData(String option) {
		return this.minigame.getCustomData().get(option);
	}

	public void processEvent(Event event) {
		if (event instanceof PlayerChatEvent) {
			PlayerChatEvent e = (PlayerChatEvent) event;
			e.setCancelled(!(boolean) this.getOption(Option.CHATTING));
		} else if (event instanceof BlockBreakEvent) {
			((BlockBreakEvent) event).setCancelled(!(boolean) this.getOption(Option.BLOCK_BREAK));
		} else if (event instanceof BlockPlaceEvent) {
			((BlockPlaceEvent) event).setCancelled(!(boolean) this.getOption(Option.BLOCK_PLACE));
		} else if (event instanceof EntityDamageByEntityEvent) {
			if ((boolean) this.getOption(Option.PVP)) {
				return;
			}

			// cancel damage by entity
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			Entity victim = e.getEntity();
			Entity damager = e.getDamager();
			if (!(victim instanceof Player)) {
				return;
			}

			// direct damage
			if (damager instanceof Player) {
				e.setCancelled(true);
			}
			// projectile damage
			else if (damager instanceof Projectile) {
				Projectile proj = (Projectile) damager;
				if (proj.getShooter() instanceof Player) {
					e.setCancelled(true);
				}
			}
		}
	}

}
