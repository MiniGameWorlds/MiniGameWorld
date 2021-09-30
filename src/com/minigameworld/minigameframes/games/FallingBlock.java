package com.minigameworld.minigameframes.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;

import com.minigameworld.MiniGameWorldMain;
import com.minigameworld.minigameframes.SoloBattleMiniGame;
import com.minigameworld.minigameframes.helpers.MiniGameCustomOption.Option;
import com.wbm.plugin.util.BlockTool;

public class FallingBlock extends SoloBattleMiniGame {
	private Material removingBlock;

	public FallingBlock() {
		super("FallingBlock", 2, 4, 120, 10);
		
		// settings
		this.getSetting().setIcon(Material.SAND);

		// options
		this.getCustomOption().setOption(Option.PVP, true);

		// task
		this.removeBelowBlockTask();
	}

	private void removeBelowBlockTask() {
		this.getTaskManager().registerTask("removeBelowBlock", new Runnable() {

			@Override
			public void run() {
				for (Player p : getPlayers()) {
					if (!isLive(p)) {
						return;
					}

					Location pLoc = p.getLocation();

					// check fell
					if (hasFollen(p)) {
						processFollenPlayer(p);
					}

					// check removingBlock
					Block belowBlock = pLoc.subtract(0, 1, 0).getBlock();
					if (belowBlock.getType() == removingBlock) {
						// remove block with delay
						new BukkitRunnable() {
							@Override
							public void run() {
								belowBlock.setType(Material.AIR);
							}
						}.runTaskLater(MiniGameWorldMain.getInstance(), 5);
					}
				}
			}
		});
	}

	private boolean hasFollen(Player p) {
		Location pLoc = p.getLocation();
		if (pLoc.getY() < ((Location) getCustomData().get("pos1")).getY() - 0.5) {
			return true;
		}
		return false;
	}

	@Override
	protected void registerCustomData() {
		super.registerCustomData();

		this.getCustomData().put("pos1", this.getLocation());
		this.getCustomData().put("pos2", this.getLocation());

		this.getCustomData().put("removingBlock", Material.STONE.name());
	}

	@Override
	protected void initGameSettings() {
		// custom data
		Location pos1 = (Location) this.getCustomData().get("pos1");
		Location pos2 = (Location) this.getCustomData().get("pos2");

		this.removingBlock = Material.valueOf((String) this.getCustomData().get("removingBlock"));

		// fill blocks
		BlockTool.setBlockWithMaterial(pos1, pos2, this.removingBlock);
	}

	private void processFollenPlayer(Player p) {
		p.setGameMode(GameMode.SPECTATOR);
		this.setLive(p, false);
		if (!this.isMinPlayersLive()) {
			this.endGame();
		}
	}

	@Override
	protected void runTaskAfterStart() {
		super.runTaskAfterStart();

		Location pos1 = (Location) this.getCustomData().get("pos1");
		Location pos2 = (Location) this.getCustomData().get("pos2");

		// fill blocks
		BlockTool.setBlockWithMaterial(pos1, pos2, Material.STONE);

		// start remove block task
		this.getTaskManager().runTaskTimer("removeBelowBlock", 0, 2);
	}

	@Override
	protected List<String> registerTutorial() {
		List<String> tutorial = new ArrayList<>();
		tutorial.add("fall: die");
		tutorial.add("block will be disappeared when you step");
		return tutorial;
	}

	@Override
	protected void processEvent(Event event) {
	}

}
