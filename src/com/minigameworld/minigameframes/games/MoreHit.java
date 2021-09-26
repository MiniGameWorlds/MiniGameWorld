package com.minigameworld.minigameframes.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.minigameworld.minigameframes.TeamBattleMiniGame;
import com.wbm.plugin.util.PlayerTool;

public class MoreHit extends TeamBattleMiniGame {
	/*
	 * hit to get score
	 */

	public MoreHit() {
		super("MoreHit", 2, 20, 10);
		this.setGroupChat(true);
		this.setTeamRegisterMethod(TeamRegisterMethod.NONE);
		this.getSetting().setIcon(Material.STICK);
	}

	@Override
	protected void processEvent(Event event) {
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			Entity victimEntity = e.getEntity();
			Entity damagerEntity = e.getDamager();
			if (victimEntity instanceof Player && damagerEntity instanceof Player) {
				Player victim = (Player) victimEntity;
				Player damager = (Player) damagerEntity;
				// other team
				if (!this.isSameTeam(victim, damager)) {
					Team team = this.getTeam(damager);
					team.plusTeamScore(1);
					PlayerTool.heal(victim);
				}
			}
		} else if (event instanceof PlayerRespawnEvent) {
			PlayerRespawnEvent e = (PlayerRespawnEvent) event;
			e.setRespawnLocation(this.getLocation());
			this.sendMessage(e.getPlayer(), "respawn!");
		}
	}

	@Override
	protected void createTeams() {
		// create teams
		Team red = new Team("red", 2);
		red.setColor(ChatColor.RED);
		this.createTeam(red);
		Team blue = new Team("blue", 2);
		blue.setColor(ChatColor.BLUE);
		this.createTeam(blue);
		Team green = new Team("green", 2);
		green.setColor(ChatColor.GREEN);
		this.createTeam(green);
	}

	@Override
	protected void registerPlayersToTeam() {
		for (Player p : this.getPlayers()) {
			if (this.getTeam("red").isEmpty()) {
				this.registerPlayerToTeam(p, "red");
			} else if (this.getTeam("green").isEmpty()) {
				this.registerPlayerToTeam(p, "green");
			} else {
				this.registerPlayerToRandomTeam(p);
			}
		}
	}

	@Override
	protected List<String> registerTutorial() {
		List<String> tutorial = new ArrayList<>();
		tutorial.add("Hit other team member: +1");
		return tutorial;
	}

}

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
