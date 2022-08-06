package com.minigameworld.dev;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.minigameworld.managers.event.GameEvent;
import com.minigameworld.minigameframes.SoloBattleMiniGame;

public class TestGame2 extends SoloBattleMiniGame {

	public TestGame2() {
		super("TestGame2", 2, 5, 30, 5);

		getSetting().setIcon(Material.LAVA_BUCKET);
	}

	@GameEvent
	protected void onChat1(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		plusScore(p, 1);
	}

	@Override
	protected List<String> tutorial() {
		return null;
	}

}
