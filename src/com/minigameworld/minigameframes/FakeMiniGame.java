package com.minigameworld.minigameframes;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.minigameworld.api.MiniGameAccessor;
import com.minigameworld.customevents.minigame.player.MiniGamePlayerJoinEvent;
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
		// check joined minigame is fake minigame
		MiniGameAccessor minigame = e.getMiniGame();
		/* [IMPORTANT] must compare with "getClass()" because the sub classes which will
		 implement this class are all different to each other.
		 (Do not compare with "FakeMiniGame.class")
		 */
		if (!getClass().isAssignableFrom(minigame.getClassType())) {
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
	protected void onEvent(Event event) {
	}

	@Override
	protected List<String> tutorial() {
		return List.of("");
	}

	@Override
	public String getFrameType() {
		return "Fake";
	}
}
