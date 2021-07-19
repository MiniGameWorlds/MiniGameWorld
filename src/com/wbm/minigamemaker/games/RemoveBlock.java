package com.wbm.minigamemaker.games;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.wbm.minigamemaker.games.frame.TeamMiniGame;
import com.wbm.plugin.util.BlockTool;
import com.wbm.plugin.util.InventoryTool;

public class RemoveBlock extends TeamMiniGame {

	/*
	 * 설명: 일정시간내 접속한 플레이어끼리 순서대로 한번씩 점프하는 게임
	 * 
	 * 타입: Team
	 */
	private Location pos1, pos2;
	private List<ItemStack> blocks;

	public RemoveBlock() {
		super("RemoveBlock", 4, 20, 10);
		this.getSetting().setScoreNotifying(true);
	}

	@Override
	protected void registerTasks() {
		super.registerTasks();
		this.getTaskManager().registerTask("every5", new BukkitRunnable() {
			@Override
			public void run() {
				minusEveryoneScore(1);
			}
		});
	}

	@Override
	protected void registerCustomData() {
		super.registerCustomData();
		Map<String, Object> data = this.getCustomData();

		// block positions
		data.put("pos1", new Location(Bukkit.getWorld("world"), 0, 4, 0));
		data.put("pos2", new Location(Bukkit.getWorld("world"), 4, 4, 4));

		// block list
		List<ItemStack> blockList = new ArrayList<ItemStack>();
		blockList.add(new ItemStack(Material.DIRT));
		blockList.add(new ItemStack(Material.COBWEB));
		blockList.add(new ItemStack(Material.STONE));
		blockList.add(new ItemStack(Material.OAK_WOOD));
		this.getCustomData().put("blocks", blockList);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initGameSetting() {
		// set positoins
		this.pos1 = (Location) this.getCustomData().get("pos1");
		this.pos2 = (Location) this.getCustomData().get("pos2");

		// set blocks
		this.blocks = (List<ItemStack>) this.getCustomData().get("blocks");
	}

	@Override
	protected void processEvent(Event event) {
		if (event instanceof BlockBreakEvent) {
			BlockBreakEvent e = (BlockBreakEvent) event;
			ItemStack block = new ItemStack(e.getBlock().getType());
			if (this.isTargetBlock(block)) {
				e.getBlock().setType(Material.AIR);
				if (this.checkAllBlocksRemoved()) {
					this.endGame();
				}
			}
		}
	}

	boolean isTargetBlock(ItemStack target) {
		return this.blocks.contains(target);
	}

	boolean checkAllBlocksRemoved() {
		return BlockTool.isAllSameBlockWithItemStack(this.pos1, this.pos2, new ItemStack(Material.AIR));
	}

	void refillAllBlocks() {
		BlockTool.fillRandomBlock(pos1, pos2, this.blocks);
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
		this.plusEveryoneScore(this.getTimeLimit() * 5);

		// start minus score timer every 5 sec
		this.getTaskManager().runTaskTimer("every5", 0, 20 * 5);

		// refill blocks
		this.refillAllBlocks();
	}

	@Override
	protected List<String> getGameTutorialStrings() {
		List<String> tutorials = new ArrayList<String>();
		tutorials.add("Game Start: +" + this.getTimeLimit() * 5);
		tutorials.add("every 5 second: -1");
		tutorials.add("Remove ALL Blocks: Game End");
		return tutorials;
	}

}