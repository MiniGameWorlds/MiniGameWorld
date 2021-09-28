package com.minigameworld.minigameframes.games;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.minigameworld.minigameframes.SoloMiniGame;
import com.minigameworld.minigameframes.utils.MiniGameCustomOption.Option;

public class FitTool extends SoloMiniGame {
	/*
	 * Break blocks with fit tools
	 */

	List<Material> blocks;

	public FitTool() {
		super("FitTool", 30, 10);
		this.getSetting().setIcon(Material.STONE_PICKAXE);
		this.getCustomOption().setOption(Option.SCORE_NOTIFYING, false);
		this.getSetting().setPassUndetectableEvents(true);
	}

	@Override
	protected void initGameSettings() {
		this.blocks = new ArrayList<Material>();
		// sword
		this.blocks.add(Material.COBWEB);
		// axe
		this.blocks.add(Material.OAK_WOOD);
		// pickaxe
		this.blocks.add(Material.COBBLESTONE);
		// shovel
		this.blocks.add(Material.DIRT);
	}

	private Material getRandomBlock() {
		int r = (int) (Math.random() * this.blocks.size());
		return this.blocks.get(r);
	}

	@Override
	public void processEvent(Event event) {
		if (event instanceof BlockBreakEvent) {
			BlockBreakEvent e = (BlockBreakEvent) event;
			Player p = e.getPlayer();

			Block b = e.getBlock();

			// plus score with specific block
			if (this.blocks.contains(b.getType())) {
				e.setCancelled(true);
				this.plusScore(p, 1);

				// random block
				b.setType(this.getRandomBlock());
			}

		} 
	}

	@Override
	protected void runTaskAfterStart() {
		// give tools
		for (Player p : this.getPlayers()) {
			p.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
			p.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
			p.getInventory().addItem(new ItemStack(Material.IRON_AXE));
			p.getInventory().addItem(new ItemStack(Material.IRON_SHOVEL));
		}
	}

	@Override
	protected List<String> registerTutorial() {
		List<String> tutorial = new ArrayList<>();
		tutorial.add("Break block: +1");
		return tutorial;
	}

}
