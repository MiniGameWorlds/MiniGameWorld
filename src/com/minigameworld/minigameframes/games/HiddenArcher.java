
package com.minigameworld.minigameframes.games;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.minigameworld.minigameframes.TeamBattleMiniGame;
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
			damageEvent.setCancelled(true);

			if (damageEvent instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) damageEvent;

				// check
				Entity damager = e.getDamager();
				Entity victimEntity = e.getEntity();
				if (!(damager instanceof Arrow && victimEntity instanceof Player)) {
					return;
				}

				Player victim = (Player) victimEntity;
				Arrow arrow = (Arrow) damager;

				if (arrow.getShooter() instanceof Player) {
					Player shooter = ((Player) arrow.getShooter());
					if (!this.isSameTeam(victim, shooter)) {
						damageEvent.setCancelled(false);

						// remove damage
						e.setDamage(0);

						this.shootPlayer(shooter, victim);
					}
				}
			}
		}
	}

	private void shootPlayer(Player shooter, Player victim) {
		// victim
		this.sendTitle(victim, ChatColor.RED + "DIE", "");
		victim.setGameMode(GameMode.SPECTATOR);
		this.setLive(victim, false);

		// damager
		this.sendTitle(shooter, "HIT", "");
		this.plusTeamScore(shooter, 1);
	}

	@Override
	protected void runTaskAfterStart() {
		super.runTaskAfterStart();

		// hide player from other teams
		for (Player p : this.getPlayers()) {
			p.addPotionEffect(
					new PotionEffect(PotionEffectType.INVISIBILITY, 20 * this.getTimeLimit(), 1).withParticles(false));
		}

		// give tools
		InventoryTool.addItemToPlayers(getPlayers(), new ItemStack(Material.BOW));
		InventoryTool.addItemToPlayers(getPlayers(), new ItemStack(Material.ARROW, 64));
	}

}
