package com.worldbiomusic.minigameworld.minigameframes.games;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.util.BlockTool;
import com.wbm.plugin.util.InventoryTool;
import com.wbm.plugin.util.LocationTool;
import com.worldbiomusic.minigameworld.minigameframes.TeamMiniGame;

public class RemoveBlock extends TeamMiniGame {

	/*
	 * Remove all blocks 
	 */
	private Location pos1, pos2;
	private List<Material> blocks;

	public RemoveBlock() {
		super("RemoveBlock", 1, 4, 60 * 3, 10);
		this.blocks = new ArrayList<>();
		this.registerTasks();
		this.getSetting().setIcon(Material.STONE_PICKAXE);
	}

	protected void registerTasks() {
		this.getTaskManager().registerTask("every5", new Runnable() {
			@Override
			public void run() {
				minusEveryoneScore(1);
			}
		});
	}

	@Override
	protected void registerCustomData() {
		Map<String, Object> data = this.getCustomData();

		// block positions
		data.put("pos1", this.getLocation());
		data.put("pos2", this.getLocation());

		// Blocks
		// save with String (Material doesn't implement ConfigurationSerialization)
		List<String> blocksData = new ArrayList<>();
		// sword
		blocksData.add(Material.COBWEB.name());
		// axe
		blocksData.add(Material.OAK_WOOD.name());
		// pickaxe
		blocksData.add(Material.COBBLESTONE.name());
		// shovel
		blocksData.add(Material.DIRT.name());

		data.put("blocks", blocksData);
	}

	@Override
	public void loadCustomData() {
		// set positoins
		this.pos1 = (Location) this.getCustomData().get("pos1");
		this.pos2 = (Location) this.getCustomData().get("pos2");

		@SuppressWarnings("unchecked")
		List<String> blocksStr = (List<String>) this.getCustomData().get("blocks");

		for (String block : blocksStr) {
			this.blocks.add(Material.valueOf(block));
		}
	}

	@Override
	protected void initGameSettings() {
	}

	@Override
	protected void processEvent(Event event) {
		if (event instanceof BlockBreakEvent) {
			BlockBreakEvent e = (BlockBreakEvent) event;
			Block block = e.getBlock();
			if (LocationTool.isIn(pos1, block.getLocation(), pos2) && this.isTargetBlock(block)) {
				e.getBlock().setType(Material.AIR);
				if (this.checkAllBlocksRemoved()) {
					this.finishGame();
				}
			}
		}
	}

	boolean isTargetBlock(Block target) {
		return this.blocks.contains(target.getType());
	}

	boolean checkAllBlocksRemoved() {
		return BlockTool.isAllSameBlockWithItemStack(this.pos1, this.pos2, new ItemStack(Material.AIR));
	}

	void refillAllBlocks() {
		BlockTool.fillBlockWithRandomMaterial(pos1, pos2, this.blocks);
	}

	@Override
	protected void runTaskAfterStart() {
		super.runTaskAfterStart();

		// give tools
		List<ItemStack> items = new ArrayList<>();
		items.add(new ItemStack(Material.IRON_AXE));
		items.add(new ItemStack(Material.IRON_PICKAXE));
		items.add(new ItemStack(Material.IRON_SHOVEL));
		items.add(new ItemStack(Material.IRON_SWORD));
		InventoryTool.addItemsToPlayers(this.getPlayers(), items);

		// add default score
		this.plusEveryoneScore(this.getTimeLimit());

		// start minus score timer every 5 sec
		this.getTaskManager().runTaskTimer("every5", 0, 20 * 5);

		// refill blocks
		this.refillAllBlocks();
	}

	@Override
	protected List<String> registerTutorial() {
		List<String> tutorial = new ArrayList<>();
		tutorial.add("Game Start: +" + this.getTimeLimit());
		tutorial.add("every 5 second: -1");
		tutorial.add("Remove ALL Blocks: Game End");
		return tutorial;
	}

}
