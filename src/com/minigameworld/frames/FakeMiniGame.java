package com.minigameworld.frames;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.minigameworld.events.minigame.player.MiniGamePlayerJoinEvent;
import com.minigameworld.util.Utils;

/**
 * <b>[Info]</b><br>
 * - Fake minigame frame<br>
 * - Player can NOT join<br>
 * - Can be used for any purpose (e.g. Teleporter)<br>
 * <br>
 * 
 * <b>[Rule]</b><br>
 * - Override and implement anything you want
 * 
 */
public abstract class FakeMiniGame extends MiniGame implements Listener {

	public FakeMiniGame(String title) {
		super(title, 1, 1, 1, 1);

		// register this as a event listener
		Utils.registerEventListener(this);
	}

	@EventHandler
	public void onMiniGamePlayerJoin(MiniGamePlayerJoinEvent e) {
		if (!e.getMiniGame().minigame().equals(this)) {
			return;
		}

		// cancel event
		e.setCancelled(true);

		// invoke hook method
		Player p = e.getPlayer();
		onFakeJoin(p);
	}

	protected abstract void onFakeJoin(Player p);

	@Override
	protected List<String> tutorial() {
		return List.of("");
	}

	@Override
	public String frameType() {
		return "Fake";
	}
}
