package com.minigameworld.frames;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.minigameworld.events.minigame.player.MiniGamePlayerJoinEvent;
import com.minigameworld.managers.event.GameEvent;
import com.minigameworld.managers.event.GameEvent.State;

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
	}

	@GameEvent(state = State.WAIT)
	protected void onMiniGamePlayerJoin(MiniGamePlayerJoinEvent e) {
		// check joined minigame is this fake minigame
		MiniGame minigame = e.getMiniGame().minigame();
		if (!minigame.equals(this)) {
			return;
		}
		
		/* [IMPORTANT] must compare with "getClass()" because the sub classes which will
		 implement this class are all different to each other.
		 (Do not compare with "FakeMiniGame.class")
		 */
		if (!getClass().isAssignableFrom(minigame.getClass())) {
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
	public String getFrameType() {
		return "Fake";
	}
}
