
package com.minigameworld.minigameframes.games;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;
import com.minigameworld.minigameframes.TeamBattleMiniGame;
import com.minigameworld.util.Utils;
import com.wbm.plugin.util.InventoryTool;

public class HiddenArcher extends TeamBattleMiniGame {
	/*
	 * shoot hiding players with bow
	 */

	public HiddenArcher() {
		super("HiddenArcher", 2, 60 * 3, 10);
		this.getSetting().setIcon(Material.BOW);
		this.setGroupChat(false);
	}

	@Override
	protected void initGameSettings() {
		super.initGameSettings();
	}

	@Override
	protected void createTeams() {
		Team r = new Team("r", 2);
		Team b = new Team("b", 2);
		Team g = new Team("g", 2);
		Team y = new Team("y", 2);

		r.setColor(ChatColor.RED);
		b.setColor(ChatColor.BLUE);
		g.setColor(ChatColor.GREEN);
		y.setColor(ChatColor.YELLOW);

		this.createTeam(r);
		this.createTeam(b);
		this.createTeam(g);
		this.createTeam(y);
	}

	@Override
	protected List<String> registerTutorial() {
		return null;
	}

	@Override
	protected void processEvent(Event event) {

		if (event instanceof EntityDamageEvent) {
			EntityDamageEvent damageEvent = (EntityDamageEvent) event;
//			damageEvent.setCancelled(true);

			if (damageEvent instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) damageEvent;

				Entity damager = e.getDamager();
				Entity victim = e.getEntity();
				if (!(damager instanceof Player && victim instanceof Arrow)) {
					return;
				}

				e.setCancelled(false);
				Utils.sendMsg((Player) damager, "you hit by an arrow");
			}
		} else if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent e = (PlayerDeathEvent) event;
		} else if (event instanceof ProjectileCollideEvent) {
			ProjectileCollideEvent e = (ProjectileCollideEvent) event;
			Utils.broadcast("collide");
			if (e.getCollidedWith() instanceof Player) {

			}
		}
	}

	@Override
	protected void runTaskAfterStart() {
		super.runTaskAfterStart();

		// hide player from other teams
		for (Player p : this.getPlayers()) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, this.getTimeLimit(), 1).withParticles(false));
//			for (Player other : this.getPlayers()) {
//				if (!this.isSameTeam(p, other)) {
//					PlayerTool.hidePlayerFromAnotherPlayer(p, other);
//				}
//			}
		}

		// give tools
		InventoryTool.addItemToPlayers(getPlayers(), new ItemStack(Material.BOW));
		InventoryTool.addItemToPlayers(getPlayers(), new ItemStack(Material.ARROW, 64));
	}

}
