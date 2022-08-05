package com.minigameworld.dev;

import java.util.List;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.minigameworld.managers.event.GameEvent;
import com.minigameworld.managers.event.GameEvent.State;
import com.minigameworld.minigameframes.SoloMiniGame;

public class TestGame extends SoloMiniGame {

	public TestGame() {
		super("TestGame", 30, 5);
	}

	@GameEvent(state = State.WAIT)
	protected void onBreakBlock(BlockBreakEvent e) {
		sendMessage(getSoloPlayer(), "Block break");
	}

	@GameEvent(state = State.PLAY)
	protected void onPlaceBlock(BlockPlaceEvent e) {
		sendMessage(getSoloPlayer(), "Block place");
	}

	@GameEvent(state = State.ALL)
	protected void onPlayerInteractEvent(PlayerInteractEvent e) {
		sendMessage(getSoloPlayer(), "PlayerInteractEvent");
	}

	@Override
	protected List<String> tutorial() {
		return List.of("Test");
	}

}
