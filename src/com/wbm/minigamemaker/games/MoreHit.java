package com.wbm.minigamemaker.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.wbm.minigamemaker.games.frame.TeamBattleMiniGame;

//class A {
//	void a() {
//		System.out.println("a");
//	}
//}
//
//class B extends A {
//	final void a() {
//		System.out.println("b");
//	}
//	
//	void b() {
//		this.a();
//	}
//}
public class MoreHit extends TeamBattleMiniGame {

//	public static void main(String[] args) {
//		B a = new B();
//		a.b();
//	}

	/*
	 * 더 많이 때리는 게임
	 */
	public MoreHit() {
		super("MoreHit", 2, 20, 10, 2, 1);
		this.setScoreNotifying(true);
		this.setAutoTeamSetup(true);
		this.setGroupChat(true);
	}

	@Override
	protected List<String> getGameTutorialStrings() {
		List<String> tutorials = new ArrayList<String>();
		tutorials.add("Hit other team member: +1");
		return tutorials;
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
					this.plusScoreToTeam(team, 1);
				}
			}
		}
	}

	@Override
	protected void registerAllPlayersToTeam() {
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
