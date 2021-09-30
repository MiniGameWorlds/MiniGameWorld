package com.minigameworld.minigameframes.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.minigameworld.MiniGameWorldMain;
import com.minigameworld.minigameframes.SoloBattleMiniGame;
import com.wbm.plugin.util.BlockTool;

public class FallingBlock extends SoloBattleMiniGame {
	/*
	 * TODO: PlayerMoveEvent사용하지 말고, 5틱마다 플레이어 밑에 블럭 지우기
	 */

	public FallingBlock() {
		super("FallingBlock", 2, 4, 120, 10);
	}

	@Override
	protected void registerCustomData() {
		super.registerCustomData();

		this.getCustomData().put("pos1", this.getLocation());
		this.getCustomData().put("pos2", this.getLocation());

//		// task
//		this.removeBlockWithDelayTask();
	}

	@Override
	protected void initGameSettings() {
		Location pos1 = (Location) this.getCustomData().get("pos1");
		Location pos2 = (Location) this.getCustomData().get("pos2");

		List<Material> mat = new ArrayList<>();
		for (int i = 0; i < 10000; i++) {
			mat.add(Material.STONE);
		}

		// fill blocks
		BlockTool.setBlockWithMaterial(pos1, pos2, mat);
	}

	@Override
	protected void processEvent(Event event) {
		if (event instanceof PlayerMoveEvent) {
			PlayerMoveEvent e = (PlayerMoveEvent) event;
			Player p = e.getPlayer();

			Location pLoc = p.getLocation();
			if (pLoc.getY() < ((Location) this.getCustomData().get("pos1")).getY() - 0.5) {
				p.setHealth(0);
			}

			Block belowBlock = pLoc.subtract(0, 1, 0).getBlock();

			new BukkitRunnable() {

				@Override
				public void run() {
					belowBlock.setType(Material.AIR);
				}
			}.runTaskLater(MiniGameWorldMain.getInstance(), 5);

		} else if (event instanceof PlayerDeathEvent) {
			PlayerDeathEvent e = (PlayerDeathEvent) event;
			Player p = e.getEntity();
			p.setGameMode(GameMode.SPECTATOR);
			this.setLive(p, false);
			if (!this.isMinPlayersLive()) {
				this.endGame();
			}
		}
	}

//	private void removeBlockWithDelayTask(Block b) {
//		this.getTaskManager().registerTask("removeBlock", new Runnable() {
//
//			@Override
//			public void run() {
//				b.setType(Material.AIR);
//			}
//		});
//	}

	@Override
	protected void runTaskAfterStart() {
		super.runTaskAfterStart();

		Location pos1 = (Location) this.getCustomData().get("pos1");
		Location pos2 = (Location) this.getCustomData().get("pos2");

		// fill blocks
		BlockTool.setBlockWithMaterial(pos1, pos2, Material.STONE);
	}

	@Override
	protected List<String> registerTutorial() {
		return null;
	}

}
