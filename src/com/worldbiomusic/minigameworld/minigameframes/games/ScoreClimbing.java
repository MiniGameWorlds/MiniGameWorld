package com.worldbiomusic.minigameworld.minigameframes.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.worldbiomusic.minigameworld.minigameframes.SoloBattleMiniGame;
import com.worldbiomusic.minigameworld.minigameframes.helpers.MiniGameCustomOption.Option;

public class ScoreClimbing extends SoloBattleMiniGame {
	/*
	 * Random score graph
	 * - Jump: check graph score
	 * - Sneak: stop my score
	 */
	int randomTime;
	Map<Player, Integer> chance;

	public ScoreClimbing() {
		super("ScoreClimbing", 2, 4, 60, 10);
		this.chance = new HashMap<Player, Integer>();
		this.getCustomOption().set(Option.SCORE_NOTIFYING, false);


		this.registerTasks();

		this.getSetting().setIcon(Material.OAK_STAIRS);
	}

	@Override
	protected void initGameSettings() {
		// set score limit
		this.randomTime = (int) (Math.random() * this.getTimeLimit());
	}

	protected void registerTasks() {
		// register task
		this.getTaskManager().registerTask("scoreTask", new Runnable() {
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

		// 3 chances
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
			// jump: check current graph score
			PlayerJumpEvent e = (PlayerJumpEvent) event;
			Player p = e.getPlayer();
			int leftChance = this.chance.get(p);
			if (leftChance > 0) {
				int score = this.getScore(p);
				this.sendMessage(p, "Current Score: " + score);
				// chance -1
				this.chance.put(p, leftChance - 1);
			} else if (leftChance == 0) {
				this.sendMessage(p, "You has no more score checking chance");
			} else if (hasStopped(p)) {
				this.sendMessage(p, "You can't check score until game end");
			}
		} else if (event instanceof PlayerToggleSneakEvent) {
			// sneak: stop my score
			PlayerToggleSneakEvent e = (PlayerToggleSneakEvent) event;
			Player p = e.getPlayer();
			this.setLive(p, false);

			this.sendMessage(p, "Your score has been stopped");

			// set chance to -1
			this.chance.put(p, -1);

			// check if all player stopped own score
			if(!this.isMinPlayersLive()) {
				this.finishGame();
			}
		} else if (event instanceof EntityDamageByEntityEvent) {
			// cancel jump event which automaticallt called by hit
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
				e.setCancelled(true);
			}
		}
	}

	@Override
	protected List<String> registerTutorial() {
		List<String> tutorial = new ArrayList<>();
		tutorial.add("Every 1 sec: score get to plus until random time and then score get to minus until game end");
		tutorial.add("Jump: check current score(max: 3)");
		tutorial.add("Sneak: stop Game and check score");
		return tutorial;
	}

}
