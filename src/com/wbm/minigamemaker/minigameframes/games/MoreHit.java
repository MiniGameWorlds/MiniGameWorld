package com.wbm.minigamemaker.minigameframes.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.wbm.minigamemaker.minigameframes.TeamBattleMiniGame;
import com.wbm.plugin.util.PlayerTool;

public class MoreHit extends TeamBattleMiniGame {
	/*
	 * 설명: 더 많이 때리는 게임 타입: TeamBattle
	 */

	public MoreHit() {
		super("MoreHit", 2, 20, 10, 2, 1);
		this.getSetting().setScoreNotifying(true);
		this.setAutoTeamSetup(true);
		this.setGroupChat(true);
	}

	@Override
	protected void initGameSetting() {
		super.initGameSetting();
	}

	@Override
	protected void processEvent(Event event) {
		super.processEvent(event);
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			Entity victimEntity = e.getEntity();
			Entity damagerEntity = e.getDamager();
			if (victimEntity instanceof Player && damagerEntity instanceof Player) {
				Player victim = (Player) victimEntity;
				Player damager = (Player) damagerEntity;
				// 다른 팀일 때
				if (!this.isSameTeam(victim, damager)) {
					Team team = this.getPlayerTeam(damager);
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
	protected void registerAllPlayersToTeam() {

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
