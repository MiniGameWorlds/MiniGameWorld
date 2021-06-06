package com.wbm.minigamemaker.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.wbm.minigamemaker.games.frame.SoloBattleMiniGame;

public class ScoreClimbing extends SoloBattleMiniGame {
	/*
	 * 설명: 랜덤 시간까지는 점수가 올라가지만, 그 후에는 점수가 깎임 (특정 이벤트로 점수 확인 3번 가능, 특정 이벤트로 멈춤 가능)
	 * 
	 * 타입: Solo
	 */
	int randomTime;
	Map<Player, Integer> chance;

	public ScoreClimbing() {
		super("ScoreClimbing", 4, 60, 10);
		this.chance = new HashMap<Player, Integer>();

	}

	@Override
	protected void initGameSetting() {
		// 상한 점수 설정
		this.randomTime = (int) (Math.random() * this.getTimeLimit());

	}

	@Override
	protected void registerTasks() {
		// register task
		this.getTaskManager().registerTask("scoreTask", new BukkitRunnable() {

			@Override
			public void run() {
				for (Player p : getPlayers()) {
					if (!hasStopped(p)) {
						if (getLeftFinishTime() > randomTime) {
							plusScore(p, 1);
						} else {
							minusScore(p, 1);
						}
					}
				}

			}
		});
	}

	@Override
	protected void runTaskAfterStart() {
		super.runTaskAfterStart();

		// 찬스 3번씩 설정
		this.chance.clear();
		this.getPlayers().forEach(p -> chance.put(p, 3));

		// timer task
		this.getTaskManager().runTaskTimer("scoreTask", 0, 20);
	}

	private boolean hasStopped(Player p) {
		return this.chance.get(p) == -1;
	}

	@Override
	protected void processEvent(Event event) {
		if (event instanceof PlayerJumpEvent) {
			// 점프 event = 점수 확인
			PlayerJumpEvent e = (PlayerJumpEvent) event;
			Player p = e.getPlayer();
			int leftChance = this.chance.get(p);
			if (leftChance > 0) {
				int score = this.getScore(p);
				p.sendMessage("Current Score: " + score);
				// chance 기회 -1
				this.chance.put(p, leftChance - 1);
			} else if (leftChance == 0) {
				p.sendMessage("You has no more score checking chance");
			} else if (hasStopped(p)) {
				p.sendMessage("You can't check score until game end");
			}
		} else if (event instanceof PlayerToggleSneakEvent) {
			// 웅크리기 event = 점수 stop
			PlayerToggleSneakEvent e = (PlayerToggleSneakEvent) event;
			Player p = e.getPlayer();

			p.sendMessage("Your score has been stopped");

			// chance를 -1로 만들기
			this.chance.put(p, -1);

			// 모든 사람의 찬스가 -1이면 모든 사람이 stop했다는것을 증명
			for (int chance : this.chance.values()) {
				if (chance != -1) {
					return;
				}
			}

			// 모든 사람이 stop을 했으므로 게임 종료
			this.endGame();
		} else if (event instanceof EntityDamageByEntityEvent) {
			// 때리면 맞는 사람에게 Jump Event가 발생되어 버림
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
				e.setCancelled(true);
			}
		}
	}

	@Override
	protected List<String> getGameTutorialStrings() {
		List<String> tutorial = new ArrayList<String>();
		tutorial.add("Every 1 sec: score get to plus until random time and then score get to minus until game end");
		tutorial.add("Jump: check current score(max: 3)");
		tutorial.add("Sneak: stop Game and check score");
		return tutorial;
	}

}
